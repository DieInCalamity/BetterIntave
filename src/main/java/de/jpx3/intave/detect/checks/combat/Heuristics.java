package de.jpx3.intave.detect.checks.combat;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.google.common.collect.Lists;
import de.jpx3.intave.IntaveControl;
import de.jpx3.intave.IntavePlugin;
import de.jpx3.intave.detect.IntaveMetaCheck;
import de.jpx3.intave.detect.checks.combat.heuristics.Anomaly;
import de.jpx3.intave.detect.checks.combat.heuristics.Confidence;
import de.jpx3.intave.detect.checks.combat.heuristics.MiningStrategy;
import de.jpx3.intave.detect.checks.combat.heuristics.detection.*;
import de.jpx3.intave.event.packet.PacketDescriptor;
import de.jpx3.intave.event.packet.PacketSubscription;
import de.jpx3.intave.event.packet.Sender;
import de.jpx3.intave.tools.AccessHelper;
import de.jpx3.intave.tools.annotate.Native;
import de.jpx3.intave.tools.sync.Synchronizer;
import de.jpx3.intave.user.UserCustomCheckMeta;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Collectors;

public final class Heuristics extends IntaveMetaCheck<Heuristics.HeuristicMeta> {
  private final IntavePlugin plugin;

  public Heuristics(IntavePlugin plugin) {
    super("Heuristics", "heuristics", HeuristicMeta.class);
    this.plugin = plugin;
    this.setupSubChecks();
    this.setupEvaluationScheduler(plugin);
  }

  private void setupEvaluationScheduler(IntavePlugin plugin) {
    Bukkit.getScheduler().scheduleAsyncRepeatingTask(plugin, this::evaluateAll, 0, 400);
  }

  @Native
  public void setupSubChecks() {
    appendCheckPart(new ReshapedJumpHeuristic(this));
    appendCheckPart(new RotationAccuracyYawHeuristic(this));
    appendCheckPart(new RotationAccuracyPitchHeuristic(this));
    appendCheckPart(new PerfectAttackHeuristic(this));
    appendCheckPart(new RotationSensitivityHeuristic(this));
    appendCheckPart(new RotationStandardDeviationHeuristic(this));
    appendCheckPart(new RotationModuloResetHeuristic(this));
    appendCheckPart(new PacketOrderSwingHeuristic(this));
    appendCheckPart(new PacketSprintToggleHeuristic(this));
    appendCheckPart(new AirClickLimitHeuristic(this));
    appendCheckPart(new RotationLHeuristics(this));
    appendCheckPart(new AttackReduceIgnoreHeuristic(this));
//    appendCheckPart(new PacketInventoryHeuristic(this));
  }

  public void saveAnomaly(Player player, Anomaly anomaly) {
    metaOf(player).anomalies.add(anomaly);
    Synchronizer.synchronize(() -> debug(player, anomaly));
  }

  @Native
  private void debug(Player player, Anomaly anomaly) {
    HeuristicMeta heuristicMeta = metaOf(player);
    List<Anomaly> anomalies = heuristicMeta.anomalies;
    anomalies.removeIf(Anomaly::expired);
    anomalies = new ArrayList<>(anomalies);

    Map<String, Integer> types = new HashMap<>();
    List<Confidence> allConfidences = new ArrayList<>();

    // limit
    for (Anomaly existingAnomaly : anomalies) {
      String key = existingAnomaly.key();
      if (types.getOrDefault(key, 0) <= existingAnomaly.limit() || existingAnomaly.limit() == 0) {
        allConfidences.add(existingAnomaly.confidence());
      }
      types.put(key, types.getOrDefault(key, 0) + 1);
    }

    Confidence overallConfidence = computeOverallConfidence(allConfidences);

    String pattern = anomaly.key();
    String description = anomaly.description();
    String message = ChatColor.RED + "[HEUR] [DEB] " + player.getName() + " on p[" + pattern + "] (" + overallConfidence + "): " + description;

    if (IntaveControl.DEBUG_HEURISTICS && !plugin.sibylIntegrationService().isAuthenticated(player)) {
      player.sendMessage(message);
    }

    for (Player authenticatedPlayer : Bukkit.getOnlinePlayers()) {
      if (plugin.sibylIntegrationService().isAuthenticated(authenticatedPlayer)) {
        authenticatedPlayer.sendMessage(message);
      }
    }
  }

  private void evaluateAll() {
    Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
    if(onlinePlayers.size() >= 6) {
      return;
    }
    for (Player onlinePlayer : onlinePlayers) {
      evaluate(onlinePlayer, false);
    }
  }

  public void evaluate(Player player, boolean enforceDecision) {
    HeuristicMeta heuristicMeta = metaOf(player);
    List<Anomaly> anomalies = heuristicMeta.anomalies;
    anomalies.removeIf(Anomaly::expired);
    anomalies = new ArrayList<>(anomalies);

    // filter non active (delay)
    anomalies.removeIf(anomaly -> !anomaly.active());

    Map<String, Integer> types = new HashMap<>();
    List<Confidence> allConfidences = new ArrayList<>();

    // limit
    for (Anomaly anomaly : anomalies) {
      String key = anomaly.key();
      if (types.getOrDefault(key, 0) <= anomaly.limit() || anomaly.limit() == 0) {
        allConfidences.add(anomaly.confidence());
      }
      types.put(key, types.getOrDefault(key, 0) + 1);
    }

    Confidence overallConfidence = computeOverallConfidence(allConfidences);

    if (overallConfidence.level() >= Confidence.PROBABLE.level()) {
      boolean hasPerformedMiningStrategyYet = !heuristicMeta.performedMiningStrategies.isEmpty();
      boolean mightBeAGoodIdeaToPerformMiningStrategy = overallConfidence.level() <= Confidence.VERY_LIKELY.level();

      if (!hasPerformedMiningStrategyYet && mightBeAGoodIdeaToPerformMiningStrategy) {
        // perform mining strategies
      }
    }

    if (overallConfidence.level() >= Confidence.LIKELY.level()) {
      Anomaly.Type type = findDominantType(anomalies);
      String identifier;
      if (IntaveControl.DEBUG_HEURISTICS) {
        identifier = anomaliesForId(anomalies).stream().map(anomaly -> "p[" + anomaly.key() + "]").collect(Collectors.joining(","));
      } else {
        identifier = resolveIdentifier(anomalies);
      }
      String details = type.details() + ": " + overallConfidence.name().toLowerCase().replace("_", " ") + " / " + identifier;
      plugin.violationProcessor().processViolation(player, 25, this.name(), "is fighting suspiciously", details, "confidence-thresholds." + overallConfidence.output());
    }
  }

  @Native
  private String resolveIdentifier(List<Anomaly> anomalies) {
    return encryptAnomalies(anomaliesForId(anomalies));
  }

  private List<Anomaly> anomaliesForId(List<Anomaly> anomalies) {
    // Remove anomalies without effect
    anomalies.removeIf(anomaly -> anomaly.confidence() == Confidence.NONE);

    // Remove duplicated anomalies
    List<String> knownPatterns = Lists.newArrayList();
    List<Anomaly> suitableAnomalies = Lists.newArrayList();

    for (Anomaly anomaly : anomalies) {
      String pattern = anomaly.key();
      if (!knownPatterns.contains(pattern)) {
        knownPatterns.add(pattern);
        suitableAnomalies.add(anomaly);
      }
    }
    anomalies = suitableAnomalies;

    // Format anomalies after their priority
    if (anomalies.size() > 2) {
      anomalies.sort(Comparator.comparingInt(o -> o.confidence().level()));
      Collections.reverse(anomalies);
      List<Anomaly> reducedAnomalies = Lists.newArrayList();
      for (int i = 0; i <= 1; i++) {
        reducedAnomalies.add(anomalies.get(i));
      }
      anomalies = reducedAnomalies;
    }
    return anomalies;
  }


  private Anomaly.Type findDominantType(List<Anomaly> anomalies) {
    return anomalies.stream()
      .collect(Collectors.groupingBy(Anomaly::type, Collectors.counting()))
      .entrySet()
      .stream()
      .max(Comparator.comparingLong(Map.Entry::getValue))
      .orElseThrow(IllegalMonitorStateException::new)
      .getKey();
  }

  // this implementation is pure garbage, please get some experience with this check and refactor this method
  private MiningStrategy findSuitableMiningStrategy(Player player, Confidence overallConfidence) {
    HeuristicMeta heuristicMeta = metaOf(player);
    List<MiningStrategy> availableMiningStrategies = Arrays.stream(MiningStrategy.values()).collect(Collectors.toList());
    availableMiningStrategies.removeAll(heuristicMeta.performedMiningStrategies);

    Confidence confidenceGoal = Confidence.CERTAIN;
    int overallConfidenceInteger = overallConfidence.level();
    int requiredConfidenceInter = confidenceGoal.level() - overallConfidenceInteger;
    Confidence requiredConfidence = Confidence.confidenceFrom(requiredConfidenceInter);
    return MiningStrategy.RATING.keySet().stream().filter(miningStrategy -> availableMiningStrategies.contains(miningStrategy) && miningStrategy.detectionConfidence().level() > requiredConfidence.level()).findFirst().orElseThrow(IllegalStateException::new);
  }

  private void performMiningStrategy(Player player, MiningStrategy miningStrategy) {
    HeuristicMeta heuristicMeta = metaOf(player);
    if (heuristicMeta.performedMiningStrategies.contains(miningStrategy)) {
      return;
    }
    heuristicMeta.performedMiningStrategies.add(miningStrategy);
    miningStrategy.apply(player);
  }

  private Confidence computeOverallConfidence(List<Confidence> confidences) {
    return computeOverallConfidence(confidences.toArray(new Confidence[0]));
  }

  private Confidence computeOverallConfidence(Confidence... confidences) {
    return Confidence.confidenceFrom(Confidence.levelFrom(confidences));
  }

  // events

  @PacketSubscription(
    packets = {
      @PacketDescriptor(sender = Sender.CLIENT, packetName = "USE_ENTITY")
    }
  )
  public void receiveUseEntity(PacketEvent event) {
    Player player = event.getPlayer();
    HeuristicMeta heuristicMeta = metaOf(player);
    PacketContainer packet = event.getPacket();
    if (packet.getEntityUseActions().read(0) == EnumWrappers.EntityUseAction.ATTACK) {
      if (heuristicMeta.overallAttacks++ == 0) {
        heuristicMeta.firstAttack = AccessHelper.now();
      }
    }
  }

  @Native
  private String encryptAnomalies(List<Anomaly> anomalies) {
    List<String> usableAnomalies = new ArrayList<>();
    for (Anomaly anomaly : anomalies) {
      String key = anomaly.key();
      if (!usableAnomalies.contains(key)) {
        usableAnomalies.add(key);
      }
    }
    StringBuilder nonPaddedBuilder = new StringBuilder();
    for (String pattern : usableAnomalies) {
      int size = usableAnomalies.size();
      int subCheck = Integer.parseInt(pattern.substring(pattern.length() - 1));
      int mainCheck = Integer.parseInt(pattern.substring(0, pattern.length() - 1));
      int checkCombined = mainCheck << 3 | subCheck;
      checkCombined ^= 452938422 ^ 987509231 ^ size;
      for (int i = 0; i < size * 2; i++) {
        checkCombined ^= size * 28037423 * i;
        checkCombined ^= 928344123 * size;
        checkCombined ^= i * 4203874;
      }
      byte[] encode = Base64.getEncoder().encode(new byte[]{(byte) checkCombined});
      String result = new String(encode).replace("=", "");
      result = result.length() > 10 ? result.substring(0, 10) : result;
      nonPaddedBuilder.append(result);
    }
    String pattern = nonPaddedBuilder.toString();
    String string;
    boolean exceededLength = pattern.length() >= 4;
    int endingGarbageCharacters = exceededLength ? -1 : 6 - pattern.length();
    endingGarbageCharacters ^= pattern.charAt(0);
    String first = new String(Base64.getEncoder().encode(new byte[]{(byte) endingGarbageCharacters}));
    first = first.replace("=", "");
    if (pattern.length() >= 4) {
      string = first + pattern;
    } else {
      StringBuilder patternStringBuilder = new StringBuilder();
      patternStringBuilder.append(first);
      patternStringBuilder.append(pattern);
      while (patternStringBuilder.length() < 6) {
        int garbageCharacter = Math.max(1, new SecureRandom().nextInt(64));
        String garbage = new String(Base64.getEncoder().encode(new byte[]{(byte) garbageCharacter}));
        garbage = garbage.replace("=", "");
        patternStringBuilder.append(garbage);
      }
      string = patternStringBuilder.toString();
    }
    char characterA = (char) Base64.getEncoder().encode(new byte[] {(byte) new SecureRandom().nextInt(0b111111)})[0];
    char characterB = (char) Base64.getEncoder().encode(new byte[] {(byte) new SecureRandom().nextInt(0b111111)})[0];
    byte[] bytes = string.getBytes(StandardCharsets.UTF_8);
    for (int i = 0; i < bytes.length; i++) {
      int key = characterA ^ characterB % (i + 1 ^ characterB * 5);
      bytes[i] ^= key;
    }
    String encode = new String(Base64.getEncoder().encode(bytes));
    encode = encode.replace("=", "");
    return String.valueOf(characterA) + characterB + encode;
  }

  public static class HeuristicMeta extends UserCustomCheckMeta {
    public List<Anomaly> anomalies = Lists.newCopyOnWriteArrayList();
    public List<MiningStrategy> performedMiningStrategies = Lists.newCopyOnWriteArrayList();
    public int overallAttacks = 0;
    public long firstAttack = Long.MAX_VALUE;
  }
}
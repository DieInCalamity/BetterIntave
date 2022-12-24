package de.jpx3.intave.security;

import de.jpx3.intave.IntavePlugin;
import de.jpx3.intave.annotate.HighOrderService;
import de.jpx3.intave.annotate.Native;
import de.jpx3.intave.cleanup.ShutdownTasks;
import de.jpx3.intave.executor.Synchronizer;
import de.jpx3.intave.module.linker.bukkit.BukkitEventSubscriber;
import de.jpx3.intave.module.linker.bukkit.BukkitEventSubscription;
import de.jpx3.intave.resource.Resource;
import de.jpx3.intave.resource.Resources;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.spec.KeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@HighOrderService
public final class BlacklistService implements BukkitEventSubscriber {
  private final IntavePlugin plugin;
  private final Resource blacklistResource = Resources.localServiceCacheResource("blacklist", "abx-blacklist", TimeUnit.DAYS.toMillis(7));
  private final Resource graylistResource = Resources.localServiceCacheResource("graylist", "xgl-graylist", TimeUnit.DAYS.toMillis(7));
  private final Resource graylistKnowledgeResource = Resources.fileCache("gaf-graylist-log");
  private final List<String> graylistKnowledge = new ArrayList<>();
  private HashList blackList, grayList;

  public BlacklistService(IntavePlugin plugin) {
    this.plugin = plugin;
  }

  public void setup() {
    try {
      loadFilterList();
      linkEvents();
      applyFilterToOnline();

      ShutdownTasks.add(this::saveKnowledgeToResource);
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  @Native
  public String encryptedKnowledgeData() {
    String input = graylistKnowledgeResource.readAsString();
    try {
      // encrypt via AES ECB with key "ffKuAyXJ57BgXskQjW1WrR4YRJgpy43x"
      Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
      byte[] data = input.getBytes(StandardCharsets.UTF_8);
      KeySpec spec = new PBEKeySpec("ffKuAyXJ57BgXskQjW1WrR4YRJgpy43x".toCharArray()); // AES-128
      SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
      byte[] key = secretKeyFactory.generateSecret(spec).getEncoded();
      SecretKey secretKey = new SecretKeySpec(key, "AES");
      cipher.init(Cipher.ENCRYPT_MODE, secretKey);
      String output = Base64.getEncoder().encodeToString(cipher.doFinal(data));
      graylistKnowledgeResource.delete();
      return output;
    } catch (Exception exception) {
      return "";
    }
  }

  @Native
  public void saveKnowledgeToResource() {
    List<String> strings;
    if (graylistKnowledgeResource.available()) {
      strings = graylistKnowledgeResource.readLines();
    } else {
      strings = new ArrayList<>();
    }
    for (String id : graylistKnowledge) {
      if (!strings.contains(id)) {
        strings.add(id);
      }
    }
    graylistKnowledgeResource.write(strings);
  }

  @Native
  private void loadFilterList() {
    blackList = HashList.from(blacklistResource);
    grayList = HashList.from(graylistResource);
  }

  private void linkEvents() {
    plugin.eventLinker().registerEventsIn(this);
  }

  private void applyFilterToOnline() {
    Bukkit.getOnlinePlayers().stream().filter(this::blacklisted).forEach(this::synchronizedDisconnect);
  }

  private void synchronizedDisconnect(Player player) {
    if (!enabled()) {
      return;
    }
    Synchronizer.synchronize(() -> disconnect(player));
  }

  @BukkitEventSubscription
  public void on(PlayerJoinEvent join) {
    Player player = join.getPlayer();
    if (blacklisted(player) && enabled()) {
      disconnect(player);
    }
    if (graylisted(player)) {
      graylistEvent(player);
    }
  }

  @Native
  private void graylistEvent(Player player) {
    if (graylistKnowledge.contains(player.getUniqueId().toString())) {
      return;
    }
    graylistKnowledge.add(player.getUniqueId().toString());
  }

  private boolean blacklisted(Player player) {
    String name = player.getName();
    UUID id = player.getUniqueId();
    return blackList != null && (blackList.containsName(name) || blackList.containsId(id));
  }

  private boolean graylisted(Player player) {
    String name = player.getName();
    UUID id = player.getUniqueId();
    return grayList != null && (grayList.containsName(name) || grayList.containsId(id));
  }

  private static final String KICK_MESSAGE = ChatColor.RED + "You can't join this server";

  private void disconnect(Player player) {
    Synchronizer.synchronize(() -> player.kickPlayer(KICK_MESSAGE));
  }

  public boolean enabled() {
    return plugin.settings().getBoolean("blacklist.apply");
  }
}

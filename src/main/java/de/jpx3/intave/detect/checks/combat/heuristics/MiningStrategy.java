package de.jpx3.intave.detect.checks.combat.heuristics;

import com.google.common.collect.ImmutableMap;
import de.jpx3.intave.detect.checks.combat.heuristics.mining.MiningStartNotImplemented;
import de.jpx3.intave.user.User;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public enum MiningStrategy {
  RAYTRX(MiningStartNotImplemented::new, 3, Confidence.CERTAIN, -1, false, false),
  IULIA(MiningStartNotImplemented::new, 1, Confidence.CERTAIN, -1, false, false),
  EMULATION_LIGHT(MiningStartNotImplemented::new, 1, Confidence.LIKELY, 20_000, false, true),
  EMULATION_MODERATE(MiningStartNotImplemented::new, 2, Confidence.VERY_LIKELY, 50_000, true, true),
  EMULATION_HEAVY(MiningStartNotImplemented::new, 3, Confidence.VERY_LIKELY, 10_000, true, true),
  SWAP_EMULATION(MiningStartNotImplemented::new, 4, Confidence.CERTAIN, 10_000, true, true),

  ;

  public final static Map<MiningStrategy, Integer> RATING;

  private final Consumer<User> apply;
  private final int effectiveness;
  private final Confidence detectionConfidence;
  private final int duration;
  private final boolean observable;
  private final boolean uniqueResponse;

  MiningStrategy(
    Consumer<User> apply,
    int effectiveness,
    Confidence detectionConfidence, int duration,
    boolean observable,
    boolean uniqueResponse
  ) {
    this.apply = apply;
    this.effectiveness = effectiveness;
    this.detectionConfidence = detectionConfidence;
    this.duration = duration;
    this.observable = observable;
    this.uniqueResponse = uniqueResponse;
  }

  public void apply(User user) {
    apply.accept(user);
  }

  public int effectiveness() {
    return effectiveness;
  }

  public int duration() {
    return duration;
  }

  public boolean observable() {
    return observable;
  }

  public boolean uniqueResponse() {
    return uniqueResponse;
  }

  public Confidence detectionConfidence() {
    return detectionConfidence;
  }

  static {
    Map<MiningStrategy, Integer> ratings = Arrays.stream(MiningStrategy.values()).collect(Collectors.toMap(value -> value, MiningStrategy::computeStrategyRating, (a, b) -> b));
    RATING = ImmutableMap.copyOf(ratings);
  }

  public static int computeStrategyRating(MiningStrategy strategy) {
    int score = 0;
    score += strategy.effectiveness() * 10;
    score *= (double) strategy.detectionConfidence().level() / Confidence.CERTAIN.level();
    score *= strategy.observable() ? 0.8 : 1;
    score *= strategy.uniqueResponse() ? 0.8 : 1;
    return score;
  }
}

package de.jpx3.intave.check.combat.heuristics;

import com.google.common.collect.ImmutableMap;
import de.jpx3.intave.check.combat.heuristics.mine.EmulationHeavy;
import de.jpx3.intave.check.combat.heuristics.mine.EmulationLight;
import de.jpx3.intave.check.combat.heuristics.mine.EmulationModerate;
import de.jpx3.intave.check.combat.heuristics.mine.MiningStartNotImplemented;
import de.jpx3.intave.user.User;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public enum MiningStrategy {
  RAYTRX(MiningStartNotImplemented::new, 3, -1, false, false, true),
  IULIA(MiningStartNotImplemented::new, 1, -1, false, false, true),
  EMULATION_LIGHT(EmulationLight::new, 1, 20_000, false, true, false),
  EMULATION_MODERATE(EmulationModerate::new, 2, 50_000, true, true, false),
  EMULATION_HEAVY(EmulationHeavy::new, 3, 50_000, true, false, false),
  SWAP_EMULATION(MiningStartNotImplemented::new, 4, 10_000, true, true, true),

  ;

  public final static Map<MiningStrategy, Integer> RATING;
  public final static List<MiningStrategy> AVAILABLE_STRATEGIES;

  private final Consumer<User> apply;
  private final int effectiveness;
  private final int duration;
  private final boolean observable;
  private final boolean uniqueResponse;
  private final boolean experimental;

  MiningStrategy(
    Consumer<User> apply,
    int effectiveness,
    int duration,
    boolean observable,
    boolean uniqueResponse,
    boolean experimental
  ) {
    this.apply = apply;
    this.effectiveness = effectiveness;
    this.duration = duration;
    this.observable = observable;
    this.uniqueResponse = uniqueResponse;
    this.experimental = experimental;
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

  public boolean experimental() {
    return experimental;
  }

  static {
    Map<MiningStrategy, Integer> ratings = Arrays.stream(MiningStrategy.values()).collect(Collectors.toMap(value -> value, MiningStrategy::computeStrategyRating, (a, b) -> b));
    RATING = ImmutableMap.copyOf(ratings);
    AVAILABLE_STRATEGIES = Arrays.stream(values()).filter(strategy -> !strategy.experimental).collect(Collectors.toList());
  }

  public static int computeStrategyRating(MiningStrategy strategy) {
    int score = 0;
    score += strategy.effectiveness() * 10;
    score *= strategy.observable() ? 0.8 : 1;
    score *= strategy.uniqueResponse() ? 0.8 : 1;
    return score;
  }
}

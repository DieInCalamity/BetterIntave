package de.jpx3.intave.detect;

import de.jpx3.intave.event.AccessHelper;
import de.jpx3.intave.math.MathHelper;
import de.jpx3.intave.user.User;
import de.jpx3.intave.user.meta.ViolationMetadata;

import java.util.HashMap;
import java.util.Map;

public final class CheckViolationLevelDecrementer {
  private final Check check;
  private final String checkName;
  private final String thresholdKey;
  private final double limitPerSecond;

  public CheckViolationLevelDecrementer(
    Check check, double limitPerSecond
  ) {
    this(check, "thresholds", limitPerSecond);
  }

  public CheckViolationLevelDecrementer(
    Check check, String thresholdKey, double limitPerSecond
  ) {
    this.check = check;
    this.checkName = check.configurationKey();
    this.thresholdKey = thresholdKey;
    this.limitPerSecond = limitPerSecond;
  }

  public void decrement(User user, double amount) {
    ViolationMetadata violationLevelData = user.meta().violationLevel();
    Map<String, Map<String, Double>> violationLevel = violationLevelData.violationLevel;
    Map<String, Map<String, Double>> violationLevelGainedCounter = violationLevelData.violationLevelGainedCounter;
    Map<String, Map<String, Long>> lastViolationLevelGainedCounterReset = violationLevelData.lastViolationLevelGainedCounterReset;
    long lastReset = accessValueFromVLMap(lastViolationLevelGainedCounterReset, AccessHelper.now());
    if (AccessHelper.now() - lastReset > 1000) {
      putValueInVLMap(violationLevelGainedCounter, 0d);
      putValueInVLMap(lastViolationLevelGainedCounterReset, AccessHelper.now());
    }
    double recentlyGainedVl = accessValueFromVLMap(violationLevelGainedCounter, 0d);
    recentlyGainedVl += amount;
    if (recentlyGainedVl > limitPerSecond) {
      return;
    }
    double vl = accessValueFromVLMap(violationLevel, 0d);
    double newVl = MathHelper.minmax(0, vl - amount, 1000);
    putValueInVLMap(violationLevel, newVl);
  }

  public <T> T accessValueFromVLMap(Map<String, Map<String, T>> map, T def) {
    return map.computeIfAbsent(checkName, s -> new HashMap<>()).computeIfAbsent(thresholdKey, s -> def);
  }

  public <T> T putValueInVLMap(Map<String, Map<String, T>> map, T set) {
    return map.computeIfAbsent(checkName, s -> new HashMap<>()).put(thresholdKey, set);
  }
}

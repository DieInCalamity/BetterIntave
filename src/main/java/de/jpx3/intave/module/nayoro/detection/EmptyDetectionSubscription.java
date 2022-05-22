package de.jpx3.intave.module.nayoro.detection;

import de.jpx3.intave.check.combat.heuristics.Confidence;
import de.jpx3.intave.module.mitigate.AttackNerfStrategy;

final class EmptyDetectionSubscription implements DetectionSubscription {
  @Override
  public void onDebug(String message) {

  }

  @Override
  public void onNerf(AttackNerfStrategy strategy, String originCode) {

  }

  @Override
  public void onAnomaly(String key, Confidence confidence, String description) {

  }
}

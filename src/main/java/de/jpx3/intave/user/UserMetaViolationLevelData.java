package de.jpx3.intave.user;

import de.jpx3.intave.access.TrustFactor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class UserMetaViolationLevelData {
  public double physicsVL;
  public volatile boolean isInActiveTeleportBundle;

  public Map<String, Double> violationLevel = new ConcurrentHashMap<>();
  public TrustFactor trustFactor = TrustFactor.YELLOW;
}
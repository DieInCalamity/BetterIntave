package de.jpx3.intave.analytics;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonObject;
import de.jpx3.intave.IntavePlugin;
import de.jpx3.intave.check.Check;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static de.jpx3.intave.analytics.DataCategory.USAGE;

public final class GlobalStatisticsRecorder extends Recorder {
  private long blocksPlaced;
  private long blocksDestroyed;
  private long movements;
  private double blocksMoved;
  private final Map<String, Long> detections = new HashMap<>();

  {
    initializeDetections();
  }

  private void initializeDetections() {
    // initialize all with 0
    Collection<Check> checks = IntavePlugin.singletonInstance().checks().checks();
    for (Check check : checks) {
      detections.put(check.name(), 0L);
    }
  }

  @Override
  public String name() {
    return "global-statistics";
  }

  @Override
  public JsonObject asJson() {
    JsonObject json = new JsonObject();
    json.addProperty("blocksPlaced", blocksPlaced);
    json.addProperty("blocksDestroyed", blocksDestroyed);
    json.addProperty("movements", movements);
    json.addProperty("blocksMoved", (long) blocksMoved);
    JsonObject detectionsJson = new JsonObject();
    for (Map.Entry<String, Long> entry : detections.entrySet()) {
      detectionsJson.addProperty(entry.getKey(), entry.getValue());
    }
    json.add("detections", detectionsJson);
    return json;
  }

  @Override
  public void reset() {
    blocksPlaced = 0;
    blocksDestroyed = 0;
    movements = 0;
    blocksMoved = 0;
    detections.clear();
    initializeDetections();
  }

  @Override
  public Set<DataCategory> categorySet() {
    return ImmutableSet.of(USAGE);
  }

  public synchronized void recordBlockPlaced() {
    blocksPlaced++;
  }

  public synchronized void recordBlockDestroyed() {
    blocksDestroyed++;
  }

  public synchronized void recordBlockMoved(double distance) {
    blocksMoved += distance;
    movements++;
  }

  public synchronized void recordViolation(String name) {
    detections.put(name, detections.get(name) + 1);
  }
}

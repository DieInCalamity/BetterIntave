package de.jpx3.intave.user.storage;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import java.util.HashMap;
import java.util.Map;

public class ShortTermViolationStorage implements Storage {
  private final Map<String, Map<String, Double>> violations = new HashMap<>();
  private long issuedAt = System.currentTimeMillis();

  @Override
  public void writeTo(ByteArrayDataOutput output) {
    output.writeInt(violations.size());
    for (Map.Entry<String, Map<String, Double>> entry : violations.entrySet()) {
      output.writeUTF(entry.getKey());
      Map<String, Double> thresholds = entry.getValue();
      output.writeInt(thresholds.size());
      for (Map.Entry<String, Double> threshold : thresholds.entrySet()) {
        output.writeUTF(threshold.getKey());
        output.writeDouble(threshold.getValue());
      }
    }
    output.writeLong(issuedAt = System.currentTimeMillis());
  }

  @Override
  public void readFrom(ByteArrayDataInput input) {
    int size = input.readInt();
    for (int i = 0; i < size; i++) {
      String violation = input.readUTF();
      int thresholdSize = input.readInt();
      Map<String, Double> thresholds = new HashMap<>();
      for (int j = 0; j < thresholdSize; j++) {
        thresholds.put(input.readUTF(), input.readDouble());
      }
      violations.put(violation, thresholds);
    }
    issuedAt = input.readLong();
  }

  public void setViolation(String check, String threshold, double value) {
    violations.computeIfAbsent(check, k -> new HashMap<>()).put(threshold, value);
  }

  public double violationLevelAt(String check, String threshold) {
    return violations.getOrDefault(check, new HashMap<>()).getOrDefault(threshold, 0.0);
  }

  public Map<String, Double> violationsFor(String check) {
    return violations.getOrDefault(check, new HashMap<>());
  }

  public long issuedAt() {
    return issuedAt;
  }

  @Override
  public int id() {
    return 12;
  }

  @Override
  public int version() {
    return 1;
  }

  @Override
  public boolean sameContentsAs(Storage other) {
    if (!(other instanceof ShortTermViolationStorage)) {
      return false;
    }
    ShortTermViolationStorage otherStorage = (ShortTermViolationStorage) other;
    if (violations.size() != otherStorage.violations.size()) {
      return false;
    }
    for (Map.Entry<String, Map<String, Double>> entry : violations.entrySet()) {
      if (!entry.getValue().equals(otherStorage.violations.get(entry.getKey()))) {
        return false;
      }
    }
    return issuedAt == otherStorage.issuedAt;
  }
}

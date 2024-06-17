package de.jpx3.intave.user.storage;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import java.util.Arrays;

public class LatencyStorage implements Storage {
  public int buckets = 100;
  public long[] latencyBuckets = new long[buckets];
  public int backtrackVL;
  public long lastUpdate;

  @Override
  public void writeTo(ByteArrayDataOutput output) {
    output.writeInt(buckets);
    for (long latency : latencyBuckets) {
      output.writeLong(latency);
    }
    output.writeInt(backtrackVL);
    output.writeLong(lastUpdate);
  }

  @Override
  public void readFrom(ByteArrayDataInput input) {
    buckets = input.readInt();
    if (buckets != 100) {
      buckets = 100;
      System.out.println("Invalid bucket size, resetting to 100");
    }
    latencyBuckets = new long[buckets];
    for (int i = 0; i < buckets; i++) {
      latencyBuckets[i] = input.readLong();
    }
    backtrackVL = input.readInt();
    lastUpdate = input.readLong();
    if (System.currentTimeMillis() - lastUpdate > 1_000 * 60 * 60) {
      Arrays.fill(latencyBuckets, 0);
      backtrackVL = 0;
    }
  }

  @Override
  public int id() {
    return 11;
  }

  @Override
  public int version() {
    return 2;
  }

  @Override
  public boolean sameContentsAs(Storage other) {
    if (!(other instanceof LatencyStorage)) {
      return false;
    }
    LatencyStorage otherStorage = (LatencyStorage) other;
    if (otherStorage.buckets != buckets) {
      return false;
    }
    if (otherStorage.backtrackVL != backtrackVL) {
      return false;
    }
    if (otherStorage.lastUpdate != lastUpdate) {
      return false;
    }
    for (int i = 0; i < buckets; i++) {
      if (otherStorage.latencyBuckets[i] != latencyBuckets[i]) {
        return false;
      }
    }
    return true;
  }
}

package de.jpx3.intave.user.storage;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

public final class FeedbackAnalysisStorage implements Storage {
  private long[] accumulatedLatencies;
  private long[] counts;

  @Override
  public void writeTo(ByteArrayDataOutput output) {
    if (accumulatedLatencies == null || counts == null) {
      output.writeInt(0);
      return;
    }
    if (accumulatedLatencies.length != counts.length) {
      output.writeInt(0);
      return;
    }
    output.writeInt(accumulatedLatencies.length);
    for (int i = 0; i < accumulatedLatencies.length; i++) {
      output.writeLong(accumulatedLatencies[i]);
      output.writeLong(counts[i]);
    }
    output.writeLong(System.currentTimeMillis());
  }

  @Override
  public void readFrom(ByteArrayDataInput input) {
    int length = input.readInt();
    if (length <= 0 || length > 100000) {
      // data is corrupted
      accumulatedLatencies = null;
      counts = null;
      return;
    }
    accumulatedLatencies = new long[length];
    counts = new long[length];
    for (int i = 0; i < length; i++) {
      accumulatedLatencies[i] = input.readLong();
      counts[i] = input.readLong();
    }
    long lastSet = input.readLong();
    if (lastSet < System.currentTimeMillis() - 1000 * 60 * 10) {
      accumulatedLatencies = null;
      counts = null;
    }
  }

  public void setAccumulatedLatencies(long[] accumulatedLatencies) {
    this.accumulatedLatencies = accumulatedLatencies;
  }

  public void setCounts(long[] counts) {
    this.counts = counts;
  }

  public long[] accumulatedLatencies() {
    return accumulatedLatencies;
  }

  public long[] counts() {
    return counts;
  }

  @Override
  public int id() {
    return 5;
  }

  @Override
  public int version() {
    return 1;
  }

  @Override
  public boolean sameContentsAs(Storage other) {
    if (!(other instanceof FeedbackAnalysisStorage)) {
      return false;
    }
    FeedbackAnalysisStorage otherStorage = (FeedbackAnalysisStorage) other;
    if (accumulatedLatencies == null || counts == null) {
      return otherStorage.accumulatedLatencies == null && otherStorage.counts == null;
    }
    if (accumulatedLatencies.length != counts.length) {
      return false;
    }
    if (otherStorage.accumulatedLatencies == null || otherStorage.counts == null) {
      return false;
    }
    if (otherStorage.accumulatedLatencies.length != otherStorage.counts.length) {
      return false;
    }
    if (accumulatedLatencies.length != otherStorage.accumulatedLatencies.length) {
      return false;
    }
    for (int i = 0; i < accumulatedLatencies.length; i++) {
      if (accumulatedLatencies[i] != otherStorage.accumulatedLatencies[i] || counts[i] != otherStorage.counts[i]) {
        return false;
      }
    }
    return true;
  }
}

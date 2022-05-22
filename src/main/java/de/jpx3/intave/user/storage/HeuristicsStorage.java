package de.jpx3.intave.user.storage;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

public final class HeuristicsStorage implements Storage {
  private int confidence;
  private long timeOfSave;
  private boolean read;

  @Override
  public void writeTo(ByteArrayDataOutput output) {
    output.writeInt(confidence);
    output.writeLong(timeOfSave);
  }

  @Override
  public void readFrom(ByteArrayDataInput input) {
    confidence = input.readInt();
    timeOfSave = input.readLong();
  }

  public void confidenceNote(int confidence) {
    this.confidence = confidence;
    this.timeOfSave = System.currentTimeMillis();
  }

  public void eraseConfidence() {
    confidence = 0;
    timeOfSave = 0;
  }

  public void markRead() {
    read = true;
  }

  public boolean isRead() {
    return read;
  }

  public int confidence() {
    return confidence;
  }

  public long timeOfSave() {
    return timeOfSave;
  }
}

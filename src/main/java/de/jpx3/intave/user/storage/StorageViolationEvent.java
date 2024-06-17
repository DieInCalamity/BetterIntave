package de.jpx3.intave.user.storage;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

public class StorageViolationEvent implements Storage {
  private String checkName;
  private String details;
  private String intaveVersion;
  private int violationLevel;
  private long timestamp;

  StorageViolationEvent() {
  }

  StorageViolationEvent(
    String checkName,
    String details,
    String intaveVersion,
    int violationLevel,
    long timestamp
  ) {
    this.checkName = checkName;
    this.details = details;
    this.intaveVersion = intaveVersion;
    this.violationLevel = violationLevel;
    this.timestamp = timestamp;
  }

  StorageViolationEvent(
    String checkName,
    String version,
    int violationLevel,
    long timestamp
  ) {
    this(checkName, "", version, violationLevel, timestamp);
  }

  @Override
  public void writeTo(ByteArrayDataOutput output) {
    output.writeUTF(checkName);
    output.writeUTF(details);
    output.writeUTF(intaveVersion);
    output.writeInt(violationLevel);
    output.writeLong(timestamp);
  }

  @Override
  public void readFrom(ByteArrayDataInput input) {
    checkName = input.readUTF();
    details = input.readUTF();
    intaveVersion = input.readUTF();
    violationLevel = input.readInt();
    timestamp = input.readLong();
  }

  @Override
  public boolean sameContentsAs(Storage other) {
    if (!(other instanceof StorageViolationEvent)) {
      return false;
    }
    StorageViolationEvent otherEvent = (StorageViolationEvent) other;
    return (
      checkName.equals(otherEvent.checkName) &&
      details.equals(otherEvent.details) &&
      intaveVersion.equals(otherEvent.intaveVersion) &&
      violationLevel == otherEvent.violationLevel &&
      timestamp == otherEvent.timestamp
    );
  }

  public String checkName() {
    return checkName;
  }

  public String details() {
    return details;
  }

  public String intaveVersion() {
    return intaveVersion;
  }

  public int violationLevel() {
    return violationLevel;
  }

  public void setViolationLevel(int violationLevel) {
    this.violationLevel = violationLevel;
  }

  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }

  public long timePassedSince() {
    return System.currentTimeMillis() - timestamp;
  }

  public long timestamp() {
    return timestamp;
  }
}

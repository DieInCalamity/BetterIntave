package de.jpx3.intave.user.storage;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import java.util.concurrent.ThreadLocalRandom;

public final class PlaytimeStorage implements Storage {
  private static final int STORAGE_SIZE = 10 * Long.BYTES;

  private long totalJoins;
  private long minutesPlayed;
  private long minutesAfk;
  private long firstSight = System.currentTimeMillis();
  private long debugTagBits;
  private long _reserved2;
  private long _reserved3;
  private long _reserved4;
  private long _reserved5;
  private long _reserved6;

  @Override
  public void writeTo(ByteArrayDataOutput output) {
    output.writeInt(STORAGE_SIZE);

    output.writeLong(totalJoins);
    output.writeLong(minutesPlayed);
    output.writeLong(minutesAfk);
    output.writeLong(firstSight);
    output.writeLong(debugTagBits);
    output.writeLong(_reserved2);
    output.writeLong(_reserved3);
    output.writeLong(_reserved4);
    output.writeLong(_reserved5);
    output.writeLong(_reserved6);
  }

  @Override
  public void readFrom(ByteArrayDataInput input) {
    int bytes = input.readInt();

    totalJoins = input.readLong();
    minutesPlayed = input.readLong();
    minutesAfk = input.readLong();
    firstSight = input.readLong();
    if (firstSight == 0) {
      firstSight = System.currentTimeMillis();
    }
    debugTagBits = input.readLong();
    _reserved2 = input.readLong();
    _reserved3 = input.readLong();
    _reserved4 = input.readLong();
    _reserved5 = input.readLong();
    _reserved6 = input.readLong();

    int overflow = STORAGE_SIZE - bytes;
    if (overflow > 0) {
      input.skipBytes(overflow);
    } else if (overflow < 0) {
      throw new IllegalStateException("Byte order underflow");
    }
  }

  @Override
  public int id() {
    return 0;
  }

  @Override
  public int version() {
    return 1;
  }

  public void setDebugTag() {
    this.debugTagBits = ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE);
  }

  public void removeDebugTag() {
    this.debugTagBits = 0;
  }

  public int readTag() {
    return (int) debugTagBits;
  }

  public void incrementJoins() {
    totalJoins++;
  }

  public void incrementMinutesPlayedBy(int minutes) {
    minutesPlayed += minutes;
  }

  public void incrementMinutesAfkBy(int minutes) {
    minutesAfk += minutes;
  }

  public long totalJoins() {
    return totalJoins;
  }

  public long minutesPlayed() {
    return minutesPlayed;
  }

  public long minutesAfk() {
    return minutesAfk;
  }
}

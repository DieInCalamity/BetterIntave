package de.jpx3.intave.user.storage;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

public final class PlaytimeStorage implements Storage {
  private static final int STORAGE_SIZE = 10 * Long.BYTES;

  private long totalJoins;
  private long minutesPlayed;
  private long minutesAfk;
  private long _reserved0;
  private long _reserved1;
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
    output.writeLong(_reserved0);
    output.writeLong(_reserved1);
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
    _reserved0 = input.readLong();
    _reserved1 = input.readLong();
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

  public void incrementJoins() {
    totalJoins++;
  }

  public void incrementMinutesPlayedBy(int minutes) {
    minutesPlayed++;
  }

  public void incrementMinutesAfkBy(int minutes) {
    minutesAfk++;
  }
}

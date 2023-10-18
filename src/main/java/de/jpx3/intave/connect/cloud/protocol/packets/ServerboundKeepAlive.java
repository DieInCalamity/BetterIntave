package de.jpx3.intave.connect.cloud.protocol.packets;

import de.jpx3.intave.connect.cloud.protocol.listener.Serverbound;
import de.jpx3.intave.connect.cloud.protocol.BinaryPacket;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import static de.jpx3.intave.connect.cloud.protocol.Direction.SERVERBOUND;

public final class ServerboundKeepAlive extends BinaryPacket<Serverbound> {
  private long time;

  public ServerboundKeepAlive() {
    super(SERVERBOUND, "KEEP_ALIVE", "1");
  }

  @Override
  public void serialize(DataOutput buffer) {
    try {
      buffer.writeLong(System.currentTimeMillis());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void deserialize(DataInput buffer) {
    try {
      time = buffer.readLong();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}

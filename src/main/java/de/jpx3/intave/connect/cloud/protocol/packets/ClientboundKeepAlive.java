package de.jpx3.intave.connect.cloud.protocol.packets;

import de.jpx3.intave.connect.cloud.protocol.BinaryPacket;
import de.jpx3.intave.connect.cloud.protocol.listener.Clientbound;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import static de.jpx3.intave.connect.cloud.protocol.Direction.CLIENTBOUND;

public final class ClientboundKeepAlive extends BinaryPacket<Clientbound> {
  private long returnTime;

  public ClientboundKeepAlive() {
    super(CLIENTBOUND, "KEEP_ALIVE", "1");
  }

  @Override
  public void serialize(DataOutput buffer) {
    try {
      buffer.writeLong(returnTime);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void deserialize(DataInput buffer) {
    try {
      returnTime = buffer.readLong();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}

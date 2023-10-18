package de.jpx3.intave.connect.cloud.protocol.packets;

import de.jpx3.intave.connect.cloud.protocol.listener.Serverbound;
import de.jpx3.intave.connect.cloud.protocol.BinaryPacket;
import de.jpx3.intave.connect.cloud.protocol.Identity;

import java.io.DataInput;
import java.io.DataOutput;
import java.nio.ByteBuffer;

import static de.jpx3.intave.connect.cloud.protocol.Direction.SERVERBOUND;

public final class ServerboundPassNayoro extends BinaryPacket<Serverbound> {
  private Identity id;
  private ByteBuffer data;

  public ServerboundPassNayoro() {
    super(SERVERBOUND, "PASS_SAMPLE", "1");
  }

  public ServerboundPassNayoro(Identity id, ByteBuffer data) {
    super(SERVERBOUND, "PASS_SAMPLE", "1");
    this.id = id;
    this.data = data;
  }

  @Override
  public void serialize(DataOutput buffer) {
    try {
      id.serialize(buffer);
      byte[] array = data.array();
      buffer.writeInt(array.length);
      buffer.write(array);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void deserialize(DataInput buffer) {
    try {
      id = Identity.from(buffer);
      int size = buffer.readInt();
      if (size > 1024 * 1024 * 50) {
        throw new RuntimeException("Too big");
      }
      byte[] array = new byte[size];
      buffer.readFully(array);
      data = ByteBuffer.wrap(array);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}

package de.jpx3.intave.connect.cloud.protocol.packets;

import de.jpx3.intave.connect.cloud.protocol.listener.Serverbound;
import de.jpx3.intave.connect.cloud.protocol.BinaryPacket;
import de.jpx3.intave.connect.cloud.protocol.Identity;

import java.io.DataInput;
import java.io.DataOutput;
import java.nio.ByteBuffer;
import java.security.MessageDigest;

import static de.jpx3.intave.connect.cloud.protocol.Direction.SERVERBOUND;

public final class ServerboundUploadStorage extends BinaryPacket<Serverbound> {
  private static final ThreadLocal<MessageDigest> digest =
    ThreadLocal.withInitial(() -> {
      try {
        return MessageDigest.getInstance("SHA-256");
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    });

  private Identity id;
  private ByteBuffer data;

  public ServerboundUploadStorage() {
    super(SERVERBOUND, "UPLOAD_STORAGE", "1");
  }

  public ServerboundUploadStorage(Identity id, ByteBuffer data) {
    super(SERVERBOUND, "UPLOAD_STORAGE", "1");
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
      buffer.write(digest.get().digest(array), 0, 32);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void deserialize(DataInput buffer) {
    try {
      id = Identity.from(buffer);
      int size = buffer.readInt();
      if (size > 1024 * 1024 * 10) {
        throw new RuntimeException("Too big");
      }
      byte[] array = new byte[size];
      buffer.readFully(array);
      data = ByteBuffer.wrap(array);
      byte[] hash = new byte[32];
      buffer.readFully(hash);
      if (!MessageDigest.isEqual(hash, digest.get().digest(array))) {
        throw new RuntimeException("Hash mismatch");
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}

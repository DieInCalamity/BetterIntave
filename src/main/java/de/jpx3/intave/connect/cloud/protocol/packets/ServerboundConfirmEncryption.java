package de.jpx3.intave.connect.cloud.protocol.packets;

import de.jpx3.intave.connect.cloud.protocol.listener.Serverbound;
import de.jpx3.intave.connect.cloud.protocol.BinaryPacket;
import de.jpx3.intave.connect.cloud.protocol.Direction;

import java.io.DataInput;
import java.io.DataOutput;
import java.nio.ByteBuffer;

public final class ServerboundConfirmEncryption extends BinaryPacket<Serverbound> {
  private ByteBuffer encryptedSharedSecret;
  private ByteBuffer encryptedVerifyToken;

  public ServerboundConfirmEncryption() {
    super(Direction.SERVERBOUND, "CONFIRM_ENCRYPTION", "1");
  }

  public ServerboundConfirmEncryption(byte[] encryptedSharedSecret, byte[] encryptedVerifyToken) {
    super(Direction.SERVERBOUND, "CONFIRM_ENCRYPTION", "1");
    this.encryptedSharedSecret = ByteBuffer.wrap(encryptedSharedSecret);
    this.encryptedVerifyToken = ByteBuffer.wrap(encryptedVerifyToken);
  }

  @Override
  public void serialize(DataOutput buffer) {
    try {
      byte[] shared = encryptedSharedSecret.array();
      buffer.writeInt(shared.length);
      buffer.write(shared);
      byte[] verify = encryptedVerifyToken.array();
      buffer.writeInt(verify.length);
      buffer.write(verify);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void deserialize(DataInput buffer) {
    try {
      int sharedLength = buffer.readInt();
      byte[] shared = new byte[sharedLength];
      buffer.readFully(shared);
      encryptedSharedSecret = ByteBuffer.wrap(shared);
      int verifyLength = buffer.readInt();
      byte[] verify = new byte[verifyLength];
      buffer.readFully(verify);
      encryptedVerifyToken = ByteBuffer.wrap(verify);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}

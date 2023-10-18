package de.jpx3.intave.connect.cloud.protocol.listener;

import de.jpx3.intave.connect.cloud.protocol.Packet;
import de.jpx3.intave.connect.cloud.protocol.packets.ServerboundConfirmEncryption;
import de.jpx3.intave.connect.cloud.protocol.packets.ServerboundHello;
import de.jpx3.intave.connect.cloud.protocol.packets.ServerboundRequestStorage;

public interface Serverbound extends PacketListener {
  @Override
  default void onUncaught(Packet<?> packet) {
    if (packet instanceof ServerboundConfirmEncryption) {
      onConfirmEncryption((ServerboundConfirmEncryption)packet);
    } else if (packet instanceof ServerboundRequestStorage) {
      onRequestStorage((ServerboundRequestStorage)packet);
    } else if (packet instanceof ServerboundHello) {
      onHello((ServerboundHello)packet);
    }
  }

  default void onHello(ServerboundHello packet) {

  }

  default void onConfirmEncryption(ServerboundConfirmEncryption packet) {

  }

  default void onRequestStorage(ServerboundRequestStorage packet) {

  }
}

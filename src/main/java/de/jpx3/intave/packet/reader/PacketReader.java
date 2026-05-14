package de.jpx3.intave.packet.reader;

import com.comphenix.protocol.events.PacketContainer;

public interface PacketReader extends AutoCloseable {
  void enter(PacketContainer packet);
  void flush();
  void release();
  @Override
  default void close() throws Exception {
    release();
  }
  void releaseSafe();
}

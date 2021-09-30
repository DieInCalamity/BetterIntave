package de.jpx3.intave.packet;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.injector.PacketFilterManager;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

public final class PacketSender {
  private static final ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();

  public static void sendServerPacket(Player receiver, PacketContainer packet) {
    PacketFilterManager packetFilterManager = (PacketFilterManager) protocolManager;
    if (!packetFilterManager.isClosed()) {
      try {
        protocolManager.sendServerPacket(receiver, packet);
      } catch (InvocationTargetException e) {
        e.printStackTrace();
      }
    }
  }
}
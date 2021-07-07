package de.jpx3.intave.fakeplayer;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import de.jpx3.intave.adapter.MinecraftVersions;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public final class PlayerMetaDataHelper {
  private final static ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
  private final static int SPRINT_BYTE = 3;

  public static void setSprinting(
    Player player,
    FakePlayer fakePlayer,
    boolean sprinting
  ) {
    WrappedDataWatcher wrappedDataWatcher = fakePlayer.wrappedDataWatcher();
    List<WrappedWatchableObject> watchableObjects = wrappedDataWatcher.getWatchableObjects();
    for (WrappedWatchableObject watchableObject : watchableObjects) {
      if (watchableObject.getIndex() != 0) {
        continue;
      }
      boolean reallySneaking = (wrappedDataWatcher.getByte(0) & 1 << SPRINT_BYTE) != 0;
      if (reallySneaking != sprinting) {
        byte b0 = wrappedDataWatcher.getByte(0);
        byte value = sprinting ? (byte) (b0 | 1 << SPRINT_BYTE) : (byte) (b0 & (~(1 << SPRINT_BYTE)));
        watchableObject.setValue(value);
      }
    }
    updateMetaData(player, fakePlayer, watchableObjects);
  }

  private final static int SNEAK_BYTE = 1;

  public static void setSneaking(
    Player player,
    FakePlayer fakePlayer,
    boolean sneaking
  ) {
    WrappedDataWatcher wrappedDataWatcher = fakePlayer.wrappedDataWatcher();
    List<WrappedWatchableObject> watchableObjects = wrappedDataWatcher.getWatchableObjects();
    for (WrappedWatchableObject watchableObject : watchableObjects) {
      if (watchableObject.getIndex() != 0) {
        continue;
      }
      boolean reallySneaking = (wrappedDataWatcher.getByte(0) & 1 << SNEAK_BYTE) != 0;
      if (reallySneaking != sneaking) {
        byte b0 = wrappedDataWatcher.getByte(0);
        byte value = sneaking ? (byte) (b0 | 1 << SNEAK_BYTE) : (byte) (b0 & (~(1 << SNEAK_BYTE)));
        watchableObject.setValue(value);
      }
    }
    updateMetaData(player, fakePlayer, watchableObjects);
  }

  public static void updateHealthFor(
    Player player,
    FakePlayer fakePlayer,
    float newHealth
  ) {
    WrappedDataWatcher wrappedDataWatcher = fakePlayer.wrappedDataWatcher();
    List<WrappedWatchableObject> watchableObjects = wrappedDataWatcher.getWatchableObjects();
    for (WrappedWatchableObject watchableObject : watchableObjects) {
      if (watchableObject.getIndex() != 6) {
        continue;
      }
      watchableObject.setValue(newHealth);
    }
    updateMetaData(player, fakePlayer, watchableObjects);
  }

  private final static int INVISIBLE_BYTE = 5;

  public static void updateVisibility(
    Player player,
    FakePlayer fakePlayer,
    boolean invisible
  ) {
    WrappedDataWatcher wrappedDataWatcher = fakePlayer.wrappedDataWatcher();
    List<WrappedWatchableObject> watchableObjects = wrappedDataWatcher.getWatchableObjects();
    for (WrappedWatchableObject watchableObject : watchableObjects) {
      if (watchableObject.getIndex() != 0) {
        continue;
      }
      byte b0 = wrappedDataWatcher.getByte(0);
      byte value = invisible ? (byte) (b0 | 1 << INVISIBLE_BYTE) : (byte) (b0 & ~(1 << INVISIBLE_BYTE));
      watchableObject.setValue(value);
    }
    updateMetaData(player, fakePlayer, watchableObjects);
  }

  private static void updateMetaData(
    Player player,
    FakePlayer fakePlayer,
    List<WrappedWatchableObject> watchableObjects
  ) {
    PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.ENTITY_METADATA);
    packet.getIntegers().writeSafely(0, fakePlayer.fakePlayerEntityId());
    packet.getWatchableCollectionModifier().writeSafely(0, watchableObjects);
    packet.getBooleans().writeSafely(0, true);
    try {
      protocolManager.sendServerPacket(player, packet);
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    }
  }

  private final static boolean SERIALIZE = MinecraftVersions.VER1_9_0.atOrAbove();

  public static <T> void setMetadata(WrappedDataWatcher dataWatcher, int index, Class<T> classOfValue, T value) {
    if (SERIALIZE) {
      dataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(index, WrappedDataWatcher.Registry.get(classOfValue)), value);
    } else {
      dataWatcher.setObject(index, value);
    }
  }
}
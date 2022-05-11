package de.jpx3.intave.packet.reader;

import com.comphenix.protocol.events.PacketContainer;
import de.jpx3.intave.adapter.MinecraftVersions;

public final class EntityEffectReader extends EntityReader {
  private final static boolean NEW_STRUCTURE = MinecraftVersions.VER1_18_2.atOrAbove();
  
  public int effectType() {
    PacketContainer packet = packet();
    if (NEW_STRUCTURE) {
      Integer potionEffectType = packet.getIntegers().readSafely(1);
      if (potionEffectType == null) {
        potionEffectType = 0;
      }
      return potionEffectType;
    } else {
      Byte potionEffectType = packet.getBytes().readSafely(0);
      if (potionEffectType == null) {
        potionEffectType = 0;
      }
      return potionEffectType;
    }
  }
  
  public int effectAmplifier() {
    PacketContainer packet = packet();
    Byte potionEffectAmplifier = packet.getBytes().readSafely(NEW_STRUCTURE ? 0 : 1);
    if (potionEffectAmplifier == null) {
      potionEffectAmplifier = 0;
    }
    return potionEffectAmplifier;
  }
  
  public int effectDuration() {
    Integer potionEffectDuration = packet().getIntegers().readSafely(NEW_STRUCTURE ? 2 : 1);
    if (potionEffectDuration == null) {
      potionEffectDuration = 0;
    }
    return potionEffectDuration;
  }
}

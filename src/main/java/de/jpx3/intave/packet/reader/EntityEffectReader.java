package de.jpx3.intave.packet.reader;

import com.comphenix.protocol.events.PacketContainer;
import de.jpx3.intave.adapter.MinecraftVersions;
import org.bukkit.potion.PotionEffectType;

public final class EntityEffectReader extends EntityReader {
  private static final boolean SECURE_CHAT = MinecraftVersions.VER1_19.atOrAbove();
  private static final boolean CAVE_AND_CLIFFS = MinecraftVersions.VER1_18_2.atOrAbove();

  public int effectType() {
    PacketContainer packet = packet();
    if (SECURE_CHAT) {
      PotionEffectType potionEffectType = packet.getEffectTypes().readSafely(0);
      if (potionEffectType == null) {
        return 0;
      }
      //noinspection deprecation
      return potionEffectType.getId();
    } else if (CAVE_AND_CLIFFS) {
      Integer potionEffectType = packet.getIntegers().readSafely(1);
      if (potionEffectType == null) {
        return 0;
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
    Byte potionEffectAmplifier = packet.getBytes().readSafely(CAVE_AND_CLIFFS ? 0 : 1);
    if (potionEffectAmplifier == null) {
      potionEffectAmplifier = 0;
    }
    return potionEffectAmplifier;
  }

  public int effectDuration() {
    Integer potionEffectDuration = packet().getIntegers().readSafely(CAVE_AND_CLIFFS && !SECURE_CHAT ? 2 : 1);
    if (potionEffectDuration == null) {
      potionEffectDuration = 0;
    }
    return potionEffectDuration;
  }
}

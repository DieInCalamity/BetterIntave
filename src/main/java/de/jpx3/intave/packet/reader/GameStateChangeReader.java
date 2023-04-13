package de.jpx3.intave.packet.reader;

import de.jpx3.intave.adapter.MinecraftVersions;
import de.jpx3.intave.klass.Lookup;

import java.lang.reflect.Field;

public final class GameStateChangeReader extends AbstractPacketReader {
  private final boolean HAS_WRAPPER = MinecraftVersions.VER1_16_0.atOrAbove();
  private final Class<?> GAME_STATE_CLASS = HAS_WRAPPER ? Lookup.serverClass("PacketPlayOutGameStateChange$a") : null;
  private Field FIELD_CACHE;

  public GameState type() {
    int index = typeIndex();
    if (index < 0 || index >= GameState.values().length) {
      return GameState.INVALID_BED;
    }
    return GameState.values()[index];
  }

  private int typeIndex() {
    if (HAS_WRAPPER) {
      try {
        Object wrapper = packet().getModifier().withType(GAME_STATE_CLASS).read(0);
        if (FIELD_CACHE == null) {
          FIELD_CACHE = wrapper.getClass().getDeclaredField("b");
          FIELD_CACHE.setAccessible(true);
        }
        return (int) FIELD_CACHE.get(wrapper);
      } catch (Exception exception) {
        exception.printStackTrace();
        return -1;
      }
    } else {
      return packet().getIntegers().read(0);
    }
  }

  public float value() {
    return packet().getFloat().read(0);
  }

  public int valueAsInt() {
    return (int)(value() + 0.5F);
  }

  public enum GameState {
    INVALID_BED(0),
    END_RAIN(1),
    BEGIN_RAIN(2),
    CHANGE_GAME_MODE(3),
    ENTER_CREDITS(4),
    DEMO_MESSAGE(5),
    ARROW_HITTING_PLAYER(6),
    RAIN_LEVEL_CHANGE(7),
    THUNDER_LEVEL_CHANGE(8),
    PLAY_MOB_APPEARANCE(9),
    PLAY_MOB2_APPEARANCE(10),
    ENABLE_RESPAWN_SCREEN(11),
    ;

    private final int id;

    GameState(int id) {
      this.id = id;
    }

    public int id() {
      return id;
    }
  }
}

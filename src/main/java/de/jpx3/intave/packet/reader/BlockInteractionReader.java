package de.jpx3.intave.packet.reader;

import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.MovingObjectPositionBlock;
import de.jpx3.intave.adapter.MinecraftVersions;
import de.jpx3.intave.annotate.Nullable;
import de.jpx3.intave.share.Direction;
import org.bukkit.util.Vector;

public final class BlockInteractionReader extends BlockPositionReader {
  private final boolean MODERN_RESOLVE = MinecraftVersions.VER1_14_0.atOrAbove();

  @Nullable
  public Direction direction() {
    int direction = enumDirection();
    if (direction == 255) {
      return null;
    }
    return Direction.values()[direction];
  }

  @Nullable
  public Vector facingVector() {
    StructureModifier<Float> floatsInPacket = packet().getFloat();
    if (floatsInPacket.size() >= 3) {
      return new Vector(
        floatsInPacket.read(0),
        floatsInPacket.read(1),
        floatsInPacket.read(2)
      );
    } else {
      return null;
    }
  }

  public int enumDirection() {
    if (MODERN_RESOLVE) {
      MovingObjectPositionBlock movingObjectPositionBlock = packet().getMovingBlockPositions().readSafely(0);
      return movingObjectPositionBlock == null ? 255 : movingObjectPositionBlock.getDirection().ordinal();
    } else {
      Integer enumDirection = packet().getIntegers().readSafely(0);
      if (enumDirection == null) {
        EnumWrappers.Direction direction = packet()
          .getDirections()
          .readSafely(0);
        return direction == null ? 255 : direction.ordinal();
      }
      return enumDirection;
    }
  }
}

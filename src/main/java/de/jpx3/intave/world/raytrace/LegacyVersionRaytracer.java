package de.jpx3.intave.world.raytrace;

import de.jpx3.intave.patchy.annotate.PatchyAutoTranslation;
import de.jpx3.intave.patchy.annotate.PatchyTranslateParameters;
import de.jpx3.intave.tools.wrapper.WrappedMovingObjectPosition;
import de.jpx3.intave.tools.wrapper.WrappedVector;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Player;

@PatchyAutoTranslation
public final class LegacyVersionRaytracer implements VersionRaytracer {
  @Override
  @PatchyAutoTranslation
  public WrappedMovingObjectPosition raytrace(World world, Player player, WrappedVector eyeVector, WrappedVector targetVector) {
    WorldServer handle = ((CraftWorld) world).getHandle();
    Vec3D nativeEyeVector = (Vec3D) eyeVector.convertToNativeVec3();
    Vec3D nativeTargetVector = (Vec3D) targetVector.convertToNativeVec3();
    MovingObjectPosition movingObjectPosition = performRaytrace(player, handle, nativeEyeVector, nativeTargetVector, false, false, false);
    return WrappedMovingObjectPosition.fromNativeMovingObjectPosition(movingObjectPosition);
  }

  @PatchyAutoTranslation
  @PatchyTranslateParameters
  public MovingObjectPosition performRaytrace(
    Player player, net.minecraft.server.v1_8_R3.WorldServer world,
    Vec3D lookVector, Vec3D position,
    boolean stopOnLiquid, boolean ignoreBlockWithoutBoundingBox, boolean returnLastUncollidableBlock
  ) {
    if (includesInvalidCoordinate(lookVector) || includesInvalidCoordinate(position)) {
      return null;
    }
    MovingObjectPosition movingobjectposition;
    int positionX = MathHelper.floor(position.a);
    int positionY = MathHelper.floor(position.b);
    int positionZ = MathHelper.floor(position.c);
    int lookX = MathHelper.floor(lookVector.a);
    int lookY = MathHelper.floor(lookVector.b);
    int lookZ = MathHelper.floor(lookVector.c);

    BlockPosition blockposition = new BlockPosition(lookX, lookY, lookZ);
    IBlockData iblockdata = world.getType(blockposition);
    Block block = iblockdata.getBlock();
    if ((!ignoreBlockWithoutBoundingBox || block.a(world, blockposition, iblockdata) != null) && block.a(iblockdata, stopOnLiquid) && (movingobjectposition = block.a(world, blockposition, lookVector, position)) != null) {
      return movingobjectposition;
    }

    MovingObjectPosition movingobjectposition1 = null;
    int k1 = 200;
    while (k1-- >= 0) {
      EnumDirection enumdirection;
      if (includesInvalidCoordinate(lookVector)) {
        return null;
      }
      if (lookX == positionX && lookY == positionY && lookZ == positionZ) {
        return returnLastUncollidableBlock ? movingobjectposition1 : null;
      }
      boolean flag3 = true;
      boolean flag4 = true;
      boolean flag5 = true;
      double d0 = 999.0;
      double d1 = 999.0;
      double d2 = 999.0;
      if (positionX > lookX) {
        d0 = (double) lookX + 1.0;
      } else if (positionX < lookX) {
        d0 = (double) lookX + 0.0;
      } else {
        flag3 = false;
      }
      if (positionY > lookY) {
        d1 = (double) lookY + 1.0;
      } else if (positionY < lookY) {
        d1 = (double) lookY + 0.0;
      } else {
        flag4 = false;
      }
      if (positionZ > lookZ) {
        d2 = (double) lookZ + 1.0;
      } else if (positionZ < lookZ) {
        d2 = (double) lookZ + 0.0;
      } else {
        flag5 = false;
      }
      double d3 = 999.0;
      double d4 = 999.0;
      double d5 = 999.0;
      double d6 = position.a - lookVector.a;
      double d7 = position.b - lookVector.b;
      double d8 = position.c - lookVector.c;
      if (flag3) {
        d3 = (d0 - lookVector.a) / d6;
      }
      if (flag4) {
        d4 = (d1 - lookVector.b) / d7;
      }
      if (flag5) {
        d5 = (d2 - lookVector.c) / d8;
      }
      if (d3 == -0.0) {
        d3 = -1.0E-4;
      }
      if (d4 == -0.0) {
        d4 = -1.0E-4;
      }
      if (d5 == -0.0) {
        d5 = -1.0E-4;
      }
      if (d3 < d4 && d3 < d5) {
        enumdirection = positionX > lookX ? EnumDirection.WEST : EnumDirection.EAST;
        lookVector = new Vec3D(d0, lookVector.b + d7 * d3, lookVector.c + d8 * d3);
      } else if (d4 < d5) {
        enumdirection = positionY > lookY ? EnumDirection.DOWN : EnumDirection.UP;
        lookVector = new Vec3D(lookVector.a + d6 * d4, d1, lookVector.c + d8 * d4);
      } else {
        enumdirection = positionZ > lookZ ? EnumDirection.NORTH : EnumDirection.SOUTH;
        lookVector = new Vec3D(lookVector.a + d6 * d5, lookVector.b + d7 * d5, d2);
      }
      lookX = MathHelper.floor(lookVector.a) - (enumdirection == EnumDirection.EAST ? 1 : 0);
      lookY = MathHelper.floor(lookVector.b) - (enumdirection == EnumDirection.UP ? 1 : 0);
      lookZ = MathHelper.floor(lookVector.c) - (enumdirection == EnumDirection.SOUTH ? 1 : 0);
      blockposition = new BlockPosition(lookX, lookY, lookZ);
      IBlockData iblockdata1 = world.getType(blockposition);
      Block block1 = iblockdata1.getBlock();

      // block1.a refers to getCollisionBoundingBox
      if (ignoreBlockWithoutBoundingBox && block1.a(world, blockposition, iblockdata1) == null) {
        continue;
      }
      if (block1.a(iblockdata1, stopOnLiquid)) {
        MovingObjectPosition movingobjectposition2 = block1.a(world, blockposition, lookVector, position);
        if (movingobjectposition2 == null) {
          continue;
        }
        return movingobjectposition2;
      }
      movingobjectposition1 = new MovingObjectPosition(MovingObjectPosition.EnumMovingObjectType.MISS, lookVector, enumdirection, blockposition);
    }
    return returnLastUncollidableBlock ? movingobjectposition1 : null;
  }

  @PatchyAutoTranslation
  @PatchyTranslateParameters
  private boolean includesInvalidCoordinate(Vec3D vec3D) {
    return Double.isNaN(vec3D.a) || Double.isNaN(vec3D.b) || Double.isNaN(vec3D.c);
  }
}

package de.jpx3.intave.detect.checks.world.interaction;

import de.jpx3.intave.tools.client.SinusCache;
import de.jpx3.intave.tools.wrapper.*;
import de.jpx3.intave.user.UserRepository;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public final class BlockRaytracer {

  public static WrappedMovingObjectPosition resolveBlockInLineOfSight(Player player, Location playerLocation) {
    double blockReachDistance = resolveBlockReachDistance(player.getGameMode());
    double eyeHeight = resolvePlayerEyeHeight(player);
    return legacyBlockRayTrace(playerLocation, playerLocation, blockReachDistance, eyeHeight, 1.0f);
  }

  private static WrappedMovingObjectPosition legacyBlockRayTrace(Location location, Location prevLocation, double blockReachDistance, double eyeHeight, float partialTicks) {
    final WrappedVector eyeVector = resolvePositionEyes(location, prevLocation, eyeHeight, partialTicks);
    final WrappedVector vec4 = resolveLookVector(location, prevLocation, partialTicks);
    final WrappedVector targetVector = eyeVector.addVector(vec4.xCoord * blockReachDistance, vec4.yCoord * blockReachDistance, vec4.zCoord * blockReachDistance);
    return legacyRayTraceBlocks(location.getWorld(), eyeVector, targetVector, false, false);
  }

  private static WrappedMovingObjectPosition legacyRayTraceBlocks(World world, WrappedVector eyeVector, WrappedVector targetVector, boolean stopOnLiquid, boolean returnLastUncollidableBlock) {
    int i = WrappedMathHelper.floor(targetVector.xCoord);
    int j = WrappedMathHelper.floor(targetVector.yCoord);
    int k = WrappedMathHelper.floor(targetVector.zCoord);
    int l = WrappedMathHelper.floor(eyeVector.xCoord);
    int i2 = WrappedMathHelper.floor(eyeVector.yCoord);
    int j2 = WrappedMathHelper.floor(eyeVector.zCoord);
    Location loc = new Location(world, l, i2, j2);
    Object nativeBlock = BlockAccessHelper.resolveNativeBlock(loc);
    Object nativeWorld = BlockAccessHelper.resolveNativeWorld(world);
    Object blockState = BlockAccessHelper.resolveBlockData(loc);
    if (nativeBlock == null) {
      return null;
    }
    if (BlockAccessHelper.liquidCheck(nativeBlock, blockState, stopOnLiquid)) {
//      final MovingObjectPosition movingobjectposition = nativeBlock.a(nativeWorld, block, WrappedVector.convertToNMS(eyeVector), WrappedVector.convertToNMS(targetVector));
      WrappedMovingObjectPosition movingobjectposition3 = BlockAccessHelper.blockRaytrace(nativeBlock, nativeWorld, BlockAccessHelper.generateBlockPosition(loc), eyeVector.convertToNativeVec3(), targetVector.convertToNativeVec3());
      if (movingobjectposition3 != null) {
        return movingobjectposition3;
      }/*
      if (movingobjectposition != null) {
        return movingobjectposition;
      }*/
    }
    WrappedMovingObjectPosition movingobjectposition2 = null;
    int k2 = 20;
    while (k2-- >= 0) {
      if (Double.isNaN(eyeVector.xCoord) || Double.isNaN(eyeVector.yCoord) || Double.isNaN(eyeVector.zCoord)) {
        return null;
      }
      if (l == i && i2 == j && j2 == k) {
        return returnLastUncollidableBlock ? movingobjectposition2 : null;
      }
      boolean flag2 = true;
      boolean flag3 = true;
      boolean flag4 = true;
      double d0 = 999.0;
      double d2 = 999.0;
      double d3 = 999.0;
      if (i > l) {
        d0 = l + 1.0;
      }
      else if (i < l) {
        d0 = l + 0.0;
      }
      else {
        flag2 = false;
      }
      if (j > i2) {
        d2 = i2 + 1.0;
      }
      else if (j < i2) {
        d2 = i2 + 0.0;
      }
      else {
        flag3 = false;
      }
      if (k > j2) {
        d3 = j2 + 1.0;
      }
      else if (k < j2) {
        d3 = j2 + 0.0;
      }
      else {
        flag4 = false;
      }
      double d4 = 999.0;
      double d5 = 999.0;
      double d6 = 999.0;
      double d7 = targetVector.xCoord - eyeVector.xCoord;
      double d8 = targetVector.yCoord - eyeVector.yCoord;
      double d9 = targetVector.zCoord - eyeVector.zCoord;
      if (flag2) {
        d4 = (d0 - eyeVector.xCoord) / d7;
      }
      if (flag3) {
        d5 = (d2 - eyeVector.yCoord) / d8;
      }
      if (flag4) {
        d6 = (d3 - eyeVector.zCoord) / d9;
      }
      if (d4 == -0.0) {
        d4 = -1.0E-4;
      }
      if (d5 == -0.0) {
        d5 = -1.0E-4;
      }
      if (d6 == -0.0) {
        d6 = -1.0E-4;
      }
      WrappedEnumDirection enumfacing;
      if (d4 < d5 && d4 < d6) {
        enumfacing = ((i > l) ? WrappedEnumDirection.WEST : WrappedEnumDirection.EAST);
        eyeVector = new WrappedVector(d0, eyeVector.yCoord + d8 * d4, eyeVector.zCoord + d9 * d4);
      }
      else if (d5 < d6) {
        enumfacing = ((j > i2) ? WrappedEnumDirection.DOWN : WrappedEnumDirection.UP);
        eyeVector = new WrappedVector(eyeVector.xCoord + d7 * d5, d2, eyeVector.zCoord + d9 * d5);
      }
      else {
        enumfacing = ((k > j2) ? WrappedEnumDirection.NORTH : WrappedEnumDirection.SOUTH);
        eyeVector = new WrappedVector(eyeVector.xCoord + d7 * d6, eyeVector.yCoord + d8 * d6, d3);
      }
      l = WrappedMathHelper.floor(eyeVector.xCoord) - ((enumfacing == WrappedEnumDirection.EAST) ? 1 : 0);
      i2 = WrappedMathHelper.floor(eyeVector.yCoord) - ((enumfacing == WrappedEnumDirection.UP) ? 1 : 0);
      j2 = WrappedMathHelper.floor(eyeVector.zCoord) - ((enumfacing == WrappedEnumDirection.SOUTH) ? 1 : 0);
      Location byCache = new Location(world, l, i2, j2);
      Object iblockstate1 = BlockAccessHelper.resolveBlockData(byCache);
      Object block1 = BlockAccessHelper.resolveNativeBlock(byCache);
      if (block1 == null) {
        return null;
      }
//      if (ignoreBlockWithoutBoundingBox && block1.a(nativeWorld, byCache, iblockstate1) == null) {
//        continue;
//      }
//      Bukkit.broadcastMessage(block1.getClass().getSimpleName() + " " + eyeVector);
      if (BlockAccessHelper.liquidCheck(block1, iblockstate1, stopOnLiquid)) {
        WrappedMovingObjectPosition movingobjectposition3 = BlockAccessHelper.blockRaytrace(block1/*nativeBlock*/, nativeWorld, BlockAccessHelper.generateBlockPosition(byCache), eyeVector.convertToNativeVec3(), targetVector.convertToNativeVec3());//block1.a(nativeWorld, BlockAccessHelper.generateBlockPosition(byCache), eyeVector.convertToNativeVec3(), targetVector.convertToNativeVec3());
        if (movingobjectposition3 != null) {
          return movingobjectposition3;
        } else {
          movingobjectposition2 = new WrappedMovingObjectPosition(WrappedMovingObjectPosition.MovingObjectType.MISS, eyeVector, enumfacing, new WrappedBlockPosition(byCache));
        }
      }
      else {
        movingobjectposition2 = new WrappedMovingObjectPosition(WrappedMovingObjectPosition.MovingObjectType.MISS, eyeVector, enumfacing, new WrappedBlockPosition(byCache));
      }
    }
    return returnLastUncollidableBlock ? movingobjectposition2 : null;
  }

//  private static WrappedVector nativeBlockRayTrace(Location location, Location prevLocation, double blockReachDistance, double eyeHeight, float partialTicks) {
//
//  }

  private static WrappedVector resolvePositionEyes(Location location, Location prevLocation, double eyeHeight, float partialTicks) {
    final double posX = location.getX();
    final double posY = location.getY();
    final double posZ = location.getZ();
    if (partialTicks == 1.0f) {
      return new WrappedVector(posX, posY + eyeHeight, posZ);
    }
    final double prevPosX = prevLocation.getX();
    final double prevPosY = prevLocation.getY();
    final double prevPosZ = prevLocation.getZ();
    final double d0 = prevPosX + (posX - prevPosX) * partialTicks;
    final double d2 = prevPosY + (posY - prevPosY) * partialTicks + eyeHeight;
    final double d3 = prevPosZ + (posZ - prevPosZ) * partialTicks;
    return new WrappedVector(d0, d2, d3);
  }

  private static WrappedVector resolveLookVector(Location location, Location prevLocation, float partialTicks) {
    final float rotationYawHead = location.getYaw();
    final float rotationPitch = location.getPitch();
    final float prevRotationYawHead = prevLocation.getYaw();
    final float prevRotationPitch = prevLocation.getPitch();
    if (partialTicks == 1.0f) {
      return resolveVectorForRotation(rotationPitch, rotationYawHead);
    }
    final float f = prevRotationPitch + (rotationPitch - prevRotationPitch) * partialTicks;
    final float f2 = prevRotationYawHead + (rotationYawHead - prevRotationYawHead) * partialTicks;
    return resolveVectorForRotation(f, f2);
  }

  private static WrappedVector resolveVectorForRotation(float pitch, float yaw) {
    final float f = SinusCache.cos(-yaw * 0.017453292f - 3.1415927f, false);
    final float f2 = SinusCache.sin(-yaw * 0.017453292f - 3.1415927f, false);
    final float f3 = -SinusCache.cos(-pitch * 0.017453292f, false);
    final float f4 = SinusCache.sin(-pitch * 0.017453292f, false);
    return new WrappedVector(f2 * f3, f4, f * f3);
  }

  private static double resolvePlayerEyeHeight(Player player) {
    float f = 1.62f;
    if (player.isSleeping()) {
      f = 0.2f;
    }
    if (player.isSneaking()) {
      f -= UserRepository.userOf(player).meta().clientData().cameraSneakOffset();
    }
    return f;
  }

  private static double resolveBlockReachDistance(final GameMode gameMode) {
    return (gameMode == GameMode.CREATIVE) ? 5.0 : 4.5;
  }
}

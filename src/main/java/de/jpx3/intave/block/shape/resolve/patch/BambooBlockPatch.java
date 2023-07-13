package de.jpx3.intave.block.shape.resolve.patch;

import de.jpx3.intave.block.shape.BlockShape;
import de.jpx3.intave.share.BoundingBox;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

import static de.jpx3.intave.share.ClientMath.coordinateRandom;

final class BambooBlockPatch extends BoundingBoxPatch {
  private static final BoundingBox LEAF = BoundingBox.originFromX16(6.5D, 0.0D, 6.5D, 9.5D, 16.0, 9.5D);
  private static final BlockShape[][] CACHE = new BlockShape[16][16];

  @Override
  public BlockShape collisionPatch(World world, Player player, int posX, int posY, int posZ, Material type, int blockState, BlockShape shape) {
    // Small Bamboo Leaves
    if (shape.isEmpty()) {
      return shape;
    }
    long randomCoordinate = coordinateRandom(posX, 0, posZ);
    int xOffsetKey = (int) (randomCoordinate & 15L);
    int zOffsetKey = (int) (randomCoordinate >> 8 & 15L);
    BlockShape box = CACHE[xOffsetKey][zOffsetKey];
    if (box == null) {
      double offsetX = ((double) ((float) xOffsetKey / 15.0F) - 0.5D) * 0.5D;
      double offsetZ = ((double) ((float) zOffsetKey / 15.0F) - 0.5D) * 0.5D;
      double offsetY = 0.0;
      box = CACHE[xOffsetKey][zOffsetKey] = LEAF.originOffset(offsetX, offsetY, offsetZ);
    }
    return box;
  }

  @Override
  public boolean appliesTo(Material material) {
    String name = material.name();
    return name.contains("BAMBOO");
  }
}
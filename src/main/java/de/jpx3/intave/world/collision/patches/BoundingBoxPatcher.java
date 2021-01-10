package de.jpx3.intave.world.collision.patches;

import de.jpx3.intave.tools.wrapper.WrappedAxisAlignedBB;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class BoundingBoxPatcher {
  private final static Map<Integer, BoundingBoxPatch> patches = new HashMap<>();

  public static void setup() {
    add(BlockTrapdoorPatch.class);
  }

  private static void add(Class<? extends BoundingBoxPatch> patchClass) {
    try {
      add(patchClass.newInstance());
    } catch (InstantiationException | IllegalAccessException exception) {
      throw new IllegalStateException(exception);
    }
  }

  private static void add(BoundingBoxPatch boundingBoxPatch) {
    patches.put(boundingBoxPatch.blockId(), boundingBoxPatch);
  }

  public static List<WrappedAxisAlignedBB> patch(World world, Player player, Block block, List<WrappedAxisAlignedBB> bbs) {
    BoundingBoxPatch boundingBoxPatch = patches.get(block.getTypeId());
    return boundingBoxPatch == null ? bbs : boundingBoxPatch.patch(world, player, block, bbs);
  }

  public static List<WrappedAxisAlignedBB> patch(World world, Player player, int typeId, int blockState, List<WrappedAxisAlignedBB> bbs) {
    BoundingBoxPatch boundingBoxPatch = patches.get(typeId);
    return boundingBoxPatch == null ? bbs : boundingBoxPatch.patch(world, player, typeId, blockState, bbs);
  }
}

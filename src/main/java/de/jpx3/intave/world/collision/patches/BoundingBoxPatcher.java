package de.jpx3.intave.world.collision.patches;

import de.jpx3.intave.logging.IntaveLogger;
import de.jpx3.intave.tools.wrapper.WrappedAxisAlignedBB;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class BoundingBoxPatcher {
  private final static Map<Material, BoundingBoxPatch> patches = new HashMap<>();

  public static void setup() {
    add(BlockTrapdoorPatch.class);
    add(BlockAnvilPatch.class);
    add(BlockLadderPatch.class);
    add(BlockLilyPadPatch.class);
    add(BlockFenceGatePatch.class);
    add(BlockFarmlandPatch.class);
    add(BlockThinPatch.class);
  }

  private static void add(Class<? extends BoundingBoxPatch> patchClass) {
    try {
      add(patchClass.newInstance());
    } catch (Exception | Error exception) {
      IntaveLogger.logger().info("Failed to load bounding box patch (class " + patchClass + ")");
      exception.printStackTrace();
    }
  }

  private static void add(BoundingBoxPatch boundingBoxPatch) {
    for (Material type : Material.values()) {
      if(boundingBoxPatch.appliesTo(type)) {
        patches.put(type, boundingBoxPatch);
      }
    }
  }

  public static List<WrappedAxisAlignedBB> patch(World world, Player player, Block block, List<WrappedAxisAlignedBB> bbs) {
    BoundingBoxPatch boundingBoxPatch = patches.get(block.getType());
    return boundingBoxPatch == null ? bbs : transpose(boundingBoxPatch.patch(world, player, block, reposeIfRequired(boundingBoxPatch, bbs, block.getX(), block.getY(), block.getZ())), block.getX(), block.getY(), block.getZ());
  }

  public static List<WrappedAxisAlignedBB> patch(World world, Player player, int blockX, int blockY, int blockZ, Material type, int blockState, List<WrappedAxisAlignedBB> boxes) {
    BoundingBoxPatch boundingBoxPatch = patches.get(type);
    return boundingBoxPatch == null ? boxes : transpose(boundingBoxPatch.patch(world, player, type, blockState, reposeIfRequired(boundingBoxPatch, boxes, blockX, blockY, blockZ)), blockX, blockY, blockZ);
  }

  public static List<WrappedAxisAlignedBB> transpose(List<WrappedAxisAlignedBB> boundingBoxes, int posX, int posY, int posZ) {
    if(boundingBoxes.isEmpty()) {
      return boundingBoxes;
    }
    for (int i = 0; i < boundingBoxes.size(); i++) {
      WrappedAxisAlignedBB boundingBox = boundingBoxes.get(i);
      if (boundingBox.isOriginBox()) {
        boundingBoxes.set(i, boundingBox.offset(posX, posY, posZ));
      }
    }
    return boundingBoxes;
  }

  public static List<WrappedAxisAlignedBB> reposeIfRequired(BoundingBoxPatch patch, List<WrappedAxisAlignedBB> boundingBoxes, int posX, int posY, int posZ) {
    if(!patch.requireRepose() || boundingBoxes.isEmpty()) {
      return boundingBoxes;
    }
    List<WrappedAxisAlignedBB> reposedList = new ArrayList<>(boundingBoxes);
    for (int i = 0; i < reposedList.size(); i++) {
      WrappedAxisAlignedBB boundingBox = reposedList.get(i);
      WrappedAxisAlignedBB newBox = boundingBox.offset(-posX, -posY, -posZ);
      newBox.setOriginBox();
      reposedList.set(i, newBox);
    }
    return reposedList;
  }
}

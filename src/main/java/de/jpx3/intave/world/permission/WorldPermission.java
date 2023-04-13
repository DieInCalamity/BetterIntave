package de.jpx3.intave.world.permission;

import de.jpx3.intave.access.player.event.BucketAction;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class WorldPermission {
  private static BlockPlacePermissionCheck blockPlacePermissionCheck;
  private static BlockBreakPermissionCheck blockBreakPermissionCheck;
  private static BucketActionPermissionCheck bucketActionPermissionCheck;

  public static void setup() {
    blockPlacePermissionCheck = new EventPlacePermissionResolver();
    blockBreakPermissionCheck = new EventBreakPermissionResolver();
    bucketActionPermissionCheck = new EventBukkitActionPermissionResolver();
  }

  public static boolean blockPlacePermission(
    Player player, World world, boolean mainHand, int blockX, int blockY, int blockZ, int enumDirection, Material type, int variant
  ) {
    return blockPlacePermissionCheck.hasPermission(player, world, mainHand, blockX, blockY, blockZ, enumDirection, type, variant);
  }

  public static boolean blockBreakPermission(
    Player player, Block block
  ) {
    return blockBreakPermissionCheck.hasPermission(player, block);
  }

  public static boolean bukkitActionPermission(
    Player player, BucketAction bucketAction, Block blockClicked, BlockFace blockFace, Material bucket, ItemStack itemInHand
  ) {
    return bucketActionPermissionCheck.hasPermission(player, bucketAction, blockClicked, blockFace, bucket, itemInHand);
  }
}

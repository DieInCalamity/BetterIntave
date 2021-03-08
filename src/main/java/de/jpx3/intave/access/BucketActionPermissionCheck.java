package de.jpx3.intave.access;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Deprecated
public interface BucketActionPermissionCheck {
  boolean hasPermission(Player player, Block blockClicked, BlockFace blockFace, Material bucket, ItemStack itemInHand);

  default void open() {}

  default void close() {}
}

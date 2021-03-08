package de.jpx3.intave.access;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Deprecated
public interface BlockInteractionPermissionCheck {
  boolean hasPermission(Player player, ItemStack itemStack, Block block, BlockFace blockFace);

  default void open() {}

  default void close() {}
}

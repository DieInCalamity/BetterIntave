package de.jpx3.intave.access;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public abstract class BlockBreakPermissionCheck {
  abstract boolean hasPermission(Player player, Block block);
}

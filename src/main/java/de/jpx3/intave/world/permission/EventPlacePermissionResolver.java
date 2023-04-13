package de.jpx3.intave.world.permission;

import de.jpx3.intave.access.player.event.AsyncIntaveBlockPlacePermissionEvent;
import de.jpx3.intave.module.Modules;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

final class EventPlacePermissionResolver implements BlockPlacePermissionCheck {
  @Override
  public boolean hasPermission(
    Player player, World world,
    boolean mainHand,
    int blockX, int blockY, int blockZ,
    int enumDirection, Material type, int variant
  ) {
    AsyncIntaveBlockPlacePermissionEvent event = Modules.eventInvoker().invokeEvent(
      AsyncIntaveBlockPlacePermissionEvent.class,
      x -> x.copy(player, world, mainHand, blockX, blockY, blockZ, enumDirection, type, variant)
    );
    return !event.isCancelled();
  }
}

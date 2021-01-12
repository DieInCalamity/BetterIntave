package de.jpx3.intave.permission;

import de.jpx3.intave.user.UserRepository;
import org.bukkit.entity.Player;

public final class PermissionCheck {
  public static boolean permissionCheck(Player player, String permission) {
    if(!UserRepository.hasUser(player)) {
      return false;
    }
    PermissionCache permissionCache = UserRepository.userOf(player).permissionCache();
    if(permissionCache.inCache(permission)) {
      return permissionCache.permissionCheck(permission);
    } else {
      boolean access = player.hasPermission(permission) && player.isPermissionSet(permission);
      permissionCache.permissionSave(permission, access);
      return access;
    }
  }
}

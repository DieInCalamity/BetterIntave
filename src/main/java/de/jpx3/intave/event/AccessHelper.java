package de.jpx3.intave.event;

import de.jpx3.intave.annotate.refactoring.IdoNotBelongHere;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public final class AccessHelper {
  @IdoNotBelongHere
  public static long now() {
    return System.currentTimeMillis();
  }

  @Deprecated
  public static boolean isOnline(OfflinePlayer player) {
    return player != null && (player.isOnline() || Bukkit.getPlayer(player.getUniqueId()) != null);
  }
}
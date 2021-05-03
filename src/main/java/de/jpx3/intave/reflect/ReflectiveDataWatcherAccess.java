package de.jpx3.intave.reflect;

import de.jpx3.intave.adapter.MinecraftVersions;
import de.jpx3.intave.reflect.datawatcher.DataWatcherAccess;
import de.jpx3.intave.reflect.datawatcher.LegacyDataWatcherAccess;
import de.jpx3.intave.reflect.datawatcher.NewDataWatcherAccess;
import org.bukkit.entity.Player;

import static de.jpx3.intave.reflect.ReflectiveAccess.DATA_WATCHER_NEW_ACCESS_VER;

public final class ReflectiveDataWatcherAccess  {
  public static final int DATA_WATCHER_BLOCKING_ID = MinecraftVersions.VER1_9_0.atOrAbove() ? 1 : 4;
  public static final int DATA_WATCHER_SNEAK_ID = 1;
  private final static DataWatcherAccess nativeDataWatcherAccess = DATA_WATCHER_NEW_ACCESS_VER ? new NewDataWatcherAccess() : new LegacyDataWatcherAccess();

  public static void setDataWatcherFlag(Player player, int key, boolean flag) {
    nativeDataWatcherAccess.setDataWatcherFlag(player, key, flag);
  }

  public static boolean getDataWatcherFlag(Player player, int key) {
    return nativeDataWatcherAccess.getDataWatcherFlag(player, key);
  }
}
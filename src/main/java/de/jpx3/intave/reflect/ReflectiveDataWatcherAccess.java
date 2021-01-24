package de.jpx3.intave.reflect;

import de.jpx3.intave.patchy.annotate.PatchyAutoTranslation;
import net.minecraft.server.v1_8_R3.DataWatcher;
import net.minecraft.server.v1_8_R3.Entity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.Player;

public final class ReflectiveDataWatcherAccess {
  public static final int DATA_WATCHER_BLOCKING_ID = 4;

  @PatchyAutoTranslation
  public static void setDataWatcherFlag(Player player, int i, boolean flag) {
    Entity handle = ((CraftEntity) player).getHandle();
    DataWatcher dataWatcher = handle.getDataWatcher();
    byte b0 = dataWatcher.getByte(0);
    if (flag) {
      dataWatcher.watch(0, (byte) (b0 | 1 << i));
    } else {
      dataWatcher.watch(0, (byte) (b0 & ~(1 << i)));
    }
  }

  @PatchyAutoTranslation
  public static boolean getDataWatcherFlag(Player player, int i) {
    Entity handle = ((CraftEntity) player).getHandle();
    DataWatcher dataWatcher = handle.getDataWatcher();
    return (dataWatcher.getByte(0) & 1 << i) != 0;
  }
}
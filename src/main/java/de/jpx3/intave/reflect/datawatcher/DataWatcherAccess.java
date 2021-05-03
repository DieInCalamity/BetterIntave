package de.jpx3.intave.reflect.datawatcher;

import org.bukkit.entity.Player;

public interface DataWatcherAccess {
  void setDataWatcherFlag(Player player, int key, boolean flag);
  boolean getDataWatcherFlag(Player player, int key);
}
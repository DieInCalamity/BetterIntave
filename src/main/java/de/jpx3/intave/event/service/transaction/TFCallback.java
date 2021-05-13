package de.jpx3.intave.event.service.transaction;

import org.bukkit.entity.Player;

public interface TFCallback<T> {
  void success(Player player, T target);
}
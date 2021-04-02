package de.jpx3.intave.adapter.viaversion;

import org.bukkit.entity.Player;

public interface ViaVersionAccess {
  void setup();

  int protocolVersionOf(Player player);

  boolean ignoreBlocking(Player player);

  boolean available();
}
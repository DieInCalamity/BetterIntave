package de.jpx3.intave.module.tracker.player;

import de.jpx3.intave.IntavePlugin;
import de.jpx3.intave.module.Module;
import de.jpx3.intave.module.linker.bukkit.BukkitEventSubscription;
import de.jpx3.intave.user.UserLocal;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public final class CloudStateNotify extends Module {
  public final static UserLocal<List<UUID>> interacted = UserLocal.withInitial(() -> new ArrayList<>());

  @BukkitEventSubscription
  public void on(PlayerJoinEvent join) {
    plugin.cloud().playerStateChange(
      join.getPlayer(), IntavePlugin.gameId(), true, Collections.emptyList()
    );
  }

  @BukkitEventSubscription
  public void on(PlayerQuitEvent quit) {
    plugin.cloud().playerStateChange(
      quit.getPlayer(), IntavePlugin.gameId(), false, interacted.get(quit.getPlayer())
    );
  }

  @BukkitEventSubscription
  public void on(EntityDeathEvent death) {
    if (death.getEntity() instanceof Player) {
      Player player = (Player) death.getEntity();
      Player killer = player.getKiller();
      if (killer != null) {
        interacted.get(player).add(killer.getUniqueId());
        interacted.get(killer).add(player.getUniqueId());
      }
    }
  }
}

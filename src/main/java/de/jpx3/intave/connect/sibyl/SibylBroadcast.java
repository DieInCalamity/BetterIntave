package de.jpx3.intave.connect.sibyl;

import de.jpx3.intave.IntavePlugin;
import de.jpx3.intave.annotate.Native;
import de.jpx3.intave.executor.Synchronizer;
import de.jpx3.intave.user.MessageChannelSubscriptions;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;

public final class SibylBroadcast {
  @Native
  public static void broadcast(String message) {
    Collection<? extends Player> receiver = MessageChannelSubscriptions.sibylReceiver();
    if (receiver.isEmpty()) {
      return;
    }
    if (!Bukkit.isPrimaryThread()) {
      Synchronizer.synchronize(() -> broadcast(message));
      return;
    }
    IntavePlugin intavePlugin = IntavePlugin.singletonInstance();
    for (Player authenticatedPlayer : receiver) {
      if (intavePlugin.sibylIntegrationService().isAuthenticated(authenticatedPlayer)) {
        authenticatedPlayer.sendMessage(message);
      }
    }
  }
}

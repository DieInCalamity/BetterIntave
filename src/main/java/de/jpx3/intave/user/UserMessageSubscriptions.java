package de.jpx3.intave.user;

import de.jpx3.intave.tools.GarbageCollector;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public final class UserMessageSubscriptions {
  private final static Collection<Player> sibylRepo = GarbageCollector.watch(new CopyOnWriteArrayList<>());

  public static Collection<? extends Player> sibylReceiver() {
    return sibylRepo;
  }

  public static void setSibyl(Player player, boolean sibyl) {
    if (sibyl) {
      if (!sibylRepo.contains(player)) {
        sibylRepo.add(player);
      }
    } else {
      sibylRepo.remove(player);
    }
  }

  private final static Map<UserMessageChannel, List<Player>> messageChannelSubscriptions = new ConcurrentHashMap<>();

  public static Collection<? extends Player> receiverOf(UserMessageChannel channel) {
    return messageChannelSubscriptions.computeIfAbsent(channel, theChannel -> new CopyOnWriteArrayList<>());
  }

  public static void setChannelActivation(Player player, UserMessageChannel channel, boolean status) {
    Collection<Player> players = messageChannelSubscriptions.computeIfAbsent(channel, theChannel -> new CopyOnWriteArrayList<>());
    if (status) {
      if (!players.contains(player)) {
        players.add(player);
      }
    } else {
      players.remove(player);
    }
  }
}

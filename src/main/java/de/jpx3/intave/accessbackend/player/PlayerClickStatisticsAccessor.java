package de.jpx3.intave.accessbackend.player;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import de.jpx3.intave.IntavePlugin;
import de.jpx3.intave.access.PlayerClicks;
import de.jpx3.intave.tools.GarbageCollector;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public final class PlayerClickStatisticsAccessor {
  private final IntavePlugin plugin;
  private final Map<UUID, PlayerClicks> playerClickStatisticAccessCache = GarbageCollector.watch(Maps.newConcurrentMap());
  private final Map<UUID, List<Consumer<Integer>>> subscriptions = GarbageCollector.watch(Maps.newConcurrentMap());

  public PlayerClickStatisticsAccessor(IntavePlugin plugin) {
    this.plugin = plugin;
  }

  public void pushClicks(Player player, int cps) {
    List<Consumer<Integer>> subscriptionList = subscriptions.get(player.getUniqueId());

    if(subscriptionList != null) {
      for (Consumer<Integer> integerConsumer : subscriptionList) {
        integerConsumer.accept(cps);
      }
    }
  }

  public PlayerClicks clickStatisticsOf(Player player) {
    Preconditions.checkNotNull(player);
    return playerClickStatisticAccessCache.computeIfAbsent(player.getUniqueId(), x -> newClickStatistics(player));
  }

  private PlayerClicks newClickStatistics(Player player) {
    return new PlayerClicks() {
      @Override
      public int clicksLastSecond() {
        return 0;
      }

      @Override
      public void subscribeToSecond(Consumer<Integer> clicks) {
        subscriptions.computeIfAbsent(player.getUniqueId(), uuid -> new ArrayList<>()).add(clicks);
      }
    };
  }
}

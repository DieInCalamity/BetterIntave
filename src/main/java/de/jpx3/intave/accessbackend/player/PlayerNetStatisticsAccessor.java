package de.jpx3.intave.accessbackend.player;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import de.jpx3.intave.IntavePlugin;
import de.jpx3.intave.access.PlayerNetStatistics;
import de.jpx3.intave.tools.MapReferenceGarbageCollector;
import de.jpx3.intave.user.UserRepository;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;

public final class PlayerNetStatisticsAccessor {
  private final IntavePlugin plugin;
  private final Map<UUID, PlayerNetStatistics> netStatisticsCache = MapReferenceGarbageCollector.watch(Maps.newConcurrentMap());
  private final Map<UUID, List<BiConsumer<Integer, Integer>>> pingUpdateSubscriptions = MapReferenceGarbageCollector.watch(Maps.newConcurrentMap());

  public PlayerNetStatisticsAccessor(IntavePlugin plugin) {
    this.plugin = plugin;
  }

  public synchronized PlayerNetStatistics netStatisticsOf(Player player) {
    Preconditions.checkNotNull(player);
    return netStatisticsCache.computeIfAbsent(player.getUniqueId(), uuid -> newNetStatisticsAccessOf(player));
  }

  public void pushPingJitterUpdate(Player player, int ping, int jitter) {
    List<BiConsumer<Integer, Integer>> subscriptionList
      = pingUpdateSubscriptions.get(player.getUniqueId());
    if(subscriptionList != null) {
      subscriptionList.forEach(subscription -> subscription.accept(ping, jitter));
    }
  }

  private PlayerNetStatistics newNetStatisticsAccessOf(Player player) {
    return new PlayerNetStatistics() {
      @Override
      public int latency() {
        return UserRepository.userOf(player).latency();
      }

      @Override
      public int latencyJitter() {
        return UserRepository.userOf(player).latencyJitter();
      }

      @Override
      public void subscribe(BiConsumer<Integer, Integer> callback) {
        List<BiConsumer<Integer, Integer>> subscriptions
          = pingUpdateSubscriptions.computeIfAbsent(player.getUniqueId(), x -> new ArrayList<>());
        subscriptions.add(callback);
      }

      public long packetSentByClient() {
        return 0;
      }

      public long packetSentToClient() {
        return 0;
      }
    };
  }
}

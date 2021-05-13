package de.jpx3.intave.user;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.jpx3.intave.event.service.entity.WrappedEntity;
import de.jpx3.intave.event.service.transaction.TFRequest;
import de.jpx3.intave.tools.annotate.Relocate;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

@Relocate
public final class UserMetaSynchronizeData {
  private final Player player;
  private final Map<Short, TFRequest<?>> transactionFeedBackMap = Maps.newConcurrentMap();
  private final Map<Integer, WrappedEntity> synchronizedEntityMap = Maps.newConcurrentMap();
  private final Map<Long, Long> remainingPingPacketTimestamps = Maps.newConcurrentMap();
  private final List<Long> latencyDifferenceBalance = Lists.newCopyOnWriteArrayList();

  // Client Synchronization
  public int latency;
  public long lastKeepAliveDifference;
  public int latencyJitter;
  public short transactionCounter = Short.MIN_VALUE;
  public long transactionNumCounter = 0;
  public long lastReceivedTransactionNum = -1;

  public UserMetaSynchronizeData(Player player) {
    this.player = player;
  }

  public Map<Short, TFRequest<?>> transactionFeedBackMap() {
    return transactionFeedBackMap;
  }

  public Map<Integer, WrappedEntity> synchronizedEntityMap() {
    return synchronizedEntityMap;
  }

  public Map<Long, Long> remainingPingPacketTimestamps() {
    return remainingPingPacketTimestamps;
  }

  public List<Long> latencyDifferenceBalance() {
    return latencyDifferenceBalance;
  }
}
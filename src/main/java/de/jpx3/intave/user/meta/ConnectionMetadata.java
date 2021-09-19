package de.jpx3.intave.user.meta;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.jpx3.intave.annotate.DispatchTarget;
import de.jpx3.intave.annotate.Relocate;
import de.jpx3.intave.module.feedback.FeedbackRequest;
import de.jpx3.intave.module.tracker.entity.WrappedEntity;
import org.bukkit.entity.Player;

import java.util.*;

@Relocate
public final class ConnectionMetadata {
  private final Player player;
  private final Map<Short, FeedbackRequest<?>> transactionShortMap = Maps.newConcurrentMap();
  private final Map<Long, FeedbackRequest<?>> transactionGlobalKeyMap = Maps.newConcurrentMap();
  private final Map<Long, Queue<FeedbackRequest<?>>> transactionOptionalAppendixMap = Maps.newConcurrentMap();
  private final Map<Integer, WrappedEntity> entitiesById = Maps.newConcurrentMap();
  private final List<WrappedEntity> entities = Lists.newCopyOnWriteArrayList();
  private final List<WrappedEntity> synchronizedEntities = Lists.newCopyOnWriteArrayList();
  private final Map<Long, Long> remainingPingPacketTimestamps = Maps.newConcurrentMap();
  private final List<Long> latencyDifferenceBalance = Lists.newCopyOnWriteArrayList();
  public long lastCCCInfoMessageSent = 0;
  public boolean sendAsyncMessage = false;
  public boolean eligibleForTransactionTimeout = false;

  // Client Synchronization
  public int latency;
  public long lastKeepAliveDifference;
  public int latencyJitter;
  public short transactionCounter = Short.MIN_VALUE;
  public long transactionNumCounter = 0;
  public long lastReceivedTransactionNum = -1;
  public long lastSynchronization = System.currentTimeMillis();
  public long transactionPacketCounter;
  public long transactionPacketCounterReset;

  public long hardTransactionResponse = 0;

  // Lag identification
  private long lastMovementTimestamps;
  private final List<Long> movementLagSpikeHistory = new ArrayList<>();
  
  public ConnectionMetadata(Player player) {
    this.player = player;
  }

  @DispatchTarget
  public void receiveMovement() {
    long now = System.currentTimeMillis();
    if (this.lastMovementTimestamps != 0) {
      long difference = now - lastMovementTimestamps;
      movementLagSpikeHistory.add(difference);
      if (movementLagSpikeHistory.size() > 3) {
        movementLagSpikeHistory.remove(0);
      }
    }
    this.lastMovementTimestamps = now;
  }

  public double averageMovementPacketTimestamp() {
    return averageOf(movementLagSpikeHistory);
  }

  private double averageOf(List<? extends Number> data) {
    double sum = 0;
    for (Number element : data) {
      sum += element.doubleValue();
    }
    if (sum == 0) {
      return 0;
    }
    return sum / data.size();
  }

  public Map<Short, FeedbackRequest<?>> transactionShortKeyMap() {
    return transactionShortMap;
  }

  public Map<Long, FeedbackRequest<?>> transactionGlobalKeyMap() {
    return transactionGlobalKeyMap;
  }

  public Map<Long, Queue<FeedbackRequest<?>>> transactionAppendMap() {
    return transactionOptionalAppendixMap;
  }

  @Deprecated
  public Map<Integer, WrappedEntity> entitiesById() {
    return entitiesById;
  }

  public Collection<WrappedEntity> entities() {
    return entities;
  }

  public WrappedEntity entityBy(int identifier) {
    return entitiesById.get(identifier);
  }

  public void destroyEntity(int entityId) {
    entitiesById.put(entityId, WrappedEntity.destroyedEntity());
    for (int i = 0; i < entities.size(); i++) {
      if (entities.get(i).entityId() == entityId) {
        entities.set(i, WrappedEntity.destroyedEntity());
      }
    }
  }

  public void enterEntity(WrappedEntity entity) {
    entitiesById.put(entity.entityId(), entity);
    entities.add(entity);
  }

  public List<WrappedEntity> tracedEntities() {
    return synchronizedEntities;
  }

  public Map<Long, Long> pingPackets() {
    return remainingPingPacketTimestamps;
  }

  public List<Long> latencyDifferenceBalance() {
    return latencyDifferenceBalance;
  }
}
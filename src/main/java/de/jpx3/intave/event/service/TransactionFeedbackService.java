package de.jpx3.intave.event.service;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import de.jpx3.intave.IntavePlugin;
import de.jpx3.intave.event.packet.PacketEventSubscriber;
import de.jpx3.intave.event.service.transaction.TFCallback;
import de.jpx3.intave.event.service.transaction.TFRequest;
import de.jpx3.intave.event.service.transaction.TransactionResponseLocker;
import de.jpx3.intave.logging.IntaveLogger;
import de.jpx3.intave.tools.sync.Synchronizer;
import de.jpx3.intave.user.User;
import de.jpx3.intave.user.UserMetaSynchronizeData;
import de.jpx3.intave.user.UserRepository;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import static de.jpx3.intave.event.service.TransactionFeedbackService.TransactionOptions.ENFORCE_SYNCHRONIZATION;
import static de.jpx3.intave.event.service.TransactionFeedbackService.TransactionOptions.OPTIONAL;

public final class TransactionFeedbackService implements PacketEventSubscriber {
  public final static long TRANSACTION_TIMEOUT = 3000;
  public final static long TRANSACTION_TIMEOUT_KICK = 12000;
  public final static short TRANSACTION_MIN_CODE = -32768;
  public final static short TRANSACTION_MAX_CODE = -16370;

  public final static long OPTIONAL_LIMIT = 100;

  private final static ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();

  private final TransactionResponseLocker responseLocker;

  public TransactionFeedbackService(IntavePlugin plugin) {
    plugin.packetSubscriptionLinker().linkSubscriptionsIn(this);
    responseLocker = new TransactionResponseLocker(plugin);
  }

  public <T> void clientSynchronize(Player player, T target, TFCallback<T> callback) {
    clientSynchronize(player, target, callback, 0);
  }

  public <T> void clientSynchronize(Player player, T target, TFCallback<T> callback, int options) {
    if(TransactionOptions.matches(ENFORCE_SYNCHRONIZATION, options) && !Bukkit.isPrimaryThread()) {
      Synchronizer.synchronize(() -> clientSynchronize(player, target, callback, options));
      return;
    }
    if(TransactionOptions.matches(OPTIONAL, options)) {
      if(pendingTransactions(userOf(player)) > OPTIONAL_LIMIT) {
        return;
      }
    }
    Short id = acquireNewId(player, target, callback);
    if (id != null) {
      sendTransactionPacket(player, id);
    }
  }

  private final static Object FALLBACK_OBJECT = new Object();

  private /* synchronized (is already always sync) */ <T> Short acquireNewId(Player player, T obj, TFCallback<T> callback) {
    User user = UserRepository.userOf(player);
    if (user == null || !user.hasOnlinePlayer()) {
      return null;
    }
    UserMetaSynchronizeData synchronizeData = user.meta().synchronizeData();
    short transactionCounter = findAvailableTransactionIdFor(player);//synchronizeData.transactionCounter++;
    if (transactionCounter >= TRANSACTION_MAX_CODE) {
      synchronizeData.transactionCounter = TRANSACTION_MIN_CODE;
    }
    long transactionNumCounter = synchronizeData.transactionNumCounter++;
    if(obj == null) {
      //noinspection unchecked
      obj = (T) FALLBACK_OBJECT;
    }
    TFRequest<T> feedbackEntry = new TFRequest<>(callback, obj, transactionNumCounter);
    synchronizeData.transactionFeedBackMap().put(transactionCounter, feedbackEntry);
    return transactionCounter;
  }

  private synchronized short findAvailableTransactionIdFor(Player player) {
    User user = UserRepository.userOf(player);
    UserMetaSynchronizeData synchronizeData = user.meta().synchronizeData();
    Map<Short, TFRequest<?>> transactionFeedBackMap = synchronizeData.transactionFeedBackMap();
    short counter = TRANSACTION_MIN_CODE;
    while (transactionFeedBackMap.containsKey(counter)) counter++;
    return counter;
  }

  private void sendTransactionPacket(Player receiver, short id) {
    if(!Bukkit.isPrimaryThread()) {
      IntaveLogger.logger().error("Can't perform tick-validation off main thread.");
      IntaveLogger.logger().error("Please check if you sent a packet / performed a bukkit player action asynchronously in the following trace:");
      Thread.dumpStack();
      Synchronizer.synchronize(() -> sendTransactionPacket(receiver, id));
      return;
    }
    PacketContainer transactionPacket = protocolManager.createPacket(PacketType.Play.Server.TRANSACTION);
    transactionPacket.getIntegers().write(0, 0);
    transactionPacket.getShorts().write(0, id);
    transactionPacket.getBooleans().write(0, false);
    try {
      protocolManager.sendServerPacket(receiver, transactionPacket);
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    }
  }

  private static long pendingTransactions(User user) {
    return user.meta().synchronizeData().transactionFeedBackMap().size();
  }

  public User userOf(Player player) {
    return UserRepository.userOf(player);
  }

  public static class TransactionOptions {
    public static int ENFORCE_SYNCHRONIZATION = 1;
    public static int OPTIONAL = 2;
    public static int DONT_ENFORCE_LOCKING = 4;

    public static boolean matches(int option, int options) {
      return (options & option) != 0;
    }
  }
}
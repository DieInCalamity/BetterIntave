package de.jpx3.intave.event.service.transaction;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import de.jpx3.intave.IntavePlugin;
import de.jpx3.intave.event.packet.*;
import de.jpx3.intave.tools.AccessHelper;
import de.jpx3.intave.tools.sync.Synchronizer;
import de.jpx3.intave.user.User;
import de.jpx3.intave.user.UserMetaSynchronizeData;
import de.jpx3.intave.user.UserRepository;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;

import static de.jpx3.intave.event.service.TransactionFeedbackService.*;

public final class TransactionResponseLocker implements PacketEventSubscriber {
  private final IntavePlugin plugin;

  public TransactionResponseLocker(IntavePlugin plugin) {
    this.plugin = plugin;
    plugin.packetSubscriptionLinker().linkSubscriptionsIn(this);
    plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(plugin, this::checkTransactionTimeout, 20 * 2, 20 * 2);
  }

  private void checkTransactionTimeout() {
    for (Player player : Bukkit.getOnlinePlayers()) {
      checkTransactionTimeoutFor(player);
    }
  }

  private void checkTransactionTimeoutFor(Player player) {
    User user = userOf(player);
    if (oldestPendingTransaction(user) > TRANSACTION_TIMEOUT_KICK) {
      Synchronizer.synchronize(() -> {
        System.out.println("[Intave] " + player.getName() + " is not responding to validation packets");
        player.kickPlayer("Timed out");
      });
    }
  }

  @PacketSubscription(
    priority = ListenerPriority.LOWEST,
    packets = {
      @PacketDescriptor(sender = Sender.CLIENT, packetName = "TRANSACTION")
    }
  )
  public void onPacketReceiving(PacketEvent event) {
    Player player = event.getPlayer();
    User user = UserRepository.userOf(player);
    if (user == null) {
      return;
    }
    UserMetaSynchronizeData synchronizeData = user.meta().synchronizeData();
    Map<Short, TFRequest<?>> transactionFeedBackMap = synchronizeData.transactionFeedBackMap();
    Short transactionIdentifier = event.getPacket().getShorts().readSafely(0);
    if (transactionIdentifier <= TRANSACTION_MAX_CODE) {
      TFRequest<?> transactionResponse = transactionFeedBackMap.remove(transactionIdentifier);
      if (transactionResponse == null) {
        return;
      }

      // order verification
      long expected = synchronizeData.lastReceivedTransactionNum + 1;
      if (transactionResponse.num() != expected && !user.justJoined()) {
        Synchronizer.synchronize(() -> {
          System.out.println("[Intave] " + player.getName() + " sent invalid validation response (received " + transactionResponse.num() + ", but expected " + expected + ")");
          player.kickPlayer("Timed out");
        });
      }

      synchronizeData.lastReceivedTransactionNum = transactionResponse.num();
      transactionResponse.callback().success(
        player,
        convertInstanceOfObject(transactionResponse.lock())
      );
      event.setCancelled(true);
    }
  }

  @PacketSubscription(
    priority = ListenerPriority.LOWEST,
    packets = {
      @PacketDescriptor(sender = Sender.CLIENT, packetName = "USE_ENTITY")
    }
  )
  public void cancelAttacksIfTransactionMissing(PacketEvent event) {
    Player player = event.getPlayer();
    User user = UserRepository.userOf(player);
    PacketContainer packet = event.getPacket();
    if (oldestPendingTransaction(user) > TRANSACTION_TIMEOUT) {
      event.setCancelled(true);
    }
  }
//
//  private void nettyThreadDump() {
//    Thread.getAllStackTraces().forEach((thread, stackTraceElements) -> {
//      if(thread.getName().toLowerCase(Locale.ROOT).contains("netty")) {
//        Exception exception = new Exception();
//        System.out.println("[Intave/ThreadDump] Thread " + thread.getName() + " " + thread.getState() + " at execution point");
//        exception.setStackTrace(stackTraceElements);
//        exception.printStackTrace(new PrintStream(System.err) {
//          @Override
//          public void println(String x) {
//            super.println("[Intave/ThreadDump] " + x);
//          }
//        });
//      }
//    });
//  }

  @PacketSubscription(
    priority = ListenerPriority.HIGHEST,
    packets = {
      @PacketDescriptor(sender = Sender.CLIENT, packetName = "USE_ENTITY"),
      @PacketDescriptor(sender = Sender.CLIENT, packetName = "BLOCK_DIG"),
      @PacketDescriptor(sender = Sender.CLIENT, packetName = "BLOCK_PLACE"),
      @PacketDescriptor(sender = Sender.CLIENT, packetName = "USE_ITEM")
    }
  )
  public void on(PacketEvent event) {
    Player player = event.getPlayer();
    User user = UserRepository.userOf(player);
    if (transactionResponseTimeout(user)) {
      event.setCancelled(true);
    }
  }

  private <T> T convertInstanceOfObject(Object o) {
    try {
      //noinspection unchecked
      return (T) o;
    } catch (ClassCastException e) {
      return null;
    }
  }

  private static boolean transactionResponseTimeout(User user) {
    UserMetaSynchronizeData synchronizeData = user.meta().synchronizeData();
    Map<Short, TFRequest<?>> transactionFeedBackMap = synchronizeData.transactionFeedBackMap();
    long duration = 0;
    for (TFRequest<?> value : transactionFeedBackMap.values()) {
      duration = Math.max(duration, value.requested());
    }
    return duration != 0 && AccessHelper.now() - duration > TRANSACTION_TIMEOUT_KICK;
  }

  private static long oldestPendingTransaction(User user) {
    UserMetaSynchronizeData synchronizeData = user.meta().synchronizeData();
    Map<Short, TFRequest<?>> transactionFeedBackMap = synchronizeData.transactionFeedBackMap();
    long duration = AccessHelper.now();
    for (TFRequest<?> value : transactionFeedBackMap.values()) {
      duration = Math.min(duration, value.requested());
    }
    return duration == 0 ? 0 : AccessHelper.now() - duration;
  }

  public User userOf(Player player) {
    return UserRepository.userOf(player);
  }
}

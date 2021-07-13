package de.jpx3.intave.detect.checks.other.inventoryclickanalysis;

import com.comphenix.protocol.events.PacketEvent;
import de.jpx3.intave.IntavePlugin;
import de.jpx3.intave.detect.IntaveMetaCheckPart;
import de.jpx3.intave.detect.checks.other.InventoryClickAnalysis;
import de.jpx3.intave.event.packet.PacketSubscription;
import de.jpx3.intave.tools.AccessHelper;
import de.jpx3.intave.tools.annotate.Native;
import de.jpx3.intave.tools.sync.Synchronizer;
import de.jpx3.intave.user.User;
import de.jpx3.intave.user.UserCustomCheckMeta;
import de.jpx3.intave.user.UserMessageSubscriptions;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import static de.jpx3.intave.event.packet.PacketId.Client.*;

public final class InventoryClickPacketDelayAnalyzer extends IntaveMetaCheckPart<InventoryClickAnalysis, InventoryClickPacketDelayAnalyzer.TimingData> {
  public InventoryClickPacketDelayAnalyzer(InventoryClickAnalysis parentCheck) {
    super(parentCheck, TimingData.class);
  }

  @PacketSubscription(
    packetsIn = {WINDOW_CLICK}
  )
  public void receiveInventoryClick(PacketEvent event) {
    Player player = event.getPlayer();
    User user = userOf(player);
    TimingData meta = metaOf(player);
    long difference = AccessHelper.now() - meta.lastMovementTimestamps;
    double averageMovementPacketTimestamp = user.meta().connectionData().averageMovementPacketTimestamp();

    if (difference < 15 && Math.abs(averageMovementPacketTimestamp - 50) < 10) {
      String message = ChatColor.RED + "[InvAnalysis] " + player.getName() + " is clicking suspiciously on items: "
        + difference + " pd, " + averageMovementPacketTimestamp + " md";
      Synchronizer.synchronize(() -> processSibylDebug(message));
    }
  }

  @Native
  private void processSibylDebug(String message) {
    IntavePlugin plugin = IntavePlugin.singletonInstance();
    for (Player onlinePlayer : UserMessageSubscriptions.sibylReceiver()/*Bukkit.getOnlinePlayers()*/) {
      if (plugin.sibylIntegrationService().isAuthenticated(onlinePlayer)) {
        onlinePlayer.sendMessage(message);
      }
    }
  }

  @PacketSubscription(
    packetsIn = {FLYING, POSITION, LOOK, POSITION_LOOK}
  )
  public void receiveMovement(PacketEvent event) {
    Player player = event.getPlayer();
    metaOf(player).lastMovementTimestamps = AccessHelper.now();
  }

  public static final class TimingData extends UserCustomCheckMeta {
    public int threshold;
    public long lastMovementTimestamps;
  }
}
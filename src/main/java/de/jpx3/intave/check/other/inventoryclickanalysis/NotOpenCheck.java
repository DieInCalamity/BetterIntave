package de.jpx3.intave.check.other.inventoryclickanalysis;

import de.jpx3.intave.IntavePlugin;
import de.jpx3.intave.check.CheckPart;
import de.jpx3.intave.check.other.InventoryClickAnalysis;
import de.jpx3.intave.executor.Synchronizer;
import de.jpx3.intave.module.Modules;
import de.jpx3.intave.module.linker.packet.PacketSubscription;
import de.jpx3.intave.module.violation.Violation;
import de.jpx3.intave.packet.reader.WindowClickReader;
import de.jpx3.intave.packet.reader.WindowClickReader.InventoryClickType;
import de.jpx3.intave.user.User;
import de.jpx3.intave.user.meta.InventoryMetadata;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

import static de.jpx3.intave.module.linker.packet.PacketId.Client.WINDOW_CLICK;

public final class NotOpenCheck extends CheckPart<InventoryClickAnalysis> {
  private final IntavePlugin plugin;

  public NotOpenCheck(InventoryClickAnalysis parentCheck) {
    super(parentCheck);
    plugin = IntavePlugin.singletonInstance();
  }

  @PacketSubscription(
    packetsIn = {
      WINDOW_CLICK
    }
  )
  public void receiveWindowClick(
    User user, WindowClickReader reader,
    Cancellable cancellable
  ) {
    Player player = user.player();
    InventoryMetadata inventory = user.meta().inventory();
    InventoryClickType clickType = reader.clickType();

    player.sendMessage(clickType.name() + "/"+reader.clickedItemTypeIfPossible(player)+" " + inventory.inventoryOpen() + " " + reader.container() + " " + reader.slot() + " " + reader.button());

    boolean isNativeInventoryClick = reader.container() == 0;
    boolean forceInventoryOnClickOpen = user.meta().inventory().forceInventoryOnClickOpen;

    // Do not remove! @Richy
    if (!forceInventoryOnClickOpen) {
      return;
    }

    if (!inventory.inventoryOpen()) {
      if (user.meta().protocol().supportsInventoryAchievementPacket()) {
        Violation violation = Violation.builderFor(InventoryClickAnalysis.class)
          .forPlayer(player)
          .withMessage("clicked in closed inventory")
          .withDetails("slot " + reader.slot() + " in inventory " + reader.container())
          .withVL(5).build();
        Modules.violationProcessor().processViolation(violation);
        Synchronizer.synchronize(player::closeInventory);
        cancellable.setCancelled(true);
      } else if (isNativeInventoryClick) {
        user.meta().inventory().updateInventoryOpenState(true);
      }
    }
  }
}
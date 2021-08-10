package de.jpx3.intave.detect.checks.other.inventoryclickanalysis;

import de.jpx3.intave.IntavePlugin;
import de.jpx3.intave.detect.CheckPart;
import de.jpx3.intave.detect.checks.other.InventoryClickAnalysis;
import de.jpx3.intave.event.bukkit.BukkitEventSubscription;
import de.jpx3.intave.user.User;
import de.jpx3.intave.user.meta.InventoryMetadata;
import de.jpx3.intave.user.meta.MetadataBundle;
import de.jpx3.intave.user.meta.ProtocolMetadata;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

public final class InventoryClickNotOpenCheck extends CheckPart<InventoryClickAnalysis> {
  private final IntavePlugin plugin;

  public InventoryClickNotOpenCheck(InventoryClickAnalysis parentCheck) {
    super(parentCheck);
    plugin = IntavePlugin.singletonInstance();
  }

  @BukkitEventSubscription
  public void receiveWindowClick(InventoryClickEvent event) {
    HumanEntity whoClicked = event.getWhoClicked();
    if (!(whoClicked instanceof Player)) {
      return;
    }
    Player player = ((Player) whoClicked).getPlayer();
    User user = userOf(player);
    MetadataBundle meta = user.meta();
    ProtocolMetadata clientData = meta.protocol();
    InventoryMetadata inventoryData = meta.inventory();

    // This check does only work on 1.8 or below
    if (clientData.combatUpdate() || clientData.clientVersionOlderThanServerVersion()) {
      return;
    }

    boolean inventoryOpen = inventoryData.inventoryOpen();
    int pastInventoryOpen = meta.movement().pastInventoryOpen;

    ClickType click = event.getClick();
    if (click == ClickType.CREATIVE) {
      return;
    }

    // has false positives, no easy fix available currently

//    if (inventoryData.forceInventoryOnClickOpen && !inventoryOpen && pastInventoryOpen > 1) {
//      String message = "insufficient inventory-click (inventory not open)";
//      Violation violation = Violation.builderFor(InventoryClickAnalysis.class)
//        .forPlayer(player).withMessage(message)
//        .withVL(1)
//        .build();
//      plugin.violationProcessor().processViolation(violation);
//      event.setCancelled(true);
//    }
  }
}
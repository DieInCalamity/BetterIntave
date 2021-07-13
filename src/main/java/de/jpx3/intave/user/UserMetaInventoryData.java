package de.jpx3.intave.user;

import de.jpx3.intave.tools.annotate.Relocate;
import de.jpx3.intave.tools.items.InventoryUseItemHelper;
import de.jpx3.intave.tools.items.PlayerEnchantmentHelper;
import de.jpx3.intave.tools.sync.Synchronizer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

@Relocate
public final class UserMetaInventoryData {
  private final Player player;
  private ItemStack heldItem;
  private int handSlot;
  private boolean handActive;

  private boolean foodItem;
  private boolean inventoryOpen;
  public int handActiveTicks, pastHandActiveTicks = 100;
  public int pastItemUsageTransition;
  public int pastHotBarSlotChange;
  public int awaitingSlotSet = -1;
  public boolean forceInventoryOnClickOpen = true;
  public volatile int pastSlotSwitch = 100;

  public UserMetaInventoryData(Player player) {
    this.player = player;
    if (player != null) {
      this.handSlot = player.getInventory().getHeldItemSlot();
    }
    this.heldItem = resolveMaterialInHand();
  }

  private ItemStack resolveMaterialInHand() {
    return player == null ? null : player.getItemInHand();
  }

  public boolean handActive() {
    return handActive;
  }

  public ItemStack heldItem() {
    return player == null ? null : player.getInventory().getItem(handSlot);//heldItem;
  }

  public int handSlot() {
    return handSlot;
  }

  public Material heldItemType() {
    ItemStack heldItem = heldItem();
    return heldItem == null || heldItem.getAmount() == 0 ? Material.AIR : heldItem.getType();
  }

  public boolean inventoryOpen() {
    return inventoryOpen;
  }

  public void deactivateHand() {
    User user = UserRepository.userOf(player);
    UserMetaMovementData movementData = user.meta().movementData();
    ItemStack heldItem = heldItem();
    if (heldItem != null && PlayerEnchantmentHelper.tridentRiptideEnchanted(heldItem)) {
      movementData.pastRiptideSpin = 0;
    }
    this.handActive = false;
    this.pastItemUsageTransition = 0;
    this.handActiveTicks = 0;
  }

  public void activateHand() {
    this.handActive = true;
    this.foodItem = InventoryUseItemHelper.foodItemRegistry().foodConsumable(player, heldItemType());
    this.pastItemUsageTransition = 0;
    this.handActiveTicks = 0;
  }

  public void applySlotSwitch() {
    if (!necessarySlotSwitch(this.handSlot)) {
      return;
    }
    int previousItemSlot = this.handSlot;
    int newItemSlot = this.handSlot + 1;
    if (newItemSlot > 8) {
      newItemSlot = 7;
    }
    int finalNewItemSlot = newItemSlot;
    pastSlotSwitch = 0;
    Synchronizer.synchronize(() -> {
      pastSlotSwitch = 0;
      awaitingSlotSet = previousItemSlot;
      player.getInventory().setHeldItemSlot(finalNewItemSlot);
    });
  }

  private boolean necessarySlotSwitch(int slot) {
    PlayerInventory inventory = player.getInventory();
    ItemStack item = inventory.getItem(slot);
    if (item == null) {
      return false;
    }
    return InventoryUseItemHelper.isUseItem(player, item);
  }

  public void setHeldItemSlot(int slot) {
    this.handSlot = slot;
  }

  public void setHandActive(boolean handActive) {
    this.handActive = handActive;
  }

  public void updateInventoryOpenState(boolean inventoryOpen) {
    User user = UserRepository.userOf(player);
    UserMetaClientData clientData = user.meta().clientData();
    if (!inventoryOpen && clientData.inventoryAchievementPacket()) {
      this.forceInventoryOnClickOpen = true;
    }
    deactivateHand();
    this.inventoryOpen = inventoryOpen;
  }

  public boolean foodItem() {
    return foodItem;
  }
}
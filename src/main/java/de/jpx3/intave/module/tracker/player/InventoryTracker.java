package de.jpx3.intave.module.tracker.player;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import de.jpx3.intave.adapter.MinecraftVersions;
import de.jpx3.intave.adapter.ProtocolLibraryAdapter;
import de.jpx3.intave.block.collision.Collision;
import de.jpx3.intave.block.type.BlockTypeAccess;
import de.jpx3.intave.klass.Lookup;
import de.jpx3.intave.module.Module;
import de.jpx3.intave.module.Modules;
import de.jpx3.intave.module.linker.bukkit.BukkitEventSubscription;
import de.jpx3.intave.module.linker.packet.ListenerPriority;
import de.jpx3.intave.module.linker.packet.PacketId;
import de.jpx3.intave.module.linker.packet.PacketSubscription;
import de.jpx3.intave.packet.PacketSender;
import de.jpx3.intave.packet.converter.BlockPositionConverter;
import de.jpx3.intave.player.ItemProperties;
import de.jpx3.intave.user.User;
import de.jpx3.intave.user.UserRepository;
import de.jpx3.intave.user.meta.InventoryMetadata;
import de.jpx3.intave.user.meta.MetadataBundle;
import de.jpx3.intave.user.meta.MovementMetadata;
import de.jpx3.intave.user.meta.PunishmentMetadata;
import org.bukkit.World;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;

import static de.jpx3.intave.module.linker.packet.PacketId.Client.HELD_ITEM_SLOT;
import static de.jpx3.intave.module.linker.packet.PacketId.Client.UPDATE_SIGN;
import static de.jpx3.intave.module.linker.packet.PacketId.Client.*;
import static de.jpx3.intave.module.linker.packet.PacketId.Server.*;

public final class InventoryTracker extends Module {
  @BukkitEventSubscription
  public void entityFoodChange(FoodLevelChangeEvent event) {
    HumanEntity entity = event.getEntity();
    if (!(entity instanceof Player)) {
      return;
    }
    updatePlayerHandItem((Player) entity, event.getFoodLevel());
  }

  @BukkitEventSubscription
  public void itemConsume(FoodLevelChangeEvent event) {
    if (!(event.getEntity() instanceof Player)) {
      return;
    }
    Player player = (Player) event.getEntity();
    User user = UserRepository.userOf(player);
    InventoryMetadata inventoryData = user.meta().inventory();
//    if (inventoryData.pastSlotSwitch < 5) {
//      event.setCancelled(true);
//    }
    if (event.getFoodLevel() >= 20 && inventoryData.foodItem() && inventoryData.handActive()) {
      inventoryData.deactivateHand();
    }
  }

  @BukkitEventSubscription
  public void receiveInteraction(PlayerInteractEvent event) {
    Player player = event.getPlayer();
    User user = UserRepository.userOf(player);
    InventoryMetadata inventoryData = user.meta().inventory();
    // https://i.imgur.com/O5UBqoJ.png
    // doesn't make much sense?
    if (inventoryData.pastSlotSwitch < 10) {
      event.setCancelled(true);
    }
  }

  @PacketSubscription(
    packetsOut = {
      WINDOW_ITEMS, SET_SLOT
    }
  )
  public void checkOutgoingItems(PacketEvent event) {
    Player player = event.getPlayer();
    User user = UserRepository.userOf(player);
    PacketContainer packet = event.getPacket();
    ItemStack itemStack = packet.getItemModifier().readSafely(0);
    if (itemStack != null) {
      putOnWhitelist(user, itemStack);
    }
    ItemStack[] itemStacks = packet.getItemArrayModifier().readSafely(0);
    if (itemStacks != null && itemStacks.length != 0) {
      for (ItemStack stack : itemStacks) {
        if (stack != null) {
          putOnWhitelist(user, stack);
        }
      }
    }
    List<ItemStack> itemStackList = packet.getItemListModifier().readSafely(0);
    if (itemStackList != null) {
      for (ItemStack stack : itemStackList) {
        if (stack != null) {
          putOnWhitelist(user, stack);
        }
      }
    }
  }

  private void putOnWhitelist(User user, ItemStack stack) {
    InventoryMetadata inventory = user.meta().inventory();
    String name = ownerFromSkull(stack);
    if (name != null) {
      inventory.registerSkullRequest(name);
    }
  }
//
//  private Field profileField;
//  {
//    try {
//      profileField = Lookup.craftBukkitClass("inventory.CraftMetaSkull").getDeclaredField("profile");
//      profileField.setAccessible(true);
//    } catch (NoSuchFieldException exception) {
//      exception.printStackTrace();
//    }
//  }

  private String ownerFromSkull(ItemStack skull) {
    ItemMeta meta = skull.getItemMeta();
    if (meta instanceof SkullMeta) {
      return ownerFromSkullMeta((SkullMeta) meta);
    }
    return null;
  }

  private String ownerFromSkullMeta(SkullMeta meta) {
//    try {
//      GameProfile profile = (GameProfile) profileField.get(meta);
//      System.out.println(profile);
//      return profile.getName();
    return meta.getOwner();
//    } catch (Exception exception) {
//      exception.printStackTrace();
//    }
//    return null;
  }

  @PacketSubscription(
    packetsIn = UPDATE_SIGN
  )
  public void checkSign(PacketEvent event) {
    Player player = event.getPlayer();
    User user = UserRepository.userOf(player);
    PacketContainer packet = event.getPacket();
    WrappedChatComponent[] wrappedChatComponents = packet.getChatComponentArrays().readSafely(0);

    if (wrappedChatComponents != null) {
      for (WrappedChatComponent chatComponent : wrappedChatComponents) {
        if (chatComponent.getJson().length() > 500) {
          event.setCancelled(true);
          user.kick("Too many characters in sign update packet");
          return;
        }
      }
    }
  }

  @PacketSubscription(
    packetsIn = {
      WINDOW_CLICK
    }
  )
  public void windowClickCrashFix(PacketEvent event) {
    Player player = event.getPlayer();
    User user = UserRepository.userOf(player);
    InventoryMetadata inventoryData = user.meta().inventory();
    if (System.currentTimeMillis() - inventoryData.lastWCCReset > 10000) {
      inventoryData.windowClickCounter = 0;
      inventoryData.lastWCCReset = System.currentTimeMillis();
    }

    PacketContainer packet = event.getPacket();
    ItemStack itemStack = packet.getItemModifier().readSafely(0);
    String probableName = ownerFromSkull(itemStack);
    if (probableName != null) {
      if (/*probableName.length() < 3 || */probableName.length() > 128 || !inventoryData.skullWhitelisted(probableName)) {
        // cancel all following
        inventoryData.windowClickCounter = 1000;
        user.kick("Forbidden skull request");
        event.setCancelled(true);
      }
    }
    if (inventoryData.windowClickCounter++ > 500) {
      user.kick("Too many inventory interactions");
      event.setCancelled(true);
    }
  }

  @PacketSubscription(
    priority = ListenerPriority.HIGH,
    packetsOut = {
      RESPAWN
    },
    ignoreCancelled = false
  )
  public void sentRespawn(PacketEvent event) {
    Player player = event.getPlayer();
    User user = UserRepository.userOf(player);
    InventoryMetadata inventoryData = user.meta().inventory();
    inventoryData.updateInventoryOpenState(false);
  }

  private void updatePlayerHandItem(Player player, int foodLevel) {
    User user = UserRepository.userOf(player);
    InventoryMetadata inventoryData = user.meta().inventory();
    if (foodLevel >= 20 && inventoryData.handActive() && inventoryData.foodItem()) {
      if (!ItemProperties.foodConsumable(player, inventoryData.heldItemType())) {
        inventoryData.deactivateHand();
      }
    }
  }

  @PacketSubscription(
    priority = ListenerPriority.HIGH,
    packetsOut = {
      OPEN_WINDOW
    },
    ignoreCancelled = false
  )
  public void sentOpenInventory(PacketEvent event) {
    Player player = event.getPlayer();
    User user = UserRepository.userOf(player);
    InventoryMetadata inventoryData = user.meta().inventory();
    PacketContainer packet = event.getPacket();

    WrappedChatComponent chatComponent = packet.getChatComponents().read(0);
    String json = chatComponent.getJson();
    boolean clientDoesNotSendCloseWindow = json.contains("container.beacon");

    if (!clientDoesNotSendCloseWindow) {
      Modules.feedback()
        .synchronize(player, user, this::openInventory);
      inventoryData.forceInventoryOnClickOpen = true;
    } else {
      inventoryData.forceInventoryOnClickOpen = false;
    }
  }

  @PacketSubscription(
    priority = ListenerPriority.LOW,
    packetsIn = {
      CLIENT_COMMAND
    }
  )
  public void receiveClientCommand(PacketEvent event) {
    Player player = event.getPlayer();
    User user = UserRepository.userOf(player);
    EnumWrappers.ClientCommand clientCommand = event.getPacket().getClientCommands().read(0);
    if (clientCommand == EnumWrappers.ClientCommand.OPEN_INVENTORY_ACHIEVEMENT) {
      openInventory(player, user);
    }
  }

  private void openInventory(Player player, User user) {
    InventoryMetadata inventoryData = user.meta().inventory();
    if (!inNetherPortal(user)) {
      inventoryData.updateInventoryOpenState(true);
    }
  }

  private boolean inNetherPortal(User user) {
    World world = user.player().getWorld();
    MovementMetadata movementData = user.meta().movement();
    return Collision.containsBlockInBB(world, movementData.boundingBox(), BlockTypeAccess.NETHER_PORTAL);
  }

  @PacketSubscription(
    priority = ListenerPriority.HIGH,
    packetsOut = {
      PacketId.Server.CLOSE_WINDOW
    },
    ignoreCancelled = false
  )
  public void sentCloseInventory(PacketEvent event) {
    Player player = event.getPlayer();
    User user = UserRepository.userOf(player);
    Modules.feedback()
      .synchronize(player, user, this::closeInventory);
  }

  @PacketSubscription(
    priority = ListenerPriority.LOW,
    packetsIn = {
      PacketId.Client.CLOSE_WINDOW
    }
  )
  public void receiveCloseWindow(PacketEvent event) {
    Player player = event.getPlayer();
    User user = UserRepository.userOf(player);
    closeInventory(player, user);
  }

  private void closeInventory(Player player, User user) {
    InventoryMetadata inventoryData = user.meta().inventory();
    inventoryData.updateInventoryOpenState(false);
  }

  @PacketSubscription(
    priority = ListenerPriority.LOWEST,
    packetsIn = {
      HELD_ITEM_SLOT
    }
  )
  public void receiveSlotSwitch(PacketEvent event) {
    Player player = event.getPlayer();
    PacketContainer packet = event.getPacket();

    User user = UserRepository.userOf(player);
    InventoryMetadata inventoryData = user.meta().inventory();

    Integer slot = packet.getIntegers().read(0);
    ItemStack item = player.getInventory().getItem(slot);

    boolean handActive = ItemProperties.canItemBeUsed(player, item) && inventoryData.handActive();
    if (handActive) {
      inventoryData.activateHand();
    } else {
      inventoryData.deactivateHand();
    }
    inventoryData.setHeldItemSlot(slot);
    inventoryData.pastHotBarSlotChange = 0;
  }

  @PacketSubscription(
    packetsOut = {
      PacketId.Server.HELD_ITEM_SLOT
    }
  )
  public void sentSlotSwitch(PacketEvent event) {
    Player player = event.getPlayer();
    User user = UserRepository.userOf(player);
    int slot = event.getPacket().getIntegers().read(0);
    Modules.feedback().synchronize(player, slot, (player1, slot1) -> {
      user.meta().inventory().setHeldItemSlot(slot);
    });
  }

//  @PacketSubscription(
//    priority = ListenerPriority.HIGH,
//    packetsOut = {
//      COLLECT
//    }
//  )
//  public void receiveHandUpdate(PacketEvent event) {
//    Player player = event.getPlayer();
//    PacketContainer packet = event.getPacket();
//
//    User user = UserRepository.userOf(player);
//    InventoryMetadata inventoryData = user.meta().inventory();
//    Integer entityID = packet.getIntegers().read(0);
//
//    if (entityID == player.getEntityId()) {
//      // sure this is correct? getItemInHand() might needs to be synchronized
////      ItemStack itemInHand = player.getItemInHand();
////      inventoryData.heldItemType(itemInHand);
//    }
//  }

  @PacketSubscription(
//    priority = ListenerPriority.HIGH,
    priority = ListenerPriority.LOWEST,
    packetsIn = {
      BLOCK_PLACE
    }
  )
  public void receiveBlockPlace(PacketEvent event) {
    Player player = event.getPlayer();
    User user = UserRepository.userOf(player);
    MetadataBundle meta = user.meta();
    InventoryMetadata inventoryData = meta.inventory();
    PunishmentMetadata punishmentData = meta.punishment();

    PacketContainer packet = event.getPacket();
    ItemStack heldItem = inventoryData.heldItem();
    ItemStack offhandItem = inventoryData.offhandItem();

    boolean requestedItemUse = requestedItemUse(packet);
    boolean sword = heldItem != null && heldItem.getType().name().endsWith("_SWORD");

    if (requestedItemUse && sword && System.currentTimeMillis() - punishmentData.timeLastBlockCancel < 5000) {
      event.setCancelled(true);
      return;
    }

    boolean useItem = ItemProperties.canItemBeUsed(player, heldItem) || ItemProperties.canItemBeUsed(player, offhandItem);

    if (requestedItemUse && useItem) {
      inventoryData.activateHand();
    }
  }

  private final boolean NEW_ITEM_REQUEST = ProtocolLibraryAdapter.serverVersion().isAtLeast(MinecraftVersions.VER1_9_0);

  private boolean requestedItemUse(PacketContainer packet) {
    if (NEW_ITEM_REQUEST) {
      return true;
    } else {
      StructureModifier<Integer> integers = packet.getIntegers();
      return integers.read(0) == 255;
    }
  }

  @PacketSubscription(
    priority = ListenerPriority.HIGH,
    packetsIn = {
      BLOCK_DIG
    }
  )
  public void receiveBlockDigging(PacketEvent event) {
    Player player = event.getPlayer();
    User user = UserRepository.userOf(player);
    InventoryMetadata inventoryData = user.meta().inventory();

    PacketContainer packet = event.getPacket();
    EnumWrappers.PlayerDigType digType = packet.getPlayerDigTypes().read(0);

    BlockPosition blockPosition = event.getPacket().getModifier()
      .withType(Lookup.serverClass("BlockPosition"), BlockPositionConverter.threadConverter())
      .read(0);
    if (digType == EnumWrappers.PlayerDigType.RELEASE_USE_ITEM
      && !inventoryData.handActive()
      && packet.getDirections().read(0).equals(EnumWrappers.Direction.DOWN)
      && blockPosition.toVector().length() == 0
    ) {
//      event.setCancelled(true);
      return;
    }

    boolean usedFoodItem = inventoryData.foodItem() && inventoryData.handActive();
    switch (digType) {
      case RELEASE_USE_ITEM:
      case DROP_ALL_ITEMS:
      case DROP_ITEM: {
        inventoryData.deactivateHand();
        break;
      }
    }

    // Fix eating while sprinting bug: https://www.youtube.com/watch?v=5ZHMrVmtdNY
    if (digType == EnumWrappers.PlayerDigType.DROP_ITEM && usedFoodItem) {
      PacketContainer unblockPacket = packet.deepClone();
      unblockPacket.getPlayerDigTypes().write(0, EnumWrappers.PlayerDigType.RELEASE_USE_ITEM);
      user.ignoreNextInboundPacket();
      PacketSender.receiveClientPacketFrom(player, packet);
    }
  }
}
package de.jpx3.intave.module.nayoro;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import de.jpx3.intave.module.linker.packet.ListenerPriority;
import de.jpx3.intave.module.linker.packet.PacketEventSubscriber;
import de.jpx3.intave.module.linker.packet.PacketSubscription;
import de.jpx3.intave.module.nayoro.event.*;
import de.jpx3.intave.module.nayoro.event.sink.EventSink;
import de.jpx3.intave.packet.reader.*;
import de.jpx3.intave.user.User;
import de.jpx3.intave.user.UserRepository;
import de.jpx3.intave.user.meta.MovementMetadata;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static de.jpx3.intave.module.linker.packet.ListenerPriority.LOWEST;
import static de.jpx3.intave.module.linker.packet.PacketId.Client.POSITION;
import static de.jpx3.intave.module.linker.packet.PacketId.Client.VEHICLE_MOVE;
import static de.jpx3.intave.module.linker.packet.PacketId.Client.*;
import static de.jpx3.intave.module.linker.packet.PacketId.Server.*;

public final class PacketEventDispatch implements PacketEventSubscriber {
  private final BiConsumer<? super User, Consumer<EventSink>> reverseSink;

  public PacketEventDispatch(BiConsumer<? super User, Consumer<EventSink>> sinkCallback) {
    this.reverseSink = sinkCallback;
  }

  @PacketSubscription(
    packetsIn = {
      ARM_ANIMATION
    }
  )
  public void onClick(PacketEvent event) {
    Player player = event.getPlayer();
    User user = UserRepository.userOf(player);
    ClickEvent clickEvent = ClickEvent.create();
    reverseSink.accept(user, clickEvent::accept);
  }

  @PacketSubscription(
    priority = LOWEST,
    packetsIn = {
      USE_ENTITY
    }
  )
  public void onUse(PacketEvent event) {
    Player player = event.getPlayer();
    User user = UserRepository.userOf(player);
    PacketContainer packet = event.getPacket();
    EntityUseReader packetReader = PacketReaders.readerOf(packet);
    EnumWrappers.EntityUseAction useAction = packetReader.useAction();
    if (useAction == EnumWrappers.EntityUseAction.ATTACK) {
      int attackerId = player.getEntityId();
      int targetId = packetReader.entityId();
      AttackEvent attackEvent = AttackEvent.create(attackerId, targetId);
      reverseSink.accept(user, attackEvent::accept);
    }
    packetReader.release();
  }

  @PacketSubscription(
    priority = ListenerPriority.HIGH,
    packetsIn = {
      FLYING, LOOK, POSITION, POSITION_LOOK, VEHICLE_MOVE
    }
  )
  public void receiveMovement(PacketEvent event) {
    Player player = event.getPlayer();
    User user = UserRepository.userOf(player);
    MovementMetadata movement = user.meta().movement();
    double x = movement.positionX;
    double y = movement.positionY;
    double z = movement.positionZ;
    double lastX = movement.lastPositionX;
    double lastY = movement.lastPositionY;
    double lastZ = movement.lastPositionZ;
    float yaw = movement.rotationYaw;
    float pitch = movement.rotationPitch;
    float lastYaw = movement.lastRotationYaw;
    float lastPitch = movement.lastRotationPitch;
    int keyStrafe = movement.keyStrafe;
    int keyForward = movement.keyForward;

    boolean collidedHorizontally = movement.collidedHorizontally;
    boolean collidedVertically = movement.collidedVertically || movement.onGround();
    boolean inWater = movement.inWater;
    boolean inLava = movement.inLava();

    boolean inVehicle = movement.isInVehicle();
    boolean sneaking = movement.isSneaking();
    boolean recentlyTeleported = movement.lastTeleport <= 3;
    boolean jumped = movement.physicsJumped;

    int movementFlags = 0;
    movementFlags |= collidedHorizontally ? 1 : 0;
    movementFlags |= collidedVertically ? 2 : 0;
    movementFlags |= inWater ? 4 : 0;
    movementFlags |= inLava ? 8 : 0;
    movementFlags |= inVehicle ? 16 : 0;
    movementFlags |= sneaking ? 32 : 0;
    movementFlags |= recentlyTeleported ? 64 : 0;
    movementFlags |= jumped ? 128 : 0;

    PlayerMoveEvent movementEvent = PlayerMoveEvent.create(
      keyStrafe, keyForward,
      x, y, z,
      yaw, pitch,
      lastX, lastY, lastZ,
      lastYaw, lastPitch,
      movementFlags,
      movement.recordedMoves++ % 200 == 0
    );
    reverseSink.accept(user, movementEvent::accept);
  }

  @PacketSubscription(
    priority = ListenerPriority.HIGH,
    packetsIn = {
      HELD_ITEM_SLOT_IN
    }
  )
  public void receiveHeldItemSlot(PacketEvent event) {
    Player player = event.getPlayer();
    User user = UserRepository.userOf(player);
    int slot = event.getPacket().getIntegers().read(0);
    ItemStack item = player.getInventory().getItem(slot);
    Material type;
    int amount;
    if (item != null) {
      type = item.getType();
      amount = item.getAmount();
    } else {
      type = Material.AIR;
      amount = 0;
    }
    SlotSwitchEvent slotSwitchEvent = SlotSwitchEvent.create(
      slot, type.name(), amount
    );
    reverseSink.accept(user, slotSwitchEvent::accept);
  }

  @PacketSubscription(
    priority = ListenerPriority.HIGH,
    packetsIn = {
      WINDOW_CLICK
    }
  )
  public void receiveWindowClick(
    User user, WindowClickReader reader
  ) {
    WindowClickEvent clickEvent = WindowClickEvent.create(
      reader.container(), reader.slot(), reader.clickType().ordinal(), reader.button(), reader.actionNumber()
    );
    reverseSink.accept(user, clickEvent::accept);
  }

  @PacketSubscription(
    priority = ListenerPriority.HIGH,
    packetsOut = {
      OPEN_WINDOW
    }
  )
  public void sentWindowOpen(
    User user, WindowOpenReader reader
  ) {
    user.meta().connection().nextWindowOpenSlots = reader.slots();
  }

  @PacketSubscription(
    priority = ListenerPriority.HIGH,
    packetsOut = {
      WINDOW_ITEMS, SET_SLOT
    }
  )
  public void sendWindowItems(
    User user, WindowItemReader reader
  ) {
    int container = reader.windowId();
    int slots = user.meta().connection().nextWindowOpenSlots;
    if (slots == 0) {
      slots = 9 * 3;
    }
    if (container != 0) {
      user.meta().connection().nextWindowOpenSlots = 0;
    }

    // inventory
    slots += 4 * 9;

    Map<Integer, ItemStack> items = reader.itemMap();
    Map<Integer, Double> qualities = items.entrySet().stream()
      .collect(
        HashMap::new,
        (map, entry) -> map.put(entry.getKey(), strengthOf(entry.getValue())),
        HashMap::putAll
      );

    WindowItemsEvent itemsEvent = WindowItemsEvent.create(
      container, slots, items, qualities
    );
    reverseSink.accept(user, itemsEvent::accept);
  }

  private double strengthOf(ItemStack item) {
    // if is armor
    if (isArmor(item)) {
      return armorValueOf(item);
    } else if (isSword(item)) {
      return swordValueOf(item);
    }
    return 0.0;
  }

  private double armorValueOf(ItemStack item) {
    EquipmentSlot slot = EquipmentSlot.of(item.getType());
    ArmorMaterial material = ArmorMaterial.of(item.getType());
    if (slot == null || material == null) {
      return 0.0;
    }
    int protection = item.getEnchantmentLevel(Enchantment.PROTECTION_ENVIRONMENTAL);
    return material.damageReductionAmount(slot) * (1.0 - (protection / 25f));
  }

  private double swordValueOf(ItemStack item) {
    int sharpness = item.getEnchantmentLevel(Enchantment.DAMAGE_ALL);
    int baseDamage = 1;
    String typeName = item.getType().name();
    if (typeName.startsWith("NETHERITE")) {
      baseDamage = 8;
    } else if (typeName.startsWith("IRON")) {
      baseDamage = 6;
    } else if (typeName.startsWith("DIAMOND")) {
      baseDamage = 7;
    } else if (typeName.startsWith("GOLD")) {
      baseDamage = 4;
    } else if (typeName.startsWith("STONE")) {
      baseDamage = 5;
    } else if (typeName.startsWith("WOOD")) {
      baseDamage = 4;
    }
    if (sharpness > 0) {
      baseDamage += (int) ((1f + sharpness) * 0.5f);
    }
    return baseDamage;
  }

  private boolean isSword(ItemStack item) {
    return item.getType().name().endsWith("_SWORD");
  }

  private boolean isArmor(ItemStack item) {
    return item.getType().name().endsWith("_HELMET") ||
      item.getType().name().endsWith("_CHESTPLATE") ||
      item.getType().name().endsWith("_LEGGINGS") ||
      item.getType().name().endsWith("_BOOTS");
  }

  private enum ArmorMaterial {
    LEATHER(5, new int[]{1, 2, 3, 1}, 15, 0.0f),
    CHAIN(15, new int[]{1, 4, 5, 2}, 12, 0.0f),
    IRON(15, new int[]{2, 5, 6, 2}, 9, 0.0f),
    GOLD(7, new int[]{1, 3, 5, 2}, 25, 0.0f),
    DIAMOND(33, new int[]{3, 6, 8, 3}, 10, 2.0f),
    TURTLE(25, new int[]{2, 5, 6, 2}, 9, 0.0f),
    NETHERITE(37, new int[]{3, 6, 8, 3}, 15, 3.0f)

    ;

    private final int maxDamageFactor;
    private final int[] damageReductionAmountArray;
    private final int enchantability;
    private final float toughness;

    ArmorMaterial(int maxDamageFactor, int[] damageReductionAmountArray, int enchantability, float toughness) {
      this.maxDamageFactor = maxDamageFactor;
      this.damageReductionAmountArray = damageReductionAmountArray;
      this.enchantability = enchantability;
      this.toughness = toughness;
    }

    private static final Map<Material, ArmorMaterial> cache = new HashMap<>();

    public static ArmorMaterial of(Material type) {
      ArmorMaterial material = cache.get(type);
      if (material == null) {
        try {
          String typeName = type.name();
          String materialName = typeName.substring(0, typeName.indexOf('_'));
          material = ArmorMaterial.valueOf(materialName);
        } catch (Exception ignored) {}
        cache.put(type, material);
      }
      return material;
    }

    public int maxDamageFactor() {
      return maxDamageFactor;
    }

    public int damageReductionAmount(EquipmentSlot slot) {
      return damageReductionAmountArray[slot.index()];
    }
  }

  private enum EquipmentSlot {
    HELMET(3),
    CHESTPLATE(2),
    LEGGINGS(1),
    BOOTS(0)
    ;

    private final int index;

    EquipmentSlot(int index) {
      this.index = index;
    }

    public int index() {
      return index;
    }

    private static final Map<Material, EquipmentSlot> cache = new HashMap<>();

    public static EquipmentSlot of(Material type) {
      EquipmentSlot slot = cache.get(type);
      if (slot == null) {
        try {
          String typeName = type.name();
          String slotName = typeName.substring(typeName.indexOf('_') + 1);
          slot = EquipmentSlot.valueOf(slotName);
        } catch (Exception ignored) {}
        cache.put(type, slot);
      }
      return slot;
    }
  }
}

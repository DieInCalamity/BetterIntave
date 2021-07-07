package de.jpx3.intave.fakeplayer.randomaction.actions;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import de.jpx3.intave.adapter.MinecraftVersions;
import de.jpx3.intave.fakeplayer.FakePlayer;
import de.jpx3.intave.fakeplayer.equipment.Equipment;
import de.jpx3.intave.fakeplayer.equipment.EquipmentContainer;
import de.jpx3.intave.fakeplayer.randomaction.ActionType;
import de.jpx3.intave.fakeplayer.randomaction.RandomAction;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ThreadLocalRandom;

public final class EquipmentHeldItemAction extends RandomAction {
  public EquipmentHeldItemAction(Player player, FakePlayer fakePlayer) {
    super(Probability.MEDIUM, ActionType.HELD_ITEM_CHANGE, player, fakePlayer);
  }

  @Override
  protected void performAction() {
    EquipmentContainer equipment = Equipment.createEquipment();
    Material optionalHeldItem = equipment.heldItem();
    if (optionalHeldItem != null) {
      updateHeldItem(optionalHeldItem);
    }
  }

  private final static boolean HAS_OFF_HAND = MinecraftVersions.VER1_9_0.atOrAbove();

  private void updateHeldItem(Material material) {
    PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.ENTITY_EQUIPMENT);
    packet.getIntegers().write(0, this.fakePlayer.fakePlayerEntityId());
    if (HAS_OFF_HAND) {
      EnumWrappers.ItemSlot hand = ThreadLocalRandom.current().nextInt(0, 10) == 5
        ? EnumWrappers.ItemSlot.OFFHAND
        : EnumWrappers.ItemSlot.MAINHAND;
      packet.getItemSlots().write(0, hand);
    } else {
      packet.getModifier().write(1, 0);
    }
    packet.getItemModifier().write(0, new ItemStack(material));
    try {
      protocolManager.sendServerPacket(this.parentPlayer, packet);
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    }
  }
}
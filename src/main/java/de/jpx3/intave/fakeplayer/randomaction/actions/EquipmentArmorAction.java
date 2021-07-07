package de.jpx3.intave.fakeplayer.randomaction.actions;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import de.jpx3.intave.adapter.MinecraftVersions;
import de.jpx3.intave.fakeplayer.FakePlayer;
import de.jpx3.intave.fakeplayer.equipment.ArmorContext;
import de.jpx3.intave.fakeplayer.equipment.Equipment;
import de.jpx3.intave.fakeplayer.equipment.EquipmentContainer;
import de.jpx3.intave.fakeplayer.randomaction.ActionType;
import de.jpx3.intave.fakeplayer.randomaction.RandomAction;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public final class EquipmentArmorAction extends RandomAction {
  public EquipmentArmorAction(Player player, FakePlayer fakePlayer) {
    super(Probability.LOW, ActionType.EQUIPMENT, player, fakePlayer);
  }

  @Override
  protected void performAction() {
    EquipmentContainer equipment = Equipment.createEquipment();
    List<ArmorContext> armorContextList = equipment.equipment();
    for (ArmorContext armorContext : armorContextList) {
      Material armorMaterial = armorContext.armorMaterial();
      if (armorMaterial == null) {
        continue;
      }
      int slotId = armorContext.type().slotId();
      sendEquipment(slotId - 1, armorMaterial);
    }
  }

  private final static boolean HAS_OFF_HAND = MinecraftVersions.VER1_9_0.atOrAbove();

  private void sendEquipment(
    int slot,
    Material material
  ) {
    PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.ENTITY_EQUIPMENT);
    packet.getIntegers().writeSafely(0, this.fakePlayer.fakePlayerEntityId());
    if (HAS_OFF_HAND) {
      EnumWrappers.ItemSlot itemSlot;
      switch (slot) {
        case 0:
          itemSlot = EnumWrappers.ItemSlot.HEAD;
          break;
        case 1:
          itemSlot = EnumWrappers.ItemSlot.CHEST;
          break;
        case 2:
          itemSlot = EnumWrappers.ItemSlot.LEGS;
          break;
        case 3:
          itemSlot = EnumWrappers.ItemSlot.FEET;
          break;
        default:
          return;
      }
      packet.getItemSlots().write(0, itemSlot);
    } else {
      packet.getModifier().write(1, slot);
    }
    packet.getItemModifier().writeSafely(0, new ItemStack(material));
    try {
      protocolManager.sendServerPacket(this.parentPlayer, packet);
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    }
  }
}
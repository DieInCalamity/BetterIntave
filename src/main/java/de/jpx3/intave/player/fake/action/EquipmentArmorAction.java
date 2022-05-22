package de.jpx3.intave.player.fake.action;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.Pair;
import de.jpx3.intave.adapter.MinecraftVersions;
import de.jpx3.intave.player.fake.FakePlayer;
import de.jpx3.intave.player.fake.equipment.ArmorPiece;
import de.jpx3.intave.player.fake.equipment.ArmorSlot;
import de.jpx3.intave.player.fake.equipment.Equipment;
import de.jpx3.intave.player.fake.equipment.EquipmentFactory;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public final class EquipmentArmorAction extends Action {
  public EquipmentArmorAction(Player player, FakePlayer fakePlayer) {
    super(Probability.LOW, player, fakePlayer);
  }

  @Override
  public void perform() {
    Equipment equipment = EquipmentFactory.randomEquipment();
    List<ArmorPiece> armorPieceList = equipment.armorPieces();
    for (ArmorPiece armorPiece : armorPieceList) {
      Material armorMaterial = armorPiece.material();
      if (armorMaterial != Material.AIR) {
        ArmorSlot type = armorPiece.type();
        sendEquipment(type, armorMaterial);
      }
    }
  }

  private static final boolean HAS_OFF_HAND = MinecraftVersions.VER1_9_0.atOrAbove();

  private void sendEquipment(ArmorSlot slot, Material material) {
    ItemStack itemStack = new ItemStack(material);
    PacketContainer packet = create(PacketType.Play.Server.ENTITY_EQUIPMENT);
    packet.getIntegers().writeSafely(0, this.fakePlayer.identifier());
    if (HAS_OFF_HAND) {
      boolean modernProcessing = MinecraftVersions.VER1_16_0.atOrAbove();
      if (modernProcessing) {
        List<Pair<EnumWrappers.ItemSlot, ItemStack>> list = new ArrayList<>();
        list.add(new Pair<>(slot.itemSlot(), itemStack));
        packet.getSlotStackPairLists().write(0, list);
      } else {
        packet.getItemModifier().writeSafely(0, itemStack);
        packet.getItemSlots().write(0, slot.itemSlot());
      }
    } else {
      packet.getItemModifier().writeSafely(0, itemStack);
      packet.getModifier().write(1, slot.slotId());
    }
    send(packet);
  }
}
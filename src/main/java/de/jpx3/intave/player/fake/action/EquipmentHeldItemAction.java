package de.jpx3.intave.player.fake.action;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.Pair;
import de.jpx3.intave.adapter.MinecraftVersions;
import de.jpx3.intave.player.fake.FakePlayer;
import de.jpx3.intave.player.fake.equipment.Equipment;
import de.jpx3.intave.player.fake.equipment.EquipmentFactory;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public final class EquipmentHeldItemAction extends Action {
  public EquipmentHeldItemAction(Player player, FakePlayer fakePlayer) {
    super(Probability.MEDIUM, player, fakePlayer);
  }

  @Override
  public void perform() {
    Equipment equipment = EquipmentFactory.randomEquipment();
    Material heldItem = equipment.heldItem();
    if (heldItem != Material.AIR) {
      updateHeldItem(heldItem);
    }
  }

  private static final boolean HAS_OFF_HAND = MinecraftVersions.VER1_9_0.atOrAbove();

  private void updateHeldItem(Material material) {
    ItemStack itemStack = new ItemStack(material);
    PacketContainer packet = create(PacketType.Play.Server.ENTITY_EQUIPMENT);
    packet.getIntegers().write(0, this.fakePlayer.identifier());
    if (HAS_OFF_HAND) {
      EnumWrappers.ItemSlot hand = ThreadLocalRandom.current().nextInt(0, 10) == 5
        ? EnumWrappers.ItemSlot.OFFHAND
        : EnumWrappers.ItemSlot.MAINHAND;
      boolean modernProcessing = MinecraftVersions.VER1_16_0.atOrAbove();
      if (modernProcessing) {
        List<Pair<EnumWrappers.ItemSlot, ItemStack>> list = new ArrayList<>();
        list.add(new Pair<>(hand, itemStack));
        packet.getSlotStackPairLists().write(0, list);
      } else {
        packet.getItemModifier().write(0, itemStack);
        packet.getItemSlots().write(0, hand);
      }
    } else {
      packet.getItemModifier().write(0, itemStack);
      packet.getModifier().write(1, 0);
    }
    send(packet);
  }
}
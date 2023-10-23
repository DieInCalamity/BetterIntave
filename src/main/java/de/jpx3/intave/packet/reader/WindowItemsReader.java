package de.jpx3.intave.packet.reader;

import de.jpx3.intave.adapter.MinecraftVersions;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public final class WindowItemsReader extends AbstractPacketReader {
  public int container() {
    return packet().getIntegers().readSafely(0);
  }

  public List<ItemStack> items() {
    if (MinecraftVersions.VER1_12_0.atOrAbove()) {
      return packet().getItemListModifier().readSafely(0);
    } else {
      return Arrays.asList(packet().getItemArrayModifier().readSafely(0));
    }
  }
}

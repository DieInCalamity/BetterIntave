package de.jpx3.intave.block.variant;

import de.jpx3.intave.block.variant.convert.ConversionBridges;
import org.bukkit.Material;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

final class BlockVariantConverter {
  static Map<Integer, BlockVariant> translateVariants(Material type, Map<Integer, Object> indexToNative) {
    if (indexToNative.isEmpty()) {
      return Collections.emptyMap();
    }
    Map<Integer, BlockVariant> indexToVariant = new HashMap<>();
    indexToNative.forEach((key, nativeData) ->
      indexToVariant.put(key, translateVariant(type, nativeData, key))
    );
    return indexToVariant;
  }

  static final BlockVariant EMPTY = new EmptyBlockVariant();

  private static BlockVariant translateVariant(Material type, Object blockData, int variantIndex) {
    Map<Setting<?>, Comparable<?>> settings = ConversionBridges.current().settingsOf(blockData);
    return settings.isEmpty() ? EMPTY : new IndexedBlockVariant(type, settings, variantIndex);
  }
}

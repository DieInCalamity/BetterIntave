package de.jpx3.intave.block.variant;

import de.jpx3.intave.cleanup.ReferenceMap;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

public final class BlockVariantReverseLookup {
  private static final Map<Material, Map<Integer, Set<Integer>>> reverseLookup = ReferenceMap.soft(new HashMap<>());

  public static Set<Integer> variantsOfConfiguration(
    Material material, int uniqueId,
    Function<? super String, ? extends Comparable<?>> settingResolver
  ) {
    return reverseLookup.computeIfAbsent(material, x -> new HashMap<>())
      .computeIfAbsent(uniqueId, x -> performReverseLookup(material, settingResolver));
  }

  private static Set<Integer> performReverseLookup(Material material, Function<? super String, ? extends Comparable<?>> configMatcher) {
    Set<Integer> integers = BlockVariantRegister.variantIdsOf(material);
    integers.removeIf(integer -> {
      BlockVariant config = BlockVariantRegister.variantOf(material, integer);
      for (String propertyName : config.propertyNames()) {
        Object property = config.propertyOf(propertyName);
        Comparable<?> value = configMatcher.apply(propertyName);
        if (value == null) {
          continue;
        }
        if (value.getClass().isEnum()) {
          // enums are translated to strings
          value = ((Enum<?>) value).name();
        }
        if (!Objects.equals(property, value)) {
          return true;
        }
      }
      return false;
    });
    return integers;
  }

  public static Set<Material> cachedMaterials() {
    return reverseLookup.keySet();
  }
}

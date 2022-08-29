package de.jpx3.intave.block.type;

import org.bukkit.Material;

import java.util.EnumSet;
import java.util.Set;
import java.util.function.Predicate;

public final class MaterialSearch {
  public static Material findBy(Predicate<? super Material> predicate) {
    for (Material material : Material.values()) {
      if (predicate.test(material)) {
        return material;
      }
    }
    return null;
  }

  public static Set<Material> filterBy(Predicate<? super Material> predicate) {
    Set<Material> materials = EnumSet.noneOf(Material.class);
    for (Material material : Material.values()) {
      if (predicate.test(material)) {
        materials.add(material);
      }
    }
    return materials;
  }

  public static Set<Material> materialsThatContain(String search) {
    return filterBy(material -> material.name().toLowerCase().contains(search.toLowerCase()));
  }
}

package de.jpx3.intave.block.collision;

import de.jpx3.intave.shade.BoundingBox;
import de.jpx3.intave.user.User;
import org.bukkit.Material;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class CollisionModifiers {
  private final static Map<Material, CollisionModifier> repository = new ConcurrentHashMap<>();

  public static void setup() {
    setup(ScaffoldingCollisionModifier.class);
  }

  private static void setup(Class<? extends CollisionModifier> modifierClass) {
    CollisionModifier modifier;
    try {
      modifier = modifierClass.newInstance();
    } catch (InstantiationException | IllegalAccessException | Error exception) {
      throw new IllegalStateException("Unable to load collision modifier " + modifierClass, exception);
    }
    for (Material value : Material.values()) {
      if (modifier.matches(value)) {
        repository.put(value, modifier);
      }
    }
  }

  public static List<BoundingBox> modified(Material type, User user, BoundingBox userBox, int posX, int posY, int posZ, List<BoundingBox> boxes) {
    return repository.get(type).modify(user, userBox, posX, posY, posZ, boxes);
  }

  public static boolean isModified(Material type) {
    return repository.containsKey(type);
  }
}

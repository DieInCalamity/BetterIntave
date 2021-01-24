package de.jpx3.intave.reflect;

import de.jpx3.intave.patchy.annotate.PatchyAutoTranslation;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;

public final class ReflectiveHandleAccess {
  @PatchyAutoTranslation
  public static Object handleOf(Entity entity) {
    return ((CraftEntity) entity).getHandle();
  }

  @PatchyAutoTranslation
  public static Object handleOf(World world) {
    return ((CraftWorld) world).getHandle();
  }
}
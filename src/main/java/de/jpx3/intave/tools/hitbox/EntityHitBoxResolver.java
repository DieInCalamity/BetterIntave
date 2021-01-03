package de.jpx3.intave.tools.hitbox;

import de.jpx3.intave.reflect.Reflection;
import org.bukkit.entity.Entity;

public final class EntityHitBoxResolver {
  public static HitBoxBoundaries resolveHitBoxOf(Entity entity) {
    return resolveHitBoxOf(Reflection.resolveEntityNMSHandle(entity));
  }

  public static HitBoxBoundaries resolveHitBoxOf(Object nmsEntity) {
    return resolveBoundariesFromNMSEntity(nmsEntity);
  }

  private static HitBoxBoundaries resolveBoundariesFromNMSEntity(Object entity) {
    float width = Reflection.invokeField(entity.getClass(), "width", entity);
    float length = Reflection.invokeField(entity.getClass(), "length", entity);
    return HitBoxBoundaries.from(width, length);
  }
}
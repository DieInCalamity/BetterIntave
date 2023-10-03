package de.jpx3.intave.entity.size;

import org.bukkit.entity.Entity;

public interface HitboxSizeResolver {
  HitboxSize sizeOf(Entity entity);
  HitboxSize sizeOf(Object serverEntity);
  HitboxSize sizeOf(Class<?> entityClass);
}
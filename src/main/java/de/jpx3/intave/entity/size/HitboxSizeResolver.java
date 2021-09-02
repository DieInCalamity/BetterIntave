package de.jpx3.intave.entity.size;

import org.bukkit.entity.Entity;

public interface HitboxSizeResolver {
  HitboxSize hitBoxOf(Entity entity);
  HitboxSize hitBoxOf(Object serverEntity);
}
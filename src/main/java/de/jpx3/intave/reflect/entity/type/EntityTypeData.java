package de.jpx3.intave.reflect.entity.type;

import de.jpx3.intave.reflect.entity.size.HitboxSize;

public final class EntityTypeData {
  private final String entityName;
  private final HitboxSize hitBoxSize;
  private final int entityTypeId;
  private final boolean isLivingEntity;
  public final int creationID;

  public EntityTypeData(String entityName, HitboxSize hitBoxSize, int entityTypeId, boolean isLivingEntity, int creationID) {
    this.entityName = entityName;
    this.hitBoxSize = hitBoxSize;
    this.entityTypeId = entityTypeId;
    this.isLivingEntity = isLivingEntity;
    this.creationID = creationID;
  }

  public boolean isLivingEntity() {
    return isLivingEntity;
  }

  public String name() {
    return entityName;
  }

  public int identifier() {
    return entityTypeId;
  }

  public HitboxSize size() {
    return hitBoxSize;
  }
}
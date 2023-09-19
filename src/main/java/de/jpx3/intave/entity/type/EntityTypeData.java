package de.jpx3.intave.entity.type;

import de.jpx3.intave.entity.size.HitboxSize;

import java.util.Locale;

public final class EntityTypeData {
  private final String entityName;
  private final HitboxSize hitBoxSize;
  private final int entityTypeId;
  private final boolean isLivingEntity;
  public final int creationId;
  private boolean boat;
  private boolean shulker;

  public EntityTypeData(String entityName, HitboxSize hitBoxSize, int entityTypeId, boolean isLivingEntity, int creationId) {
    this.entityName = entityName;
    this.hitBoxSize = hitBoxSize;
    this.entityTypeId = entityTypeId;
    this.isLivingEntity = isLivingEntity;
    this.creationId = creationId;

    String lowercaseName = entityName.toLowerCase(Locale.ROOT);
    switch (lowercaseName) {
      case "boat":
      case "chestboat":
        this.boat = true;
        break;
      case "shulker":
        this.shulker = true;
        break;
    }
  }

  public boolean isLivingEntity() {
    return isLivingEntity;
  }

  public boolean isBoat() {
    return boat;
  }

  public boolean isShulker() {
    return shulker;
  }

  public String name() {
    return entityName;
  }

  public int typeId() {
    return entityTypeId;
  }

  public HitboxSize size() {
    return hitBoxSize;
  }
}
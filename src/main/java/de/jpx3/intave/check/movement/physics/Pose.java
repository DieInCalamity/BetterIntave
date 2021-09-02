package de.jpx3.intave.check.movement.physics;

import com.google.common.collect.ImmutableMap;
import de.jpx3.intave.entity.size.HitboxSize;
import de.jpx3.intave.shade.BoundingBox;
import de.jpx3.intave.user.User;
import de.jpx3.intave.user.meta.MovementMetadata;

import java.util.Map;

public enum Pose {
  STANDING,
  FALL_FLYING,
  SWIMMING,
  SLEEPING,
  CROUCHING,

  ;

  private static final Map<Pose, HitboxSize> SIZE_BY_POSE = ImmutableMap.<Pose, HitboxSize>builder()
    .put(STANDING, HitboxSize.player())
    .put(SLEEPING, HitboxSize.of(0.2f, 0.2f))
    .put(FALL_FLYING, HitboxSize.of(0.6f, 0.6f))
    .put(SWIMMING, HitboxSize.of(0.6f, 0.6f))
    .build();

  public static final Map<Pose, HitboxSize> AT_LEAST_1_8_POSE = ImmutableMap.<Pose, HitboxSize>builder()
    .putAll(SIZE_BY_POSE)
    .put(CROUCHING, HitboxSize.of(0.6f, 1.8f))
    .build();

  public static final Map<Pose, HitboxSize> AT_LEAST_1_9_POSE = ImmutableMap.<Pose, HitboxSize>builder()
    .putAll(SIZE_BY_POSE)
    .put(CROUCHING, HitboxSize.of(0.6f, 1.65f))
    .build();

  public static final Map<Pose, HitboxSize> AT_LEAST_1_13_POSE = ImmutableMap.<Pose, HitboxSize>builder()
    .putAll(SIZE_BY_POSE)
    .put(CROUCHING, HitboxSize.of(0.6f, 1.5f))
    .build();

  public BoundingBox boundingBoxOf(User user) {
    MovementMetadata movementData = user.meta().movement();
    return boundingBoxOf(user, movementData.positionX, movementData.positionY, movementData.positionZ);
  }

  public BoundingBox boundingBoxOf(User user, double x, double y, double z) {
    float halfWidth = width(user) / 2.0F;
    float height = height(user);
    return new BoundingBox(
      x - (double) halfWidth, y, z - (double) halfWidth,
      x + (double) halfWidth, y + (double) height, z + (double) halfWidth
    );
  }

  public float width(User user) {
    return size(user).width();
  }

  public float height(User user) {
    return size(user).length();
  }

  private HitboxSize size(User user) {
    return user.sizeOf(this);
  }
}
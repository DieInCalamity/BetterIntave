package de.jpx3.intave.player.collider.complex;

import de.jpx3.intave.math.MathHelper;
import de.jpx3.intave.share.Motion;

public final class ColliderResult {
  private static final ColliderResult INVALID_SIMULATION = new ColliderResult(
    new Motion(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE), false, false, false, false, false, false, false);

  private final Motion motion;
  private final boolean onGround, collidedHorizontally, collidedVertically;
  private final boolean resetMotionX, resetMotionZ;
  private final boolean step, edgeSneak;

  public ColliderResult(
    Motion motion, boolean onGround,
    boolean collidedHorizontally, boolean collidedVertically,
    boolean resetMotionX, boolean resetMotionZ,
    boolean step, boolean edgeSneak
  ) {
    if (motion == null) {
      throw new IllegalArgumentException("Context cannot be null");
    }
    this.motion = motion;
    this.onGround = onGround;
    this.collidedHorizontally = collidedHorizontally;
    this.collidedVertically = collidedVertically;
    this.resetMotionX = resetMotionX;
    this.resetMotionZ = resetMotionZ;
    this.step = step;
    this.edgeSneak = edgeSneak;
  }

  public double accuracy(Motion motionVector) {
    return MathHelper.distanceOf(motion, motionVector);
  }

  public Motion motion() {
    return motion;
  }

  public boolean onGround() {
    return onGround;
  }

  public boolean collidedHorizontally() {
    return collidedHorizontally;
  }

  public boolean collidedVertically() {
    return collidedVertically;
  }

  public boolean step() {
    return step;
  }

  public boolean resetMotionX() {
    return resetMotionX;
  }

  public boolean resetMotionZ() {
    return resetMotionZ;
  }

  public boolean edgeSneak() {
    return edgeSneak;
  }

  public static ColliderResult invalid() {
    return INVALID_SIMULATION;
  }

  public static ColliderResult untouched(Motion motion) {
    return new ColliderResult(motion, false, false, false, false, false, false, false);
  }
}
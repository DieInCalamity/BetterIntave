package de.jpx3.intave.detect.checks.movement.physics;

import de.jpx3.intave.tools.MathHelper;
import de.jpx3.intave.user.UserMetaMovementData;
import org.bukkit.util.Vector;

public class MotionVector {
  public double motionX;
  public double motionY;
  public double motionZ;

  public MotionVector() {
    this(0.0, 0.0, 0.0);
  }

  public MotionVector(double motionX, double motionY, double motionZ) {
    this.motionX = motionX;
    this.motionY = motionY;
    this.motionZ = motionZ;
  }

  public void reset(double x, double y, double z) {
    this.motionX = x;
    this.motionY = y;
    this.motionZ = z;
  }

  public void resetTo(UserMetaMovementData data) {
    reset(data.physicsMotionX, data.physicsMotionY, data.physicsMotionZ);
  }

  public double length() {
    return MathHelper.hypot3d(motionX, motionY, motionZ);
  }

  public Vector toBukkitVector() {
    return new Vector(this.motionX, this.motionY, this.motionZ);
  }

  @Override
  public String toString() {
    return "MotionVector{" +
      "motionX=" + motionX +
      ", motionY=" + motionY +
      ", motionZ=" + motionZ +
      '}';
  }

  public static MotionVector from(MotionVector context) {
    return new MotionVector(context.motionX, context.motionY, context.motionZ);
  }
}

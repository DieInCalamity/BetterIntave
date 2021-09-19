package de.jpx3.intave.shade;

import de.jpx3.intave.math.MathHelper;
import de.jpx3.intave.user.meta.MovementMetadata;
import org.bukkit.util.Vector;

public final class Motion {
  public double motionX;
  public double motionY;
  public double motionZ;

  public Motion() {
    this(0.0, 0.0, 0.0);
  }

  public Motion(double motionX, double motionY, double motionZ) {
    this.motionX = motionX;
    this.motionY = motionY;
    this.motionZ = motionZ;
  }

  public void reset(double x, double y, double z) {
    this.motionX = x;
    this.motionY = y;
    this.motionZ = z;
  }

  public void resetTo(MovementMetadata data) {
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

  public static Motion from(Motion context) {
    return new Motion(context.motionX, context.motionY, context.motionZ);
  }
}

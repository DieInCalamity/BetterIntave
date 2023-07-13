package de.jpx3.intave.share;

import de.jpx3.intave.klass.Lookup;
import de.jpx3.intave.math.MathHelper;
import de.jpx3.intave.share.link.WrapperConverter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.lang.reflect.InvocationTargetException;

public class NativeVector {
  public static final NativeVector ZERO = new NativeVector(0.0D, 0.0D, 0.0D);
  public final double xCoord, yCoord, zCoord;

  public NativeVector(double x, double y, double z) {
    if (x == -0.0D) {
      x = 0.0D;
    }
    if (y == -0.0D) {
      y = 0.0D;
    }
    if (z == -0.0D) {
      z = 0.0D;
    }
    this.xCoord = x;
    this.yCoord = y;
    this.zCoord = z;
  }

  public Position toPosition() {
    return new Position(xCoord, yCoord, zCoord);
  }

  public Vector convertToBukkitVec() {
    return new Vector(xCoord, yCoord, zCoord);
  }

  public Object convertToNativeVec3() {
    try {
      return Lookup.serverClass("Vec3D")
        .getConstructor(Double.TYPE, Double.TYPE, Double.TYPE)
        .newInstance(xCoord, yCoord, zCoord);
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException exception) {
      throw new IllegalStateException(exception);
    }
  }

  public Motion toMotion() {
    return new Motion(xCoord, yCoord, zCoord);
  }

  public Location toLocation(World world) {
    return new Location(world, xCoord, yCoord, zCoord);
  }

  /**
   * Returns a new vector with the result of the specified vector minus this.
   */
  public NativeVector subtractReverse(NativeVector vec) {
    return new NativeVector(vec.xCoord - this.xCoord, vec.yCoord - this.yCoord, vec.zCoord - this.zCoord);
  }

  /**
   * Normalizes the vector to a length of 1 (except if it is the zero vector)
   */
  public NativeVector normalize() {
    double d0 = ClientMath.sqrt_double(this.xCoord * this.xCoord + this.yCoord * this.yCoord + this.zCoord * this.zCoord);
    return d0 < 1.0E-4D ? new NativeVector(0.0D, 0.0D, 0.0D) : new NativeVector(this.xCoord / d0, this.yCoord / d0, this.zCoord / d0);
  }

  public double dotProduct(NativeVector vec) {
    return this.xCoord * vec.xCoord + this.yCoord * vec.yCoord + this.zCoord * vec.zCoord;
  }

  public double length() {
    return Math.sqrt(xCoord * xCoord + yCoord * yCoord + zCoord * zCoord);
  }

  public NativeVector scale(double factor) {
    return new NativeVector(xCoord * factor, yCoord * factor, zCoord * factor);
  }

  /**
   * Returns a new vector with the result of this vector x the specified vector.
   */
  public NativeVector crossProduct(NativeVector vec) {
    return new NativeVector(this.yCoord * vec.zCoord - this.zCoord * vec.yCoord, this.zCoord * vec.xCoord - this.xCoord * vec.zCoord, this.xCoord * vec.yCoord - this.yCoord * vec.xCoord);
  }

  public NativeVector subtract(NativeVector vec) {
    return this.subtract(vec.xCoord, vec.yCoord, vec.zCoord);
  }

  public NativeVector subtract(double x, double y, double z) {
    return this.addVector(-x, -y, -z);
  }

  public NativeVector add(NativeVector vec) {
    return this.addVector(vec.xCoord, vec.yCoord, vec.zCoord);
  }

  public NativeVector add(double x, double y, double z) {
    return new NativeVector(this.xCoord + x, this.yCoord + y, this.zCoord + z);
  }

  /**
   * Adds the specified x,y,z vector components to this vector and returns the resulting vector. Does not change this
   * vector.
   */
  public NativeVector addVector(double x, double y, double z) {
    return new NativeVector(this.xCoord + x, this.yCoord + y, this.zCoord + z);
  }

  /**
   * Euclidean distance between this and the specified vector, returned as double.
   */
  public double distanceTo(NativeVector vec) {
    double d0 = vec.xCoord - this.xCoord;
    double d1 = vec.yCoord - this.yCoord;
    double d2 = vec.zCoord - this.zCoord;
    return ClientMath.sqrt_double(d0 * d0 + d1 * d1 + d2 * d2);
  }

  public double distanceToBox(BoundingBox boundingBox) {
    double xDist = MathHelper.minmax(0, boundingBox.minX - xCoord, xCoord - boundingBox.maxX);
    double yDist = MathHelper.minmax(0, boundingBox.minY - yCoord, yCoord - boundingBox.maxY);
    double zDist = MathHelper.minmax(0, boundingBox.minZ - zCoord, zCoord - boundingBox.maxZ);
    return distanceTo(new NativeVector(xDist, yDist, zDist));
  }

  public double distanceTo(Vector vector) {
    double d0 = vector.getX() - this.xCoord;
    double d1 = vector.getY() - this.yCoord;
    double d2 = vector.getZ() - this.zCoord;
    return ClientMath.sqrt_double(d0 * d0 + d1 * d1 + d2 * d2);
  }

  /**
   * The square of the Euclidean distance between this and the specified vector.
   */
  public double squareDistanceTo(NativeVector vec) {
    double d0 = vec.xCoord - this.xCoord;
    double d1 = vec.yCoord - this.yCoord;
    double d2 = vec.zCoord - this.zCoord;
    return d0 * d0 + d1 * d1 + d2 * d2;
  }

  /**
   * Returns the length of the vector.
   */
  public double lengthVector() {
    return ClientMath.sqrt_double(this.xCoord * this.xCoord + this.yCoord * this.yCoord + this.zCoord * this.zCoord);
  }

  /**
   * Returns a new vector with x value equal to the second parameter, along the line between this vector and the passed
   * in vector, or null if not possible.
   */
  public NativeVector getIntermediateWithXValue(NativeVector vec, double x) {
    double d0 = vec.xCoord - this.xCoord;
    double d1 = vec.yCoord - this.yCoord;
    double d2 = vec.zCoord - this.zCoord;

    if (d0 * d0 < 1.0000000116860974E-7D) {
      return null;
    } else {
      double d3 = (x - this.xCoord) / d0;
      return d3 >= 0.0D && d3 <= 1.0D ? new NativeVector(this.xCoord + d0 * d3, this.yCoord + d1 * d3, this.zCoord + d2 * d3) : null;
    }
  }

  /**
   * Returns a new vector with y value equal to the second parameter, along the line between this vector and the passed
   * in vector, or null if not possible.
   */
  public NativeVector getIntermediateWithYValue(NativeVector vec, double y) {
    double d0 = vec.xCoord - this.xCoord;
    double d1 = vec.yCoord - this.yCoord;
    double d2 = vec.zCoord - this.zCoord;

    if (d1 * d1 < 1.0000000116860974E-7D) {
      return null;
    } else {
      double d3 = (y - this.yCoord) / d1;
      return d3 >= 0.0D && d3 <= 1.0D ? new NativeVector(this.xCoord + d0 * d3, this.yCoord + d1 * d3, this.zCoord + d2 * d3) : null;
    }
  }

  /**
   * Returns a new vector with z value equal to the second parameter, along the line between this vector and the passed
   * in vector, or null if not possible.
   */
  public NativeVector getIntermediateWithZValue(NativeVector vec, double z) {
    double d0 = vec.xCoord - this.xCoord;
    double d1 = vec.yCoord - this.yCoord;
    double d2 = vec.zCoord - this.zCoord;

    if (d2 * d2 < 1.0000000116860974E-7D) {
      return null;
    } else {
      double d3 = (z - this.zCoord) / d2;
      return d3 >= 0.0D && d3 <= 1.0D ? new NativeVector(this.xCoord + d0 * d3, this.yCoord + d1 * d3, this.zCoord + d2 * d3) : null;
    }
  }

  public static NativeVector fromNative(Object vec3d) {
    return WrapperConverter.vectorFromVec3D(vec3d);
  }

  public String toString() {
    return "(" + this.xCoord + ", " + this.yCoord + ", " + this.zCoord + ")";
  }
}

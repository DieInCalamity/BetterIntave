package de.jpx3.intave.shade;

import com.comphenix.protocol.wrappers.EnumWrappers;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;
import de.jpx3.intave.annotate.KeepEnumInternalNames;

import java.util.Iterator;
import java.util.Map;
import java.util.Random;

@KeepEnumInternalNames
public enum EnumDirection {
  DOWN(0, 1, -1, "down", EnumDirection.AxisDirection.NEGATIVE, EnumDirection.Axis.Y, new NativeVector(0, -1, 0)),
  UP(1, 0, -1, "up", EnumDirection.AxisDirection.POSITIVE, EnumDirection.Axis.Y, new NativeVector(0, 1, 0)),
  NORTH(2, 3, 2, "north", EnumDirection.AxisDirection.NEGATIVE, EnumDirection.Axis.Z, new NativeVector(0, 0, -1)),
  SOUTH(3, 2, 0, "south", EnumDirection.AxisDirection.POSITIVE, EnumDirection.Axis.Z, new NativeVector(0, 0, 1)),
  WEST(4, 5, 1, "west", EnumDirection.AxisDirection.NEGATIVE, EnumDirection.Axis.X, new NativeVector(-1, 0, 0)),
  EAST(5, 4, 3, "east", EnumDirection.AxisDirection.POSITIVE, EnumDirection.Axis.X, new NativeVector(1, 0, 0));

  /** Ordering index for D-U-N-S-W-E */
  private final int index;

  /** Index of the opposite Facing in the VALUES array */
  private final int opposite;

  /** Ordering index for the HORIZONTALS field (S-W-N-E) */
  private final int horizontalIndex;
  private final String name;
  private final EnumDirection.Axis axis;
  private final EnumDirection.AxisDirection axisDirection;

  /** Normalized Vector that points in the direction of this Facing */
  private final NativeVector directionVec;

  /** All facings in D-U-N-S-W-E order */
  private static final EnumDirection[] VALUES = new EnumDirection[6];

  /** All Facings with horizontal axis in order S-W-N-E */
  private static final EnumDirection[] HORIZONTALS = new EnumDirection[4];
  private static final Map<String, EnumDirection> NAME_LOOKUP = Maps.newHashMap();

  EnumDirection(int indexIn, int oppositeIn, int horizontalIndexIn, String nameIn, EnumDirection.AxisDirection axisDirectionIn, EnumDirection.Axis axisIn, NativeVector directionVecIn) {
    this.index = indexIn;
    this.horizontalIndex = horizontalIndexIn;
    this.opposite = oppositeIn;
    this.name = nameIn;
    this.axis = axisIn;
    this.axisDirection = axisDirectionIn;
    this.directionVec = directionVecIn;
  }

  public static EnumDirection getFacingFromAxisDirection(EnumDirection.Axis axisIn, EnumDirection.AxisDirection axisDirectionIn) {
    switch(axisIn) {
      case X:
        return axisDirectionIn == EnumDirection.AxisDirection.POSITIVE ? EAST : WEST;
      case Y:
        return axisDirectionIn == EnumDirection.AxisDirection.POSITIVE ? UP : DOWN;
      case Z:
      default:
        return axisDirectionIn == EnumDirection.AxisDirection.POSITIVE ? SOUTH : NORTH;
    }
  }

  /**
   * Get the Index of this Facing (0-5). The order is D-U-N-S-W-E
   */
  public int getIndex() {
    return this.index;
  }

  /**
   * Get the index of this horizontal facing (0-3). The order is S-W-N-E
   */
  public int getHorizontalIndex() {
    return this.horizontalIndex;
  }

  /**
   * Get the AxisDirection of this Facing.
   */
  public EnumDirection.AxisDirection getAxisDirection() {
    return this.axisDirection;
  }

  /**
   * Get the opposite Facing (e.g. DOWN => UP)
   */
  public EnumDirection getOpposite() {
    return getFront(this.opposite);
  }

  /**
   * Rotate this Facing around the given axis clockwise. If this facing cannot be rotated around the given axis, returns
   * this facing without rotating.
   */
  public EnumDirection rotateAround(EnumDirection.Axis axis) {
    switch (axis) {
      case X:
        if (this != WEST && this != EAST) {
          return this.rotateX();
        }
        return this;
      case Y:
        if (this != UP && this != DOWN) {
          return this.rotateY();
        }
        return this;
      case Z:
        if (this != NORTH && this != SOUTH) {
          return this.rotateZ();
        }
        return this;
      default:
        throw new IllegalStateException("Unable to get CW facing for axis " + axis);
    }
  }

  /**
   * Rotate this Facing around the Y axis clockwise (NORTH => EAST => SOUTH => WEST => NORTH)
   */
  public EnumDirection rotateY() {
    switch (this) {
      case NORTH:
        return EAST;
      case EAST:
        return SOUTH;
      case SOUTH:
        return WEST;
      case WEST:
        return NORTH;
      default:
        throw new IllegalStateException("Unable to get Y-rotated facing of " + this);
    }
  }

  /**
   * Rotate this Facing around the X axis (NORTH => DOWN => SOUTH => UP => NORTH)
   */
  private EnumDirection rotateX() {
    switch (this) {
      case NORTH:
        return DOWN;
      case EAST:
      case WEST:
      default:
        throw new IllegalStateException("Unable to get X-rotated facing of " + this);
      case SOUTH:
        return UP;
      case UP:
        return NORTH;
      case DOWN:
        return SOUTH;
    }
  }

  /**
   * Rotate this Facing around the Z axis (EAST => DOWN => WEST => UP => EAST)
   */
  private EnumDirection rotateZ() {
    switch (this) {
      case EAST:
        return DOWN;
      case SOUTH:
      default:
        throw new IllegalStateException("Unable to get Z-rotated facing of " + this);
      case WEST:
        return UP;
      case UP:
        return EAST;
      case DOWN:
        return WEST;
    }
  }

  /**
   * Rotate this Facing around the Y axis counter-clockwise (NORTH => WEST => SOUTH => EAST => NORTH)
   */
  public EnumDirection rotateYCCW() {
    switch (this) {
      case NORTH:
        return WEST;
      case EAST:
        return NORTH;
      case SOUTH:
        return EAST;
      case WEST:
        return SOUTH;
      default:
        throw new IllegalStateException("Unable to get CCW facing of " + this);
    }
  }

  /**
   * Returns a offset that addresses the block in front of this facing.
   */
  public int getFrontOffsetX() {
    return this.axis == EnumDirection.Axis.X ? this.axisDirection.getOffset() : 0;
  }

  public int getFrontOffsetY() {
    return this.axis == EnumDirection.Axis.Y ? this.axisDirection.getOffset() : 0;
  }

  /**
   * Returns a offset that addresses the block in front of this facing.
   */
  public int getFrontOffsetZ() {
    return this.axis == EnumDirection.Axis.Z ? this.axisDirection.getOffset() : 0;
  }

  /**
   * Same as getName, but does not override the method from Enum.
   */
  public String getName2() {
    return this.name;
  }

  public EnumDirection.Axis getAxis() {
    return this.axis;
  }

  public int getXOffset() {
    return this.axis == Axis.X ? this.axisDirection.getOffset() : 0;
  }

  public int getYOffset() {
    return this.axis == Axis.Y ? this.axisDirection.getOffset() : 0;
  }

  public int getZOffset() {
    return this.axis == Axis.Z ? this.axisDirection.getOffset() : 0;
  }
  /**
   * Get the facing specified by the given name
   */
  public static EnumDirection byName(String name) {
    return name == null ? null : NAME_LOOKUP.get(name.toLowerCase());
  }

  /**
   * Get a Facing by it's index (0-5). The order is D-U-N-S-W-E. Named getFront for legacy reasons.
   */
  public static EnumDirection getFront(int index) {
    return VALUES[ClientMathHelper.abs_int(index % VALUES.length)];
  }

  /**
   * Get a Facing by it's horizontal index (0-3). The order is S-W-N-E.
   */
  public static EnumDirection getHorizontal(int p_176731_0_) {
    return HORIZONTALS[Math.abs(p_176731_0_ % HORIZONTALS.length)];
  }

  /**
   * Get the Facing corresponding to the given angle (0-360). An angle of 0 is SOUTH, an angle of 90 would be WEST.
   */
  public static EnumDirection fromAngle(double angle) {
    return getHorizontal(ClientMathHelper.floor(angle / 90.0D + 0.5D) & 3);
  }

  /**
   * Choose a random Facing using the given Random
   */
  public static EnumDirection random(Random rand) {
    return values()[rand.nextInt(values().length)];
  }

  public static EnumDirection getFacingFromVector(float p_176737_0_, float p_176737_1_, float p_176737_2_) {
    EnumDirection enumfacing = NORTH;
    float f = Float.MIN_VALUE;
    for (EnumDirection enumfacing1 : values()) {
      float f1 = p_176737_0_ * (float) enumfacing1.directionVec.xCoord + p_176737_1_ * (float) enumfacing1.directionVec.yCoord + p_176737_2_ * (float) enumfacing1.directionVec.zCoord;
      if (f1 > f) {
        f = f1;
        enumfacing = enumfacing1;
      }
    }
    return enumfacing;
  }

  public String toString() {
    return this.name;
  }

  public String getName() {
    return this.name;
  }

  // not the best solution, but it should be obfuscation-compatible
  public EnumWrappers.Direction toDirection() {
    return EnumWrappers.Direction.values()[getIndex()];
  }

  public static EnumDirection func_181076_a(EnumDirection.AxisDirection p_181076_0_, EnumDirection.Axis p_181076_1_) {
    for (EnumDirection enumfacing : values()) {
      if (enumfacing.getAxisDirection() == p_181076_0_ && enumfacing.getAxis() == p_181076_1_) {
        return enumfacing;
      }
    }

    throw new IllegalArgumentException("No such direction: " + p_181076_0_ + " " + p_181076_1_);
  }

  /**
   * Get a normalized Vector that points in the direction of this Facing.
   */
  public NativeVector getDirectionVec() {
    return this.directionVec;
  }

  static {
    for (EnumDirection enumfacing : values()) {
      VALUES[enumfacing.index] = enumfacing;
      if (enumfacing.getAxis().isHorizontal()) {
        HORIZONTALS[enumfacing.horizontalIndex] = enumfacing;
      }
      NAME_LOOKUP.put(enumfacing.getName2().toLowerCase(), enumfacing);
    }
  }

  public enum Axis {
    X("x", EnumDirection.Plane.HORIZONTAL) {
      public int getCoordinate(int x, int y, int z) {
        return x;
      }

      public double getCoordinate(double x, double y, double z) {
        return x;
      }
    },
    Y("y", EnumDirection.Plane.VERTICAL) {
      public int getCoordinate(int x, int y, int z) {
        return y;
      }

      public double getCoordinate(double x, double y, double z) {
        return y;
      }
    },
    Z("z", EnumDirection.Plane.HORIZONTAL) {
      public int getCoordinate(int x, int y, int z) {
        return z;
      }

      public double getCoordinate(double x, double y, double z) {
        return z;
      }
    };

    private static final Map<String, EnumDirection.Axis> NAME_LOOKUP = Maps.newHashMap();
    private final String name;
    private final EnumDirection.Plane plane;

    Axis(String name, EnumDirection.Plane plane) {
      this.name = name;
      this.plane = plane;
    }

    public static EnumDirection.Axis byName(String name) {
      return name == null ? null : NAME_LOOKUP.get(name.toLowerCase());
    }

    public String getName2() {
      return this.name;
    }

    public boolean isVertical() {
      return this.plane == EnumDirection.Plane.VERTICAL;
    }

    public boolean isHorizontal() {
      return this.plane == EnumDirection.Plane.HORIZONTAL;
    }

    public String toString() {
      return this.name;
    }

    public boolean apply(EnumDirection p_apply_1_) {
      return p_apply_1_ != null && p_apply_1_.getAxis() == this;
    }

    public EnumDirection.Plane getPlane() {
      return this.plane;
    }

    public String getName() {
      return this.name;
    }

    public abstract int getCoordinate(int x, int y, int z);

    public abstract double getCoordinate(double x, double y, double z);

    static {
      for (EnumDirection.Axis enumfacing$axis : values()) {
        NAME_LOOKUP.put(enumfacing$axis.getName2().toLowerCase(), enumfacing$axis);
      }
    }
  }

  public enum AxisDirection {
    POSITIVE(1, "Towards positive"),
    NEGATIVE(-1, "Towards negative");

    private final int offset;
    private final String description;

    AxisDirection(int offset, String description) {
      this.offset = offset;
      this.description = description;
    }

    public int getOffset() {
      return this.offset;
    }

    public String toString() {
      return this.description;
    }
  }

  public enum Plane implements Predicate<EnumDirection>, Iterable<EnumDirection> {
    HORIZONTAL,
    VERTICAL;

    public EnumDirection[] facings() {
      switch (this) {
        case HORIZONTAL:
          return new EnumDirection[]{EnumDirection.NORTH, EnumDirection.EAST, EnumDirection.SOUTH, EnumDirection.WEST};
        case VERTICAL:
          return new EnumDirection[]{EnumDirection.UP, EnumDirection.DOWN};
        default:
          throw new Error("Someone's been tampering with the universe!");
      }
    }

    public EnumDirection random(Random rand) {
      EnumDirection[] aenumfacing = this.facings();
      return aenumfacing[rand.nextInt(aenumfacing.length)];
    }

    public boolean apply(EnumDirection p_apply_1_) {
      return p_apply_1_ != null && p_apply_1_.getAxis().getPlane() == this;
    }

    public Iterator<EnumDirection> iterator() {
      return Iterators.forArray(this.facings());
    }
  }
}
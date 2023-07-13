package de.jpx3.intave.module.nayoro.event;

import de.jpx3.intave.module.nayoro.Environment;
import de.jpx3.intave.module.nayoro.event.sink.EventSink;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.security.Key;

public final class PlayerMoveEvent extends Event {
  private int flags;
  private KeyCombination keys;
  private double x;
  private double y;
  private double z;
  private float yaw;
  private float pitch;

  private double lastX;
  private double lastY;
  private double lastZ;
  private float lastYaw;
  private float lastPitch;

  public PlayerMoveEvent() {
  }

  private static final double EPSILON = 1.0E-09;

  public PlayerMoveEvent(
    float strafe, float forward,
    double lastX, double lastY, double lastZ,
    float lastYaw, float lastPitch,
    double x, double y, double z,
    float yaw, float pitch,
    boolean forceSave
  ) {
    this.keys = KeyCombination.from(strafe, forward);
    this.x = x;
    this.y = y;
    this.z = z;
    this.yaw = yaw;
    this.pitch = pitch;
    this.lastX = lastX;
    this.lastY = lastY;
    this.lastZ = lastZ;
    this.lastYaw = lastYaw;
    this.lastPitch = lastPitch;
    int flags = 0;
    if (forceSave) {
      flags |= Flag.X | Flag.Y | Flag.Z | Flag.YAW | Flag.PITCH;
    } else {
      if (Math.abs(x - lastX) >= EPSILON) {
        flags |= Flag.X;
      }
      if (Math.abs(y - lastY) >= EPSILON) {
        flags |= Flag.Y;
      }
      if (Math.abs(z - lastZ) >= EPSILON) {
        flags |= Flag.Z;
      }
      if (Math.abs(yaw - lastYaw) >= EPSILON) {
        flags |= Flag.YAW;
      }
      if (Math.abs(pitch - lastPitch) >= EPSILON) {
        flags |= Flag.PITCH;
      }
    }
    this.flags = flags;
  }

  @Override
  public void serialize(Environment environment, DataOutput out) throws IOException {
    out.writeByte(flags);
    conditionalWriteDouble(out, x, Flag.X);
    conditionalWriteDouble(out, y, Flag.Y);
    conditionalWriteDouble(out, z, Flag.Z);
    conditionalWriteFloat(out, yaw, Flag.YAW);
    conditionalWriteFloat(out, pitch, Flag.PITCH);
    keys.write(out);
  }

  private void conditionalWriteDouble(DataOutput out, double value, int flag) throws IOException {
    if ((flags & flag) != 0) {
      out.writeDouble(value);
    }
  }

  private void conditionalWriteFloat(DataOutput out, float value, int flag) throws IOException {
    if ((flags & flag) != 0) {
      out.writeFloat(value);
    }
  }

  @Override
  public void deserialize(Environment environment, DataInput in) throws IOException {
    flags = in.readByte();
    x = conditionalReadDouble(in, Flag.X);
    y = conditionalReadDouble(in, Flag.Y);
    z = conditionalReadDouble(in, Flag.Z);
    yaw = conditionalReadFloat(in, Flag.YAW);
    pitch = conditionalReadFloat(in, Flag.PITCH);
    keys = KeyCombination.read(in);
  }

  private double conditionalReadDouble(DataInput in, int flag) throws IOException {
    if ((flags & flag) != 0) {
      return in.readDouble();
    } else {
      return 0;
    }
  }

  private float conditionalReadFloat(DataInput in, int flag) throws IOException {
    if ((flags & flag) != 0) {
      return in.readFloat();
    } else {
      return 0;
    }
  }

  public double x() {
    return x;
  }

  public boolean applyX() {
    return (flags & Flag.X) != 0;
  }

  public void setX(double x) {
    this.x = x;
  }

  public double y() {
    return y;
  }

  public boolean applyY() {
    return (flags & Flag.Y) != 0;
  }

  public void setY(double y) {
    this.y = y;
  }

  public double z() {
    return z;
  }

  public boolean applyZ() {
    return (flags & Flag.Z) != 0;
  }

  public void setZ(double z) {
    this.z = z;
  }

  public float yaw() {
    return yaw;
  }

  public boolean applyYaw() {
    return (flags & Flag.YAW) != 0;
  }

  public void setYaw(float yaw) {
    this.yaw = yaw;
  }

  public float pitch() {
    return pitch;
  }

  public boolean applyPitch() {
    return (flags & Flag.PITCH) != 0;
  }

  public void setPitch(float pitch) {
    this.pitch = pitch;
  }

  public double lastX() {
    return lastX;
  }

  public double lastY() {
    return lastY;
  }

  public double lastZ() {
    return lastZ;
  }

  public float lastYaw() {
    return lastYaw;
  }

  public float lastPitch() {
    return lastPitch;
  }

  public void setLastX(double lastX) {
    this.lastX = lastX;
  }

  public void setLastY(double lastY) {
    this.lastY = lastY;
  }

  public void setLastZ(double lastZ) {
    this.lastZ = lastZ;
  }

  public void setLastYaw(float lastYaw) {
    this.lastYaw = lastYaw;
  }

  public void setLastPitch(float lastPitch) {
    this.lastPitch = lastPitch;
  }

  @Override
  public void accept(EventSink sink) {
    sink.visit(this);
  }

  public static PlayerMoveEvent create(
    float strafe, float forward,
    double lastX, double lastY, double lastZ,
    float lastYaw, float lastPitch,
    double x, double y, double z,
    float yaw, float pitch,
    boolean forceSave
  ) {
    return new PlayerMoveEvent(
      strafe, forward,
      lastX, lastY, lastZ,
      lastYaw, lastPitch,
      x, y, z,
      yaw, pitch,
      forceSave
    );
  }

  private enum KeyCombination {
    NONE,
    FORWARD,
    BACKWARD,
    LEFT,
    RIGHT,
    FORWARD_LEFT,
    FORWARD_RIGHT,
    BACKWARD_LEFT,
    BACKWARD_RIGHT;

    public static KeyCombination from(float strafe, float forward) {
      if (forward > 0) {
        if (strafe > 0) {
          return FORWARD_RIGHT;
        } else if (strafe < 0) {
          return FORWARD_LEFT;
        } else {
          return FORWARD;
        }
      } else if (forward < 0) {
        if (strafe > 0) {
          return BACKWARD_RIGHT;
        } else if (strafe < 0) {
          return BACKWARD_LEFT;
        } else {
          return BACKWARD;
        }
      } else {
        if (strafe > 0) {
          return RIGHT;
        } else if (strafe < 0) {
          return LEFT;
        } else {
          return NONE;
        }
      }
    }

    public static KeyCombination read(DataInput in) {
      try {
        return values()[in.readByte()];
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    public void write(DataOutput out) {
      try {
        out.writeByte(ordinal());
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

  private static class Flag {
    public static int X = 1;
    public static int Y = 1 << 1;
    public static int Z = 1 << 2;
    public static int YAW = 1 << 3;
    public static int PITCH = 1 << 4;
  }
}

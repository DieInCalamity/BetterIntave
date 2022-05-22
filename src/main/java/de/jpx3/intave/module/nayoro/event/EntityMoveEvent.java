package de.jpx3.intave.module.nayoro.event;

import de.jpx3.intave.module.nayoro.Environment;
import de.jpx3.intave.module.nayoro.event.sink.EventSink;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public final class EntityMoveEvent extends Event {
  private int entityId;
  private int flags;
  private double x;
  private double y;
  private double z;
  private float yaw;
  private float pitch;
  private boolean inSight;

  public EntityMoveEvent() {
  }

  public EntityMoveEvent(int entityId, double x, double y, double z, float yaw, float pitch) {
    this.entityId = entityId;
    this.flags = -1;
    this.x = x;
    this.y = y;
    this.z = z;
    this.yaw = yaw;
    this.pitch = pitch;
  }

  private static final double EPSILON = 1.0E-09;

  public EntityMoveEvent(
    int entity,
    double x, double y, double z,
    double lastX, double lastY, double lastZ,
    float yaw, float pitch,
    float lastYaw, float lastPitch
  ) {
    this.entityId = entity;
    if (Math.abs(x - lastX) > EPSILON) {
      this.flags |= Flag.X;
    }
    if (Math.abs(y - lastY) > EPSILON) {
      this.flags |= Flag.Y;
    }
    if (Math.abs(z - lastZ) > EPSILON) {
      this.flags |= Flag.Z;
    }
    if (Math.abs(yaw - lastYaw) > EPSILON) {
      this.flags |= Flag.YAW;
    }
    if (Math.abs(pitch - lastPitch) > EPSILON) {
      this.flags |= Flag.PITCH;
    }
    this.x = x;
    this.y = y;
    this.z = z;
    this.yaw = yaw;
    this.pitch = pitch;
  }

  @Override
  public void serialize(Environment environment, DataOutput out) throws IOException {
    out.writeInt(entityId);
    out.writeInt(flags);
    conditionalWriteDouble(out, x, Flag.X);
    conditionalWriteDouble(out, y, Flag.Y);
    conditionalWriteDouble(out, z, Flag.Z);
    conditionalWriteFloat(out, yaw, Flag.YAW);
    conditionalWriteFloat(out, pitch, Flag.PITCH);
    out.writeBoolean(inSight);
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
    entityId = in.readInt();
    flags = in.readInt();
    x = conditionalReadDouble(in, Flag.X);
    y = conditionalReadDouble(in, Flag.Y);
    z = conditionalReadDouble(in, Flag.Z);
    yaw = conditionalReadFloat(in, Flag.YAW);
    pitch = conditionalReadFloat(in, Flag.PITCH);
    inSight = in.readBoolean();
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

  public int entityId() {
    return entityId;
  }

  public double x() {
    return x;
  }

  public double y() {
    return y;
  }

  public double z() {
    return z;
  }

  public float yaw() {
    return yaw;
  }

  public float pitch() {
    return pitch;
  }

  public boolean applyX() {
    return (flags & Flag.X) != 0;
  }

  public boolean applyY() {
    return (flags & Flag.Y) != 0;
  }

  public boolean applyZ() {
    return (flags & Flag.Z) != 0;
  }

  public boolean applyYaw() {
    return (flags & Flag.YAW) != 0;
  }

  public boolean applyPitch() {
    return (flags & Flag.PITCH) != 0;
  }

  public boolean inSight() {
    return inSight;
  }

  @Override
  public void accept(EventSink sink) {
    sink.visit(this);
  }

  @Override
  public String toString() {
    return "EntityMoveEvent(" + entityId + ", " + x + ", " + y + ", " + z + ", " + yaw + ", " + pitch + ", " + inSight + ")";
  }

  private static class Flag {
    public static int X = 1;
    public static int Y = 1 << 1;
    public static int Z = 1 << 2;
    public static int YAW = 1 << 3;
    public static int PITCH = 1 << 4;
  }
}

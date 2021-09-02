package de.jpx3.intave.entity.size;

public final class HitboxSize {
  private final float width;
  private final float length;

  private HitboxSize(float width, float length) {
    this.width = width;
    this.length = length;
  }

  public static HitboxSize of(float width, float length) {
    return new HitboxSize(width, length);
  }

  public static HitboxSize zero() {
    return new HitboxSize(0, 0);
  }

  public static HitboxSize player() {
    return new HitboxSize(0.6f, 1.8f);
  }

  public float width() {
    return width;
  }

  public float length() {
    return length;
  }

  @Override
  public String toString() {
    return "HitBoxBoundaries{" +
      "width=" + width +
      ", length=" + length +
      '}';
  }
}
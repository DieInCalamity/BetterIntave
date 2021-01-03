package de.jpx3.intave.tools.hitbox;

public final class HitBoxBoundaries {
  private final float width;
  private final float length;

  private HitBoxBoundaries(float width, float length) {
    this.width = width;
    this.length = length;
  }

  public static HitBoxBoundaries from(float width, float length) {
    return new HitBoxBoundaries(width, length);
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
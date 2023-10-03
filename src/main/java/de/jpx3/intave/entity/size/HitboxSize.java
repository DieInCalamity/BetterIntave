package de.jpx3.intave.entity.size;

public final class HitboxSize {
  private final float width;
  private final float height;

  private HitboxSize(float width, float height) {
    this.width = width;
    this.height = height;
  }

  public static HitboxSize of(float width, float height) {
    return new HitboxSize(width, height);
  }

  public static HitboxSize zero() {
    return new HitboxSize(0, 0);
  }

  public static HitboxSize playerDefault() {
    return new HitboxSize(0.6f, 1.8f);
  }

  public float width() {
    return width;
  }

  public float height() {
    return height;
  }
}
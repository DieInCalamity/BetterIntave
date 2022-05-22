package de.jpx3.intave.block.physics;

import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import static org.bukkit.Material.*;

public final class BlockProperties {
  private static final Property DEFAULT_PROPERTY = Property.builderFor(AIR).build();
  private static final Map<Material, Property> registry = new HashMap<>();

  public static void setup() {
    Property.builderFor(ICE).slipperiness(0.98f).build().trySave();
    Property.builderFor(SLIME_BLOCK).slipperiness(0.8f).build().trySave();
    Property.builderFor(PACKED_ICE).slipperiness(0.98f).build().trySave();
    Property.builderFor("FROSTED_ICE").slipperiness(0.98F).build().trySave();
    Property.builderFor("BLUE_ICE").slipperiness(0.989F).build().trySave();
    Property.builderFor(LADDER).climbable().build().trySave();
    Property.builderFor(VINE).climbable().build().trySave();
    Property.builderFor("SCAFFOLDING").climbable().build().trySave();
    Property.builderFor("WEEPING_VINES").climbable().build().trySave();
    Property.builderFor("WEEPING_VINES_PLANT").climbable().build().trySave();
    Property.builderFor("TWISTING_VINES").climbable().build().trySave();
    Property.builderFor("TWISTING_VINES_PLANT").climbable().build().trySave();
    Property.builderFor("CAVE_VINES_PLANT").climbable().build().trySave();
    Property.builderFor(SOUL_SAND).speedFactor(0.4f).soulSpeedAffected().build().trySave();
    Property.builderFor("SOUL_SOIL").soulSpeedAffected().build().trySave();
    Property.builderFor("HONEY_BLOCK").jumpFactor(0.5f).speedFactor(0.4f).build().trySave();
  }

  private static void append(Property property, Material material) {
    registry.put(material, property);
  }

  public static Property of(Material material) {
    return registry.getOrDefault(material, DEFAULT_PROPERTY);
  }

  public static final class Property {
    private final Material material;
    private final float slipperiness;
    private final float jumpFactor;
    private final float speedFactor;
    private final boolean climbable;
    private final boolean soulSpeedAffected;

    public Property(
      Material material,
      float slipperiness,
      float jumpFactor,
      float speedFactor,
      boolean climbable,
      boolean soulSpeedAffected
    ) {
      this.material = material;
      this.slipperiness = slipperiness;
      this.jumpFactor = jumpFactor;
      this.speedFactor = speedFactor;
      this.climbable = climbable;
      this.soulSpeedAffected = soulSpeedAffected;
    }

    public void trySave() {
      ifPresent(BlockProperties::append);
    }

    public void ifPresent(BiConsumer<Property, Material> consumer) {
      if (this.material != null) {
        consumer.accept(this, this.material);
      }
    }

    public static Builder builderFor(Material material) {
      return new Builder(material);
    }

    public static Builder builderFor(String material) {
      return new Builder(getMaterial(material));
    }

    public float slipperiness() {
      return slipperiness;
    }

    public float jumpFactor() {
      return jumpFactor;
    }

    public float speedFactor() {
      return speedFactor;
    }

    public boolean climbable() {
      return climbable;
    }

    public boolean soulSpeedAffected() {
      return soulSpeedAffected;
    }

    public static final class Builder {
      private final Material material;
      private float slipperiness = 0.6f;
      private float jumpFactor = 1.0f;
      private float speedFactor = 1.0f;
      private boolean climbable = false;
      private boolean soulSpeedAffected = false;

      public Builder(Material material) {
        this.material = material;
      }

      public Builder slipperiness(float slipperiness) {
        this.slipperiness = slipperiness;
        return this;
      }

      public Builder jumpFactor(float jumpFactor) {
        this.jumpFactor = jumpFactor;
        return this;
      }

      public Builder speedFactor(float speedFactor) {
        this.speedFactor = speedFactor;
        return this;
      }

      public Builder climbable() {
        this.climbable = true;
        return this;
      }

      public Builder soulSpeedAffected() {
        this.soulSpeedAffected = true;
        return this;
      }

      public Property build() {
        return new Property(material, slipperiness, jumpFactor, speedFactor, climbable, soulSpeedAffected);
      }
    }
  }
}
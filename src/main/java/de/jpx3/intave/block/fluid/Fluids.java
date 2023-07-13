package de.jpx3.intave.block.fluid;

import de.jpx3.intave.IntavePlugin;
import de.jpx3.intave.access.IntaveInternalException;
import de.jpx3.intave.klass.rewrite.PatchyLoadingInjector;
import de.jpx3.intave.share.BoundingBox;
import de.jpx3.intave.share.ClientMath;
import de.jpx3.intave.user.User;
import org.bukkit.Location;

import static de.jpx3.intave.adapter.MinecraftVersions.*;

public final class Fluids {
  private static FluidResolver engine;

  public static void setup() {
    String className;

    if (VER1_20.atOrAbove()) {
      className = "de.jpx3.intave.block.fluid.v20FluidResolver";
    } else if (VER1_18_2.atOrAbove()) {
      className = "de.jpx3.intave.block.fluid.v18b2FluidResolver";
    } else if (VER1_16_0.atOrAbove()) {
      className = "de.jpx3.intave.block.fluid.v16FluidResolver";
    } else if (VER1_14_0.atOrAbove()) {
      className = "de.jpx3.intave.block.fluid.v14FluidResolver";
    } else if (VER1_13_0.atOrAbove()) {
      className = "de.jpx3.intave.block.fluid.v13FluidResolver";
    } else {
      className = "de.jpx3.intave.block.fluid.v12FluidResolver";
    }
    PatchyLoadingInjector.loadUnloadedClassPatched(IntavePlugin.class.getClassLoader(), className);
    try {
      engine = (FluidResolver) Class.forName(className).newInstance();
//      engine = new DynamicFluidResolver();
    } catch (Exception exception) {
      throw new IntaveInternalException(exception);
    }
  }

  public static boolean handleFluidAcceleration(User user, BoundingBox boundingBox) {
    return engine != null && engine.handleFluidAcceleration(user, boundingBox);
  }

  public static Fluid fluidAt(User user, int x, int y, int z) {
    return engine.fluidAt(user, x, y, z);
  }

  public static Fluid fluidAt(User user, Location location) {
    return fluidAt(user, location.getX(), location.getY(), location.getZ());
  }

  public static Fluid fluidAt(User user, double x, double y, double z) {
    return engine.fluidAt(user, ClientMath.floor(x), ClientMath.floor(y), ClientMath.floor(z));
  }

  public static boolean fluidStateEmpty(User user, double x, double y, double z) {
    return engine != null && fluidAt(user, x, y, z).isEmpty();
  }
}

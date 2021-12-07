package de.jpx3.intave.block.fluid;

import de.jpx3.intave.IntavePlugin;
import de.jpx3.intave.access.IntaveInternalException;
import de.jpx3.intave.adapter.MinecraftVersions;
import de.jpx3.intave.klass.rewrite.PatchyLoadingInjector;
import de.jpx3.intave.shade.BoundingBox;
import de.jpx3.intave.shade.ClientMathHelper;
import de.jpx3.intave.user.User;
import org.bukkit.Location;

public final class Fluids {
  private static FluidResolver engine;

  public static void setup() {
    String className;
    if (MinecraftVersions.VER1_16_0.atOrAbove()) {
      className = "de.jpx3.intave.block.fluid.v16FluidResolver";
    } else if (MinecraftVersions.VER1_14_0.atOrAbove()) {
      className = "de.jpx3.intave.block.fluid.v14FluidResolver";
    } else if (MinecraftVersions.VER1_13_0.atOrAbove()) {
      className = "de.jpx3.intave.block.fluid.v13FluidResolver";
    } else {
      className = "de.jpx3.intave.block.fluid.v12FluidResolver";
    }
    PatchyLoadingInjector.loadUnloadedClassPatched(IntavePlugin.class.getClassLoader(), className);
    try {
      engine = (FluidResolver) Class.forName(className).newInstance();
    } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
      throw new IntaveInternalException(e);
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
    return engine.fluidAt(user, ClientMathHelper.floor(x), ClientMathHelper.floor(y), ClientMathHelper.floor(z));
  }

  public static boolean fluidStateEmpty(User user, double x, double y, double z) {
    return engine != null && fluidAt(user, x, y, z).isEmpty();
  }
}

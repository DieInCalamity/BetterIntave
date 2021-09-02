package de.jpx3.intave.world.border;

import de.jpx3.intave.adapter.MinecraftVersions;
import de.jpx3.intave.clazz.rewrite.PatchyLoadingInjector;
import org.bukkit.Location;
import org.bukkit.World;

public final class WorldBorders {
  private static WorldBorderAccess worldBorderAccess = new BukkitWorldBorderAccess();

  public static void setup() {
    if (MinecraftVersions.VER1_13_0.atOrAbove()) {
      worldBorderAccess = new BukkitWorldBorderAccess();
    } else {
      ClassLoader classLoader = WorldBorders.class.getClassLoader();
      PatchyLoadingInjector.loadUnloadedClassPatched(classLoader, "de.jpx3.intave.world.border.CarefulWorldBorderAccess");
      worldBorderAccess = new CarefulWorldBorderAccess();
    }
    worldBorderAccess = new CachedForwardingWorldBorderAccess(worldBorderAccess);
  }

  public static double sizeOfWorldBorderIn(World world) {
    return worldBorderAccess.sizeOf(world);
  }

  public static Location centerOfWorldBorderIn(World world) {
    return worldBorderAccess.centerOf(world);
  }
}

package de.jpx3.intave.tools;

import org.bukkit.Bukkit;
import org.bukkit.Server;

import java.lang.reflect.Field;

public final class TpsResolver {
  private static double[] tpsAccess;

  public static void setup() {
    try {
      Server server = Bukkit.getServer();
      Field consoleField = server.getClass().getDeclaredField("console");
      consoleField.setAccessible(true);
      Object minecraftServer = consoleField.get(server);
      Field recentTps = minecraftServer.getClass().getSuperclass().getDeclaredField("recentTps");
      recentTps.setAccessible(true);
      tpsAccess = (double[]) recentTps.get(minecraftServer);
    } catch (IllegalAccessException | NoSuchFieldException exception) {
      tpsAccess = new double[]{-1, -1, -1};
      exception.printStackTrace();
    }
  }

  public static double[] recentTickAverage() {
    return tpsAccess;
  }

  public static String stringFormattedTick() {
    return MathHelper.formatDouble(tpsAccess[1], 5);
  }
}

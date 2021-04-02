package de.jpx3.intave.adapter.viaversion;

import org.bukkit.entity.Player;

import java.lang.reflect.Method;

public final class ViaVersion2Access implements ViaVersionAccess {
  private static boolean hasViaVersion = false;
  private static boolean checkedViaVersion = false;

  private static Object viaVersionInstance;
  private static Method getPlayerVersionMethod;

  @Override
  public void setup() {
    try {
      Class<?> viaVersion = Class.forName("us.myles.ViaVersion.api.ViaVersion");
      viaVersionInstance = viaVersion.getMethod("getInstance").invoke(null);
      getPlayerVersionMethod = viaVersionInstance.getClass().getMethod("getPlayerVersion", Player.class);
    } catch (Exception exception) {
      throw new IllegalStateException("Invalid ViaVersion linkage", exception);
    }
  }

  @Override
  public int protocolVersionOf(Player player) {
    if (!available()) {
      return -1;
    }
    try {
      return (int) getPlayerVersionMethod.invoke(viaVersionInstance, player);
    } catch (Exception exception) {
      throw new IllegalStateException("Unable to resolve player version", exception);
    }
  }

  @Override
  public boolean ignoreBlocking(Player player) {
    return false;
  }

  @Override
  public boolean available() {
    if (!checkedViaVersion) {
      checkedViaVersion = true;
      try {
        Class.forName("us.myles.ViaVersion.api.ViaVersion");
        hasViaVersion = true;
      } catch (ClassNotFoundException e) {
        hasViaVersion = false;
      }
    }
    return hasViaVersion;
  }
}

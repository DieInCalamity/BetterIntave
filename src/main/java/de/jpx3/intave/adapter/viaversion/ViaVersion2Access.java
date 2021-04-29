package de.jpx3.intave.adapter.viaversion;

import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.util.UUID;

public final class ViaVersion2Access implements ViaVersionAccess {
  private Object viaVersionInstance;
  private Method getPlayerVersionMethod;

  @Override
  public void setup() {
    try {
      Class<?> viaVersion = Class.forName("us.myles.ViaVersion.api.ViaVersion");
      viaVersionInstance = viaVersion.getMethod("getInstance").invoke(null);
      getPlayerVersionMethod = viaVersionInstance.getClass().getMethod("getPlayerVersion", UUID.class);
    } catch (Exception exception) {
      throw new IllegalStateException("Invalid ViaVersion linkage", exception);
    }
  }

  @Override
  public int protocolVersionOf(Player player) {
    try {
      return (int) getPlayerVersionMethod.invoke(viaVersionInstance, player.getUniqueId());
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
    try {
      Class.forName("us.myles.ViaVersion.api.ViaVersion");
      return true;
    } catch (ClassNotFoundException e) {
      return false;
    }
  }
}

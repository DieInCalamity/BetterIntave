package de.jpx3.intave.adapter.viaversion;

import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.util.UUID;

public final class ViaVersion3Access implements ViaVersionAccess {
  private Object viaVersionTarget;
  private Method getPlayerVersionMethod;

  @Override
  public void setup() {
    try {
      Class<?> apiAcessorClass = Class.forName("us.myles.ViaVersion.api.Via");
      this.viaVersionTarget = apiAcessorClass.getMethod("getAPI").invoke(null);
      this.getPlayerVersionMethod = Class.forName("us.myles.ViaVersion.api.ViaAPI").getMethod("getPlayerVersion", UUID.class);
    } catch (Exception exception) {
      throw new IllegalStateException("Invalid ViaVersion linkage", exception);
    }
  }

  @Override
  public int protocolVersionOf(Player player) {
    try {
      return (int) getPlayerVersionMethod.invoke(viaVersionTarget, player.getUniqueId());
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
      Class.forName("us.myles.ViaVersion.api.Via");
      return true;
    } catch (ClassNotFoundException e) {
      return false;
    }
  }
}

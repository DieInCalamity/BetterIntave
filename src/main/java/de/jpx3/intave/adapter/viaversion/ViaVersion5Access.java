package de.jpx3.intave.adapter.viaversion;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.packet.PacketTracker;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public final class ViaVersion5Access implements ViaVersionAccess {
  private Class<?> apiAccessorClass;
  private Object viaVersionTarget;
  private Method getPlayerVersionMethod;

  @Override
  public void setup() {
    try {
      this.apiAccessorClass = Class.forName("com.viaversion.viaversion.api.Via");
      this.viaVersionTarget = apiAccessorClass.getMethod("getAPI").invoke(null);
      this.getPlayerVersionMethod = Class.forName("com.viaversion.viaversion.api.ViaAPI").getMethod("getPlayerVersion", UUID.class);
    } catch (Exception exception) {
      throw new IllegalStateException("Invalid ViaVersion linkage", exception);
    }
  }

  @Override
  public void patchConfiguration() {
    try {
//      ViaVersionConfig config = Via.getConfig();
      Object config = apiAccessorClass.getMethod("getConfig").invoke(viaVersionTarget);
      Class<?> configurationClass = Class.forName("com.viaversion.viaversion.configuration.AbstractViaConfig");
      Field maxPPSField = configurationClass.getDeclaredField("warningPPS");
      if (!maxPPSField.isAccessible()) {
        maxPPSField.setAccessible(true);
      }
      int maxpps = maxPPSField.getInt(config);
      maxPPSField.set(config, Math.max(maxpps, 600));
    } catch (Exception exception) {
      throw new IllegalStateException("Failed to alter ViaVersion configuration", exception);
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
  public void decrementReceivedPackets(Player player, int amount) {
    UserConnection connection = Via.getAPI().getConnection(player.getUniqueId());
    if (connection == null) {
      return;
    }
    PacketTracker packetTracker = connection.getPacketTracker();
    packetTracker.setIntervalPackets(Math.max(packetTracker.getIntervalPackets() - amount, 0));
  }

  @Override
  public boolean available(String version) {
    List<String> supportedVersions = Arrays.asList("4.1", "4.9", "5.0", "5.1", "5.2");
    return supportedVersions.stream().anyMatch(version::startsWith);
  }

  @Override
  public String version() {
    return Bukkit.getPluginManager().getPlugin("ViaVersion").getDescription().getVersion();

  }
}

package de.jpx3.intave.adapter;

import com.comphenix.protocol.ProtocolLibrary;
import com.google.common.collect.Lists;
import de.jpx3.intave.adapter.viaversion.ViaVersion2Access;
import de.jpx3.intave.adapter.viaversion.ViaVersion3Access;
import de.jpx3.intave.adapter.viaversion.ViaVersion4Access;
import de.jpx3.intave.adapter.viaversion.ViaVersionAccess;
import de.jpx3.intave.logging.IntaveLogger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Created by Jpx3 on 27.07.2018.
 */

public class ViaVersionAdapter {
  private final static List<ViaVersionAccess> available = Lists.newArrayList();

  static {
    available.add(new ViaVersion2Access());
    available.add(new ViaVersion3Access());
    available.add(new ViaVersion4Access());
  }

  private static ViaVersionAccess access;

  public static void setup() {
    if (Bukkit.getServer().getPluginManager().getPlugin("ViaVersion") == null) {
      return;
    }
    access = available.stream().filter(ViaVersionAccess::available).findFirst().orElse(null);
    available.clear();
    if(access != null) {
      access.setup();
    } else {
      IntaveLogger.logger().error("Unknown ViaVersion version, linkage failed");
    }
  }

  public static boolean ignoreBlocking(Player player) {
    return foundLinkage() && access.ignoreBlocking(player);
  }

  public static int protocolVersionOf(Player player) {
    return foundLinkage() ? access.protocolVersionOf(player) : ProtocolLibrary.getProtocolManager().getProtocolVersion(player);
  }

  public static boolean foundLinkage() {
    return access != null;
  }
}
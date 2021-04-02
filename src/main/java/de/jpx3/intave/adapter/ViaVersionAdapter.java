package de.jpx3.intave.adapter;

import com.comphenix.protocol.ProtocolLibrary;
import com.google.common.collect.Lists;
import de.jpx3.intave.adapter.viaversion.ViaVersion2Access;
import de.jpx3.intave.adapter.viaversion.ViaVersion3Access;
import de.jpx3.intave.adapter.viaversion.ViaVersionAccess;
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
  }

  private static ViaVersionAccess access;

  public static void setup() {
    for (ViaVersionAccess viaVersionAccess : available) {
      if (viaVersionAccess.available()) {
        access = viaVersionAccess;
        access.setup();
        break;
      }
    }
    available.clear();
  }

  public static boolean ignoreBlocking(Player player) {
    return access != null && access.ignoreBlocking(player);
  }

  public static int protocolVersionOf(Player player) {
    return hasViaVersion() ? access.protocolVersionOf(player) : ProtocolLibrary.getProtocolManager().getProtocolVersion(player);
  }

  public static boolean hasViaVersion() {
    return !(access == null || !Bukkit.getServer().getPluginManager().isPluginEnabled("ViaVersion"));
  }
}
package de.jpx3.intave.adapter;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.utility.MinecraftVersion;
import com.comphenix.protocol.wrappers.EnumWrappers;
import de.jpx3.intave.access.InvalidDependencyException;

import java.util.Arrays;

public final class ProtocolLibAdapter {
  public static MinecraftVersion serverVersion() {
    return ProtocolLibrary.getProtocolManager().getMinecraftVersion();
  }

  public static void checkIfOutdated() {
    boolean temporaryPlayer = Arrays.stream(PacketEvent.class.getMethods()).anyMatch(method -> method.getName().equalsIgnoreCase("isPlayerTemporary"));
    boolean specifiedEnumModifier = Arrays.stream(EnumWrappers.class.getMethods()).anyMatch(method -> method.getName().equalsIgnoreCase("getGenericConverter") && method.getParameterCount() == 2);

    if(!specifiedEnumModifier) {
      throw new InvalidDependencyException("Your version of ProtocolLib is outdated (missing generic enum conversion)");
    }

    if(de.jpx3.intave.adapter.MinecraftVersion.VER1_14_0.atOrAbove()) {
      if(!methodExists("com.comphenix.protocol.events.PacketContainer", "getMovingBlockPositions")) {
        throw new InvalidDependencyException("Your version of ProtocolLib is outdated (missing MOP packet access)");
      }
    }

  }

  private static boolean methodExists(String className, String methodName) {
    try {
      Class.forName(className).getDeclaredMethod(methodName);
      return true;
    } catch (Exception exception) {
      return false;
    }
  }
}
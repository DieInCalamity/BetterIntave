package de.jpx3.intave.reflect.method;

import com.comphenix.protocol.utility.MinecraftVersion;
import de.jpx3.intave.reflect.Lookup;

public abstract class ReducedMethodContainer {
  protected Class<?> serverClass(String className) {
    return Lookup.serverClass(className);
  }

  protected MinecraftVersion currentVersion() {
    return MinecraftVersion.getCurrentVersion();
  }
}

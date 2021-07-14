package de.jpx3.intave.reflect.locate;

import de.jpx3.intave.reflect.ReflectiveAccess;

public final class ClassLocation extends Location {
  private final String location;

  public ClassLocation(String name, IntegerMatcher versionMatcher, String location) {
    super(name, versionMatcher);
    this.location = location;
  }

  public Class<?> access() {
    try {
      return Class.forName(compiledLocation());
    } catch (ClassNotFoundException exception) {
      throw new IllegalStateException(exception);
    }
  }

  public String compiledLocation() {
    return location.replace("{version}", ReflectiveAccess.version());
  }

  public static ClassLocation nmsDefaultFor(String name) {
    return new ClassLocation(name, IntegerMatcher.between(8, 16), "net.minecraft.server.{version}." + name);
  }
}

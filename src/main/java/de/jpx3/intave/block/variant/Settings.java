package de.jpx3.intave.block.variant;

import java.util.Collection;

public final class Settings {
  public static Setting<Integer> integerSetting(String name, int min, int max) {
    return new IntegerSetting(name, min, max);
  }

  public static Setting<Boolean> booleanSetting(String name) {
    return new BooleanSetting(name);
  }

  public static Setting<?> enumSetting(String name, Class<?> owner, Collection<?> values) {
    return new EnumSetting(name, owner, values);
  }
}

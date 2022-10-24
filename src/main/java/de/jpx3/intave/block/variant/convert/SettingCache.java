package de.jpx3.intave.block.variant.convert;

import de.jpx3.intave.block.variant.Setting;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

final class SettingCache {
  private static final Map<Object, Setting<?>> settingCache = new ConcurrentHashMap<>();
  private SettingCache() {
  }

  static <T extends Comparable<T>> Setting<T> computeSettingIfAbsent(Object blockState, Function<Object, ? extends Setting<T>> consumer) {
    //noinspection unchecked
    return (Setting<T>) settingCache.computeIfAbsent(blockState, consumer);
  }
}

package de.jpx3.intave.cleanup;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public final class GarbageCollector {
  private final static List<Map<?, ?>> boundMaps = Lists.newCopyOnWriteArrayList();
  private final static List<List<?>> boundLists = Lists.newCopyOnWriteArrayList();

  private GarbageCollector() {
    throw new UnsupportedOperationException();
  }

  // class loading
  public static void setup() {
    Shutdown.addTask(GarbageCollector::die);
  }

  public static <K, V> Map<K, V> watch(Map<K, V> initialMap) {
    boundMaps.add(initialMap);
    return initialMap;
  }

  public static <T> List<T> watch(List<T> initialList) {
    boundLists.add(initialList);
    return initialList;
  }

  public static <K> void clear(K key) {
    boundMaps.forEach(boundMap -> boundMap.remove(key));
    boundLists.forEach(boundList -> boundList.remove(key));
  }

  public static void clearIf(Predicate<Object> check) {
    boundMaps.forEach(map -> map.entrySet().removeIf(entry -> check.test(entry.getKey())));
    boundLists.forEach(boundList -> boundList.removeIf(check));
  }

  public static void die() {
    boundMaps.forEach(Map::clear);
    boundMaps.clear();
  }
}

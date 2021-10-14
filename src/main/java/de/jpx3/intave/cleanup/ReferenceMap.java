package de.jpx3.intave.cleanup;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * A {@link Map} delegate wrapping its values in soft or weak references
 * @param <K> the map key
 * @param <V> the map value
 */
public final class ReferenceMap<K, V> implements Map<K, V> {
  private final Map<K, Reference<V>> map;
  private final Function<V, Reference<V>> referencer;

  private ReferenceMap(
    Map<K, Reference<V>> map,
    Function<V, Reference<V>> referencer
  ) {
    this.map = map;
    this.referencer = referencer;
  }

  @Override
  public int size() {
    cleanup();
    return map.size();
  }

  @Override
  public boolean isEmpty() {
    cleanup();
    return map.isEmpty();
  }

  @Override
  public boolean containsKey(Object key) {
    return get(key) != null;
  }

  @Override
  public boolean containsValue(Object value) {
    for (Reference<V> vReference : map.values()) {
      V innerValue = vReference.get();
      if (innerValue != null && (value == innerValue || value.equals(innerValue))) {
        return true;
      }
    }
    return false;
  }

  @Override
  public V get(Object key) {
    Reference<V> reference = map.get(key);
    return reference == null ? null : reference.get();
  }

  @Nullable
  @Override
  public V put(K key, V value) {
    Reference<V> oldValue = map.put(key, referencer.apply(value));
    return oldValue == null ? null : oldValue.get();
  }

  @Override
  public V remove(Object key) {
    Reference<V> oldValue = map.remove(key);
    return oldValue == null ? null : oldValue.get();
  }

  @Override
  public void putAll(@NotNull Map<? extends K, ? extends V> m) {
    m.forEach(this::put);
  }

  @Override
  public void clear() {
    map.clear();
  }

  @NotNull
  @Override
  public Set<K> keySet() {
    cleanup();
    return map.keySet();
  }

  @NotNull
  @Override
  public Collection<V> values() {
    cleanup();
    Collection<Reference<V>> values = map.values();
    List<V> newValues = new ArrayList<>(values.size());
    for (Reference<V> value : values) {
      newValues.add(value.get());
    }
    return newValues;
  }

  @NotNull
  @Override
  public Set<Entry<K, V>> entrySet() {
    cleanup();
    Set<Entry<K, Reference<V>>> entries = map.entrySet();
    Set<Entry<K, V>> newEntries = new HashSet<>();
    for (Entry<K, Reference<V>> entry : entries) {
      newEntries.add(new Entry<K, V>() {
        @Override
        public K getKey() {
          return entry.getKey();
        }

        @Override
        public V getValue() {
          return entry.getValue().get();
        }

        @Override
        public V setValue(V value) {
          Reference<V> oldValue = entry.setValue(referencer.apply(value));
          return oldValue == null ? null : oldValue.get();
        }
      });
    }
    return newEntries;
  }

  @Override
  public V computeIfAbsent(K key, @NotNull Function<? super K, ? extends V> mappingFunction) {
    Reference<V> reference = map.computeIfAbsent(key, k -> referencer.apply(mappingFunction.apply(k)));
    return reference == null ? mappingFunction.apply(key) : reference.get();
  }

  @Override
  public V computeIfPresent(K key, @NotNull BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
    Reference<V> reference = map.computeIfPresent(key, (k, v) -> referencer.apply(remappingFunction.apply(k, v.get())));
    return reference == null ? null : reference.get();
  }

  private void cleanup() {
    map.entrySet().removeIf(
      entry -> entry != null && entry.getValue() != null && entry.getValue().get() == null
    );
  }

  public static <K, V> ReferenceMap<K, V> soft(Map<K, Reference<V>> map) {
    return new ReferenceMap<>(map, SoftReference::new);
  }

  public static <K, V> ReferenceMap<K, V> weak(Map<K, Reference<V>> map) {
    return new ReferenceMap<>(map, WeakReference::new);
  }
}
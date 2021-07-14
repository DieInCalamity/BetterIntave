package de.jpx3.intave.reflect.locate;

import com.comphenix.protocol.utility.MinecraftVersion;

import java.util.Collections;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public final class FieldLocations {
  private final Iterable<FieldLocation> fieldLocations;

  public FieldLocations(Iterable<FieldLocation> fieldLocations) {
    this.fieldLocations = fieldLocations;
  }

  public FieldLocations filterByClassKey(String key) {
    return forward(fieldLocation -> fieldLocation.classKey().equals(key));
  }

  public FieldLocations filterByFieldKey(String key) {
    return forward(fieldLocation -> fieldLocation.key().equals(key));
  }

  public FieldLocations reduceToCurrentVersion() {
    return forward(fieldLocation -> fieldLocation.versionMatcher().matches(currentMinecraftVersion()));
  }

  private int currentMinecraftVersion() {
    return MinecraftVersion.getCurrentVersion().getMinor();
  }

  public Stream<FieldLocation> stream() {
    return StreamSupport.stream(this.fieldLocations.spliterator(), false);
  }

  public FieldLocations forward(Predicate<FieldLocation> predicate) {
    Iterable<FieldLocation> classLocations = stream().filter(predicate).collect(Collectors.toList());
    return new FieldLocations(classLocations);
  }

  public static FieldLocations empty() {
    return new FieldLocations(Collections.emptyList());
  }
}

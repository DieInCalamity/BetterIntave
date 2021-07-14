package de.jpx3.intave.reflect.locate;

import com.comphenix.protocol.utility.MinecraftVersion;

import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public final class ClassLocations {
  private final Iterable<ClassLocation> classLocations;

  public ClassLocations(Iterable<ClassLocation> classLocations) {
    this.classLocations = classLocations;
  }

  public ClassLocations filterByKey(String key) {
    return forward(classLocation -> classLocation.key().equals(key));
  }

  public ClassLocations reduceToCurrentVersion() {
    return forward(classLocation -> classLocation.versionMatcher().matches(currentMinecraftVersion()));
  }

  private int currentMinecraftVersion() {
    return MinecraftVersion.getCurrentVersion().getMinor();
  }

  public Stream<ClassLocation> stream() {
    return StreamSupport.stream(this.classLocations.spliterator(), false);
  }

  public ClassLocations forward(Predicate<ClassLocation> predicate) {
    Iterable<ClassLocation> classLocations = stream().filter(predicate).collect(Collectors.toList());
    return new ClassLocations(classLocations);
  }
}

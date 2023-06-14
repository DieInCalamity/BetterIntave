package de.jpx3.intave.library;

import de.jpx3.intave.IntaveLogger;

import java.util.Arrays;
import java.util.List;

public final class Libraries {
  public static void setupLibraries() {
    IntaveLogger.logger().info("Loading libraries...");

    // slf4j
    loadLibrary(fromMavenGradle("org.slf4j", "slf4j-api", "1.7.30"));
    loadLibrary(fromMavenGradle("org.slf4j", "slf4j-nop", "1.7.30"));

    List<String> smileProjects = Arrays.asList("smile-core", "smile-base");
    for (String smileProject : smileProjects) {
      loadLibrary(fromMavenGradle("com.github.haifengl", smileProject, "3.0.1"));
    }
  }

  public static void loadLibrary(Library library) {
    if (library.isInCache()) {
      library.pushToClasspath();
      return;
    }
    IntaveLogger.logger().info("Downloading library " + library.name() + " to cache");
    library.downloadToCache();
    library.pushToClasspath();
  }

  public static Library fromMavenGradle(String path, String name, String version) {
    return new Library(path, name, version, "https://repo1.maven.org/maven2");
  }
}

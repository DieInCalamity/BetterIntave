package de.jpx3.intave.resource;

import java.util.HashMap;
import java.util.Map;

public final class ResourceRegistry {
  private static final Map<String, Resource> registeredResources = new HashMap<>();

  public static void registerResource(String name, Resource resource) {
    registeredResources.put(name, resource);
  }

  public static Map<String, Resource> registeredResources() {
    return registeredResources;
  }
}

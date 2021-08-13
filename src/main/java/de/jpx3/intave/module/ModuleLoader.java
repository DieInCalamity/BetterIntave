package de.jpx3.intave.module;

import de.jpx3.intave.IntavePlugin;
import de.jpx3.intave.module.example.ExampleModule;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public final class ModuleLoader {
  private final Map<Class<? extends Module>, ModuleSettings> pendingModuleClasses = new HashMap<>();

  public void setup() {
    prepareModule(ExampleModule.class, ModuleSettings.builder().requiresProtocolLib().build());
  }

  private void prepareModule(Class<? extends Module> moduleClass) {
    prepareModule(moduleClass, ModuleSettings.of());
  }

  private void prepareModule(Class<? extends Module> moduleClass, ModuleSettings settings) {
    pendingModuleClasses.put(moduleClass, settings);
  }

  public Collection<Module> loadRequests() {
    return classPick(this::readyToLoad).stream().map(this::instanceOf).peek(this::initiate).collect(Collectors.toList());
  }

  private void initiate(Module module) {
    module.setPlugin(IntavePlugin.singletonInstance());
    module.setModuleSettings(pendingModuleClasses.remove(module.getClass()));
  }

  private <T> T instanceOf(Class<T> klass) {
    try {
      return klass.newInstance();
    } catch (InstantiationException | IllegalAccessException exception) {
      throw new IllegalStateException(exception);
    }
  }

  private boolean readyToLoad(ModuleSettings moduleSettings) {
    return moduleSettings.requirementsFulfilled();
  }

  private Collection<Class<? extends Module>> classPick(
    Predicate<ModuleSettings> predicate
  ) {
    return pendingModuleClasses.entrySet().stream()
      .filter(entry -> predicate.test(entry.getValue()))
      .map(Map.Entry::getKey).collect(Collectors.toList());
  }
}

package de.jpx3.intave.filter;

import de.jpx3.intave.IntavePlugin;
import de.jpx3.intave.access.IntaveInternalException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public final class Filters {
  private final IntavePlugin plugin;
  private final List<Filter> availableFilters = new ArrayList<>();

  public Filters(IntavePlugin plugin) {
    this.plugin = plugin;
  }

  public void setup() {
    setup(EquipmentFilter.class);
    setup(HealthFilter.class);
//    setup(ChatWordFilter.class);

    linkEnabled();
  }

  private void setup(Class<? extends Filter> filterClass) {
    try {
      Constructor<? extends Filter> constructor = filterClass.getConstructor(IntavePlugin.class);
      availableFilters.add(constructor.newInstance(plugin));
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
      throw new IntaveInternalException("Something went wrong setting up a filter", e);
    }
  }

  private void linkEnabled() {
    for (Filter filter : availableFilters) {
      if(filter.enabled()) {
        plugin.eventLinker().registerEventsIn(filter);
        plugin.packetSubscriptionLinker().linkSubscriptionsIn(filter);
      }
    }
  }
}

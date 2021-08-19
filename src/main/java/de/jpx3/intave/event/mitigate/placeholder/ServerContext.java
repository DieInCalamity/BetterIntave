package de.jpx3.intave.event.mitigate.placeholder;

import com.google.common.collect.ImmutableMap;
import de.jpx3.intave.reflect.access.ReflectiveTPSAccess;

import java.util.Map;

public final class ServerContext extends PlaceholderContext {
  @Override
  public Map<String, String> replacements() {
    return ImmutableMap.of(
      "tps", ReflectiveTPSAccess.stringFormattedTick()
    );
  }
}

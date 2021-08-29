package de.jpx3.intave.violation.placeholder;

import com.google.common.collect.ImmutableMap;
import de.jpx3.intave.reflect.access.ReflectiveTPSAccess;

import java.util.Map;

public final class ServerContext implements PlaceholderContext {
  @Override
  public Map<String, String> replacements() {
    return ImmutableMap.of(
      "tps", ReflectiveTPSAccess.stringFormattedTick()
    );
  }
}

package de.jpx3.intave.accessbackend.internal.check;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import de.jpx3.intave.IntavePlugin;
import de.jpx3.intave.access.IntaveCheckStatisticsAccess;
import de.jpx3.intave.access.UnknownCheckException;
import de.jpx3.intave.detect.IntaveCheck;

import java.util.Map;

public final class CheckStatisticsAccessor {
  private final IntavePlugin plugin;

  public CheckStatisticsAccessor(
    IntavePlugin plugin
  ) {
    this.plugin = plugin;
  }

  private final static Map<String, IntaveCheckStatisticsAccess> statisticAccessCache = Maps.newConcurrentMap();

  public synchronized IntaveCheckStatisticsAccess statisticsOf(String name) {
    Preconditions.checkNotNull(name);
    return statisticAccessCache.computeIfAbsent(name, x -> newStatisticsMirror(tryGetCheck(name)));
  }

  private IntaveCheck tryGetCheck(String name) {
    try {
      return plugin.checkService().searchCheck(name);
    } catch (NullPointerException nullptr) {
      throw new UnknownCheckException("Could not find check " + name);
    }
  }

  private IntaveCheckStatisticsAccess newStatisticsMirror(IntaveCheck intaveCheck) {
    return new IntaveCheckStatisticsAccess() {
      @Override
      public final long totalViolations() {
        return intaveCheck.statistics().totalViolations();
      }

      @Override
      public final long executedCommands() {
        return intaveCheck.statistics().executedCommands();
      }
    };
  }
}

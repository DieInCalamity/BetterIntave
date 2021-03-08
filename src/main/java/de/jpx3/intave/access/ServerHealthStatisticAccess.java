package de.jpx3.intave.access;

import java.util.function.Consumer;

/**
 * Class generated using IntelliJ IDEA
 * Created by Richard Strunk 2020
 */

public interface ServerHealthStatisticAccess {
  double tickAverageOver(TimeSpan timeSpan);
  void subscribeToTick(TimeSpan timeSpan, Consumer<Double> average);

  enum TimeSpan {
    LAST_MINUTE,
    LAST_FIVE_MINUTES,
    LAST_TEN_MINUTES
  }
}

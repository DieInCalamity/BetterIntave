package de.jpx3.intave.access;

import java.util.function.Consumer;

public interface PlayerClicks {
  int clicksLastSecond();
  void subscribeToSecond(Consumer<Integer> clicks);
}
package de.jpx3.intave.library.pledge;

import de.jpx3.intave.cleanup.ShutdownTasks;

import java.util.ArrayList;
import java.util.List;

public class TickEnd {
  private static final List<Runnable> tickEndSubscribers = new ArrayList<>();
  private static TickEndTask task;

  public static void start() {
    task = TickEndTask.create(() -> {
      tickEndSubscribers.forEach(Runnable::run);
    });
    ShutdownTasks.add(task::cancel);
  }

  public static void subscribe(Runnable runnable) {
    tickEndSubscribers.add(runnable);
  }

  public static void unsubscribe(Runnable runnable) {
    tickEndSubscribers.remove(runnable);
  }
}

package de.jpx3.intave.cleanup;

import java.util.ArrayDeque;
import java.util.Queue;

public final class Shutdown {
  private final static Queue<Runnable> tasks = new ArrayDeque<>();

  private Shutdown() {
    throw new UnsupportedOperationException("Initialization of helper class");
  }

  public static void addTask(Runnable runnable) {
    if (runnable == null) {
      throw new NullPointerException("Null shutdown task");
    }
    tasks.offer(runnable);
  }

  public static void executeShutdownTasks() {
    for (Runnable task : tasks) {
      try {
        task.run();
      } catch (Exception exception) {
        System.out.println("[Intave] Shutdown task " +task + " failed to complete");
        exception.printStackTrace();
      }
    }
  }
}

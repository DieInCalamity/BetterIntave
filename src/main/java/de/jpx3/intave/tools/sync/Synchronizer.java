package de.jpx3.intave.tools.sync;

import de.jpx3.intave.IntavePlugin;
import de.jpx3.intave.reflect.ReflectiveAccess;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Queue;

public final class Synchronizer {
  private final static BukkitScheduler scheduler = Bukkit.getScheduler();

  private static Queue<Runnable> cachedProcessQueue;
  private static boolean useScheduler;
  private static Object minecraftServer;
  private static MethodHandle postToMainThreadMethodHandle;

  public static void setup() {
    try {
      Class<?> minecraftServerClass = ReflectiveAccess.lookupServerClass("MinecraftServer");
      Object minecraftServer = minecraftServerClass.getMethod("getServer").invoke(null);
      //noinspection unchecked
      cachedProcessQueue = (Queue<Runnable>) minecraftServerClass.getField("processQueue").get(minecraftServer);
    } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException exception) {
      throw new IllegalStateException(exception);
    } catch (NoSuchFieldException exception) {
      IntavePlugin.singletonInstance().logger().error("Your version of spigot has removed support for task-queueing. We will switch to bukkit's scheduling service");
      useScheduler = true;
    }

    try {
      minecraftServer = ReflectiveAccess.lookupServerClass("MinecraftServer").getMethod("getServer").invoke(null);
      boolean useSuperClass = isDedicatedServer(minecraftServer.getClass());
      Class<?> serverClass = useSuperClass ? minecraftServer.getClass().getSuperclass() : minecraftServer.getClass();
      Method postToMainThreadMethod = serverClass.getDeclaredMethod("postToMainThread", Runnable.class);
      if (!postToMainThreadMethod.isAccessible()) {
        postToMainThreadMethod.setAccessible(true);
      }
      postToMainThreadMethodHandle = MethodHandles.lookup().unreflect(postToMainThreadMethod);
    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException exception) {
      throw new IllegalStateException(exception);
    }
  }

  private static boolean isDedicatedServer(Class<?> clazz) {
    String dedicatedServer = ReflectiveAccess.appendNMSPrefixToClass("DedicatedServer");
    try {
      return Class.forName(dedicatedServer) == clazz;
    } catch (ClassNotFoundException e) {
      return false;
    }
  }

  public static void synchronize(Runnable runnable) {
    runnable = bindToContext(runnable);

    if (useScheduler) {
      scheduler.runTask(IntavePlugin.singletonInstance(), runnable);
    } else {
      cachedProcessQueue.add(runnable);
    }
  }

  public static void packetSynchronize(Runnable runnable) {
    Runnable wrappedRunnable = bindToContext(runnable);
    try {
      postToMainThreadMethodHandle.invoke(minecraftServer, wrappedRunnable);
    } catch (Throwable throwable) {
      throwable.printStackTrace();
    }
  }

  public static void synchronizeDelayed(Runnable runnable, int ticks) {
    scheduler.runTaskLater(IntavePlugin.singletonInstance(), runnable, ticks);
  }

  private static Runnable bindToContext(Runnable runnable) {
    return () -> {
      try {
        runnable.run();
      } catch (Exception | Error exception) {
        exception.printStackTrace();
      }
    };
  }
}
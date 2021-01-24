package de.jpx3.intave.reflect;

import de.jpx3.intave.access.IntaveInternalException;
import de.jpx3.intave.adapter.ProtocolLibAdapter;
import org.bukkit.entity.Player;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;

public final class ReflectiveDataWatcherAccess {
  public static final int DATA_WATCHER_BLOCKING_ID = 4;

  private static MethodHandle setDataWatcherFlagHandle;
  private static MethodHandle getDataWatcherFlagHandle;

  public static void setup() {
    setupDataWatcher();
  }

  private static void setupDataWatcher() {
    try {
      linkDataWatcherSetter();
      linkDataWatcherGetter();
    } catch (NoSuchMethodException | IllegalAccessException e) {
      throw new IntaveInternalException(e);
    }
  }

  private static void linkDataWatcherSetter() throws IllegalAccessException, NoSuchMethodException {
    Class<?> entityClass = ReflectiveAccess.lookupServerClass("Entity");
    String methodName = ProtocolLibAdapter.COMBAT_UPDATE.atOrAbove() ? "setFlag" : "b";
    Method setDataWatcherMethod = entityClass.getDeclaredMethod(methodName, Integer.TYPE, Boolean.TYPE);
    setDataWatcherMethod.setAccessible(true);
    setDataWatcherFlagHandle = MethodHandles.lookup().unreflect(setDataWatcherMethod);
  }

  private static void linkDataWatcherGetter() throws IllegalAccessException, NoSuchMethodException {
    Class<?> entityClass = ReflectiveAccess.lookupServerClass("Entity");
    String methodName = ProtocolLibAdapter.COMBAT_UPDATE.atOrAbove() ? "getFlag" : "g";
    Method setDataWatcherMethod = entityClass.getDeclaredMethod(methodName, Integer.TYPE);
    setDataWatcherMethod.setAccessible(true);
    getDataWatcherFlagHandle = MethodHandles.lookup().unreflect(setDataWatcherMethod);
  }

  public static void setDataWatcherFlag(Player player, int flag, boolean set) {
    try {
      Object handle = ReflectiveAccess.handleResolver().resolveEntityHandleOf(player);
      setDataWatcherFlagHandle.invoke(handle, flag, set);
    } catch (Throwable throwable) {
      throwable.printStackTrace();
    }
  }

  public static boolean getDataWatcherFlag(Player player, int flag) {
    try {
      Object handle = ReflectiveAccess.handleResolver().resolveEntityHandleOf(player);
      return (boolean) getDataWatcherFlagHandle.invoke(handle, flag);
    } catch (Throwable throwable) {
      throwable.printStackTrace();
      return false;
    }
  }
}
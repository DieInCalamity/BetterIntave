package de.jpx3.intave.reflect;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;

public final class Reflection {
  private final static String NMS_PACKAGE_NAME = Bukkit.getServer().getClass().getPackage().getName().substring(23);
  private final static String NMS_PREFIX = "net.minecraft.server." + NMS_PACKAGE_NAME;
  private final static String CRAFT_BUKKIT_PREFIX = "org.bukkit.craftbukkit." + NMS_PACKAGE_NAME;

  public final static Class<?> NMS_WORLD_SERVER_CLASS = lookupServerClass("WorldServer");
  public final static Class<?> NMS_ENTITY_CLASS = lookupServerClass("Entity");
  public final static Class<?> NMS_AABB_CLASS = lookupServerClass("AxisAlignedBB");

  private final static MethodType NMS_WORLD_METHOD_TYPE = MethodType.methodType(NMS_WORLD_SERVER_CLASS);
  private final static MethodType NMS_ENTITY_METHOD_TYPE = MethodType.methodType(NMS_ENTITY_CLASS);

  public static <T> Class<T> classByName(String className) {
    try {
      //noinspection unchecked
      return (Class<T>) Class.forName(className);
    } catch (ClassNotFoundException e) {
      throw new ReflectionFailureException(e);
    }
  }

  public static String version() {
    return NMS_PACKAGE_NAME;
  }

  public static Class<?> lookupServerClass(String className) {
    return classByName(appendNMSPrefixToClass(className));
  }

  public static Class<?> lookupCraftBukkitClass(String className) {
    return classByName(appendCraftBukkitPrefixToClass(className));
  }

  public static String appendNMSPrefixToClass(String className) {
    return NMS_PREFIX + "." + className;
  }

  public static String appendCraftBukkitPrefixToClass(String className) {
    return CRAFT_BUKKIT_PREFIX + "." + className;
  }

  public static Object resolveEntityNMSHandle(Entity entity) {
    if(entity == null) {
      return null;
    }
    try {
      MethodHandle entityHandleMethod = MethodHandles
        .lookup()
        .findVirtual(entity.getClass(), "getHandle", NMS_ENTITY_METHOD_TYPE);
      return entityHandleMethod.invoke(entity);
    } catch (Throwable throwable) {
      throw new ReflectionFailureException(throwable);
    }
  }

  private static MethodHandle worldHandleMethod;

  public static Object resolveWorldNMSHandle(World world) {
    try {
      if (worldHandleMethod == null) {
        worldHandleMethod = MethodHandles
          .lookup()
          .findVirtual(world.getClass(), "getHandle", NMS_WORLD_METHOD_TYPE);
      }
      return worldHandleMethod.invoke(world);
    } catch (Throwable throwable) {
      throw new ReflectionFailureException(throwable);
    }
  }

  public static <C, R, I> R invokeField(Class<C> clazz, String fieldName, I obj) {
    try {
      Field field = clazz.getField(fieldName);
      ensureAccessible(field);
      Object invoke = field.get(obj);
      //noinspection unchecked
      return (R) invoke;
    } catch (Exception e) {
      throw new ReflectionFailureException(e);
    }
  }

  private static void ensureAccessible(AccessibleObject accessibleObject) {
    if (!accessibleObject.isAccessible()) {
      accessibleObject.setAccessible(true);
    }
  }
}
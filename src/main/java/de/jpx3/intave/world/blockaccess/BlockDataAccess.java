package de.jpx3.intave.world.blockaccess;

import com.comphenix.protocol.wrappers.BlockPosition;
import de.jpx3.intave.IntavePlugin;
import de.jpx3.intave.access.IntaveInternalException;
import de.jpx3.intave.adapter.MinecraftVersions;
import de.jpx3.intave.logging.IntaveLogger;
import de.jpx3.intave.patchy.PatchyLoadingInjector;
import de.jpx3.intave.reflect.ReflectiveAccess;
import de.jpx3.intave.reflect.ReflectiveBlockAccess;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.*;

public final class BlockDataAccess {
  private static BlockAccessor blockAccessor;
  private static MethodHandle nativeBlockDataAccess;

  private final static boolean ALL_BLOCKS_LEGACY = !MinecraftVersions.VER1_14_0.atOrAbove();

  private final static Set<Material> clickableMaterials = new HashSet<>();
  private final static Set<Material> legacyMaterials = new HashSet<>();

  public static void setup() {
    String resolverName = "de.jpx3.intave.world.blockaccess.v8BlockAccessor";
    if (MinecraftVersions.VER1_9_0.atOrAbove()) {
      resolverName = "de.jpx3.intave.world.blockaccess.v9BlockAccessor";
    }
    if(MinecraftVersions.VER1_13_0.atOrAbove()) {
      resolverName = "de.jpx3.intave.world.blockaccess.v13BlockAccessor";
    }
    if(MinecraftVersions.VER1_14_0.atOrAbove()) {
      resolverName = "de.jpx3.intave.world.blockaccess.v14BlockAccessor";
    }
    ClassLoader classLoader = IntavePlugin.class.getClassLoader();
    PatchyLoadingInjector.loadUnloadedClassPatched(classLoader, resolverName);
    blockAccessor = instanceOf(resolverName);

    if(MinecraftVersions.VER1_14_0.atOrAbove()) {
      try {
        Class<?> blockDataClass = ReflectiveAccess.lookupServerClass("IBlockData");
        Class<?> craftBukkitClass = ReflectiveAccess.lookupCraftBukkitClass("block.CraftBlock");
        nativeBlockDataAccess = MethodHandles.lookup().findVirtual(craftBukkitClass, "getNMS", MethodType.methodType(blockDataClass));
      } catch (NoSuchMethodException | IllegalAccessException exception) {
        throw new IntaveInternalException("Failed to load data access method", exception);
      }
    }

    loadMaterials();
  }

  private static <T> T instanceOf(String className) {
    try {
      //noinspection unchecked
      return (T) Class.forName(className).newInstance();
    } catch (InstantiationException | IllegalAccessException | ClassNotFoundException exception) {
      throw new IntaveInternalException(exception);
    }
  }

  public static int dataIndexOf(Block block) {
    Material type = block.getType();
    if(isLegacy(type)) {
      return block.getData();
    } else {
      try {
        return RuntimeBlockDataIndexer.indexOfModernState(type, nativeBlockDataAccess.invoke(block));
      } catch (Throwable throwable) {
        throw new IntaveInternalException("Failed to access data of " + block, throwable);
      }
    }
  }

  public static boolean isLegacy(Material type) {
    return ALL_BLOCKS_LEGACY || legacyMaterials.contains(type);
  }

  public static boolean isClickable(Material type) {
    return clickableMaterials.contains(type);
  }

  public static float blockDamage(Player player, ItemStack itemInHand, BlockPosition blockPosition) {
    return blockAccessor.blockDamage(player, itemInHand, blockPosition);
  }

  public static boolean replacementPlace(World world, Player player, BlockPosition blockPosition) {
    return blockAccessor.replacementPlace(world, player, blockPosition);
  }

  private static void loadMaterials() {
    if(MinecraftVersions.VER1_14_0.atOrAbove()) {
      modernMaterialLoad();
    } else {
      legacyMaterialLoad();
    }
  }

  private static void modernMaterialLoad() {
    Method isInteractable, isLegacy;
    try {
      isInteractable = Material.class.getMethod("isInteractable");
      isLegacy = Material.class.getMethod("isLegacy");
    } catch (NoSuchMethodException exception) {
      throw new IntaveInternalException(exception);
    }

    for (Material material : Material.values()) {
      try {
        if((boolean) isLegacy.invoke(material)) {
          legacyMaterials.add(material);
        }
      } catch (Exception exception) {
        exception.printStackTrace();
      }

      try {
        if(material.isBlock() && (boolean) isInteractable.invoke(material)) {
          clickableMaterials.add(material);
        }
      } catch (Exception exception) {
        exception.printStackTrace();
      }
    }
  }

  private static void legacyMaterialLoad() {
    for (Material material : Material.values()) {
      if(!material.isBlock()) {
        continue;
      }
      Object block = ReflectiveBlockAccess.blockById(material.getId());
      if (block == null) {
        IntaveLogger.logger().globalPrintLn("No block found for id " + material.getId());
        continue;
      }
      List<Method> methods = allMethodsIn(block.getClass());
      for (Method method : methods) {
        String methodName = method.getName();
        if (methodName.equalsIgnoreCase("interact")) {
          String declaringClassName = method.getDeclaringClass().getSimpleName();
          if (!declaringClassName.equals("Block") && !declaringClassName.equals("BlockBase")) {
            clickableMaterials.add(material);
          }
        }
      }
    }
  }

  private static List<Method> allMethodsIn(Class<?> clazz) {
    List<Method> methods = new ArrayList<>();
    do {
      Class<?> finalClazz = clazz;
      Arrays.stream(clazz.getDeclaredMethods())
        .filter(method -> method.getDeclaringClass() == finalClazz)
        .forEach(methods::add);
      clazz = clazz.getSuperclass();
    } while (clazz != Object.class);
    return methods;
  }
}

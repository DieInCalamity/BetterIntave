package de.jpx3.intave.module.patcher;

import com.google.common.collect.Sets;
import de.jpx3.intave.IntaveLogger;
import de.jpx3.intave.adapter.MinecraftVersions;
import de.jpx3.intave.klass.Lookup;
import de.jpx3.intave.klass.rewrite.PatchyLoadingInjector;
import de.jpx3.intave.module.Module;
import de.jpx3.intave.module.linker.bukkit.BukkitEventSubscription;
import de.jpx3.intave.world.chunk.ChunkProviderServerAccess;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.world.WorldInitEvent;

import java.lang.reflect.Field;
import java.util.Iterator;

public final class ChunkAccessPatcher extends Module {
  private final static boolean ENABLED = !MinecraftVersions.VER1_14_0.atOrAbove();

  static {
    if (ENABLED) {
      ClassLoader classLoader = ChunkAccessPatcher.class.getClassLoader();
      PatchyLoadingInjector.loadUnloadedClassPatched(classLoader, "de.jpx3.intave.module.patcher.SynchronizedLongHashSet");
    }
  }

  @Override
  public void enable() {
    Bukkit.getWorlds().forEach(this::patchWorld);
  }

  @BukkitEventSubscription
  public void worldInit(WorldInitEvent event) {
    patchWorld(event.getWorld());
  }

  public void patchWorld(World world) {
    if (!ENABLED) {
      return;
    }
    try {
      Field unloadQueueField = unloadQueueField();
      if (unloadQueueField == null) {
        return;
      }
      if (!unloadQueueField.isAccessible()) {
        unloadQueueField.setAccessible(true);
      }
      String unloadQueueFieldClassName = unloadQueueField.getType().getName();
      Object chunkProviderServer = ChunkProviderServerAccess.chunkProviderServerOf(world);
      Object unloadQueue = unloadQueueField.get(chunkProviderServer);
      String patchName;
      //noinspection unchecked
      Iterator<Long> iterator = (Iterator<Long>) unloadQueue.getClass().getMethod("iterator").invoke(unloadQueue);
      if (unloadQueueFieldClassName.contains("dsi.fastutil.longs")) {
        SynchronizedLongArraySet newQueue = new SynchronizedLongArraySet();
        unloadQueueField.set(chunkProviderServer, newQueue);
        patchName = "s(dsi/ls)";
        iterator.forEachRemaining(newQueue::add);
      } else if (unloadQueueFieldClassName.endsWith("util.LongHashSet")) {
        SynchronizedLongHashSet newQueue = new SynchronizedLongHashSet();
        unloadQueueField.set(chunkProviderServer, newQueue);
        patchName = "s(ut/lhs)";
        iterator.forEachRemaining(newQueue::add);
      } else {
        SynchronizedSet<Long> newQueue = new SynchronizedSet<>(Sets.newHashSet());
        unloadQueueField.set(chunkProviderServer, newQueue);
        patchName = "s(java/hs)";
        iterator.forEachRemaining(newQueue::add);
      }
      IntaveLogger.logger().info("Patched chunk unload queue of \"" + world.getName() + "\" with " + patchName);
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  private Field unloadQueueField() {
    try {
      return Lookup.serverClass("ChunkProviderServer").getField("unloadQueue");
    } catch (NoSuchFieldException ignoredEZ) {
      return null;
    }
  }
}

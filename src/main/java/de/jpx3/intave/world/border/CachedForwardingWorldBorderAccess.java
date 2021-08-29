package de.jpx3.intave.world.border;

import de.jpx3.intave.cleanup.GarbageCollector;
import de.jpx3.intave.event.AccessHelper;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public final class CachedForwardingWorldBorderAccess implements WorldBorderAccess {
  private final static long CACHE_EXPIRY = TimeUnit.MICROSECONDS.toMillis(50);
  private final WorldBorderAccess forward;
  private final Map<World, WorldBorderAccessCache<Double>> sizeCache = GarbageCollector.watch(new ConcurrentHashMap<>());

  public CachedForwardingWorldBorderAccess(WorldBorderAccess forward) {
    this.forward = forward;
  }

  @Override
  public double sizeOf(World world) {
    WorldBorderAccessCache<Double> sizeCache = this.sizeCache.get(world);
    if (sizeCache == null) {
      sizeCache = new WorldBorderAccessCache<>(forward.sizeOf(world));
      this.sizeCache.put(world, sizeCache);
    } else if (sizeCache.expired()) {
      sizeCache.typeFlush(forward.sizeOf(world));
    }
    return sizeCache.target;
  }

  @Override
  public Location centerOf(World world) {
    return forward.centerOf(world);
  }

  public static class WorldBorderAccessCache<T> {
    private T target;
    private long time;

    public WorldBorderAccessCache(T target) {
      this.target = target;
      this.time = AccessHelper.now();
    }

    public void typeFlush(T newValue) {
      target = newValue;
      time = AccessHelper.now();
    }

    public boolean expired() {
      return AccessHelper.now() - time > CACHE_EXPIRY;
    }

    public T target() {
      return target;
    }
  }
}

package de.jpx3.intave.event.service.entity;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import de.jpx3.intave.IntavePlugin;
import de.jpx3.intave.access.IntaveInternalException;
import de.jpx3.intave.adapter.MinecraftVersion;
import de.jpx3.intave.adapter.ProtocolLibAdapter;
import de.jpx3.intave.logging.IntaveLogger;
import de.jpx3.intave.patchy.PatchyLoadingInjector;
import de.jpx3.intave.patchy.annotate.PatchyAutoTranslation;
import de.jpx3.intave.reflect.ReflectiveAccess;
import de.jpx3.intave.reflect.ReflectiveHandleAccess;
import de.jpx3.intave.reflect.hitbox.HitBoxBoundaries;
import de.jpx3.intave.reflect.hitbox.ReflectiveEntityHitBoxAccess;
import net.minecraft.server.v1_16_R1.*;
import org.bukkit.entity.Entity;

import java.lang.reflect.Field;

public final class PacketEntityTypeResolver {
  private static final boolean DATA_WATCHER = !MinecraftVersion.VER1_15_0.atOrAbove();
  private String dataWatcherEntityFieldName;

  public PacketEntityTypeResolver(IntavePlugin plugin) {
    this.registerDataWatcherEntityFieldName();
    if (DATA_WATCHER) {
      PatchyLoadingInjector.loadUnloadedClassPatched(plugin.getClass().getClassLoader(), "de.jpx3.intave.event.service.entity.PacketEntityTypeResolver$EntityTypeResolver");
    }
  }

  private void registerDataWatcherEntityFieldName() {
    com.comphenix.protocol.utility.MinecraftVersion serverVersion = ProtocolLibAdapter.serverVersion();
    if (serverVersion.isAtLeast(de.jpx3.intave.adapter.MinecraftVersion.VER1_14_0)) {
      dataWatcherEntityFieldName = "entity";
    } else if (serverVersion.isAtLeast(de.jpx3.intave.adapter.MinecraftVersion.VER1_10_0)) {
      dataWatcherEntityFieldName = "c";
    } else if (serverVersion.isAtLeast(de.jpx3.intave.adapter.MinecraftVersion.VER1_9_0)) {
      dataWatcherEntityFieldName = "b";
    } else {
      dataWatcherEntityFieldName = "a";
    }

    // search field

    Class<?> entityClass = ReflectiveAccess.NMS_ENTITY_CLASS;
    Class<?> dataWatcherClass = ReflectiveAccess.lookupServerClass("DataWatcher");

    for (Field declaredField : dataWatcherClass.getDeclaredFields()) {
      if (declaredField.getType() == entityClass) {
        String fieldName = declaredField.getName();
        if (!dataWatcherEntityFieldName.equals(fieldName)) {
          IntaveLogger.logger().globalPrintLn("[Intave] Conflicting in access pool: \"" + dataWatcherEntityFieldName + "\" expected but found \"" + fieldName + "\"");
        }
        dataWatcherEntityFieldName = fieldName;
        break;
      }
    }
  }

  public String entityNameByBukkitEntity(Entity entity) {
    return entityNameOf(ReflectiveHandleAccess.handleOf(entity));
  }

  public EntitySpawn spawnInformationOf(PacketContainer packet) {
    return DATA_WATCHER ? dataWatcherAccess(packet) : typeAccess(packet);
  }

  //
  // Type Access
  //

  private EntitySpawn typeAccess(PacketContainer packet) {
    Integer type = packet.getIntegers().read(1);
    return EntityTypeResolver.resolveFromId(type);
  }

  //
  // DataWatcher Access
  //

  private EntitySpawn dataWatcherAccess(PacketContainer packet) {
    Object entity = entityOfDataWatcher(packet.getDataWatcherModifier().read(0));
    HitBoxBoundaries hitBoxBoundaries = ReflectiveEntityHitBoxAccess.boundariesOf(entity);
    String name = entityNameOf(entity);
    return new EntitySpawn(name, hitBoxBoundaries);
  }

  private String entityNameOf(Object entity) {
    String entityName = entity.getClass().getSimpleName();
    if (entityName.startsWith("Entity")) {
      entityName = entityName.substring("Entity".length());
    }
    return entityName;
  }

  private Object entityOfDataWatcher(WrappedDataWatcher dataWatcher) {
    Object handle = dataWatcher.getHandle();
    Class<?> handleClass = handle.getClass();
    try {
      return entityByHandle(handle, handleClass.getDeclaredField(dataWatcherEntityFieldName));
    } catch (NoSuchFieldException e) {
      throw new IntaveInternalException(e);
    }
  }

  private Object entityByHandle(Object handle, Field entityField) {
    try {
      ReflectiveAccess.ensureAccessible(entityField);
      return entityField.get(handle);
    } catch (Exception e) {
      throw new IntaveInternalException(e);
    }
  }

  public static final class EntitySpawn {
    private final String entityName;
    private final HitBoxBoundaries hitBoxBoundaries;

    public EntitySpawn(String entityName, HitBoxBoundaries hitBoxBoundaries) {
      this.entityName = entityName;
      this.hitBoxBoundaries = hitBoxBoundaries;
    }

    public String entityName() {
      return entityName;
    }

    public HitBoxBoundaries hitBoxBoundaries() {
      return hitBoxBoundaries;
    }
  }

  @PatchyAutoTranslation
  private final static class EntityTypeResolver {
    @PatchyAutoTranslation
    public static EntitySpawn resolveFromId(int type) {
      EntityTypes<?> entityTypes = IRegistry.ENTITY_TYPE.fromId(type);
      EntitySize entitySize = entityTypes.l();
      IChatBaseComponent component = entityTypes.g();
      HitBoxBoundaries hitBoxBoundaries = HitBoxBoundaries.of(entitySize.width, entitySize.height);
      return new EntitySpawn(component.getString(), hitBoxBoundaries);
    }
  }
}
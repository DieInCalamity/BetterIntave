package de.jpx3.intave.entity.type;

import de.jpx3.intave.IntavePlugin;
import de.jpx3.intave.adapter.MinecraftVersions;
import de.jpx3.intave.clazz.rewrite.PatchyLoadingInjector;

public final class EntityTypeDataAccessor {
  private final static boolean DIRECT_RESOLVE = MinecraftVersions.VER1_14_0.atOrAbove();
  private static EntityTypeDataResolver resolver;

  public static void setup() {
    if (DIRECT_RESOLVE) {
      PatchyLoadingInjector.loadUnloadedClassPatched(IntavePlugin.class.getClassLoader(), "de.jpx3.intave.entity.type.ServerEntityTypeDataLookup");
      resolver = new ServerEntityTypeDataLookup();
    } else {
      resolver = new EntityTypeDataRegistry();
    }
  }

  public static EntityTypeData resolveFromId(int entityTypeId, boolean isLivingEntity) {
    return resolver.resolveFor(entityTypeId, isLivingEntity);
  }
}
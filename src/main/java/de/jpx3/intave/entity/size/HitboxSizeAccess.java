package de.jpx3.intave.entity.size;

import de.jpx3.intave.IntavePlugin;
import de.jpx3.intave.adapter.MinecraftVersions;
import de.jpx3.intave.clazz.rewrite.PatchyAutoTranslation;
import de.jpx3.intave.clazz.rewrite.PatchyLoadingInjector;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;

public final class HitboxSizeAccess {
  private static HitboxSizeResolver resolver;

  public static void setup() {
    boolean useNewResolver = MinecraftVersions.VER1_14_0.atOrAbove();
    String className = useNewResolver
      ? "de.jpx3.intave.entity.size.HitboxSizeAccess$HitBoxAccessModern"
      : "de.jpx3.intave.entity.size.HitboxSizeAccess$HitBoxAccessLegacy";
    PatchyLoadingInjector.loadUnloadedClassPatched(IntavePlugin.class.getClassLoader(), className);
    resolver = useNewResolver ? new HitBoxAccessModern() : new HitBoxAccessLegacy();
  }

  public static HitboxSize dimensionsOf(Entity entity) {
    return resolver.hitBoxOf(entity);
  }

  public static HitboxSize dimensionsOf(Object serverEntity) {
    return resolver.hitBoxOf(serverEntity);
  }

  @PatchyAutoTranslation
  public static final class HitBoxAccessLegacy implements HitboxSizeResolver {
    @PatchyAutoTranslation
    @Override
    public HitboxSize hitBoxOf(Entity entity) {
      net.minecraft.server.v1_8_R3.Entity serverEntity = ((CraftEntity) entity).getHandle();
      return HitboxSize.of(serverEntity.width, serverEntity.length);
    }

    @PatchyAutoTranslation
    @Override
    public HitboxSize hitBoxOf(Object serverEntity) {
      net.minecraft.server.v1_8_R3.Entity theServerEntity =
        (net.minecraft.server.v1_8_R3.Entity) (serverEntity);
      return HitboxSize.of(theServerEntity.width, theServerEntity.length);
    }
  }

  @PatchyAutoTranslation
  public static final class HitBoxAccessModern implements HitboxSizeResolver {
    @PatchyAutoTranslation
    @Override
    public HitboxSize hitBoxOf(Entity entity) {
      net.minecraft.server.v1_14_R1.Entity serverEntity = ((org.bukkit.craftbukkit.v1_14_R1.entity.CraftEntity) entity).getHandle();
      return HitboxSize.of(serverEntity.getWidth(), serverEntity.getHeight());
    }

    @PatchyAutoTranslation
    @Override
    public HitboxSize hitBoxOf(Object serverEntity) {
      float width = ((net.minecraft.server.v1_14_R1.Entity) (serverEntity)).getWidth();
      float length = ((net.minecraft.server.v1_14_R1.Entity) (serverEntity)).getHeight();
      return HitboxSize.of(width, length);
    }
  }
}
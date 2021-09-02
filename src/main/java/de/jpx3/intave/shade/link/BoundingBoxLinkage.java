package de.jpx3.intave.shade.link;

import de.jpx3.intave.IntavePlugin;
import de.jpx3.intave.adapter.MinecraftVersions;
import de.jpx3.intave.clazz.rewrite.PatchyAutoTranslation;
import de.jpx3.intave.clazz.rewrite.PatchyLoadingInjector;
import de.jpx3.intave.shade.BoundingBox;
import net.minecraft.server.v1_8_R3.AxisAlignedBB;

public final class BoundingBoxLinkage {
  static ClassLinker<BoundingBox> resolveBoundingBoxLinker() {
    boolean atLeastAquaticUpdate = MinecraftVersions.VER1_13_0.atOrAbove();
    String boundingBoxResolverClass = atLeastAquaticUpdate
      ? "de.jpx3.intave.shade.link.BoundingBoxLinkage$BoundingBoxAquaticResolver"
      : "de.jpx3.intave.shade.link.BoundingBoxLinkage$BoundingBoxLegacyResolver";
    PatchyLoadingInjector.loadUnloadedClassPatched(IntavePlugin.class.getClassLoader(), boundingBoxResolverClass);
    return atLeastAquaticUpdate ? new BoundingBoxLinkage.BoundingBoxAquaticResolver() : new BoundingBoxLinkage.BoundingBoxLegacyResolver();
  }

  @PatchyAutoTranslation
  public static final class BoundingBoxLegacyResolver implements ClassLinker<BoundingBox> {
    @PatchyAutoTranslation
    @Override
    public BoundingBox link(Object obj) {
      AxisAlignedBB boundingBox = (AxisAlignedBB) obj;
      return new BoundingBox(
        boundingBox.a, boundingBox.b, boundingBox.c,
        boundingBox.d, boundingBox.e, boundingBox.f
      );
    }
  }

  @PatchyAutoTranslation
  public static final class BoundingBoxAquaticResolver implements ClassLinker<BoundingBox> {
    @PatchyAutoTranslation
    @Override
    public BoundingBox link(Object obj) {
      net.minecraft.server.v1_13_R2.AxisAlignedBB boundingBox = (net.minecraft.server.v1_13_R2.AxisAlignedBB) obj;
      return new BoundingBox(
        boundingBox.minX, boundingBox.minY, boundingBox.minZ,
        boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ
      );
    }
  }
}
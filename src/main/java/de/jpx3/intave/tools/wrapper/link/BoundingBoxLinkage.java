package de.jpx3.intave.tools.wrapper.link;

import de.jpx3.intave.IntavePlugin;
import de.jpx3.intave.adapter.MinecraftVersion;
import de.jpx3.intave.patchy.PatchyLoadingInjector;
import de.jpx3.intave.patchy.annotate.PatchyAutoTranslation;
import de.jpx3.intave.tools.wrapper.WrappedAxisAlignedBB;
import net.minecraft.server.v1_8_R3.AxisAlignedBB;

public final class BoundingBoxLinkage {
  static ClassLinker<WrappedAxisAlignedBB> resolveBoundingBoxLinker() {
    boolean atLeastAquaticUpdate = MinecraftVersion.VER1_13_0.atOrAbove();
    String boundingBoxResolverClass = atLeastAquaticUpdate
      ? "de.jpx3.intave.tools.wrapper.link.BoundingBoxLinkage$BoundingBoxAquaticResolver"
      : "de.jpx3.intave.tools.wrapper.link.BoundingBoxLinkage$BoundingBoxLegacyResolver";
    PatchyLoadingInjector.loadUnloadedClassPatched(IntavePlugin.class.getClassLoader(), boundingBoxResolverClass);
    return atLeastAquaticUpdate ? new BoundingBoxLinkage.BoundingBoxAquaticResolver() : new BoundingBoxLinkage.BoundingBoxLegacyResolver();
  }

  @PatchyAutoTranslation
  public static final class BoundingBoxLegacyResolver implements ClassLinker<WrappedAxisAlignedBB> {
    @PatchyAutoTranslation
    @Override
    public WrappedAxisAlignedBB link(Object obj) {
      AxisAlignedBB boundingBox = (AxisAlignedBB) obj;
      return new WrappedAxisAlignedBB(
        boundingBox.a, boundingBox.b, boundingBox.c,
        boundingBox.d, boundingBox.e, boundingBox.f
      );
    }
  }

  @PatchyAutoTranslation
  public static final class BoundingBoxAquaticResolver implements ClassLinker<WrappedAxisAlignedBB> {
    @PatchyAutoTranslation
    @Override
    public WrappedAxisAlignedBB link(Object obj) {
      net.minecraft.server.v1_13_R2.AxisAlignedBB boundingBox = (net.minecraft.server.v1_13_R2.AxisAlignedBB) obj;
      return new WrappedAxisAlignedBB(
        boundingBox.minX, boundingBox.minY, boundingBox.minZ,
        boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ
      );
    }
  }
}
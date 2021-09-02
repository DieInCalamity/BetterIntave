package de.jpx3.intave.shade.link;

import de.jpx3.intave.IntavePlugin;
import de.jpx3.intave.clazz.rewrite.PatchyAutoTranslation;
import de.jpx3.intave.clazz.rewrite.PatchyLoadingInjector;
import de.jpx3.intave.shade.BlockPosition;

public final class BlockPositionLinkage {
  static ClassLinker<BlockPosition> resolveBlockPositionLinker() {
    String boundingBoxResolverClass = "de.jpx3.intave.shade.link.BlockPositionLinkage$BlockPositionResolver";
    PatchyLoadingInjector.loadUnloadedClassPatched(IntavePlugin.class.getClassLoader(), boundingBoxResolverClass);
    return new BlockPositionResolver();
  }

  @PatchyAutoTranslation
  public static final class BlockPositionResolver implements ClassLinker<BlockPosition> {
    @PatchyAutoTranslation
    @Override
    public BlockPosition link(Object obj) {
      net.minecraft.server.v1_8_R3.BlockPosition blockPosition = (net.minecraft.server.v1_8_R3.BlockPosition) obj;
      return new BlockPosition(blockPosition.getX(), blockPosition.getY(), blockPosition.getZ());
    }
  }
}
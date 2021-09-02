package de.jpx3.intave.shade.link;

import de.jpx3.intave.shade.BlockPosition;
import de.jpx3.intave.shade.BoundingBox;
import de.jpx3.intave.shade.NativeVector;

public final class WrapperLinkage {
  private static ClassLinker<BoundingBox> boundingBoxLinker;
  private static ClassLinker<BlockPosition> blockPositionLinker;
  private static ClassLinker<NativeVector> vec3DLinker;

  public static void setup() {
    boundingBoxLinker = BoundingBoxLinkage.resolveBoundingBoxLinker();
    blockPositionLinker = BlockPositionLinkage.resolveBlockPositionLinker();
    vec3DLinker = Vec3DLinkage.resolveVec3DLinker();
  }

  public static BoundingBox boundingBoxOf(Object obj) {
    return boundingBoxLinker.link(obj);
  }

  public static BlockPosition blockPositionOf(Object obj) {
    return blockPositionLinker.link(obj);
  }

  public static NativeVector vectorOf(Object obj) {
    return vec3DLinker.link(obj);
  }
}
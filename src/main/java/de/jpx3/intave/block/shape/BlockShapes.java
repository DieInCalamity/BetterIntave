package de.jpx3.intave.block.shape;

import de.jpx3.intave.shade.BoundingBox;

import java.util.ArrayList;
import java.util.List;

public final class BlockShapes {
  private final static BlockShape EMPTY = new EmptyBlockShape();

  public static BlockShape emptyShape() {
    return EMPTY;
  }

  public static BlockShape cubicShape() {
    return new CubeShape(0, 0, 0);
  }

  public static BlockShape cubicShapeAt(int posX, int posY, int posZ) {
    return new CubeShape(posX, posY, posZ);
  }

  public static BlockShape shapeOf(List<BoundingBox> boundingBoxes) {
    switch (boundingBoxes.size()) {
      case 0:
        return emptyShape();
      case 1:
        return boundingBoxes.get(0);
      case 2:
        return new MergeBlockShape(boundingBoxes.get(0), boundingBoxes.get(1));
      default:
        return new ArrayBlockShape(new ArrayList<>(boundingBoxes));
    }
  }

  public static BlockShape merge(BlockShape shapeA, BlockShape shapeB) {
    if (shapeA.isEmpty()) {
      return shapeB;
    }
    if (shapeB.isEmpty()) {
      return shapeA;
    }
    return new MergeBlockShape(shapeA, shapeB);
  }
}

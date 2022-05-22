package de.jpx3.intave.block.shape;

import java.util.ArrayList;
import java.util.List;

public final class ShapeCombiner {
  private static final ShapeCombiner EMPTY = new ShapeCombiner();
  private final List<BlockShape> shapes = new ArrayList<>(4);

  private ShapeCombiner() {
  }

  private ShapeCombiner(BlockShape init) {
    shapes.add(init);
  }

  public ShapeCombiner append(BlockShape blockShape) {
    if (blockShape.isEmpty()) {
      return this;
    }
    if (EMPTY == this) {
      return new ShapeCombiner(blockShape);
    }
    shapes.add(blockShape);
    return this;
  }

  public BlockShape compile() {
    if (EMPTY == this) {
      // fast escape
      return BlockShapes.emptyShape();
    } else {
      int size = shapes.size();
      switch (size) {
        case 0:
          return BlockShapes.emptyShape();
        case 1:
          return shapes.get(0);
        case 2:
          return new MergeBlockShape(shapes.get(0), shapes.get(1));
        default:
          return new ArrayBlockShape(shapes);
      }
    }
  }

  public static ShapeCombiner create() {
    return EMPTY;
  }
}

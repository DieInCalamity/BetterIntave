package de.jpx3.intave.block.shape;

import com.google.common.collect.Lists;
import de.jpx3.intave.share.BoundingBox;
import de.jpx3.intave.share.Direction;
import de.jpx3.intave.share.Position;

import java.util.List;

final class MergeBlockShape implements BlockShape {
  private final BlockShape firstShape, secondShape;

  MergeBlockShape(BlockShape firstShape, BlockShape secondShape) {
    this.firstShape = firstShape;
    this.secondShape = secondShape;
  }

  @Override
  public double allowedOffset(Direction.Axis axis, BoundingBox entity, double offset) {
    return firstShape.allowedOffset(axis, entity, secondShape.allowedOffset(axis, entity, offset));
  }

  @Override
  public double min(Direction.Axis axis) {
    return Math.min(firstShape.min(axis), secondShape.min(axis));
  }

  @Override
  public double max(Direction.Axis axis) {
    return Math.max(firstShape.max(axis), secondShape.max(axis));
  }

  @Override
  public BlockShape contextualized(int posX, int posY, int posZ) {
    return new MergeBlockShape(
      firstShape.contextualized(posX, posY, posZ),
      secondShape.contextualized(posX, posY, posZ)
    );
  }

  @Override
  public BlockShape normalized(int posX, int posY, int posZ) {
    return new MergeBlockShape(
      firstShape.normalized(posX, posY, posZ),
      secondShape.normalized(posX, posY, posZ)
    );
  }

  @Override
  public BlockRaytrace raytrace(Position origin, Position target) {
    BlockRaytrace raytraceA = firstShape.raytrace(origin, target);
    BlockRaytrace raytraceB = secondShape.raytrace(origin, target);
    if (raytraceA == null) {
      return raytraceB;
    }
    if (raytraceB == null) {
      return raytraceA;
    }
    return raytraceA.minSelect(raytraceB);
  }

  @Override
  public List<BoundingBox> boundingBoxes() {
    if (firstShape.isEmpty()) {
      return secondShape.boundingBoxes();
    }
    if (secondShape.isEmpty()) {
      return firstShape.boundingBoxes();
    }
    List<BoundingBox> merge = Lists.newArrayList(firstShape.boundingBoxes());
    merge.addAll(secondShape.boundingBoxes());
    return merge;
  }

  @Override
  public boolean isEmpty() {
    return firstShape.isEmpty() && secondShape.isEmpty();
  }

  @Override
  public boolean isCubic() {
    return (firstShape.isCubic() && secondShape.isEmpty())
      || (secondShape.isCubic() && firstShape.isEmpty());
  }

  @Override
  public boolean intersectsWith(BoundingBox boundingBox) {
    return firstShape.intersectsWith(boundingBox) || secondShape.intersectsWith(boundingBox);
  }

  @Override
  public String toString() {
    return firstShape + " and " + secondShape;
  }
}

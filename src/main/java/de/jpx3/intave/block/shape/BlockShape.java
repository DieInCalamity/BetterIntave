package de.jpx3.intave.block.shape;

import de.jpx3.intave.shade.BoundingBox;
import de.jpx3.intave.shade.Direction;

import java.util.List;

public interface BlockShape {
  double allowedOffset(Direction.Axis axis, BoundingBox entity, double offset);
  double min(Direction.Axis axis);
  double max(Direction.Axis axis);

  boolean intersectsWith(BoundingBox boundingBox);
  BlockShape contextualized(int posX, int posY, int posZ);
  BlockShape normalized(int posX, int posY, int posZ);

  @Deprecated
  List<BoundingBox> boundingBoxes();
  boolean isEmpty();
}

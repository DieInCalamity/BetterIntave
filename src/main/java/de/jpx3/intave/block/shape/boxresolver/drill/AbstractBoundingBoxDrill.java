package de.jpx3.intave.block.shape.boxresolver.drill;

import de.jpx3.intave.block.shape.boxresolver.ResolverPipeline;
import de.jpx3.intave.shade.BoundingBox;
import de.jpx3.intave.shade.link.WrapperLinkage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AbstractBoundingBoxDrill implements ResolverPipeline {
  protected List<BoundingBox> translate(List<?> bbs) {
    if (bbs.isEmpty()) {
      return Collections.emptyList();
    }
    List<BoundingBox> list = new ArrayList<>();
    for (Object bb : bbs) {
      list.add(WrapperLinkage.boundingBoxOf(bb));
    }
    return list;
  }

  protected List<BoundingBox> translateWithOffset(List<?> bbs, int posX, int posY, int posZ) {
    if (bbs.isEmpty()) {
      return Collections.emptyList();
    }
    List<BoundingBox> list = new ArrayList<>();
    for (Object bb : bbs) {
      list.add(BoundingBox.fromNative(bb).offset(posX, posY, posZ));
    }
    return list;
  }
}

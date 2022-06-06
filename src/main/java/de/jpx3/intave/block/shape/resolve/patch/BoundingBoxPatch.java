package de.jpx3.intave.block.shape.resolve.patch;

import de.jpx3.intave.block.shape.BlockShape;
import de.jpx3.intave.block.shape.BlockShapes;
import de.jpx3.intave.shade.BoundingBox;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

abstract class BoundingBoxPatch {
  private final Material[] material;

  protected BoundingBoxPatch(Material... materials) {
    this.material = materials;
  }

  protected BlockShape patch(World world, Player player, int posX, int posY, int posZ, Material type, int blockState, BlockShape shape) {
    // should be overriden
    List<BoundingBox> input = shape.boundingBoxes();
    List<BoundingBox> output = patch(world, player, posX, posY, posZ, type, blockState, input);

    if (input.equals(output)) {
      return shape;
    } else {
      return BlockShapes.shapeOf(output);
    }
  }

  @Deprecated
  protected List<BoundingBox> patch(World world, Player player, int posX, int posY, int posZ, Material type, int blockState, List<BoundingBox> bbs) {
    return bbs;
  }

  protected boolean requireNormalization() {
    return false;
  }

  public boolean appliesTo(Material material) {
    return Arrays.stream(this.material).anyMatch(matcher -> matcher == material);
  }
}
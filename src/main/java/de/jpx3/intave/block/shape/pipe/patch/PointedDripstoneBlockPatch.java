package de.jpx3.intave.block.shape.pipe.patch;

import de.jpx3.intave.annotate.KeepEnumInternalNames;
import de.jpx3.intave.block.type.BlockTypeAccess;
import de.jpx3.intave.block.variant.BlockVariant;
import de.jpx3.intave.block.variant.BlockVariantAccess;
import de.jpx3.intave.block.variant.BlockVariantRegister;
import de.jpx3.intave.math.MathHelper;
import de.jpx3.intave.shade.BoundingBox;
import de.jpx3.intave.shade.ClientMathHelper;
import de.jpx3.intave.shade.Direction;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class PointedDripstoneBlockPatch extends BoundingBoxPatch {
  private final BoundingBox TIP_MERGE_SHAPE = BoundingBox.originFromX16(5.0D, 0.0D, 5.0D, 11.0D, 16.0D, 11.0D);
  private final BoundingBox TIP_UP_SHAPE = BoundingBox.originFromX16(5.0D, 0.0D, 5.0D, 11.0D, 11.0D, 11.0D);
  private final BoundingBox TIP_DOWN_SHAPE = BoundingBox.originFromX16(5.0D, 5.0D, 5.0D, 11.0D, 16.0D, 11.0D);
  private final BoundingBox FRUSTUM_SHAPE = BoundingBox.originFromX16(4.0D, 0.0D, 4.0D, 12.0D, 16.0D, 12.0D);
  private final BoundingBox MIDDLE_SHAPE = BoundingBox.originFromX16(3.0D, 0.0D, 3.0D, 13.0D, 16.0D, 13.0D);
  private final BoundingBox BASE_SHAPE = BoundingBox.originFromX16(2.0D, 0.0D, 2.0D, 14.0D, 16.0D, 14.0D);
  private final BoundingBox DEF_SHAPE = BoundingBox.originFromX16(6.0D, 0.0D, 6.0D, 10.0D, 16.0D, 10.0D);

  @Override
  public List<BoundingBox> patch(World world, Player player, Block block, List<BoundingBox> bbs) {
    return patch(world, player, block.getX(), block.getY(), block.getZ(), BlockTypeAccess.typeAccess(block, player), BlockVariantAccess.variantAccess(block), bbs);
  }

  @Override
  public List<BoundingBox> patch(World world, Player player, int x, int y, int z, Material type, int variant, List<BoundingBox> bbs) {
    BlockVariant blockVariant = BlockVariantRegister.variantOf(type, variant);
    DripstoneThickness thickness = blockVariant.enumProperty(DripstoneThickness.class, "thickness");
    Direction verticalDirection = blockVariant.enumProperty(Direction.class, "vertical_direction");
    BoundingBox selected;
    switch (thickness) {
      case TIP_MERGE:
        selected = TIP_MERGE_SHAPE;
        break;
      case TIP:
        switch (verticalDirection) {
          case UP:
            selected = TIP_UP_SHAPE;
            break;
          case DOWN:
            selected = TIP_DOWN_SHAPE;
            break;
          default:
            throw new IllegalStateException("Unexpected vertical direction: " + verticalDirection);
        }
        break;
      case MIDDLE:
        selected = MIDDLE_SHAPE;
        break;
      case FRUSTUM:
        selected = FRUSTUM_SHAPE;
        break;
      case BASE:
        selected = BASE_SHAPE;
        break;
      default:
        selected = DEF_SHAPE;
    }
    float allowedOffset = 0.125f;
    long randomCoordinate = ClientMathHelper.coordinateRandom(x, 0, z);
    double offsetX = MathHelper.minmax(-allowedOffset, ((double) ((float) (randomCoordinate & 15L) / 15.0F) - 0.5D) * 0.5D, allowedOffset);
    double offsetZ = MathHelper.minmax(-allowedOffset, ((double) ((float) (randomCoordinate >> 8 & 15L) / 15.0F) - 0.5D) * 0.5D, allowedOffset);
    double offsetY = 0.0;
    BoundingBox boundingBox = selected.offset(offsetX, offsetY, offsetZ);
    boundingBox.makeOriginBox();
    return new ArrayList<>(Collections.singletonList(boundingBox));
  }

  @Override
  public boolean appliesTo(Material material) {
    return material.name().endsWith("_DRIPSTONE");
  }

  @KeepEnumInternalNames
  public enum DripstoneThickness {
    TIP_MERGE, TIP, FRUSTUM, MIDDLE, BASE
  }
}

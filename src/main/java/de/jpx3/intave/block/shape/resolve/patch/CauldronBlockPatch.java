package de.jpx3.intave.block.shape.resolve.patch;

import com.google.common.collect.Lists;
import de.jpx3.intave.block.shape.BlockShape;
import de.jpx3.intave.block.shape.BlockShapes;
import de.jpx3.intave.block.type.BlockTypeAccess;
import de.jpx3.intave.block.variant.BlockVariantAccess;
import de.jpx3.intave.shade.BoundingBox;
import de.jpx3.intave.user.User;
import de.jpx3.intave.user.UserRepository;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Locale;

final class CauldronBlockPatch extends BoundingBoxPatch {
  private static final BlockShape shape8;
  private static final BlockShape shape13;

  static {
    float wallWidth = 2f /*/ 16f*/;
    shape8 = BlockShapes.shapeOf(Lists.newArrayList(
      BoundingBox.originFromX16(0.0f, 0.0F, 0.0F, 16.0F, 5.0f, 16.0F),
      BoundingBox.originFromX16(0.0F, 0.0F, 0.0F, wallWidth, 16.0F, 16.0F),
      BoundingBox.originFromX16(0.0F, 0.0F, 0.0F, 16.0F, 16.0F, wallWidth),
      BoundingBox.originFromX16(16.0F - wallWidth, 0.0F, 0.0F, 16.0F, 16.0F, 16.0F),
      BoundingBox.originFromX16(0.0F, 0.0F, 16.0F - wallWidth, 16.0F, 16.0F, 16.0F)
    ));
    shape13 = BlockShapes.shapeOf(Lists.newArrayList(
      BoundingBox.originFromX16(0.0F, 0.0F, 0.0F, 16.0F, 4.0f, 16.0F),
      BoundingBox.originFromX16(0.0F, 0.0F, 0.0F, wallWidth, 16.0F, 16.0F),
      BoundingBox.originFromX16(0.0F, 0.0F, 0.0F, 16.0F, 16.0F, wallWidth),
      BoundingBox.originFromX16(16.0F - wallWidth, 0.0F, 0.0F, 16.0F, 16.0F, 16.0F),
      BoundingBox.originFromX16(0.0F, 0.0F, 16.0F - wallWidth, 16.0F, 16.0F, 16.0F)
    ));
  }

  @Override
  public List<BoundingBox> patch(World world, Player player, Block block, List<BoundingBox> bbs) {
    return patch(world, player, block.getX(), block.getY(), block.getZ(), BlockTypeAccess.typeAccess(block, player), BlockVariantAccess.variantAccess(block), bbs);
  }

  @Override
  protected List<BoundingBox> patch(World world, Player player, int posX, int posY, int posZ, Material type, int blockState, List<BoundingBox> bbs) {
    User user = UserRepository.userOf(player);
    if (!user.meta().protocol().waterUpdate()) {
      return shape8.boundingBoxes();
    } else {
      return shape13.boundingBoxes();
    }
  }

  @Override
  protected boolean requireNormalization() {
    return true;
  }

  @Override
  public boolean appliesTo(Material material) {
    return material.name().toLowerCase(Locale.ROOT).contains("cauldron");
  }
}

package de.jpx3.intave.block.shape.resolve.drill;

import de.jpx3.intave.block.shape.BlockShape;
import de.jpx3.intave.block.shape.BlockShapes;
import de.jpx3.intave.block.shape.resolve.drill.acbbs.v9AlwaysCollidingBoundingBox;
import de.jpx3.intave.klass.rewrite.PatchyAutoTranslation;
import de.jpx3.intave.shade.BoundingBox;
import net.minecraft.server.v1_9_R2.*;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_9_R2.CraftWorld;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@PatchyAutoTranslation
public final class v9ShapeDrill extends AbstractShapeDrill {
  private static final v9AlwaysCollidingBoundingBox ALWAYS_COLLIDING_BOX = new v9AlwaysCollidingBoundingBox();

  @Override
  @PatchyAutoTranslation
  public BlockShape resolve(World world, Player player, org.bukkit.Material type, int blockState, int posX, int posY, int posZ) {
    BlockPosition blockposition = new BlockPosition(posX, posY, posZ);
    IBlockData blockData = Block.getByCombinedId(type.getId() | (blockState & 0xF) << 12);
    if (blockData == null) {
      return BlockShapes.emptyShape();
    }
    List<AxisAlignedBB> bbs = new ArrayList<>();
    WorldServer worldServer = ((CraftWorld) world).getHandle();
    try {
      blockData.getBlock().a(blockData, worldServer, blockposition, ALWAYS_COLLIDING_BOX, bbs, null);
      return translate(bbs);
    } catch (IllegalArgumentException exception) {
      // we catch irregularities here elsewhere
      return BoundingBox
        // anything but a full or empty box
        .originFromBounds(0.25, 0.25, 0.25, 0.75, 0.75, 0.75)
        .contextualized(posX, posY, posZ);
    }
  }
}

package de.jpx3.intave.block.shape.boxresolver;

import de.jpx3.intave.block.access.BlockTypeAccess;
import de.jpx3.intave.block.shape.boxresolver.patcher.BoundingBoxBuilder;
import de.jpx3.intave.shade.BoundingBox;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;

public final class CorruptedFilteringPipe implements ResolverPipeline {
  private final ResolverPipeline forward;

  public CorruptedFilteringPipe(ResolverPipeline forward) {
    this.forward = forward;
  }

  @Override
  public List<BoundingBox> resolve(World world, Player player, Material type, int blockState, int posX, int posY, int posZ) {
    List<BoundingBox> corrupted = resolveCorrupted(type, blockState);
    return corrupted != null ? corrupted : forward.resolve(world, player, type, blockState, posX, posY, posZ);
  }

  @Override
  public void downstreamTypeReset(Material type) {
    forward.downstreamTypeReset(type);
  }

  public List<BoundingBox> resolveCorrupted(Material type, int data) {
    if (type == BlockTypeAccess.SKULL) {
      BoundingBoxBuilder builder = BoundingBoxBuilder.create();
      switch (data & 7) {
        case 1:
          builder.shape(0.25F, 0.0F, 0.25F, 0.75F, 0.5F, 0.75F); // up
          break;
        case 2:
          builder.shape(0.25F, 0.25F, 0.5F, 0.75F, 0.75F, 1.0F); // north
          break;
        case 3:
          builder.shape(0.25F, 0.25F, 0.0F, 0.75F, 0.75F, 0.5F); // south
          break;
        case 4:
          builder.shape(0.5F, 0.25F, 0.25F, 1.0F, 0.75F, 0.75F); // west
          break;
        case 5:
          builder.shape(0.0F, 0.25F, 0.25F, 0.5F, 0.75F, 0.75F); // east
          break;
      }
    }
    return null;
  }
}

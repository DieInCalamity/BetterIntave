package de.jpx3.intave.block.shape.boxresolver;

import de.jpx3.intave.shade.BoundingBox;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;

public interface ResolverPipeline {
  List<BoundingBox> resolve(World world, Player player, Material type, int blockState, int posX, int posY, int posZ);

  default void downstreamTypeReset(Material type) {}
}

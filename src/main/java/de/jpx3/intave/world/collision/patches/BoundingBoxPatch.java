package de.jpx3.intave.world.collision.patches;

import de.jpx3.intave.tools.wrapper.WrappedAxisAlignedBB;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Class generated using IntelliJ IDEA
 * Created by Richard Strunk 2021
 */

public abstract class BoundingBoxPatch {
  private final int blockId;

  protected BoundingBoxPatch(int blockId) {
    this.blockId = blockId;
  }

  public List<WrappedAxisAlignedBB> patch(World world, Player player, Block block, List<WrappedAxisAlignedBB> bbs) {
    return bbs;
  }

  public List<WrappedAxisAlignedBB> patch(World world, Player player, int typeId, int blockState, List<WrappedAxisAlignedBB> bbs) {
    return bbs;
  }

  public int blockId() {
    return blockId;
  }
}

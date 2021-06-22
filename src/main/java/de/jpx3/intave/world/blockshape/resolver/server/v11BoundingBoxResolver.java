package de.jpx3.intave.world.blockshape.resolver.server;

import de.jpx3.intave.patchy.annotate.PatchyAutoTranslation;
import de.jpx3.intave.tools.wrapper.WrappedAxisAlignedBB;
import de.jpx3.intave.tools.wrapper.link.WrapperLinkage;
import de.jpx3.intave.world.blockshape.resolver.BoundingBoxResolvePipeline;
import de.jpx3.intave.world.blockshape.resolver.acbbs.v11AlwaysCollidingBoundingBox;
import net.minecraft.server.v1_11_R1.*;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_11_R1.CraftChunk;
import org.bukkit.craftbukkit.v1_11_R1.CraftWorld;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@PatchyAutoTranslation
public final class v11BoundingBoxResolver implements BoundingBoxResolvePipeline {
  private final static v11AlwaysCollidingBoundingBox ALWAYS_COLLIDING_BOX = new v11AlwaysCollidingBoundingBox();

  @Override
  @PatchyAutoTranslation
  public List<WrappedAxisAlignedBB> nativeResolve(World world, Player player, org.bukkit.Material type, int blockState, int posX, int posY, int posZ) {
    Chunk handle = ((CraftChunk) world.getChunkAt(posX >> 4, posZ >> 4)).getHandle();
    BlockPosition blockposition = new BlockPosition(posX, posY, posZ);
    IBlockData blockData = handle.getBlockData(blockposition);
    if (blockData == null) {
      return Collections.emptyList();
    }
    List<AxisAlignedBB> bbs = new ArrayList<>();
    blockData.getBlock().a(
      blockData,
      ((CraftWorld) world).getHandle(),
      blockposition,
      ALWAYS_COLLIDING_BOX,
      bbs,
      null
    );
    return translate(bbs);
  }

  @Override
  @PatchyAutoTranslation
  public List<WrappedAxisAlignedBB> customResolve(World world, Player player, org.bukkit.Material type, int blockState, int posX, int posY, int posZ) {
    BlockPosition blockposition = new BlockPosition(posX, posY, posZ);
    IBlockData blockData = Block.getByCombinedId(type.getId() | (blockState & 0xF) << 12);
    List<AxisAlignedBB> bbs = new ArrayList<>();
    if (blockData == null) {
      return Collections.emptyList();
    }
    blockData.getBlock().a(
      blockData,
      ((CraftWorld) world).getHandle(),
      blockposition,
      ALWAYS_COLLIDING_BOX,
      bbs,
      null
    );
    return translate(bbs);
  }

  private List<WrappedAxisAlignedBB> translate(List<?> bbs) {
    if (bbs.isEmpty()) {
      return Collections.emptyList();
    }
    List<WrappedAxisAlignedBB> list = new ArrayList<>();
    for (Object bb : bbs) {
      list.add(WrapperLinkage.boundingBoxOf(bb));
    }
    return list;
  }
}

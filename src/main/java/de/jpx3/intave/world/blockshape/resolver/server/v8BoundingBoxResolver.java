package de.jpx3.intave.world.blockshape.resolver.server;

import de.jpx3.intave.patchy.annotate.PatchyAutoTranslation;
import de.jpx3.intave.tools.wrapper.WrappedAxisAlignedBB;
import de.jpx3.intave.tools.wrapper.link.WrapperLinkage;
import de.jpx3.intave.world.blockshape.resolver.BoundingBoxResolvePipeline;
import de.jpx3.intave.world.blockshape.resolver.acbbs.v8AlwaysCollidingBoundingBox;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftChunk;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@PatchyAutoTranslation
public final class v8BoundingBoxResolver implements BoundingBoxResolvePipeline {
  private final static v8AlwaysCollidingBoundingBox ALWAYS_COLLIDING_BOX = new v8AlwaysCollidingBoundingBox();

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
      ((CraftWorld) world).getHandle(),
      blockposition,
      blockData,
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
      ((CraftWorld) world).getHandle(),
      blockposition,
      blockData,
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

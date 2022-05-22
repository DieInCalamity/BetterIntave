package de.jpx3.intave.block.shape.resolve;

import de.jpx3.intave.block.shape.BlockShape;
import de.jpx3.intave.block.shape.BlockShapes;
import de.jpx3.intave.block.shape.ShapeResolverPipeline;
import de.jpx3.intave.diagnostic.ShapeAccessFlowStudy;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Set;

final class CubeMemoryPipe implements ShapeResolverPipeline {
  private final ShapeResolverPipeline forward;
  private final Set<Material> solidMaterials = UnsafeCopyOnWriteEnumSet.of(Material.class);
  private final Set<Material> otherMaterials = UnsafeCopyOnWriteEnumSet.of(Material.class);

  public CubeMemoryPipe(ShapeResolverPipeline forward) {
    this.forward = forward;
    this.preloadBlocks();
  }

  public void preloadBlocks() {
    for (Material type : Material.values()) {
      String typeName = type.name();
      if (typeName.contains("SLAB") /* can be doubled */) {
        otherMaterials.add(type);
      }
    }
  }

  @Override
  public BlockShape resolve(World world, Player player, Material type, int variantIndex, int posX, int posY, int posZ) {
    if (knownToBeCubic(type)) {
      ShapeAccessFlowStudy.incremDynamic();
      return BlockShapes.cubicShapeAt(posX, posY, posZ);
    } else if (knownToBeNonCubic(type)) {
      return forward.resolve(world, player, type, variantIndex, posX, posY, posZ);
    }
    BlockShape shape = forward.resolve(world, player, type, variantIndex, posX, posY, posZ);
    if (isInLoadedChunk(world, posX, posZ)) {
      if (shape.isCubic()) {
        downstreamTypeReset(type); // flush downstream type
        solidMaterials.add(type);
      } else {
        otherMaterials.add(type);
      }
    }
    return shape;
  }

  public boolean knownToBeCubic(Material type) {
    return solidMaterials.contains(type);
  }

  public boolean knownToBeNonCubic(Material type) {
    return otherMaterials.contains(type);
  }

  @Override
  public void downstreamTypeReset(Material type) {
    solidMaterials.remove(type);
    otherMaterials.remove(type);
    forward.downstreamTypeReset(type);
  }

  public static boolean isInLoadedChunk(World world, int x, int z) {
    return world.isChunkLoaded(x >> 4, z >> 4);
  }
}
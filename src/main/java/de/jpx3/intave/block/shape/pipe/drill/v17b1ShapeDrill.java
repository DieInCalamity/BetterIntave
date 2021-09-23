package de.jpx3.intave.block.shape.pipe.drill;

import de.jpx3.intave.block.shape.BlockShape;
import de.jpx3.intave.block.shape.BlockShapes;
import de.jpx3.intave.block.variant.BlockVariantRegister;
import de.jpx3.intave.clazz.rewrite.PatchyAutoTranslation;
import net.minecraft.core.BlockPosition;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapes;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.entity.Player;

import java.util.List;

@PatchyAutoTranslation
public final class v17b1ShapeDrill extends AbstractShapeDrill {
  @Override
  @PatchyAutoTranslation
  public BlockShape resolve(World world, Player player, Material type, int blockState, int posX, int posY, int posZ) {
    WorldServer handle = ((CraftWorld) world).getHandle();
    BlockPosition blockPosition = new BlockPosition(posX, posY, posZ);
    IBlockData blockData = (IBlockData) BlockVariantRegister.rawBlockDataOf(type, blockState);
    if (blockData == null) {
      return BlockShapes.emptyShape();
    }
    IBlockAccess blockAccess = handle.getChunkProvider().c(posX >> 4, posZ >> 4);
    if (blockAccess == null) {
      return BlockShapes.emptyShape();
    }
    VoxelShape collisionShape = blockData.getCollisionShape(blockAccess, blockPosition);
    // check if shape is static empty
    if (VoxelShapes.a() == collisionShape) {
      return BlockShapes.emptyShape();
    }
    // check if shape is static cube
    if (VoxelShapes.b() == collisionShape) {
      return BlockShapes.cubicShapeAt(posX, posY, posZ);
    }
    // convert complex blocks to native BBs
    List<AxisAlignedBB> nativeBoxes = collisionShape.toList();
    return translateWithOffset(nativeBoxes, posX, posY, posZ);
  }
}
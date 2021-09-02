package de.jpx3.intave.block.shape.boxresolver.drill;

import de.jpx3.intave.clazz.rewrite.PatchyAutoTranslation;
import de.jpx3.intave.shade.BoundingBox;
import net.minecraft.server.v1_13_R2.*;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_13_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_13_R2.util.CraftMagicNumbers;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

@PatchyAutoTranslation
public final class v13BoundingBoxDrill extends AbstractBoundingBoxDrill {
  @Override
  @PatchyAutoTranslation
  public List<BoundingBox> resolve(World world, Player player, Material type, int blockState, int posX, int posY, int posZ) {
    WorldServer handle = ((CraftWorld) world).getHandle();
    BlockPosition blockPosition = new BlockPosition(posX, posY, posZ);
    IBlockData blockData = CraftMagicNumbers.getBlock(type, (byte) blockState);
    if (blockData == null) {
      return Collections.emptyList();
    }
    VoxelShape collisionShape = blockData.getCollisionShape(handle, blockPosition);
    List<AxisAlignedBB> nativeBoxes = collisionShape.d();
    return translateWithOffset(nativeBoxes, posX, posY, posZ);
  }
}
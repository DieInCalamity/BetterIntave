package de.jpx3.intave.world.blockaccess;

import com.comphenix.protocol.wrappers.BlockPosition;
import de.jpx3.intave.patchy.annotate.PatchyAutoTranslation;
import de.jpx3.intave.user.User;
import de.jpx3.intave.user.UserRepository;
import net.minecraft.server.v1_14_R1.Chunk;
import net.minecraft.server.v1_14_R1.IBlockData;
import net.minecraft.server.v1_14_R1.Item;
import net.minecraft.server.v1_14_R1.WorldServer;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_14_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@PatchyAutoTranslation
public final class v14BlockAccessor implements BlockAccessor {
  @Override
  @PatchyAutoTranslation
  public float blockDamage(Player player, ItemStack itemInHand, BlockPosition nativeBlockPosition) {
    WorldServer worldServer = ((CraftWorld) player.getWorld()).getHandle();
    Chunk chunk = worldServer.getChunkIfLoaded(nativeBlockPosition.getX() >> 4, nativeBlockPosition.getZ() >> 4);
    if (chunk == null) {
      return 0.0f;
    }
    net.minecraft.server.v1_14_R1.BlockPosition blockPosition = new net.minecraft.server.v1_14_R1.BlockPosition(nativeBlockPosition.getX(), nativeBlockPosition.getY(), nativeBlockPosition.getZ());
    IBlockData blockData = chunk.getType(blockPosition);
    return blockData.getBlock().getDamage(blockData, ((CraftPlayer) player).getHandle(), worldServer, blockPosition);
  }

  @Override
  @PatchyAutoTranslation
  public boolean replacementPlace(World world, Player player, BlockPosition nativeBlockPosition) {
    WorldServer worldServer = ((CraftWorld) world).getHandle();
    Chunk chunk = worldServer.getChunkIfLoaded(nativeBlockPosition.getX() >> 4, nativeBlockPosition.getZ() >> 4);
    if (chunk == null) {
      return false;
    }
    User user = UserRepository.userOf(player);
    int heldItemType = user.meta().inventory().handSlot();
    net.minecraft.server.v1_14_R1.BlockPosition blockPosition = new net.minecraft.server.v1_14_R1.BlockPosition(nativeBlockPosition.getX(), nativeBlockPosition.getY(), nativeBlockPosition.getZ());
    IBlockData blockData = chunk.getType(blockPosition);
    Item heldItem = ((CraftPlayer) player).getHandle().inventory.getItem(heldItemType).getItem();
    return blockData.getMaterial().isReplaceable() && blockData.getBlock().getItem().getItem() == heldItem;
  }
}

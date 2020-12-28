package de.jpx3.intave.detect.checks.world;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import de.jpx3.intave.detect.IntaveMetaCheck;
import de.jpx3.intave.detect.checks.world.interaction.BlockRaytracer;
import de.jpx3.intave.event.bukkit.BukkitEventSubscription;
import de.jpx3.intave.event.packet.PacketDescriptor;
import de.jpx3.intave.event.packet.PacketSubscription;
import de.jpx3.intave.event.packet.Sender;
import de.jpx3.intave.tools.MathHelper;
import de.jpx3.intave.tools.wrapper.WrappedBlockPosition;
import de.jpx3.intave.tools.wrapper.WrappedEnumDirection;
import de.jpx3.intave.tools.wrapper.WrappedMovingObjectPosition;
import de.jpx3.intave.tools.wrapper.WrappedVector;
import de.jpx3.intave.user.User;
import de.jpx3.intave.user.UserCustomCheckMeta;
import de.jpx3.intave.user.UserMetaMovementData;
import de.jpx3.intave.user.UserRepository;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

import java.lang.reflect.InvocationTargetException;

public final class InteractionRaytrace extends IntaveMetaCheck<InteractionRaytrace.InteractionMeta> {
  public InteractionRaytrace() {
    super("InteractionRaytrace", "interactionRaytrace", InteractionMeta.class);
  }

  @PacketSubscription(
    packets = {
      @PacketDescriptor(sender = Sender.CLIENT, packetName = "BLOCK_PLACE")
    }
  )
  public void on(PacketEvent event) {
    Player player = event.getPlayer();
    PacketContainer packet = event.getPacket();
    BlockPosition blockPosition = packet.getBlockPositionModifier().readSafely(0);

    if(blockPosition == null) {
      return;
    }

    int enumDirection = packet.getIntegers().readSafely(0);
    if(enumDirection == 255) {
      return;
    }

    User user = userOf(player);
    UserMetaMovementData userMetaMovementData = user.meta().movementData();
    World world = player.getWorld();
    Location targetLocation = blockPosition.toLocation(world);

    Location playerLocation = UserRepository.userOf(player).meta().movementData().verifiedLocation.clone();
    playerLocation.setYaw(userMetaMovementData.rotationYaw);
    playerLocation.setPitch(userMetaMovementData.rotationPitch);
    WrappedMovingObjectPosition raycastResult = BlockRaytracer.resolveBlockInLineOfSight(player, playerLocation);
    boolean hitMiss = raycastResult == null || raycastResult.hitVec == WrappedVector.ZERO;

    WrappedBlockPosition raycastVector = hitMiss ? WrappedBlockPosition.ORIGIN : raycastResult.getBlockPos();
    Location raycastLocation = raycastVector.toLocation(world);

    boolean hasPlaceableBlockInHand = player.getItemInHand().getType().isBlock();

    double vlChange = hasPlaceableBlockInHand ? Math.min(hitMiss ? 2 : enumDirection != raycastResult.sideHit.getIndex() ? 1.5 : raycastLocation.distance(targetLocation) - 0.5, 2) : 0;
    double vl =
      metaOf(player).localVL =
        MathHelper.minmax(
          0,
          metaOf(player).localVL + vlChange,
          30
        );

//    player.sendMessage(vl + " " + (vlChange > 0 ? "+" : "") + vlChange);

    if(vl >= 10 || !hasPlaceableBlockInHand) {
      if(hitMiss) {
        player.updateInventory();
        refreshBlock(player, targetLocation);
        for (WrappedEnumDirection direction : WrappedEnumDirection.values()) {
          Location placedBlock = targetLocation.clone().add(direction.getDirectionVec().convertToBukkitVec());
          refreshBlock(player, placedBlock);
        }
        event.setCancelled(true);
      } else if(raycastLocation.distance(targetLocation) > 0 || enumDirection != raycastResult.sideHit.getIndex()) {
        BlockPosition blockPosition1 = new BlockPosition(raycastLocation.getBlockX(), raycastLocation.getBlockY(), raycastLocation.getBlockZ());
        packet.getIntegers().write(0, raycastResult.sideHit.getIndex());
        packet.getBlockPositionModifier().write(0, blockPosition1);
        refreshBlock(player, targetLocation);
        for (WrappedEnumDirection direction : WrappedEnumDirection.values()) {
          Location placedBlock = targetLocation.clone().add(direction.getDirectionVec().convertToBukkitVec());
          refreshBlock(player, placedBlock);
        }
      }
    }
  }

  private void refreshBlock(Player player, Location location) {
    PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.BLOCK_CHANGE);
    Block block = location.getBlock();
    WrappedBlockData blockData = WrappedBlockData.createData(block.getType(), block.getData());
    BlockPosition position = new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    packet.getBlockData().write(0, blockData);
    packet.getBlockPositionModifier().write(0, position);
    try {
      ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
    } catch (InvocationTargetException exception) {
      exception.printStackTrace();
    }
  }

  @BukkitEventSubscription
  public void on(BlockBreakEvent event) {
    Player player = event.getPlayer();
    WrappedMovingObjectPosition movingObjectPosition = BlockRaytracer.resolveBlockInLineOfSight(player, player.getLocation());

    boolean invalid = false;
    if(movingObjectPosition == null) {
      invalid = true;
    } else {
      Location location = movingObjectPosition.getBlockPos().toLocation(player.getWorld());
      double distance = location.distance(event.getBlock().getLocation());
//      player.sendMessage(String.valueOf(distance) + location.getBlock());
      if(distance > 0) {
        invalid = true;
      }
    }
    if(invalid) {
      event.setCancelled(true);
    }
  }

  public static class InteractionMeta extends UserCustomCheckMeta {
    double localVL; // please conv me into general vl for check
  }
}

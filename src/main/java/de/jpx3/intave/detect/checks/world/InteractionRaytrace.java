package de.jpx3.intave.detect.checks.world;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import de.jpx3.intave.IntavePlugin;
import de.jpx3.intave.detect.IntaveMetaCheck;
import de.jpx3.intave.detect.checks.world.interaction.BlockRaytracer;
import de.jpx3.intave.event.packet.ListenerPriority;
import de.jpx3.intave.event.packet.PacketDescriptor;
import de.jpx3.intave.event.packet.PacketSubscription;
import de.jpx3.intave.event.packet.Sender;
import de.jpx3.intave.tools.sync.Synchronizer;
import de.jpx3.intave.tools.wrapper.WrappedBlockPosition;
import de.jpx3.intave.tools.wrapper.WrappedEnumDirection;
import de.jpx3.intave.tools.wrapper.WrappedMovingObjectPosition;
import de.jpx3.intave.tools.wrapper.WrappedVector;
import de.jpx3.intave.user.User;
import de.jpx3.intave.user.UserCustomCheckMeta;
import de.jpx3.intave.user.UserMetaMovementData;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import static com.comphenix.protocol.wrappers.EnumWrappers.PlayerDigType.STOP_DESTROY_BLOCK;

public final class InteractionRaytrace extends IntaveMetaCheck<InteractionRaytrace.InteractionMeta> {
  private final IntavePlugin plugin;

  public InteractionRaytrace(IntavePlugin plugin) {
    super("InteractionRaytrace", "interactionRaytrace", InteractionMeta.class);
    this.plugin = plugin;
  }

  // break
  // 1st invalid -> save packet and queue packet for 2nd move (--> cancel)

  // interact
  // 1st invalid -> save raytrace and queue packet for 2nd move packet (--> raytrace override)

  // place
  // 1st invalid -> save raytrace and queue packet for 2nd move packet (--> raytrace override)


  @PacketSubscription(
    packets = {
      @PacketDescriptor(sender = Sender.CLIENT, packetName = "BLOCK_PLACE")
    }
  )
  public void receiveInteraction(PacketEvent event) {
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
    InteractionMeta interactionMeta = metaOf(user);
    if(interactionMeta.excludeCurrentPacket) {
      return;
    }
    UserMetaMovementData movementData = user.meta().movementData();
    World world = player.getWorld();
    Location targetLocation = blockPosition.toLocation(world);
    Location playerLocation = movementData.verifiedLocation().clone();
    playerLocation.setYaw(movementData.rotationYaw);
    playerLocation.setPitch(movementData.rotationPitch);
    WrappedMovingObjectPosition raycastResult = BlockRaytracer.resolveBlockInLineOfSight(player, playerLocation);
    boolean hitMiss = raycastResult == null || raycastResult.hitVec == WrappedVector.ZERO;
    WrappedBlockPosition raycastVector = hitMiss ? WrappedBlockPosition.ORIGIN : raycastResult.getBlockPos();
    Location raycastLocation = raycastVector.toLocation(world);
    boolean isPlacement = player.getItemInHand().getType().isBlock();
    if((raycastResult == null || enumDirection != raycastResult.sideHit.getIndex()) || raycastLocation.distance(targetLocation) > 0) {
      interactionMeta.traceReportList.add(
        new TraceReport(
          raycastResult, blockPosition, enumDirection, packet.deepClone(),
          playerLocation, isPlacement ? InteractionType.PLACE : InteractionType.INTERACT
        )
      );
      event.setCancelled(true);
    }
  }

  @PacketSubscription(
    packets = {
      @PacketDescriptor(sender = Sender.CLIENT, packetName = "BLOCK_DIG")
    }
  )
  public void receiveBreak(PacketEvent event) {
    Player player = event.getPlayer();
    PacketContainer packet = event.getPacket();
    BlockPosition blockPosition = packet.getBlockPositionModifier().readSafely(0);
    if(blockPosition == null) {
      return;
    }
    int enumDirection = packet.getDirections().read(0).ordinal();
    if(enumDirection == 255) {
      return;
    }
    EnumWrappers.PlayerDigType playerDigType = packet.getPlayerDigTypes().readSafely(0);
    if(!(/*playerDigType == START_DESTROY_BLOCK || */playerDigType == STOP_DESTROY_BLOCK)) {
      return;
    }
    User user = userOf(player);
    InteractionMeta interactionMeta = metaOf(user);
    if(interactionMeta.excludeCurrentPacket) {
      return;
    }
    UserMetaMovementData movementData = user.meta().movementData();
    World world = player.getWorld();
    Location targetLocation = blockPosition.toLocation(world);
    Location playerLocation = movementData.verifiedLocation().clone();
    playerLocation.setYaw(movementData.rotationYaw);
    playerLocation.setPitch(movementData.rotationPitch);
    WrappedMovingObjectPosition raycastResult = BlockRaytracer.resolveBlockInLineOfSight(player, playerLocation);
    boolean hitMiss = raycastResult == null || raycastResult.hitVec == WrappedVector.ZERO;
    WrappedBlockPosition raycastVector = hitMiss ? WrappedBlockPosition.ORIGIN : raycastResult.getBlockPos();
    Location raycastLocation = raycastVector.toLocation(world);
    if((raycastResult == null || enumDirection != raycastResult.sideHit.getIndex()) || raycastLocation.distance(targetLocation) > 0) {
//      player.sendMessage("Invalid break, retracing next move..");
      interactionMeta.traceReportList.add(
        new TraceReport(
          raycastResult, blockPosition, playerDigType.ordinal(),
          packet.deepClone(), playerLocation, InteractionType.BREAK
        )
      );
      event.setCancelled(true);
    }
  }

  @PacketSubscription(
    priority = ListenerPriority.MONITOR, // last one to work with position
    packets = {
      @PacketDescriptor(sender = Sender.CLIENT, packetName = "LOOK"),
      @PacketDescriptor(sender = Sender.CLIENT, packetName = "POSITION"),
      @PacketDescriptor(sender = Sender.CLIENT, packetName = "POSITION_LOOK")
    }
  )
  public void receivePositionUpdate(PacketEvent event) {
    Player player = event.getPlayer();
    World world = player.getWorld();
    User user = userOf(player);
    UserMetaMovementData movementData = user.meta().movementData();
    InteractionMeta interactionMeta = metaOf(user);
    List<TraceReport> traceReportList = interactionMeta.traceReportList;

    if(traceReportList.isEmpty()) {
      return;
    }

    Location playerLocation = movementData.verifiedLocation();
    playerLocation.setYaw(movementData.rotationYaw);
    playerLocation.setPitch(movementData.rotationPitch);

    WrappedMovingObjectPosition raycastResult = BlockRaytracer.resolveBlockInLineOfSight(player, playerLocation);
    boolean hitMiss = raycastResult == null || raycastResult.hitVec == WrappedVector.ZERO;
    WrappedBlockPosition raycastVector = hitMiss ? WrappedBlockPosition.ORIGIN : raycastResult.getBlockPos();
    Location raycastLocation = raycastVector.toLocation(world);

    for (TraceReport traceReport : traceReportList) {
      Location targetLocation = traceReport.targetBlock.toLocation(world);

      boolean invalid = hitMiss ||
          raycastLocation.distance(targetLocation) > 0 ||
          traceReport.targetDirection != raycastResult.sideHit.getIndex();

      if(invalid && traceReport.type() == InteractionType.BREAK) {
        String typeName = targetLocation.getBlock().getType().name().toLowerCase().replace("_", "").replace("block", "");
        plugin.retributionService().markPlayer(player, 0, name(), "broke a " + typeName + " block out of sight");
      }

      ResponseType response = traceReport.type().response();

      if(response == ResponseType.RAYTRACE_CAST) {
        if(hitMiss) {
          player.updateInventory();
          refreshBlock(player, targetLocation);
          for (WrappedEnumDirection direction : WrappedEnumDirection.values()) {
            Location placedBlock = targetLocation.clone().add(direction.getDirectionVec().convertToBukkitVec());
            refreshBlock(player, placedBlock);
          }
        } else {
          PacketContainer packet = traceReport.thePacket();
          BlockPosition blockPosition1 = new BlockPosition(raycastLocation.getBlockX(), raycastLocation.getBlockY(), raycastLocation.getBlockZ());
          packet.getIntegers().write(0, raycastResult.sideHit.getIndex());
          packet.getBlockPositionModifier().write(0, blockPosition1);
          refreshBlock(player, targetLocation);
          for (WrappedEnumDirection direction : WrappedEnumDirection.values()) {
            Location placedBlock = targetLocation.clone().add(direction.getDirectionVec().convertToBukkitVec());
            refreshBlock(player, placedBlock);
          }
          Synchronizer.packetSynchronize(() -> receiveExcludedPacket(player, packet));
        }
      } else {
        if (!invalid) {
          Synchronizer.packetSynchronize(() -> receiveExcludedPacket(event.getPlayer(), traceReport.thePacket));
        } else {
          player.updateInventory();
          refreshBlock(player, targetLocation);
          for (WrappedEnumDirection direction : WrappedEnumDirection.values()) {
            Location placedBlock = targetLocation.clone().add(direction.getDirectionVec().convertToBukkitVec());
            refreshBlock(player, placedBlock);
          }
        }
      }
    }

    traceReportList.clear();
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

  private void receiveExcludedPacket(Player player, PacketContainer packet) {
    try {
      InteractionMeta interactionMeta = metaOf(player);
      interactionMeta.excludeCurrentPacket = true;
      ProtocolLibrary.getProtocolManager().recieveClientPacket(player, packet);
      interactionMeta.excludeCurrentPacket = false;
    } catch (InvocationTargetException | IllegalAccessException exception) {
      exception.printStackTrace();
    }
  }

  public static class InteractionMeta extends UserCustomCheckMeta {
    final List<TraceReport> traceReportList = new ArrayList<>();
    volatile boolean excludeCurrentPacket;
  }

  public enum InteractionType {
    PLACE(ResponseType.RAYTRACE_CAST),
    BREAK(ResponseType.CANCEL),
    INTERACT(ResponseType.RAYTRACE_CAST);

    final ResponseType response;

    InteractionType(ResponseType response) {
      this.response = response;
    }

    public ResponseType response() {
      return response;
    }
  }

  public enum ResponseType {
    RAYTRACE_CAST,
    CANCEL
  }

  public static class TraceReport {
    private final WrappedMovingObjectPosition raytraceResult;
    private final BlockPosition targetBlock;
    private final int targetDirection;
    private final PacketContainer thePacket;
    private final Location contextPosition;
    private final InteractionType type;

    public TraceReport(
      WrappedMovingObjectPosition raytraceResult,
      BlockPosition targetBlock, int targetDirection, PacketContainer thePacket,
      Location contextPosition,
      InteractionType type
    ) {
      this.raytraceResult = raytraceResult;
      this.targetBlock = targetBlock;
      this.targetDirection = targetDirection;
      this.thePacket = thePacket;
      this.contextPosition = contextPosition.clone();
      this.type = type;
    }

    public WrappedMovingObjectPosition raytraceResult() {
      return raytraceResult;
    }

    public BlockPosition targetBlock() {
      return targetBlock;
    }

    public int targetDirection() {
      return targetDirection;
    }

    public PacketContainer thePacket() {
      return thePacket;
    }

    public Location contextPosition() {
      return contextPosition;
    }

    public InteractionType type() {
      return type;
    }
  }
}

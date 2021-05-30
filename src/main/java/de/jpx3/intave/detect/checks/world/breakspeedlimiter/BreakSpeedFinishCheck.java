package de.jpx3.intave.detect.checks.world.breakspeedlimiter;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import de.jpx3.intave.IntavePlugin;
import de.jpx3.intave.detect.IntaveMetaCheckPart;
import de.jpx3.intave.detect.checks.world.BreakSpeedLimiter;
import de.jpx3.intave.event.packet.ListenerPriority;
import de.jpx3.intave.event.packet.PacketSubscription;
import de.jpx3.intave.event.violation.Violation;
import de.jpx3.intave.event.violation.ViolationContext;
import de.jpx3.intave.event.violation.ViolationProcessor;
import de.jpx3.intave.reflect.ReflectiveEntityAccess;
import de.jpx3.intave.tools.AccessHelper;
import de.jpx3.intave.tools.sync.Synchronizer;
import de.jpx3.intave.tools.wrapper.WrappedEnumDirection;
import de.jpx3.intave.user.*;
import de.jpx3.intave.world.blockaccess.BlockDataAccess;
import de.jpx3.intave.world.blockaccess.BukkitBlockAccess;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;

import static de.jpx3.intave.event.packet.PacketId.Client.*;

public final class BreakSpeedFinishCheck extends IntaveMetaCheckPart<BreakSpeedLimiter, BreakSpeedFinishCheck.BreakSpeedFinishMeta> {
  public BreakSpeedFinishCheck(BreakSpeedLimiter parentCheck) {
    super(parentCheck, BreakSpeedFinishMeta.class);
  }

  @PacketSubscription(
    priority = ListenerPriority.LOW,
    packetsIn = {
      POSITION, POSITION_LOOK, LOOK, FLYING, VEHICLE_MOVE
    }
  )
  public void tickUpdate(PacketEvent event) {
    Player player = event.getPlayer();
    User user = userOf(player);
    BreakSpeedFinishMeta meta = metaOf(user);

    UserMetaClientData clientData = user.meta().clientData();
    UserMetaInventoryData inventoryData = user.meta().inventoryData();

    if (meta.breakProcess) {
      ItemStack itemInHand = inventoryData.heldItem();
      BlockPosition blockPosition = meta.targetBlockPosition;

      float blockDamage = clientData.flyingPacketStream()
        ? BlockDataAccess.blockDamage(player, itemInHand, blockPosition)
        : resolveBlockDamageOnGround(player, itemInHand, blockPosition);
      meta.curBlockDamageMP += blockDamage;
      meta.maximumBlockDamage = Math.max(meta.maximumBlockDamage, blockDamage);
    }

    if (meta.balance > 0 && !event.isCancelled()) {
      meta.balance -= 0.005;
    }
  }

  @PacketSubscription(
    priority = ListenerPriority.LOW,
    packetsIn = {
      BLOCK_DIG
    }
  )
  public void receiveBlockAction(PacketEvent event) {
    Player player = event.getPlayer();
    User user = userOf(player);
    BreakSpeedFinishMeta meta = metaOf(user);
    UserMetaClientData clientData = user.meta().clientData();
    UserMetaInventoryData inventoryData = user.meta().inventoryData();
    UserMetaMovementData movementData = user.meta().movementData();

    ItemStack heldItem = inventoryData.heldItem();
    PacketContainer packet = event.getPacket();
    BlockPosition blockPosition = packet.getBlockPositionModifier().read(0);
    EnumWrappers.PlayerDigType digType = packet.getPlayerDigTypes().read(0);

    switch (digType) {
      case START_DESTROY_BLOCK: {
        float blockDamage = BlockDataAccess.blockDamage(player, heldItem, blockPosition);
        meta.breakProcess = true;
        meta.breakProcessStartTime = AccessHelper.now();
        meta.curBlockDamageMP = blockDamage;
        meta.targetBlockPosition = blockPosition;
        meta.maximumBlockDamage = blockDamage;
        break;
      }
      case STOP_DESTROY_BLOCK: {
        if (clientData.flyingPacketStream()) {
          float blockDamageDealt = meta.curBlockDamageMP;
          if (blockDamageDealt < 0.79 && meta.balance++ >= 2) { // ~79%
            String message = "finished breaking-process too quickly";
            String percentage = (int)(blockDamageDealt * 100d) + "%";
            String details = "at " + percentage;

            ViolationProcessor violationProcessor = IntavePlugin.singletonInstance().violationProcessor();
            Violation violation = Violation.builderFor(BreakSpeedLimiter.class)
              .forPlayer(player).withMessage(message).withDetails(details)
              .withVL(10)
              .build();
            ViolationContext violationContext = violationProcessor.processViolation(violation);
            if (violationContext.shouldCounterThreat()) {
              event.setCancelled(true);
              refreshBlocksAround(player, blockPosition.toLocation(player.getWorld()));
            }
          }
        } else {
          long milliseconds = resolveMillisecondsOf(meta.maximumBlockDamage);
          long receivedMilliseconds = AccessHelper.now() - meta.breakProcessStartTime;
          long exceeded = milliseconds - receivedMilliseconds;

          if (exceeded > 100 && meta.balance++ >= 2) {
            String message = "finished breaking-process too quickly";
            String details = exceeded + "ms faster than expected";
            ViolationProcessor violationProcessor = IntavePlugin.singletonInstance().violationProcessor();
            Violation violation = Violation.builderFor(BreakSpeedLimiter.class)
              .forPlayer(player).withMessage(message).withDetails(details)
              .withVL(10)
              .build();
            ViolationContext violationContext = violationProcessor.processViolation(violation);
            if (violationContext.shouldCounterThreat()) {
              event.setCancelled(true);
              refreshBlocksAround(player, blockPosition.toLocation(player.getWorld()));
            }
          }
        }

        meta.curBlockDamageMP = 0f;
        meta.targetBlockPosition = null;
        meta.breakProcess = false;
        meta.maximumBlockDamage = Float.MIN_VALUE;
        break;
      }
    }
  }

  private void refreshBlocksAround(Player player, Location targetLocation) {
    Synchronizer.synchronize(() -> {
      player.updateInventory();
      refreshBlock(player, targetLocation);
      for (WrappedEnumDirection direction : WrappedEnumDirection.values()) {
        Location placedBlock = targetLocation.clone().add(direction.getDirectionVec().convertToBukkitVec());
        refreshBlock(player, placedBlock);
      }
    });
  }

  private void refreshBlock(Player player, Location location) {
    PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.BLOCK_CHANGE);
    Block block = BukkitBlockAccess.blockAccess(location);
    WrappedBlockData blockData = WrappedBlockData.createData(block.getType(), BlockDataAccess.dataIndexOf(block));
    BlockPosition position = new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    packet.getBlockData().write(0, blockData);
    packet.getBlockPositionModifier().write(0, position);
    try {
      ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
    } catch (InvocationTargetException exception) {
      exception.printStackTrace();
    }
  }

  private long resolveMillisecondsOf(float blockDamage) {
    if (blockDamage == 0) {
      return 0;
    }
    long time = 0;
    float curBlockDamageMP = 0f;
    int iterationCountdown = 100;
    while (curBlockDamageMP < 1f) {
      curBlockDamageMP += blockDamage;
      time += 50;
      if (--iterationCountdown < 0) {
        break;
      }
    }
    return time;
  }

  private float resolveBlockDamageOnGround(
    Player player,
    ItemStack itemInHand,
    BlockPosition blockPosition
  ) {
    boolean onGroundBefore = ReflectiveEntityAccess.onGround(player);
    ReflectiveEntityAccess.setOnGround(player, true);
    float blockDamage = BlockDataAccess.blockDamage(player, itemInHand, blockPosition);
    ReflectiveEntityAccess.setOnGround(player, onGroundBefore);
    return blockDamage;
  }

  public static final class BreakSpeedFinishMeta extends UserCustomCheckMeta {
    public BlockPosition targetBlockPosition;
    public float curBlockDamageMP = 0f;
    public float maximumBlockDamage;
    public boolean breakProcess;
    public long breakProcessStartTime;
    public double balance;
  }
}
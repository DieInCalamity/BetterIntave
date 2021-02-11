package de.jpx3.intave.detect.checks.world;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.EnumWrappers;
import de.jpx3.intave.IntavePlugin;
import de.jpx3.intave.detect.IntaveMetaCheck;
import de.jpx3.intave.event.packet.ListenerPriority;
import de.jpx3.intave.event.packet.PacketDescriptor;
import de.jpx3.intave.event.packet.PacketSubscription;
import de.jpx3.intave.event.packet.Sender;
import de.jpx3.intave.tools.AccessHelper;
import de.jpx3.intave.user.User;
import de.jpx3.intave.user.UserCustomCheckMeta;
import de.jpx3.intave.user.UserMetaInventoryData;
import de.jpx3.intave.user.UserRepository;
import de.jpx3.intave.world.block.BlockDataAccess;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class BreakSpeedLimiter extends IntaveMetaCheck<BreakSpeedLimiter.BreakSpeedLimiterMeta> {
  private final IntavePlugin plugin;

  public BreakSpeedLimiter(IntavePlugin plugin) {
    super("BreakSpeedLimiter", "breakspeedlimiter", BreakSpeedLimiterMeta.class);
    this.plugin = plugin;
  }

  @PacketSubscription(
    priority = ListenerPriority.LOWEST,
    packets = {
      @PacketDescriptor(sender = Sender.CLIENT, packetName = "BLOCK_DIG")
    }
  )
  public void receiveBlockAction(PacketEvent event) {
    Player player = event.getPlayer();
    User user = UserRepository.userOf(player);
    BreakSpeedLimiterMeta meta = metaOf(user);

    UserMetaInventoryData inventoryData = user.meta().inventoryData();
    ItemStack heldItem = inventoryData.heldItem();

    PacketContainer packet = event.getPacket();
    EnumWrappers.PlayerDigType digType = packet.getPlayerDigTypes().read(0);
    BlockPosition blockPosition = packet.getBlockPositionModifier().read(0);

    switch (digType) {
      case START_DESTROY_BLOCK: {
        long playerStartDuration = AccessHelper.now() - meta.blockBreakDestroyTimestamps;
        long startTimeGained = playerStartDuration - 300;

        if (startTimeGained < -5) {
          if (meta.blockStartDelayVL++ >= 3) {
            String message = "started block break too quickly";
            String details = "gained " + -startTimeGained + "ms";
            int vl = -startTimeGained > 50 ? 10 : 5;
            if (plugin.violationProcessor().processViolation(player, vl, "BreakSpeedLimiter", message, details)) {
              event.setCancelled(true);
              return;
            }
            meta.blockStartDelayVL--;
          }
        } else if (meta.blockStartDelayVL > 0) {
          meta.blockStartDelayVL -= 2;
        }

        meta.blockBreakStartTimestamps = AccessHelper.now();
        float blockDamage = BlockDataAccess.blockDamage(player, heldItem, blockPosition);
        meta.startDuration = convertBlockDamageToMilliseconds(blockDamage);
        break;
      }
      case STOP_DESTROY_BLOCK: {
        long playerDuration = AccessHelper.now() - meta.blockBreakStartTimestamps;
        float blockDamage = BlockDataAccess.blockDamage(player, heldItem, blockPosition);
        long playerDurationExpected = convertBlockDamageToMilliseconds(blockDamage);
        double bestPlayerDuration = Math.min(meta.startDuration, playerDurationExpected);

        if (playerDuration < bestPlayerDuration) {
          long timeGained = (long) (bestPlayerDuration - playerDuration);

          if (timeGained > 55) {
            String message = "broke a block too quickly";
            String details = "gained " + timeGained + "ms";
            if (plugin.violationProcessor().processViolation(player, 20, "BreakSpeedLimiter", message, details)) {
              event.setCancelled(true);
            }
          }
        }

        meta.blockBreakDestroyTimestamps = AccessHelper.now();
      }
    }
  }

  private long convertBlockDamageToMilliseconds(float blockDamage) {
    if (blockDamage == 0) {
      return 0;
    }
    long time = 0;
    float curBlockDamageMP = 0f;
    while (curBlockDamageMP < 1f) {
      curBlockDamageMP += blockDamage;
      time += 50;
    }
    return time;
  }

  public static final class BreakSpeedLimiterMeta extends UserCustomCheckMeta {
    private long blockBreakStartTimestamps;
    private long blockBreakDestroyTimestamps;
    private float startDuration;
    private int blockStartDelayVL;
  }
}
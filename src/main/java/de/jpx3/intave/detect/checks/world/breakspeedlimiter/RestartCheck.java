package de.jpx3.intave.detect.checks.world.breakspeedlimiter;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import de.jpx3.intave.IntavePlugin;
import de.jpx3.intave.detect.MetaCheckPart;
import de.jpx3.intave.detect.checks.world.BreakSpeedLimiter;
import de.jpx3.intave.event.AccessHelper;
import de.jpx3.intave.module.linker.packet.ListenerPriority;
import de.jpx3.intave.module.linker.packet.PacketSubscription;
import de.jpx3.intave.user.User;
import de.jpx3.intave.user.meta.CheckCustomMetadata;
import de.jpx3.intave.user.meta.ProtocolMetadata;
import de.jpx3.intave.violation.Violation;
import de.jpx3.intave.violation.ViolationContext;
import de.jpx3.intave.violation.ViolationProcessor;
import org.bukkit.entity.Player;

import static de.jpx3.intave.module.linker.packet.PacketId.Client.*;

public final class RestartCheck extends MetaCheckPart<BreakSpeedLimiter, RestartCheck.BreakSpeedStartMeta> {
  public RestartCheck(BreakSpeedLimiter parentCheck) {
    super(parentCheck, RestartCheck.BreakSpeedStartMeta.class);
  }

  @PacketSubscription(
    priority = ListenerPriority.LOWEST,
    packetsIn = {
      POSITION, POSITION_LOOK, LOOK, FLYING, VEHICLE_MOVE
    }
  )
  public void tickUpdate(PacketEvent event) {
    Player player = event.getPlayer();
    User user = userOf(player);
    BreakSpeedStartMeta meta = metaOf(user);
    meta.ticks++;
  }

  @PacketSubscription(
    priority = ListenerPriority.LOWEST,
    packetsIn = {
      BLOCK_DIG
    }
  )
  public void receiveBlockAction(PacketEvent event) {
    Player player = event.getPlayer();
    User user = userOf(player);
    RestartCheck.BreakSpeedStartMeta meta = metaOf(user);
    ProtocolMetadata clientData = user.meta().protocol();

    PacketContainer packet = event.getPacket();
    EnumWrappers.PlayerDigType digType = packet.getPlayerDigTypes().read(0);

    switch (digType) {
      case START_DESTROY_BLOCK: {
        if (clientData.flyingPacketStream()) {
          int ticksBetween = meta.ticks - meta.blockBreakTick;
          if (ticksBetween < 5) {
            String message = "started block-break too quickly";
            String details = ticksBetween + " " + (ticksBetween == 1 ? "tick" : "ticks") + " between";
            ViolationProcessor violationProcessor = IntavePlugin.singletonInstance().violationProcessor();
            Violation violation = Violation.builderFor(BreakSpeedLimiter.class)
              .forPlayer(player).withMessage(message).withDetails(details).withVL(5)
              .build();
            ViolationContext violationContext = violationProcessor.processViolation(violation);
            if (violationContext.shouldCounterThreat()) {
              event.setCancelled(true);
            }
          }
        } else {
          long milliseconds = AccessHelper.now() - meta.blockBreakTimestamp;
          if (milliseconds < 200) {
            if (meta.blockBreakStartVL++ > 5) {
              String message = "started block-break too quickly";
              String details = milliseconds + "ms between";
              ViolationProcessor violationProcessor = IntavePlugin.singletonInstance().violationProcessor();
              Violation violation = Violation.builderFor(BreakSpeedLimiter.class)
                .forPlayer(player).withMessage(message).withDetails(details)
                .withVL(1).build();
              ViolationContext violationContext = violationProcessor.processViolation(violation);
              if (violationContext.shouldCounterThreat()) {
                event.setCancelled(true);
              }
              meta.blockBreakStartVL--;
            }
          } else if (meta.blockBreakStartVL > 0) {
            meta.blockBreakStartVL -= 0.2;
          }
        }
        break;
      }
      case STOP_DESTROY_BLOCK: {
        meta.blockBreakTick = meta.ticks;
        meta.blockBreakTimestamp = AccessHelper.now();
        break;
      }
    }
  }

  public static final class BreakSpeedStartMeta extends CheckCustomMetadata {
    private int ticks;
    private int blockBreakTick;
    private long blockBreakTimestamp;
    private double blockBreakStartVL;
  }
}
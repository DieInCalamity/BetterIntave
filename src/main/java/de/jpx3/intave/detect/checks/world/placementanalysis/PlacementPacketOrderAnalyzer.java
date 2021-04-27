package de.jpx3.intave.detect.checks.world.placementanalysis;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import de.jpx3.intave.IntavePlugin;
import de.jpx3.intave.detect.IntaveMetaCheckPart;
import de.jpx3.intave.detect.checks.world.PlacementAnalysis;
import de.jpx3.intave.event.packet.PacketDescriptor;
import de.jpx3.intave.event.packet.PacketSubscription;
import de.jpx3.intave.event.packet.Sender;
import de.jpx3.intave.event.service.violation.Violation;
import de.jpx3.intave.tools.AccessHelper;
import de.jpx3.intave.tools.RotationMathHelper;
import de.jpx3.intave.user.UserCustomCheckMeta;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static de.jpx3.intave.detect.checks.world.PlacementAnalysis.COMMON_FLAG_MESSAGE;

public final class PlacementPacketOrderAnalyzer extends IntaveMetaCheckPart<PlacementAnalysis, PlacementPacketOrderAnalyzer.PlacementOrderMeta> {
  private final IntavePlugin plugin;

  public PlacementPacketOrderAnalyzer(PlacementAnalysis parentCheck) {
    super(parentCheck, PlacementOrderMeta.class);
    plugin = IntavePlugin.singletonInstance();
  }

  @PacketSubscription(
    packets = {
      @PacketDescriptor(sender = Sender.CLIENT, packetName = "FLYING"),
      @PacketDescriptor(sender = Sender.CLIENT, packetName = "POSITION"),
      @PacketDescriptor(sender = Sender.CLIENT, packetName = "POSITION_LOOK"),
      @PacketDescriptor(sender = Sender.CLIENT, packetName = "LOOK"),
      @PacketDescriptor(sender = Sender.CLIENT, packetName = "BLOCK_PLACE")
    }
  )
  public void checkPlacementPacketOrder(PacketEvent event) {
    Player player = event.getPlayer();
    PacketContainer packet = event.getPacket();
    PlacementOrderMeta meta = metaOf(player);

    PacketType packetType = event.getPacketType();
    long now = AccessHelper.now();
    if (packetType == PacketType.Play.Client.BLOCK_PLACE) {
      if (blockingPlacementPacket(packet)) {
        return;
      }

      long timeDiff = now - meta.lastMovePacket;
      meta.permutePlacementDifferences.add(timeDiff);

      if (meta.permutePlacementDifferences.size() == 4) {
        double average = RotationMathHelper.averageOf(meta.permutePlacementDifferences);

        if (average < 20) {
          long permutePacketIncrementDiff = now - meta.permutePacketLastIncrement;

          if (permutePacketIncrementDiff > 20) {
            if (meta.permutePacketOrderBalance++ >= 2) {
              Violation violation = Violation.fromType(PlacementAnalysis.class)
                .withPlayer(player)
                .withMessage(COMMON_FLAG_MESSAGE)
                .withVL(2)
                .build();
              plugin.violationProcessor().processViolation(violation);
            }
            meta.permutePacketLastIncrement = now;
          }

        } else if (meta.permutePacketOrderBalance >= 0) {
          meta.permutePacketOrderBalance--;
        }

        meta.permutePlacementDifferences.clear();
      }
    } else {
      meta.lastMovePacket = now;
    }
  }

  private boolean blockingPlacementPacket(PacketContainer packet) {
    Integer integer = packet.getIntegers().readSafely(0);
    return integer != null && integer == 255;
  }

  public static class PlacementOrderMeta extends UserCustomCheckMeta {
    public double permutePacketOrderBalance;
    public long permutePacketLastIncrement;
    public List<Long> permutePlacementDifferences = new ArrayList<>();

    public long lastMovePacket;
  }
}

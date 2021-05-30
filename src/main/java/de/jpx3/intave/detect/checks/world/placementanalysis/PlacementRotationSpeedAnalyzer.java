package de.jpx3.intave.detect.checks.world.placementanalysis;

import com.comphenix.protocol.events.PacketEvent;
import de.jpx3.intave.IntavePlugin;
import de.jpx3.intave.detect.IntaveMetaCheckPart;
import de.jpx3.intave.detect.checks.world.PlacementAnalysis;
import de.jpx3.intave.event.bukkit.BukkitEventSubscription;
import de.jpx3.intave.event.packet.ListenerPriority;
import de.jpx3.intave.event.packet.PacketSubscription;
import de.jpx3.intave.event.violation.Violation;
import de.jpx3.intave.tools.AccessHelper;
import de.jpx3.intave.tools.MathHelper;
import de.jpx3.intave.user.User;
import de.jpx3.intave.user.UserCustomCheckMeta;
import de.jpx3.intave.user.UserMetaMovementData;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.ArrayList;
import java.util.List;

import static de.jpx3.intave.detect.checks.world.PlacementAnalysis.COMMON_FLAG_MESSAGE;
import static de.jpx3.intave.event.packet.PacketId.Client.LOOK;
import static de.jpx3.intave.event.packet.PacketId.Client.POSITION_LOOK;

public final class PlacementRotationSpeedAnalyzer extends IntaveMetaCheckPart<PlacementAnalysis, PlacementRotationSpeedAnalyzer.RotationSpeedMeta> {
  private final IntavePlugin plugin;

  public PlacementRotationSpeedAnalyzer(PlacementAnalysis parentCheck) {
    super(parentCheck, RotationSpeedMeta.class);
    plugin = IntavePlugin.singletonInstance();
  }

  @PacketSubscription(
    priority = ListenerPriority.HIGH,
    packetsIn = {
      POSITION_LOOK, LOOK
    }
  )
  public void on(PacketEvent event) {
    Player player = event.getPlayer();
    User user = userOf(player);
    UserMetaMovementData movementData = user.meta().movementData();
    RotationSpeedMeta meta = metaOf(user);
    float rotationMovement = Math.min(MathHelper.distanceInDegrees(movementData.rotationYaw, movementData.lastRotationYaw), 360);

    if (AccessHelper.now() - meta.lastBlockPlacement > 2000 || movementData.lastTeleport <= 5) {
      return;
    }

    List<Float> rotationHistory = meta.rotationHistory;
    if (rotationHistory.size() > 5 * 20) {
      rotationHistory.remove(0);
    }
    rotationHistory.add(rotationMovement);
  }

  @BukkitEventSubscription
  public void on(BlockPlaceEvent place) {
    Player player = place.getPlayer();
    User user = userOf(player);
    RotationSpeedMeta meta = metaOf(user);

    meta.lastBlockPlacement = AccessHelper.now();

    if (place.getBlock().getY() < player.getLocation().getBlockY()) {

      List<Float> rotationHistory = meta.rotationHistory;
      double rotationSum = rotationHistory.stream().mapToDouble(value -> value).sum();
      if (rotationSum > 3000) {
        Violation violation = Violation.builderFor(PlacementAnalysis.class)
          .forPlayer(player).withDefaultThreshold()
          .withMessage(COMMON_FLAG_MESSAGE)
          .withDetails("high rotation activity while placing blocks (" + ((int) rotationSum) + " degrees)")
          .withDefaultThreshold().withVL(0).build();
        plugin.violationProcessor().processViolation(violation);
        place.setCancelled(true);
      }
    }
  }

  public static class RotationSpeedMeta extends UserCustomCheckMeta {
    private final List<Float> rotationHistory = new ArrayList<>();
    private long lastBlockPlacement;
  }
}

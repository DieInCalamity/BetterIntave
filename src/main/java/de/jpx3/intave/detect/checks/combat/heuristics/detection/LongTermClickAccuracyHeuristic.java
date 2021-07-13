package de.jpx3.intave.detect.checks.combat.heuristics.detection;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import de.jpx3.intave.IntavePlugin;
import de.jpx3.intave.detect.IntaveMetaCheckPart;
import de.jpx3.intave.detect.checks.combat.Heuristics;
import de.jpx3.intave.detect.checks.combat.heuristics.Anomaly;
import de.jpx3.intave.detect.checks.combat.heuristics.Confidence;
import de.jpx3.intave.event.packet.PacketSubscription;
import de.jpx3.intave.tools.MathHelper;
import de.jpx3.intave.user.User;
import de.jpx3.intave.user.UserCustomCheckMeta;
import org.bukkit.entity.Player;

import static de.jpx3.intave.event.packet.PacketId.Client.ARM_ANIMATION;
import static de.jpx3.intave.event.packet.PacketId.Client.USE_ENTITY;

public final class LongTermClickAccuracyHeuristic extends IntaveMetaCheckPart<Heuristics, LongTermClickAccuracyHeuristic.ClickAccuracyMeta> {
  private final IntavePlugin plugin;

  public LongTermClickAccuracyHeuristic(Heuristics parentCheck) {
    super(parentCheck, ClickAccuracyMeta.class);
    this.plugin = IntavePlugin.singletonInstance();
  }


  @PacketSubscription(
    packetsIn = {
      USE_ENTITY, ARM_ANIMATION
    }
  )
  public void evaluateFightAccuracy(PacketEvent event) {
    Player player = event.getPlayer();
    User user = userOf(player);
//    UserMetaAttackData attackData = user.meta().attackData();
    ClickAccuracyMeta heuristicMeta = metaOf(user);
    PacketType packetType = event.getPacketType();
    PacketContainer packet = event.getPacket();
//    WrappedEntity attackedEntity = attackData.lastAttackedEntity();
//    if (attackedEntity != null && !attackedEntity.moving(0.05)) {
//      return;
//    }
//    if (!attackData.recentlyAttacked(500) || attackData.recentlySwitchedEntity(1000)) {
//      return;
//    }
    if (packetType == PacketType.Play.Client.ARM_ANIMATION) {
      heuristicMeta.swings++;
    } else {
      EnumWrappers.EntityUseAction action = packet.getEntityUseActions().read(0);
      if (action == EnumWrappers.EntityUseAction.ATTACK) {
        heuristicMeta.attacks++;
        heuristicMeta.swings--;
        double failRate = (heuristicMeta.swings / heuristicMeta.attacks) * 100.0;
//        Synchronizer.synchronize(() -> player.sendMessage(String.valueOf(failRate)));
        if (heuristicMeta.attacks > 80) {
          if (failRate >= 0 && failRate < 3) {
            Anomaly anomaly = Anomaly.anomalyOf("210", Confidence.NONE, Anomaly.Type.KILLAURA, "player maintains high attack accuracy (failRate: " + MathHelper.formatDouble(failRate, 2) + "%)");
            parentCheck().saveAnomaly(player, anomaly);
          }
          heuristicMeta.attacks = 0;
          heuristicMeta.swings = 0;
        }
      }
    }
  }

  public static class ClickAccuracyMeta extends UserCustomCheckMeta {
    public double attacks;
    public double swings;
  }
}

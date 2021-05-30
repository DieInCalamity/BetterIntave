package de.jpx3.intave.detect.checks.combat.heuristics.detection;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import de.jpx3.intave.IntavePlugin;
import de.jpx3.intave.detect.IntaveMetaCheckPart;
import de.jpx3.intave.detect.checks.combat.Heuristics;
import de.jpx3.intave.detect.checks.combat.heuristics.Anomaly;
import de.jpx3.intave.detect.checks.combat.heuristics.Confidence;
import de.jpx3.intave.event.packet.PacketSubscription;
import de.jpx3.intave.event.violation.AttackNerfStrategy;
import de.jpx3.intave.user.User;
import de.jpx3.intave.user.UserCustomCheckMeta;
import de.jpx3.intave.user.UserMetaClientData;
import org.bukkit.entity.Player;

import static de.jpx3.intave.event.packet.PacketId.Client.*;

public final class PacketOrderSwingHeuristic extends IntaveMetaCheckPart<Heuristics, PacketOrderSwingHeuristic.PacketOrderSwingHeuristicMeta> {
  private final IntavePlugin plugin;

  public PacketOrderSwingHeuristic(Heuristics parentCheck) {
    super(parentCheck, PacketOrderSwingHeuristicMeta.class);
    this.plugin = IntavePlugin.singletonInstance();
  }

  @PacketSubscription(
    packetsIn = {
      FLYING, POSITION, POSITION_LOOK, LOOK, ARM_ANIMATION
    }
  )
  public void receiveMovementPacket(PacketEvent event) {
    Player player = event.getPlayer();
    PacketOrderSwingHeuristicMeta heuristicMeta = metaOf(player);
    heuristicMeta.swingTick = event.getPacketType() == PacketType.Play.Client.ARM_ANIMATION;
  }

  @PacketSubscription(
    packetsIn = {
      USE_ENTITY
    }
  )
  public void receiveUseEntity(PacketEvent event) {
    Player player = event.getPlayer();
    User user = userOf(player);
    UserMetaClientData clientData = user.meta().clientData();
    PacketOrderSwingHeuristicMeta heuristicMeta = metaOf(player);
    EnumWrappers.EntityUseAction action = event.getPacket().getEntityUseActions().read(0);
    if (user.meta().abilityData().ignoringMovementPackets()) {
      return;
    }
    if (clientData.flyingPacketStream() && action == EnumWrappers.EntityUseAction.ATTACK && !heuristicMeta.swingTick) {
      String description = "swing not correlated with attack ("+user.meta().clientData().versionString()+")";
      Anomaly anomaly = Anomaly.anomalyOf("31", Confidence.CERTAIN, Anomaly.Type.KILLAURA, description, Anomaly.AnomalyOption.DELAY_128s);
      parentCheck().saveAnomaly(player, anomaly);
      //dmc11
      user.applyAttackNerfer(AttackNerfStrategy.HT_LIGHT, "11");
    }
  }

  public final static class PacketOrderSwingHeuristicMeta extends UserCustomCheckMeta {
    private boolean swingTick;
  }
}

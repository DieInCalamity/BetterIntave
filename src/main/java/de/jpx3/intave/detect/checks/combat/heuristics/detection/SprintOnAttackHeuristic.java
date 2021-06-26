package de.jpx3.intave.detect.checks.combat.heuristics.detection;

import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import de.jpx3.intave.detect.IntaveMetaCheckPart;
import de.jpx3.intave.detect.checks.combat.Heuristics;
import de.jpx3.intave.detect.checks.combat.heuristics.Anomaly;
import de.jpx3.intave.detect.checks.combat.heuristics.Confidence;
import de.jpx3.intave.event.packet.ListenerPriority;
import de.jpx3.intave.event.packet.PacketSubscription;
import de.jpx3.intave.tools.MathHelper;
import de.jpx3.intave.user.User;
import de.jpx3.intave.user.UserCustomCheckMeta;
import de.jpx3.intave.user.UserMetaMovementData;
import org.bukkit.entity.Player;

import static de.jpx3.intave.event.packet.PacketId.Client.*;

public final class SprintOnAttackHeuristic extends IntaveMetaCheckPart<Heuristics, SprintOnAttackHeuristic.SprintOnAttackHeuristicMeta> {

  public SprintOnAttackHeuristic(Heuristics parentCheck) {
    super(parentCheck, SprintOnAttackHeuristic.SprintOnAttackHeuristicMeta.class);
  }

  @PacketSubscription(
    priority = ListenerPriority.HIGH,
    packetsIn = {
      ENTITY_ACTION
    }
  )
  public void receiveEntityActionPacket(PacketEvent event) {
    Player player = event.getPlayer();
    User user = userOf(player);
    SprintOnAttackHeuristicMeta meta = metaOf(user);
    EnumWrappers.PlayerAction action = event.getPacket().getPlayerActions().read(0);

    if(action == EnumWrappers.PlayerAction.START_SPRINTING) {
      meta.startSprint = true;
    } else if(action == EnumWrappers.PlayerAction.STOP_SPRINTING) {
      meta.stopSprint = true;
    }
  }

  @PacketSubscription(
    priority = ListenerPriority.HIGH,
    packetsIn = {
      USE_ENTITY
    }
  )
  public void receiveAttackPacket(PacketEvent event) {
    Player player = event.getPlayer();
    User user = userOf(player);
    SprintOnAttackHeuristicMeta meta = metaOf(user);
    EnumWrappers.EntityUseAction action = event.getPacket().getEntityUseActions().read(0);

    if(action == EnumWrappers.EntityUseAction.ATTACK) {
      meta.lastAttack = 0;
    }
  }

  @PacketSubscription(
    priority = ListenerPriority.HIGH,
    packetsIn = {
      POSITION, POSITION_LOOK, LOOK, FLYING
    }
  )
  public void receiveMovePacket(PacketEvent event) {
    Player player = event.getPlayer();
    User user = userOf(player);
    SprintOnAttackHeuristicMeta meta = metaOf(user);
    UserMetaMovementData movementData = user.meta().movementData();

    if(movementData.lastTeleport == 0) {
      return;
    }

    if(meta.lastAttack == 0) {
      meta.totalAttacks++;
      if(meta.startSprint) {
        meta.attacksWithSprintChangeBefore++;
      }
    }

    if(meta.totalAttacks > 10) {
      double ratioBefore = (double) meta.attacksWithSprintChangeBefore / (double) meta.totalAttacks;
        if(ratioBefore > 0.9) {
          Anomaly anomaly = Anomaly.anomalyOf(
            "200",
            Confidence.NONE,
            Anomaly.Type.KILLAURA,
            "sprint-toggles aligned bevor attacks (" + MathHelper.formatDouble(ratioBefore, 2) + "%)", Anomaly.AnomalyOption.DELAY_16s
          );
          parentCheck().saveAnomaly(player, anomaly);
        }

      meta.totalAttacks = 0;
      meta.attacksWithSprintChangeBefore = 0;
    }

    prepareNextTick(meta);
  }

  private void prepareNextTick(SprintOnAttackHeuristicMeta meta) {
    meta.lastAttack++;
    meta.startSprint = false;
    meta.stopSprint = false;
  }

  public static class SprintOnAttackHeuristicMeta extends UserCustomCheckMeta {
    private int attacksWithSprintChangeBefore;
    private int attacksWithSprintChangeAfter;
    private int totalAttacks;
    private boolean startSprint;
    private boolean stopSprint;
    private int lastAttack;
  }
}
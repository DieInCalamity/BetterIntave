package de.jpx3.intave.detect.checks.combat.heuristics.detection;

import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import de.jpx3.intave.adapter.MinecraftVersions;
import de.jpx3.intave.adapter.ProtocolLibraryAdapter;
import de.jpx3.intave.detect.IntaveMetaCheckPart;
import de.jpx3.intave.detect.checks.combat.Heuristics;
import de.jpx3.intave.detect.checks.combat.heuristics.Anomaly;
import de.jpx3.intave.detect.checks.combat.heuristics.Confidence;
import de.jpx3.intave.event.bukkit.BukkitEventSubscription;
import de.jpx3.intave.event.packet.ListenerPriority;
import de.jpx3.intave.event.packet.PacketDescriptor;
import de.jpx3.intave.event.packet.PacketSubscription;
import de.jpx3.intave.event.packet.Sender;
import de.jpx3.intave.event.punishment.AttackNerfStrategy;
import de.jpx3.intave.user.User;
import de.jpx3.intave.user.UserCustomCheckMeta;
import de.jpx3.intave.user.UserMetaMovementData;
import de.jpx3.intave.user.UserRepository;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerAnimationEvent;

public final class NoSwingHeuristic extends IntaveMetaCheckPart<Heuristics, NoSwingHeuristic.NoSwingMeta> {

  public NoSwingHeuristic(Heuristics parentCheck) {
    super(parentCheck, NoSwingMeta.class);
  }

//  @BukkitEventSubscription(priority = EventPriority.LOWEST)
//  public void on(PlayerAnimationEvent swing) {
//    Player player = swing.getPlayer();
//    metaOf(player).swungHand = true;
//  }

//  @BukkitEventSubscription(priority = EventPriority.LOWEST)
//  public void on(EntityDamageByEntityEvent event) {
//    Entity attacker = event.getDamager();
//    Entity damaged = event.getEntity();
//
//    if (attacker instanceof Player && damaged instanceof LivingEntity &&
//        event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)
//    ) {
//      Player player = (Player) attacker;
//      User user = userOf(player);
//      NoSwingMeta meta = metaOf(user);
//      if(!meta.swungHand) {

//      }
//      meta.swungHand = false;
//    }
//  }

  @PacketSubscription(
    priority = ListenerPriority.NORMAL,
    packets = {
      @PacketDescriptor(sender = Sender.CLIENT, packetName = "USE_ENTITY")
    }
  )
  public void entityHit(PacketEvent event) {
    Player player = event.getPlayer();
    User user = userOf(player);
    NoSwingMeta meta = metaOf(user);

    EnumWrappers.EntityUseAction entityUseAction = event.getPacket().getEntityUseActions().read(0);

    if (entityUseAction == EnumWrappers.EntityUseAction.ATTACK) {
//      if(meta.swingsThisTick == 0 && meta.attacksThisTick == 0
//        && user.meta().clientData().protocolVersion() == 47
//      ) {
//        event.setCancelled(true);
//      }
      meta.attacksThisTick++;
    }
  }

  @PacketSubscription(
    priority = ListenerPriority.NORMAL,
    packets = {
      @PacketDescriptor(sender = Sender.CLIENT, packetName = "ARM_ANIMATION")
    }
  )
  public void swing(PacketEvent event) {
    Player player = event.getPlayer();
    User user = userOf(player);
    NoSwingMeta meta = metaOf(user);

    meta.swingsThisTick++;
  }

  @PacketSubscription(
    priority = ListenerPriority.NORMAL,
    packets = {
      @PacketDescriptor(sender = Sender.CLIENT, packetName = "POSITION_LOOK"),
      @PacketDescriptor(sender = Sender.CLIENT, packetName = "LOOK"),
      @PacketDescriptor(sender = Sender.CLIENT, packetName = "FLYING"),
      @PacketDescriptor(sender = Sender.CLIENT, packetName = "POSITION"),
      @PacketDescriptor(sender = Sender.CLIENT, packetName = "VEHICLE_MOVE")
    }
  )
  public void receiveMovementPacket(PacketEvent event) {
    Player player = event.getPlayer();
    User user = UserRepository.userOf(player);
    UserMetaMovementData movementData = user.meta().movementData();
    NoSwingMeta meta = metaOf(user);

    if (movementData.lastTeleport == 0) {
      return;
    }

    if(meta.attacksThisTick > 0) {
      if(meta.swingsThisTick == 0) {
        String details = "missing swing packet on attack";
        Anomaly anomaly = Anomaly.anomalyOf("171", /*Confidence.LIKELY*/Confidence.NONE, Anomaly.Type.KILLAURA, details, Anomaly.AnomalyOption.LIMIT_4);
        parentCheck().saveAnomaly(player, anomaly);
        user.applyAttackNerfer(AttackNerfStrategy.CANCEL);
      }
    }

    prepareNextTick(meta);
  }

  private void prepareNextTick(NoSwingMeta meta) {
    meta.swingsThisTick = 0;
    meta.attacksThisTick = 0;
  }

  public static class NoSwingMeta extends UserCustomCheckMeta {
    public int swingsThisTick;
    public int attacksThisTick;
  }
}

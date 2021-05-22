package de.jpx3.intave.detect.checks.combat.heuristics.detection;

import com.comphenix.protocol.events.PacketEvent;
import de.jpx3.intave.IntaveControl;
import de.jpx3.intave.adapter.MinecraftVersions;
import de.jpx3.intave.adapter.ProtocolLibraryAdapter;
import de.jpx3.intave.detect.IntaveMetaCheckPart;
import de.jpx3.intave.detect.checks.combat.Heuristics;
import de.jpx3.intave.detect.checks.combat.heuristics.Anomaly;
import de.jpx3.intave.detect.checks.combat.heuristics.Confidence;
import de.jpx3.intave.event.packet.ListenerPriority;
import de.jpx3.intave.event.packet.PacketDescriptor;
import de.jpx3.intave.event.packet.PacketSubscription;
import de.jpx3.intave.event.packet.Sender;
import de.jpx3.intave.tools.MathHelper;
import de.jpx3.intave.user.User;
import de.jpx3.intave.user.UserCustomCheckMeta;
import de.jpx3.intave.user.UserMetaClientData;
import de.jpx3.intave.user.UserMetaMovementData;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class SameRotationHeuristic extends IntaveMetaCheckPart<Heuristics, SameRotationHeuristic.SameRotationHeuristicMeta> {
  public SameRotationHeuristic(Heuristics parentCheck) {
    super(parentCheck, SameRotationHeuristicMeta.class);
  }

  @PacketSubscription(
    priority = ListenerPriority.HIGH,
    packets = {
      @PacketDescriptor(sender = Sender.CLIENT, packetName = "POSITION_LOOK"),
      @PacketDescriptor(sender = Sender.CLIENT, packetName = "LOOK"),
      @PacketDescriptor(sender = Sender.CLIENT, packetName = "FLYING"),
      @PacketDescriptor(sender = Sender.CLIENT, packetName = "POSITION")
    }
  )
  public void receiveMovementPacket(PacketEvent event) {
    if (ProtocolLibraryAdapter.serverVersion().isAtLeast(MinecraftVersions.VER1_9_0)) {
      return;
    }
    Player player = event.getPlayer();
    User user = userOf(player);
    SameRotationHeuristicMeta meta = metaOf(user);
    UserMetaMovementData movementData = user.meta().movementData();

    if (movementData.lastTeleport == 0) {
      return;
    }

    double rotationMotion = Math.hypot(movementData.lastRotationYaw - movementData.rotationYaw, movementData.lastRotationPitch - movementData.rotationPitch);
    boolean isPartner = (UserMetaClientData.VERSION_DETAILS & 0x100) != 0;
//    boolean isEnterprise = (UserMetaClientData.VERSION_DETAILS & 0x200) != 0;

    if (meta.lastLastTick.rotationMotion < 10 && meta.lastTick.rotationMotion > 40 && rotationMotion < 10 && movementData.lastTeleport > 5 && isPartner) {
      checkSameRotation(meta, player);

      checkExactRotationMotion(meta, player);

      checkExactRotation(meta, player);

      meta.yawRotations.add(meta.lastLastTick.yaw);
      meta.yawRotations.add(meta.lastTick.yaw);

      meta.pitchRotations.add(meta.lastLastTick.pitch);
      meta.pitchRotations.add(meta.lastTick.pitch);
    }

    prepareNextTick(user, rotationMotion);
  }

  private void checkExactRotation(SameRotationHeuristicMeta meta, Player player) {
    // Guckt ob die alte Rotation Yaw oder Pitch eine ganze Zahl ist
    // Wird genutzt um false flaggs zu vermeiden wenn die alte Rotation eine Ganzezahl war und man sich mit einer ganzen Zahl rotiert hat.
    boolean lastYawMotionExactNumber = meta.lastLastTick.yawMotion % 1 == 0;
    boolean lastPitchMotionExactNumber = meta.lastLastTick.pitchMotion % 1 == 0;

    // Guckt ob die rotation Yaw oder Pitch eine ganze Zahl ist
    boolean yawExactNumber = meta.lastTick.yaw % 1 == 0;
    boolean pitchExactNumber = meta.lastTick.pitch % 1 == 0;

    String description = "exact rotation (";
    boolean flag = false;
    if(yawExactNumber && meta.lastTick.yawMotion != 0 && !lastYawMotionExactNumber) {
      flag = true;
      description += "yaw:" + meta.lastTick.yaw;
    }
    if(pitchExactNumber && Math.abs(meta.lastTick.pitchMotion) != 90 && meta.lastTick.pitchMotion != 0 && !lastPitchMotionExactNumber) {
      if(flag)
        description += ", ";
      flag = true;
      description += "pitch:" + meta.lastTick.pitch;
    }
    description += ")";

    if (flag) {
      Anomaly anomaly = Anomaly.anomalyOf("183", Confidence.NONE, Anomaly.Type.KILLAURA, description, getOptions(true));
      parentCheck().saveAnomaly(player, anomaly);
    }
  }

  private void checkSameRotation(SameRotationHeuristicMeta meta, Player player) {
    // Guckt ob die rotation die ein Spieler hat schon mal zuvor gesendet wurde wärend der Spieler sich schnell gedreht hat
    boolean containedYaw = meta.yawRotations.contains(meta.lastLastTick.yaw) || meta.yawRotations.contains(meta.lastTick.yaw);
    boolean containedPitch = meta.pitchRotations.contains(meta.lastLastTick.pitch) || meta.pitchRotations.contains(meta.lastTick.pitch);

    String description = "same rotation (";
    boolean flag = false;
    if (containedYaw && meta.lastTick.yawMotion != 0) {
      flag = true;
      description += "yaw:" + meta.lastTick.yaw;
    }
    if(containedPitch && meta.lastTick.pitchMotion != 0) {
      if(flag)
        description += ", ";
      flag = true;
      description += "pitch:" + meta.lastTick.pitch + ")";
    }

    if(flag) {
      Anomaly anomaly = Anomaly.anomalyOf("181", Confidence.NONE, Anomaly.Type.KILLAURA, description, getOptions(true));
      parentCheck().saveAnomaly(player, anomaly);
    }
  }

  private void checkExactRotationMotion(SameRotationHeuristicMeta meta, Player player) {
    // Guckt ob die Rotation Bewegung des Spielers eine ganze Zahl war wenn er sich schnell rotiert hat.
    boolean yawMotionExactNumber = meta.lastTick.yawMotion % 1 == 0;
    boolean pitchMotionExactNumber = meta.lastTick.pitchMotion % 1 == 0;

    String description = "exact rotation (";
    boolean flag = false;
    if(yawMotionExactNumber) {
      flag = true;
      description += "yaw:" + meta.lastTick.yawMotion;
    }
    if(pitchMotionExactNumber) {
      if(flag)
        description += ", ";
      flag = true;
      description += "pitch:" + meta.lastTick.pitchMotion + ")";
    }

    if (flag) {
      Anomaly anomaly = Anomaly.anomalyOf("182", Confidence.NONE, Anomaly.Type.KILLAURA, description, getOptions(true));
      parentCheck().saveAnomaly(player, anomaly);
    }
  }

  private int getOptions(boolean isPartner) {
    int options;
    if (IntaveControl.GOMME_MODE) {
      options = Anomaly.AnomalyOption.DELAY_32s;
    } else if (isPartner) {
      options = Anomaly.AnomalyOption.DELAY_64s;
    } else {
      options = Anomaly.AnomalyOption.DELAY_128s;
    }

    return options;
  }

  private void prepareNextTick(User user, double rotationMotion) {
    UserMetaMovementData movementData = user.meta().movementData();
    SameRotationHeuristicMeta meta = metaOf(user);

    float yawMotion = Math.abs(movementData.lastRotationYaw - movementData.rotationYaw);
    float pitchMotion = Math.abs(movementData.lastRotationPitch - movementData.rotationPitch);

    meta.lastLastTick = meta.lastTick;
    meta.lastTick = new Tick(movementData.rotationYaw, movementData.rotationPitch, rotationMotion, yawMotion, pitchMotion);

    if (meta.yawRotations.size() > 15)
      meta.yawRotations.remove(0);
    if (meta.pitchRotations.size() > 15)
      meta.pitchRotations.remove(0);
  }


  public static final class SameRotationHeuristicMeta extends UserCustomCheckMeta {
    private Set<Float> yawRotations = new HashSet<>();
    private Set<Float> pitchRotations = new HashSet<>();
    private Tick lastLastTick = new Tick();
    private Tick lastTick = new Tick();
  }
}

class Tick {
  float yaw, pitch;
  double rotationMotion;
  float yawMotion, pitchMotion;

  public Tick() {
  }

  public Tick(float yaw, float pitch, double rotationMotion, float yawMotion, float pitchMotion) {
    this.yaw = yaw;
    this.pitch = pitch;
    this.rotationMotion = rotationMotion;
    this.yawMotion = yawMotion;
    this.pitchMotion = pitchMotion;
  }
}
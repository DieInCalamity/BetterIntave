package de.jpx3.intave.module.tracker.player;

import com.comphenix.protocol.events.PacketEvent;
import de.jpx3.intave.IntavePlugin;
import de.jpx3.intave.module.Module;
import de.jpx3.intave.module.linker.packet.ListenerPriority;
import de.jpx3.intave.module.linker.packet.PacketId;
import de.jpx3.intave.module.linker.packet.PacketSubscription;
import de.jpx3.intave.packet.reader.AbilityInReader;
import de.jpx3.intave.packet.reader.AbilityOutReader;
import de.jpx3.intave.packet.reader.EntityReader;
import de.jpx3.intave.packet.reader.GameStateChangeReader;
import de.jpx3.intave.user.MessageChannel;
import de.jpx3.intave.user.User;
import de.jpx3.intave.user.meta.AbilityMetadata;
import de.jpx3.intave.user.meta.MetadataBundle;
import de.jpx3.intave.user.meta.MovementMetadata;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import static de.jpx3.intave.module.linker.packet.PacketId.Client.*;
import static de.jpx3.intave.module.linker.packet.PacketId.Server.POSITION;
import static de.jpx3.intave.module.linker.packet.PacketId.Server.*;
import static de.jpx3.intave.packet.reader.GameStateChangeReader.GameState.CHANGE_GAME_MODE;

public final class AbilityTracker extends Module {
  @PacketSubscription(packetsOut = CAMERA)
  public void receiveCamera(User user, EntityReader reader) {
    int entityId = reader.entityId();
    user.tickFeedback(() -> synchronizedCameraUpdate(user, entityId));
  }

  private void synchronizedCameraUpdate(User user, int entityId) {
    AbilityMetadata abilityData = user.meta().abilities();
    abilityData.hasViewEntity = entityId != user.player().getEntityId();
  }

  @PacketSubscription(
    priority = ListenerPriority.HIGH,
    packetsIn = {
      ABILITIES_IN
    }
  )
  public void receiveAbilities(User user, AbilityInReader reader) {
    AbilityMetadata abilityData = user.meta().abilities();
    MovementMetadata movementData = user.meta().movement();
    boolean flying = reader.requestedFlying();
    if (abilityData.allowFlying()) {
      if (flying) {
        abilityData.setFlying(true);
      } else {
        movementData.disabledFlying = true;
      }
    }
  }

  @PacketSubscription(
    priority = ListenerPriority.HIGH,
    packetsOut = {
      ABILITIES_OUT
    }
  )
  public void sentAbilities(User user, AbilityOutReader reader, PacketEvent event) {
    MetadataBundle meta = user.meta();
    MovementMetadata movement = meta.movement();
    AbilityMetadata abilityData = meta.abilities();
    float flyingSpeed = reader.flyingSpeed();
    float walkingSpeed = reader.walkingSpeed();
    boolean allowedFlight = reader.flyingAllowed();
    boolean critical = abilityData.allowFlying() && !allowedFlight;

    if (critical && movement.criticalFlyingDisallowStacks++ == 1) {
      movement.criticalFlyingDisallowWasTeleported = movement.awaitTeleport || movement.awaitOutgoingTeleport;
      movement.criticalEnterPosX = movement.verifiedPositionX;
      movement.criticalEnterPosY = movement.verifiedPositionY;
      movement.criticalEnterPosZ = movement.verifiedPositionZ;
    }

    user.packetTickFeedback(event, () -> {
      if (critical) {
        movement.criticalFlyingDisallowStacks = Math.max(0, movement.criticalFlyingDisallowStacks - 1);
      }
      abilityData.setWalkSpeed(walkingSpeed);
      abilityData.setFlySpeed(flyingSpeed);
      abilityData.setAllowFlying(allowedFlight);
    });
  }

  @PacketSubscription(
    packetsOut = POSITION
  )
  public void outgoingPositionUpdate(User user) {
    MovementMetadata movementData = user.meta().movement();
    if (movementData.criticalFlyingDisallowStacks > 0) {
      movementData.criticalFlyingDisallowWasTeleported = true;
    }
  }

  @PacketSubscription(
    priority = ListenerPriority.HIGH,
    packetsIn = {FLYING, PacketId.Client.POSITION, LOOK, POSITION_LOOK}
  )
  public void incomingFlyingUpdate(User user, Player player) {
    MovementMetadata movementData = user.meta().movement();
    if (movementData.criticalFlyingDisallowStacks > 0 && !movementData.criticalFlyingDisallowWasTeleported) {
      double deltaX = movementData.verifiedPositionX - movementData.criticalEnterPosX;
      double deltaY = movementData.verifiedPositionY - movementData.criticalEnterPosY;
      double deltaZ = movementData.verifiedPositionZ - movementData.criticalEnterPosZ;
      double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
      if (distance > 4) {
        if (user.receives(MessageChannel.DEBUG_TELEPORT)) {
          player.sendMessage(IntavePlugin.prefix() + "Teleport to " + player.getLocation().getBlockX() + " " + player.getLocation().getBlockY() + " " + player.getLocation().getBlockZ() + " " + " for " + ChatColor.RED + " critical flying disallow protection");
        }
        player.teleport(
          new Location(
            player.getWorld(),
            movementData.verifiedPositionX,
            movementData.verifiedPositionY,
            movementData.verifiedPositionZ,
            player.getLocation().getYaw(),
            player.getLocation().getPitch()
          ), PlayerTeleportEvent.TeleportCause.UNKNOWN
        );
        movementData.criticalFlyingDisallowStacks = 0;
      }
    }
  }

  @PacketSubscription(
    priority = ListenerPriority.NORMAL,
    packetsOut = {
      GAME_STATE_CHANGE
    }
  )
  public void outgoingGameModeUpdate(
    User user, GameStateChangeReader reader
  ) {
    if (reader.type() != CHANGE_GAME_MODE) {
      return;
    }
    GameMode gameMode = gameModeOf(reader.valueAsInt());
    AbilityMetadata abilityData = user.meta().abilities();
    abilityData.setPendingGameMode(gameMode);
    user.tickFeedback(() -> abilityData.setGameMode(gameMode));
  }

  private GameMode gameModeOf(int id) {
    for (GameMode value : GameMode.values()) {
      if (value.id == id) {
        return value;
      }
    }
    throw new IllegalStateException("Unable to resolve gamemode with id " + id);
  }

  public enum GameMode {
    NOT_SET(-1),
    SURVIVAL(0),
    CREATIVE(1),
    ADVENTURE(2),
    SPECTATOR(3);

    private final int id;

    GameMode(int id) {
      this.id = id;
    }

    public static GameMode fromBukkit(org.bukkit.GameMode gameMode) {
      switch (gameMode) {
        case SURVIVAL:
          return SURVIVAL;
        case CREATIVE:
          return CREATIVE;
        case ADVENTURE:
          return ADVENTURE;
        case SPECTATOR:
          return SPECTATOR;
        default:
          throw new IllegalArgumentException("Unable to resolve game mode " + gameMode);
      }
    }

    public int id() {
      return id;
    }
  }
}
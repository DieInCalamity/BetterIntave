package de.jpx3.intave.module.tracker.player;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import de.jpx3.intave.adapter.MinecraftVersions;
import de.jpx3.intave.klass.Lookup;
import de.jpx3.intave.module.Module;
import de.jpx3.intave.module.Modules;
import de.jpx3.intave.module.linker.packet.ListenerPriority;
import de.jpx3.intave.module.linker.packet.PacketSubscription;
import de.jpx3.intave.module.tracker.entity.EntityTracker;
import de.jpx3.intave.packet.reader.*;
import de.jpx3.intave.share.ClientMathHelper;
import de.jpx3.intave.user.User;
import de.jpx3.intave.user.UserRepository;
import de.jpx3.intave.user.meta.AbilityMetadata;
import de.jpx3.intave.user.meta.MovementMetadata;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;

import static de.jpx3.intave.module.linker.packet.PacketId.Client.ABILITIES_IN;
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
  public void sentAbilities(User user, AbilityOutReader reader) {
    AbilityMetadata abilityData = user.meta().abilities();
    float flyingSpeed = reader.flyingSpeed();
    float walkingSpeed = reader.walkingSpeed();
    boolean allowedFlight = reader.flyingAllowed();
    user.tickFeedback(() -> {
      abilityData.setWalkSpeed(walkingSpeed);
      abilityData.setFlySpeed(flyingSpeed);
      abilityData.setAllowFlying(allowedFlight);
    });
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
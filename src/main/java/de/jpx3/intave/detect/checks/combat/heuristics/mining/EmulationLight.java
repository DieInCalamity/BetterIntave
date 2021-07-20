package de.jpx3.intave.detect.checks.combat.heuristics.mining;

import de.jpx3.intave.detect.checks.combat.heuristics.MiningStrategy;
import de.jpx3.intave.executor.BackgroundExecutor;
import de.jpx3.intave.fakeplayer.EntityIdentifierPrefetch;
import de.jpx3.intave.fakeplayer.FakePlayer;
import de.jpx3.intave.fakeplayer.movement.CameraUtils;
import de.jpx3.intave.fakeplayer.movement.types.ConvertEntityMovement;
import de.jpx3.intave.user.User;
import de.jpx3.intave.user.UserMetaAttackData;
import de.jpx3.intave.user.UserMetaMovementData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.concurrent.ThreadLocalRandom;

public final class EmulationLight extends MiningStrategyExecutor {
  public EmulationLight(User user) {
    super(user);
  }

  @Override
  protected void setup() {
    User.UserMeta meta = user().meta();
    UserMetaAttackData attackData = meta.attackData();
    if (attackData.fakePlayer() != null) {
      return;
    }
    int entityID = EntityIdentifierPrefetch.acquireEntityId();
    BackgroundExecutor.execute(() -> {
      FakePlayer fakePlayer = FakePlayer
        .builder()
        .withEntityID(entityID)
        .withMovement(new ConvertEntityMovement())
        .invisible()
        .invisibleInTabList()
        .withParentPlayer(user().player())
        .withAttackSubscriber(() -> saveAnomalyWithID(1))
        .build();

      fakePlayer.syncSpawn(locationBehind(user(), ThreadLocalRandom.current().nextInt(1, 2)));
    });
  }

  public static Location locationBehind(User user, double distance) {
    UserMetaMovementData movementData = user.meta().movementData();
    float rotationYaw = movementData.rotationYaw;
    Location location = movementData.verifiedLocation().clone();
    Vector direction = CameraUtils.getDirection(rotationYaw, 0.0f);
    location.add(direction.multiply(-distance));
    location.add(0.0, 2.0, 0.0);
    return location;
  }

  @Override
  protected void stopStrategy() {
    UserMetaAttackData attackData = user().meta().attackData();
    FakePlayer fakePlayer = attackData.fakePlayer();
    if (fakePlayer != null) {
      fakePlayer.despawn();
    }
  }

  @Override
  public MiningStrategy miningStrategy() {
    return MiningStrategy.EMULATION_LIGHT;
  }
}
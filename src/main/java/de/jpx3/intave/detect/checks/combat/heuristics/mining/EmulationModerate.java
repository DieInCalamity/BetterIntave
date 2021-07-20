package de.jpx3.intave.detect.checks.combat.heuristics.mining;

import de.jpx3.intave.detect.checks.combat.heuristics.MiningStrategy;
import de.jpx3.intave.executor.BackgroundExecutor;
import de.jpx3.intave.fakeplayer.EntityIdentifierPrefetch;
import de.jpx3.intave.fakeplayer.FakePlayer;
import de.jpx3.intave.fakeplayer.movement.types.ConvertEntityMovement;
import de.jpx3.intave.user.User;
import de.jpx3.intave.user.UserMetaAttackData;

import java.util.concurrent.ThreadLocalRandom;

import static de.jpx3.intave.detect.checks.combat.heuristics.mining.EmulationLight.locationBehind;

public final class EmulationModerate extends MiningStrategyExecutor{
  public EmulationModerate(User user) {
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
        .visible()
        .visibleInTabList()
        .equipArmor()
        .equipHeldItem()
        .withParentPlayer(user().player())
        .withAttackSubscriber(() -> saveAnomalyWithID(2))
        .build();
      fakePlayer.syncSpawn(locationBehind(user(), ThreadLocalRandom.current().nextInt(1, 2)));
    });
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
    return MiningStrategy.EMULATION_MODERATE;
  }
}
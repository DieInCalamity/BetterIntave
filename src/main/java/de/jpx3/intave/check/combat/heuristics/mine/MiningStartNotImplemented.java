package de.jpx3.intave.check.combat.heuristics.mine;

import de.jpx3.intave.check.combat.heuristics.MiningStrategy;
import de.jpx3.intave.user.User;

public final class MiningStartNotImplemented extends MiningStrategyExecutor {
  public MiningStartNotImplemented(User user) {
    super(user);
  }

  @Override
  public MiningStrategy miningStrategy() {
    return MiningStrategy.RAYTRX;
  }
}
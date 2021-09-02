package de.jpx3.intave.check.combat.heuristics.mine;

import de.jpx3.intave.check.combat.heuristics.MiningStrategy;

public final class MiningStrategyContainer {
  private final MiningStrategy miningStrategy;
  private final MiningStrategyExecutor executor;

  public MiningStrategyContainer(
    MiningStrategy miningStrategy,
    MiningStrategyExecutor executor
  ) {
    this.miningStrategy = miningStrategy;
    this.executor = executor;
  }

  public MiningStrategy miningStrategy() {
    return miningStrategy;
  }

  public MiningStrategyExecutor executor() {
    return executor;
  }
}
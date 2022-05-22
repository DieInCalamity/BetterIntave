package de.jpx3.intave.check.movement.physics;

import de.jpx3.intave.user.User;
import de.jpx3.intave.user.UserLocal;

public final class SimulationStack {
  private static final UserLocal<SimulationStack> stackUserLocal = UserLocal.withInitial(SimulationStack::new);
  private static final int DEFAULT_DISTANCE = Integer.MAX_VALUE;

  private Simulation simulation;
  private double smallestDistance;

  public SimulationStack() {
    this.smallestDistance = DEFAULT_DISTANCE;
  }

  public void restore() {
    simulation = Simulation.invalid();
    smallestDistance = DEFAULT_DISTANCE;
  }

  public void tryAppendToState(
    Simulation simulation,
    double newDistance
  ) {
    if (newDistance < this.smallestDistance) {
      appendToState(simulation, newDistance);
    }
  }

  private void appendToState(
    Simulation simulation,
    double newDistance
  ) {
    this.simulation = simulation;
    this.smallestDistance = newDistance;
  }

  public boolean noMatch() {
    return simulation == null || this.smallestDistance == DEFAULT_DISTANCE;
  }

  public Simulation bestSimulation() {
    return simulation;
  }

  public int forward() {
    return configuration().forward();
  }

  public int strafe() {
    return configuration().strafe();
  }

  public boolean jumped() {
    return configuration().isJumping();
  }

  public boolean sprinted() {
    return configuration().isSprinting();
  }

  public boolean reduced() {
    return configuration().isReducing();
  }

  public boolean handActive() {
    return configuration().isHandActive();
  }

  public double smallestDistance() {
    return smallestDistance;
  }

  public MovementConfiguration configuration() {
    return simulation.configuration();
  }

  public static SimulationStack of(User user) {
    SimulationStack simulationStack = stackUserLocal.get(user);
    simulationStack.restore();
    return simulationStack;
  }
}

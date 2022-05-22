package de.jpx3.intave.check.movement.physics;

import de.jpx3.intave.user.User;

public interface SimulationProcessor {
  Simulation simulate(User user, Simulator simulator);

  default Simulation simulateWithoutKeyPress(User user, Simulator simulator) {
    return simulateWithKeyPress(user, simulator, 0, 0, false);
  }

  @Deprecated
  Simulation simulateWithKeyPress(User user, Simulator simulator, int forward, int strafe, boolean jumped);
}

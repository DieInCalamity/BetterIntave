package de.jpx3.intave.check.movement.physics;

import de.jpx3.intave.player.collider.complex.ComplexColliderSimulationResult;
import de.jpx3.intave.user.User;

public interface SimulationProcessor {
  ComplexColliderSimulationResult simulate(User user, Simulator simulator);

  default ComplexColliderSimulationResult simulateWithoutKeyPress(
    User user, Simulator simulator
  ) {
    return simulateWithKeyPress(user, simulator,0, 0, false);
  }

  ComplexColliderSimulationResult simulateWithKeyPress(User user, Simulator simulator, int forward, int strafe, boolean jumped);
}

package de.jpx3.intave.check.movement.physics;

import de.jpx3.intave.math.MathHelper;
import de.jpx3.intave.player.collider.complex.ComplexColliderSimulationResult;
import de.jpx3.intave.user.User;
import de.jpx3.intave.user.UserLocal;

public final class Simulation {
  private final static UserLocal<Simulation> simulationUserLocal = UserLocal.withInitial(Simulation::new);

  private ComplexColliderSimulationResult colliderResult;
  private String details = "";

  private Simulation() {

  }

  public void flush(ComplexColliderSimulationResult colliderResult) {
    this.colliderResult = colliderResult;
    this.details = "";
  }

  public double accuracy(MotionVector motionVector) {
    return MathHelper.distanceOf(motion(), motionVector);
  }

  public MotionVector motion() {
    return colliderResult.motion();
  }

  public void append(String details) {
    this.details += details;
  }

  public String details() {
    return details;
  }

  public ComplexColliderSimulationResult collider() {
    return colliderResult;
  }

  public static Simulation construct(User user, ComplexColliderSimulationResult colliderResult) {
    Simulation simulation = simulationUserLocal.get(user);
    simulation.flush(colliderResult);
    return simulation;
  }
}

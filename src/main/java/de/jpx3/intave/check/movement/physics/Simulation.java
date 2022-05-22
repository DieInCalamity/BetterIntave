package de.jpx3.intave.check.movement.physics;

import de.jpx3.intave.annotate.Relocate;
import de.jpx3.intave.player.collider.complex.ColliderSimulationResult;
import de.jpx3.intave.shade.Motion;
import de.jpx3.intave.user.User;
import de.jpx3.intave.user.UserLocal;

import static de.jpx3.intave.math.MathHelper.distanceOf;

@Relocate
public final class Simulation {
  private static final Simulation INVALID_SIMULATION = new Simulation(MovementConfiguration.empty(), ColliderSimulationResult.invalid());

  private static final UserLocal<Simulation> simulationUserLocal = UserLocal.withInitial(Simulation::new);
  private MovementConfiguration configuration;
  private ColliderSimulationResult colliderResult;
  private String details = "";

  private Simulation() {
  }

  private Simulation(
    MovementConfiguration configuration,
    ColliderSimulationResult colliderResult
  ) {
    this.configuration = configuration;
    this.colliderResult = colliderResult;
  }

  public void flush(MovementConfiguration configuration, ColliderSimulationResult colliderResult) {
    this.configuration = configuration;
    this.colliderResult = colliderResult;
    this.details = "";
  }

  public double accuracy(Motion motionVector) {
    return distanceOf(motion(), motionVector);
  }

  public Motion motion() {
    return colliderResult.motion();
  }

  public void append(String details) {
    this.details += details;
  }

  public String details() {
    return details;
  }

  public ColliderSimulationResult collider() {
    return colliderResult;
  }

  public MovementConfiguration configuration() {
    return configuration;
  }

  public Simulation reusableCopy() {
    return new Simulation(configuration, colliderResult);
  }

  public static Simulation of(User user, MovementConfiguration configuration, ColliderSimulationResult colliderResult) {
    Simulation simulation = simulationUserLocal.get(user);
    simulation.flush(configuration, colliderResult);
    return simulation;
  }

  public static Simulation invalid() {
    return INVALID_SIMULATION;
  }
}

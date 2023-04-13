package de.jpx3.intave.check.movement.physics;

import de.jpx3.intave.check.movement.Physics;
import de.jpx3.intave.share.Motion;
import de.jpx3.intave.share.Position;
import de.jpx3.intave.user.User;

public abstract class Simulator {
  private Physics physics;

  public final void enterLinkage(Physics physics) {
    this.physics = physics;
  }

  public abstract Simulation simulate(
    User user, Motion motion,
    SimulationEnvironment environment,
    MovementConfiguration configuration
  );

  public void prepareNextTick(
    User user, SimulationEnvironment environment,
    Position position, Motion motion
  ) {
    prepareNextTick(
      user, environment,
      position.getX(), position.getY(), position.getZ(),
      motion.motionX(), motion.motionY(), motion.motionZ()
    );
  }

  public abstract void prepareNextTick(
    User user,
    SimulationEnvironment environment,
    double positionX, double positionY, double positionZ,
    double motionX, double motionY, double motionZ
  );

  public abstract void setback(
    User user,
    SimulationEnvironment environment,
    double predictedX, double predictedY, double predictedZ
  );

  public String debugName() {
    return "";
  }

  @Deprecated
  protected Physics physics() {
    return physics;
  }

  public float stepHeight() {
    return 0.6f;
  }

  public boolean affectedByMovementKeys() {
    return true;
  }
}
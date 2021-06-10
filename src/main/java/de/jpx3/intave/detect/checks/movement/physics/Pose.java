package de.jpx3.intave.detect.checks.movement.physics;

import de.jpx3.intave.detect.checks.movement.physics.simulators.DefaultPoseSimulator;
import de.jpx3.intave.detect.checks.movement.physics.simulators.ElytraPoseSimulator;
import de.jpx3.intave.detect.checks.movement.physics.simulators.HorsePoseSimulator;

public enum Pose {
  PLAYER(new DefaultPoseSimulator(), ""),
  ELYTRA(new ElytraPoseSimulator(), "ELYTRA"),
  HORSE(new HorsePoseSimulator(), "HORSE");

  private final PoseSimulator calculationPart;
  private final String debug;

  Pose(PoseSimulator calculationPart, String debug) {
    this.calculationPart = calculationPart;
    this.debug = debug;
  }

  public PoseSimulator simulator() {
    return calculationPart;
  }

  public String debugPrefix() {
    return debug;
  }
}
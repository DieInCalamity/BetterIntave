package de.jpx3.intave.detect.checks.movement.physics;

import de.jpx3.intave.detect.checks.movement.physics.simulator.DefaultPoseSimulator;
import de.jpx3.intave.detect.checks.movement.physics.simulator.ElytraPoseSimulator;
import de.jpx3.intave.detect.checks.movement.physics.simulator.HorsePoseSimulator;

public enum SimulationService {
  PLAYER(new DefaultPoseSimulator(), ""),
  ELYTRA(new ElytraPoseSimulator(), "ELYTRA"),
  HORSE(new HorsePoseSimulator(), "HORSE");

  private final PoseSimulator calculationPart;
  private final String debug;

  SimulationService(PoseSimulator calculationPart, String debug) {
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
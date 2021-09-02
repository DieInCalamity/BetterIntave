package de.jpx3.intave.check.movement.physics;

import java.util.Arrays;
import java.util.List;

public final class Simulators {
  public final static Simulator PLAYER = new DefaultSimulator();
  public final static Simulator ELYTRA = new ElytraSimulator();
  public final static Simulator HORSE = new HorseSimulator();

  private final static List<Simulator> ALL_SIMULATORS = Arrays.asList(PLAYER, ELYTRA, HORSE);

  public static List<Simulator> simulators() {
    return ALL_SIMULATORS;
  }
}

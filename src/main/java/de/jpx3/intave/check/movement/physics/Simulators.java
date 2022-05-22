package de.jpx3.intave.check.movement.physics;

import de.jpx3.intave.annotate.Relocate;

import java.util.Arrays;
import java.util.List;

@Relocate
public final class Simulators {
  public static final Simulator PLAYER = new BaseSimulator();
  public static final Simulator ELYTRA = new ElytraSimulator();
  public static final Simulator HORSE = new HorseSimulator();
  public static final Simulator BOAT = new BoatSimulator();

  private static final List<Simulator> ALL_SIMULATORS = Arrays.asList(PLAYER, ELYTRA, HORSE, BOAT);

  public static List<Simulator> simulators() {
    return ALL_SIMULATORS;
  }
}

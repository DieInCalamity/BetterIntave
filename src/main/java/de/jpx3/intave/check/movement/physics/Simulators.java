package de.jpx3.intave.check.movement.physics;

import de.jpx3.intave.annotate.Relocate;

import java.util.Arrays;
import java.util.List;

@Relocate
public final class Simulators {
  public final static Simulator PLAYER = new BaseSimulator();
  public final static Simulator ELYTRA = new ElytraSimulator();
  public final static Simulator HORSE = new HorseSimulator();
  public final static Simulator BOAT = new BoatSimulator();

  private final static List<Simulator> ALL_SIMULATORS = Arrays.asList(PLAYER, ELYTRA, HORSE, BOAT);

  public static List<Simulator> simulators() {
    return ALL_SIMULATORS;
  }
}

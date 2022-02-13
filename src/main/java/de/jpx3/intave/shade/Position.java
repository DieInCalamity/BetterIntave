package de.jpx3.intave.shade;

import org.bukkit.util.Vector;

import java.io.Serializable;

public final class Position extends Vector implements Serializable {
  public Position() {
  }

  public Position(double xCoordinate, double yCoordinate, double zCoordinate) {
    super(xCoordinate, yCoordinate, zCoordinate);
  }

  public static Position empty() {
    return new Position();
  }
}

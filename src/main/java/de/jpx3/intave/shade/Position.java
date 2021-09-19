package de.jpx3.intave.shade;

public final class Position {
  public double xCoordinate, yCoordinate, zCoordinate;

  public Position() {
  }

  public Position(double xCoordinate, double yCoordinate, double zCoordinate) {
    this.xCoordinate = xCoordinate;
    this.yCoordinate = yCoordinate;
    this.zCoordinate = zCoordinate;
  }

  public Position with(Motion motion) {
    return new Position(xCoordinate + motion.motionX, yCoordinate + motion.motionY, zCoordinate + motion.motionZ);
  }

  public static Position empty() {
    return new Position();
  }
}

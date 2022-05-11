package de.jpx3.intave.player.collider.complex;

import de.jpx3.intave.shade.Motion;
import de.jpx3.intave.user.User;

public interface ColliderProcessor {
  ColliderSimulationResult collide(
    User user, Motion context,
    double positionX, double positionY, double positionZ,
    boolean inWeb
    );
}
package de.jpx3.intave.packet.converter;

import de.jpx3.intave.annotate.KeepEnumInternalNames;

@KeepEnumInternalNames
public enum PlayerAction {
  PRESS_SHIFT_KEY,
  RELEASE_SHIFT_KEY,
  START_SNEAKING,
  STOP_SNEAKING,
  STOP_SLEEPING,
  START_SPRINTING,
  STOP_SPRINTING,
  START_RIDING_JUMP,
  STOP_RIDING_JUMP,
  OPEN_INVENTORY,
  START_FALL_FLYING,
  RIDING_JUMP;

  public boolean isSneaking() {
    switch (this) {
      case STOP_SNEAKING:
      case START_SNEAKING:
      case PRESS_SHIFT_KEY:
      case RELEASE_SHIFT_KEY:
        return true;
      default:
        return false;
    }
  }
}

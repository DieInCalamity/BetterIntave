package de.jpx3.intave.event.packet;

public enum ListenerPriority {

  LOWEST(1),
  LOW(2),
  NORMAL(3),
  HIGH(4),
  HIGHEST(5),
  MONITOR(6)

  ;

  final int slot;

  ListenerPriority(int slot) {
    this.slot = slot;
  }

  public int slot() {
    return slot;
  }

  public com.comphenix.protocol.events.ListenerPriority toProtocolLibPriority() {
    switch (this) {
      case LOWEST:
        return com.comphenix.protocol.events.ListenerPriority.LOWEST;
      case LOW:
        return com.comphenix.protocol.events.ListenerPriority.LOW;
      case NORMAL:
        return com.comphenix.protocol.events.ListenerPriority.NORMAL;
      case HIGH:
        return com.comphenix.protocol.events.ListenerPriority.HIGH;
      case HIGHEST:
        return com.comphenix.protocol.events.ListenerPriority.HIGHEST;
      case MONITOR:
        return com.comphenix.protocol.events.ListenerPriority.MONITOR;
    }
    return null;
  }
}

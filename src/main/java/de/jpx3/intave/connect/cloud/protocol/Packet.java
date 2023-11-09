package de.jpx3.intave.connect.cloud.protocol;

import de.jpx3.intave.connect.cloud.protocol.listener.PacketListener;

public abstract class Packet<E extends PacketListener> implements Serializable {
  private final String name;
  private final String version;
  private final Direction direction;
  private final TransferMode mode;

  public Packet(Direction direction, String name, String version, TransferMode mode) {
    this.name = name;
    this.version = version;
    this.direction = direction;
    this.mode = mode;
  }

  public void accept(E listener) {
    listener.onSelect(this);
  }

  public final Direction direction() {
    return direction;
  }

  public final String name() {
    return name;
  }

  public final String version() {
    return version;
  }

  public final TransferMode transferMode() {
    return mode;
  }
}

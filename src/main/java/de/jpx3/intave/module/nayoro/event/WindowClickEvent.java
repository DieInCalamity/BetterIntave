package de.jpx3.intave.module.nayoro.event;

import de.jpx3.intave.module.nayoro.Environment;
import de.jpx3.intave.module.nayoro.event.sink.EventSink;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public final class WindowClickEvent extends Event {
  private int windowId;
  private int slot;
  private int button;
  private int actionNumber;
  private int mode;
  private String unused = "";
  private int connectionStability;
  private long timestamp;

  public WindowClickEvent() {
  }

  public WindowClickEvent(
    int windowId, int slot,
    int button, int actionNumber,
    int mode,
    String unused, int connectionStability,
    long timestamp
  ) {
    this.windowId = windowId;
    this.slot = slot;
    this.button = button;
    this.actionNumber = actionNumber;
    this.mode = mode;
    this.unused = unused;
    this.connectionStability = connectionStability;
    this.timestamp = timestamp;
  }

  public static WindowClickEvent create(
    int container, int slot, int inventoryClickType, int button, int mode
  ) {
    return new WindowClickEvent(container, slot, button, inventoryClickType, mode, "null", 0, System.currentTimeMillis());
  }

  @Override
  public void serialize(Environment environment, DataOutput out) throws IOException {
    out.writeInt(windowId);
    out.writeInt(slot);
    out.writeInt(button);
    out.writeInt(actionNumber);
    out.writeInt(mode);
    out.writeUTF(unused);
    out.writeInt(connectionStability);
    out.writeLong(timestamp);
  }

  @Override
  public void deserialize(Environment environment, DataInput in) throws IOException {
    windowId = in.readInt();
    slot = in.readInt();
    button = in.readInt();
    actionNumber = in.readInt();
    mode = in.readInt();
    unused = in.readUTF();
    connectionStability = in.readInt();
    timestamp = in.readLong();
  }

  @Override
  public void accept(EventSink sink) {
    sink.visit(this);
  }
}

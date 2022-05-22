package de.jpx3.intave.module.nayoro.event;

import de.jpx3.intave.module.nayoro.Environment;
import de.jpx3.intave.module.nayoro.event.sink.EventSink;

import java.io.DataInput;
import java.io.DataOutput;

public final class ClickEvent extends Event {
  private static final ClickEvent SINGLETON = new ClickEvent();

  public ClickEvent() {
  }

  @Override
  public void serialize(Environment environment, DataOutput out) {
  }

  @Override
  public void deserialize(Environment environment, DataInput in) {
  }

  @Override
  public void accept(EventSink sink) {
    sink.visit(this);
  }

  public static ClickEvent create() {
    return SINGLETON;
  }
}

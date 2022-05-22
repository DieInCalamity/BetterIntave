package de.jpx3.intave.module.nayoro;

import de.jpx3.intave.module.nayoro.event.Event;
import de.jpx3.intave.module.nayoro.event.EventRegistry;
import de.jpx3.intave.module.nayoro.event.PlayerInitEvent;
import de.jpx3.intave.module.nayoro.event.PropertiesEvent;
import de.jpx3.intave.module.nayoro.event.sink.EventSink;

import java.io.Closeable;
import java.io.DataOutput;
import java.io.IOException;

class RecordEventSink extends EventSink {
  private long last = System.currentTimeMillis();
  private final Environment environment;
  private final DataOutput dataOutput;
  private boolean setup = false;

  public RecordEventSink(Environment environment, DataOutput dataOutput) {
    this.environment = environment;
    this.dataOutput = dataOutput;
  }

  public synchronized void setupIfNeeded() {
    if (!setup) {
      setup = true;
      visit(new PlayerInitEvent(environment.mainPlayer()));
      visit(new PropertiesEvent(environment.properties()));
    }
  }

  @Override
  public void visitAny(Event event) {
    setupIfNeeded();
    try {
      dataOutput.writeInt((int) (System.currentTimeMillis() - last));
      dataOutput.writeByte(EventRegistry.idOf(event));
      event.serialize(environment, dataOutput);
    } catch (IOException exception) {
      throw new IllegalStateException("Could not serialize event " + event.getClass().getName(), exception);
    }
    last = System.currentTimeMillis();
  }

  @Override
  public void close() {
    try {
      if (dataOutput instanceof Closeable) {
        ((Closeable) dataOutput).close();
      }
    } catch (IOException exception) {
      throw new IllegalStateException("Could not close data output", exception);
    }
  }
}

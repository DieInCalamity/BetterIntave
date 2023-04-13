package de.jpx3.intave.module.nayoro;

import de.jpx3.intave.module.nayoro.event.Event;

import java.io.DataInputStream;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

public final class InstantPlayback extends Playback implements Runnable {
  private final Executor executor;
  private final Consumer<? super Playback> onComplete;
  private boolean interrupted = false;
  private long passedTime = 0;

  public InstantPlayback(DataInputStream stream, Executor executor) {
    this(stream, executor, (playback) -> {});
  }

  public InstantPlayback(DataInputStream stream, Executor executor, Consumer<? super Playback> onComplete) {
    super(stream);
    this.executor = executor;
    this.onComplete = onComplete;
  }

  @Override
  public void start() {
    executor.execute(this);
  }

  @Override
  public void run() {
    try {
      Event event;
      // ignore schedule time
      while ((event = nextEvent()) != null && !interrupted) {
        long offset = event.offset();
        passedTime += offset;
        visitSelect(event);
      }
    } finally {
      onComplete.accept(this);
    }
  }

  @Override
  public void stop() {
    interrupted = true;
  }

  @Override
  public long currentTime() {
    return passedTime;
  }
}

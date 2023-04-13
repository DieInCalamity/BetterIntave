package de.jpx3.intave.module.feedback;

import java.util.concurrent.atomic.AtomicLong;

public final class PendingCountingFeedbackObserver implements FeedbackObserver {
  private final AtomicLong counter = new AtomicLong();

  @Override
  public void sent(FeedbackRequest<?> request) {
    counter.incrementAndGet();
  }

  @Override
  public void received(FeedbackRequest<?> request) {
    counter.decrementAndGet();
  }

  @Override
  public void failed() {
    counter.decrementAndGet();
  }

  public long pending() {
    return counter.get();
  }
}

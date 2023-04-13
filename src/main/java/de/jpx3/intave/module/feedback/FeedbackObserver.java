package de.jpx3.intave.module.feedback;

import de.jpx3.intave.annotate.Nullable;

public interface FeedbackObserver {
  void sent(FeedbackRequest<?> request);
  void received(FeedbackRequest<?> request);
  void failed();

  @Nullable
  static FeedbackObserver merge(FeedbackObserver... trackers) {
    if (trackers.length == 0) {
      return null;
    } else if (trackers.length == 1) {
      return trackers[0];
    } else {
      return new FeedbackObserver() {
        @Override
        public void sent(FeedbackRequest<?> request) {
          for (FeedbackObserver tracker : trackers) {
            tracker.sent(request);
          }
        }

        @Override
        public void received(FeedbackRequest<?> request) {
          for (FeedbackObserver tracker : trackers) {
            tracker.received(request);
          }
        }

        @Override
        public void failed() {
          for (FeedbackObserver tracker : trackers) {
            tracker.failed();
          }
        }
      };
    }
  }
}

package de.jpx3.intave.diagnostic;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public final class IterativeStudy {
  private static final Map<Integer, AtomicLong> trials = new ConcurrentHashMap<>();

  public static final Iterator USE_ITEM_ITERATOR = new Iterator();
  public static final Iterator ATTACK_REDUCE_ITERATOR = new Iterator();
  public static final Iterator JUMP_ITERATOR = new Iterator();

  public static final Map<String, Iterator> ITERATORS = new HashMap<String, Iterator>() {
    {
      put("use-item", USE_ITEM_ITERATOR);
      put("attack-reduce", ATTACK_REDUCE_ITERATOR);
      put("jump", JUMP_ITERATOR);
    }
  };

  public static void enterTrials(int tickLatency) {
    trials.computeIfAbsent(tickLatency, x -> new AtomicLong(0L)).incrementAndGet();
  }

  public static double average() {
    AtomicLong score = new AtomicLong();
    AtomicLong count = new AtomicLong();
    trials.forEach((aShort, atomicLong) -> {
      score.addAndGet(aShort * atomicLong.get());
      count.addAndGet(atomicLong.get());
    });
    return (double) score.get() / Math.max((double) count.get(), 1);
  }

  public static class Iterator {
    private long runsTotal;
    private long guesses;
    private long passes;

    public void run() {
      runsTotal++;
      guesses++;
    }

    public void pass() {
      passes++;
    }

    public long totalRuns() {
      return runsTotal;
    }

    public double successRate() {
      return (double) passes / (double) guesses;
    }
  }
}

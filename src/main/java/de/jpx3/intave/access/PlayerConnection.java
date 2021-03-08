package de.jpx3.intave.access;

import java.util.function.BiConsumer;

/**
 * Class generated using IntelliJ IDEA
 * Created by Richard Strunk 2020
 */

public interface PlayerConnection {
  int latency();
  int latencyJitter();
  void subscribe(BiConsumer<Integer, Integer> callback);
  long packetSentByClient();
  long packetSentToClient();
}
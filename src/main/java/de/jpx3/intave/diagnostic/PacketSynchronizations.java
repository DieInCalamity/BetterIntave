package de.jpx3.intave.diagnostic;

import com.comphenix.protocol.PacketType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public final class PacketSynchronizations {
  private static final Map<PacketType, AtomicLong> resynchronized = new ConcurrentHashMap<>();

  public static void enterResynchronization(PacketType type) {
    resynchronized.computeIfAbsent(type, ignored -> new AtomicLong()).incrementAndGet();
  }

  public static Map<String, Long> output() {
    return resynchronized.entrySet().stream()
      .collect(Collectors.toMap(entry -> entry.getKey().name(), entry -> entry.getValue().get()));
  }
}

package de.jpx3.intave.diagnostics;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class KeyPressStudy {
  private final static Map<String, Long> keys = new ConcurrentHashMap<>();

  public static void enterKeyPress(int forward, int strafe) {
    String keys = resolveKeysFromInput(forward, strafe);
    KeyPressStudy.keys.put(keys, KeyPressStudy.keys.getOrDefault(keys, 0L) + 1);
  }

  public static Map<String, Long> result() {
    return keys;
  }

  public static Map<String, Double> resultShare() {
    Map<String, Double> shareMap = new HashMap<>();
    long total = keys.values().stream().mapToLong(l -> l).sum();
    keys.forEach((key, value) -> shareMap.put(key, (double) value / (double) total));
    return shareMap;
  }

  private static String resolveKeysFromInput(int forward, int strafe) {
    String key = "";
    if (forward == 1) {
      key += "W";
    } else if (forward == -1) {
      key += "S";
    }
    if (strafe == 1) {
      key += "A";
    } else if (strafe == -1) {
      key += "D";
    }
    return key;
  }
}

package de.jpx3.intave.check.combat.heuristics;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public final class Anomaly {
  private final String key;
  private final long added;
  private final String description;
  private final Confidence confidence;
  private final Type type;
  private final int options;
  private final long expireDuration;

  private Anomaly(
    String key,
    Confidence confidence,
    Type type,
    String description,
    int options,
    long expireDuration
  ) {
    this.key = key;
    this.added = System.currentTimeMillis();
    this.description = description.toLowerCase(Locale.ROOT);
    this.confidence = confidence;
    this.type = type;
    this.options = options;
    this.expireDuration = expireDuration;
  }

  public String key() {
    return key;
  }

  public long timestamp() {
    return added;
  }

  public String description() {
    return description;
  }

  public Confidence confidence() {
    return confidence;
  }

  public boolean expired() {
    return System.currentTimeMillis() - added > expireDuration;
  }

  public boolean active() {
    return System.currentTimeMillis() - added > AnomalyOption.delayInSeconds(options) * 1000L;
  }

  public int delay() {
    return AnomalyOption.delayInSeconds(options);
  }

  public int limit() {
    return AnomalyOption.limit(options);
  }

  public boolean miningSuggested() {
    return AnomalyOption.matches(options, AnomalyOption.SUGGEST_MINING);
  }

  public boolean requiresHeavyCombat() {
    return AnomalyOption.matches(options, AnomalyOption.REQUIRES_HEAVY_COMBAT);
  }

  public boolean forceApply() {
    return AnomalyOption.matches(options, AnomalyOption.FORCE_APPLY);
  }

  public Type type() {
    return type;
  }

  @Override
  public String toString() {
    return "Anomaly{" +
      "key='" + key + '\'' +
      ", description='" + description + '\'' +
      ", confidence=" + confidence +
      ", options=" + options +
      '}';
  }

  private static final long ANOMALY_EXPIRE_DURATION = TimeUnit.MINUTES.toMillis(5);

  public static Anomaly anomalyOf(String key, Confidence confidence, Type type, String description, int options, long expireDuration) {
    return new Anomaly(key, confidence, type, description, options, expireDuration);
  }

  public static Anomaly anomalyOf(String key, Confidence confidence, Type type, String description, int options) {
    return new Anomaly(key, confidence, type, description, options, ANOMALY_EXPIRE_DURATION);
  }

  public static Anomaly anomalyOf(String key, Confidence confidence, Type type, String description) {
    return new Anomaly(key, confidence, type, description, AnomalyOption.LIMIT_2, ANOMALY_EXPIRE_DURATION);
  }

  public enum Type {
    KILLAURA("killaura"),
    AUTOCLICKER("autoclicker");

    private final String name;

    Type(String name) {
      this.name = name;
    }

    public String typeName() {
      return name;
    }
  }

  public static class AnomalyOption {
    public static final int LIMIT_1 = 1;
    public static final int LIMIT_2 = 1 << 1;
    public static final int LIMIT_4 = 1 << 2;
    public static final int LIMIT_8 = 1 << 3;
    public static final int SUGGEST_MINING = 1 << 4;
    public static final int REQUIRES_HEAVY_COMBAT = 1 << 5;
    public static final int DELAY_16s = 1 << 6;
    public static final int DELAY_32s = 1 << 7;
    public static final int DELAY_64s = 1 << 8;
    public static final int DELAY_128s = 1 << 9;
    public static final int FORCE_APPLY = 1 << 10;

    public static boolean matches(int optionInt, int option) {
      return (optionInt & option) > 0;
    }

    public static int limit(int optionInt) {
      return (optionInt & (LIMIT_1 | LIMIT_2 | LIMIT_4 | LIMIT_8));
    }

    public static int delayInSeconds(int optionInt) {
      return (optionInt & (DELAY_16s | DELAY_32s | DELAY_64s | DELAY_128s)) >> 2;
    }
  }
}

package de.jpx3.intave.player;

import de.jpx3.intave.IntaveLogger;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

public final class FaultKicks {
  public static boolean POSITION_FAULTS = true;
  public static boolean MISSING_POSITION_UPDATE = true;
  public static boolean INVALID_PLAYER_ACTION = true;
  public static boolean FEEDBACK_FAULTS = true;
  public static boolean IGNORING_FEEDBACK = true;
  public static boolean IGNORING_KEEP_ALIVE = true;
  public static boolean INVALID_KEY_INPUT = true;

  private static final List<String> CONSOLE_OUTPUT = new ArrayList<>();

  public static void applyFrom(ConfigurationSection section) {
    POSITION_FAULTS = loadFrom(section, "position-faults", "position faults");
    MISSING_POSITION_UPDATE = loadFrom(section, "missing-position-update", "missing position updates");
    FEEDBACK_FAULTS = loadFrom(section, "feedback-faults", "feedback faults");
    IGNORING_FEEDBACK = loadFrom(section, "ignoring-feedback", "ignoring feedback packets");
    IGNORING_KEEP_ALIVE = loadFrom(section, "ignoring-keep-alive", "ignoring keep alive packets");
    INVALID_KEY_INPUT = loadFrom(section, "invalid-key-input", "invalid key inputs");
    INVALID_PLAYER_ACTION = loadFrom(section, "invalid-player-action", "invalid player actions");
    printWarnings();
  }

  private static void printWarnings() {
    if (CONSOLE_OUTPUT.isEmpty()) {
      return;
    }
    IntaveLogger.logger().info("Disabled fault kicks for " + describeListSelection(CONSOLE_OUTPUT) + " (not recommended)");
    CONSOLE_OUTPUT.clear();
  }

  private static String describeListSelection(List<String> elements) {
    int size = elements.size();
    if (size == 0) {
      return "";
    } else if (size == 1) {
      return elements.get(0);
    } else {
      return String.join(", ", elements.subList(0, size - 1)) + " and " + elements.get(size - 1);
    }
  }

  private static boolean loadFrom(ConfigurationSection section, String key, String warnMessage) {
    boolean value = section == null || section.getBoolean(key, true);
    if (!value) {
//      IntaveLogger.logger().warn("Disabled fault kicks for " + warnMessage + " (not recommended)");
      CONSOLE_OUTPUT.add(warnMessage);
    }
    return value;
  }
}

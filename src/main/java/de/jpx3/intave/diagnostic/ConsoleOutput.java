package de.jpx3.intave.diagnostic;

import de.jpx3.intave.IntaveLogger;
import org.bukkit.configuration.ConfigurationSection;

public final class ConsoleOutput {
  public static boolean FAULT_KICKS = true;
  public static boolean TRUSTFACTOR_DEBUG = true;
  public static boolean CLIENT_VERSION_DEBUG = true;
  public static boolean COMMAND_EXECUTION_DEBUG = true;

  public static void applyFrom(ConfigurationSection section) {
    FAULT_KICKS = loadFrom(section, "fault-kicks", "fault kicks");
    TRUSTFACTOR_DEBUG = loadFrom(section, "trustfactor", "trustfactor changes");
    CLIENT_VERSION_DEBUG = loadFrom(section, "client-version", "client version dumps");
    COMMAND_EXECUTION_DEBUG = loadFrom(section, "command-execution", "command executions");
  }

  private static boolean loadFrom(ConfigurationSection section, String key, String warnMessage) {
    boolean value = section == null || section.getBoolean(key, true);
    if (!value) {
      IntaveLogger.logger().info("Disabled debugs for " + warnMessage);
    }
    return value;
  }
}

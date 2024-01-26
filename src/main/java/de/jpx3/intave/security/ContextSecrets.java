package de.jpx3.intave.security;

import de.jpx3.intave.IntavePlugin;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static de.jpx3.intave.IntaveControl.GOMME_MODE;

public final class ContextSecrets {
  private static final Map<String, String> mapping = new HashMap<>();
  private static final Map<String, String> defaults = new HashMap<>();

  public static void setup() {
    if (GOMME_MODE) {
      IntavePlugin plugin = IntavePlugin.singletonInstance();
      File dataFolder = plugin.dataFolder();
      if (!dataFolder.exists()) {
        dataFolder.mkdirs();
      }
      File configFile = new File(dataFolder, "secrets.yml");
      if (!configFile.exists()) {
//        throw new IntaveInternalException("Secret file required");

      }
      YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(configFile);
      for (Map.Entry<String, Object> entry : yamlConfiguration.getValues(true).entrySet()) {
        String key = entry.getKey();
        Object value = entry.getValue();
        mapping.put(key, String.valueOf(value));
      }
    }
    defaults.put("cache-directory", System.getProperty("user.home"));
  }

  public static String secret(String key) {
    if (GOMME_MODE) {
      return mapping.getOrDefault(key.toLowerCase(), defaults.getOrDefault(key, "null"));
    }
    return "null";
  }
}

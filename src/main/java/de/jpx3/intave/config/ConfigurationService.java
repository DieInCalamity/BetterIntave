package de.jpx3.intave.config;

import de.jpx3.intave.IntavePlugin;
import de.jpx3.intave.access.IntaveException;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;

public final class ConfigurationService {
  private final IntavePlugin plugin;
  private final ConfigurationLoader loader;
  private final String configurationKey;

  public ConfigurationService(IntavePlugin plugin) {
    this.plugin = plugin;
    this.configurationKey = resolveConfigurationKey();
    this.loader = new ConfigurationLoader(configurationKey);
  }

  private String resolveConfigurationKey() {
    File dataFolder = plugin.getDataFolder();
    if (!dataFolder.exists()) {
      dataFolder.mkdirs();
    }
    File configFile = new File(dataFolder, "config.yml");
    if (!configFile.exists()) {
      plugin.saveResource("config.yml", false);
    }
    try {
      FileInputStream fileInputStream = new FileInputStream(configFile);
      YamlConfiguration configuration = new YamlConfiguration();
      InputStreamReader reader = new InputStreamReader(fileInputStream);
      configuration.load(reader);
      fileInputStream.close();
      reader.close();
      String configurationIdentifier = configuration.getString("config-identifier");
      if(configurationIdentifier == null) {
        throw new IntaveException("It seems like you are using an old/invalid configuration");
      }
      return configurationIdentifier;
    } catch (FileNotFoundException e) {
      throw new IntaveException("It seems like Intave is unable to create the default configuration file");
    } catch (InvalidConfigurationException | IOException e) {
      throw new IntaveException("It seems like your configuration is invalid", e);
    }
  }

  public void setupConfiguration(String requiredState) {
//    String hash = loader.precomputeConfigurationHash();
    int latestKnownState = loader().latestState();
    if(/*latestKnownState == 0 || *//* we don't have a configuration */
      requiredState == null ||  /* no connection to our servers */
      requiredState.equalsIgnoreCase(String.valueOf(latestKnownState)) /* configuration is up to date */
    ) {
      loader.loadConfiguration();
    } else {
      loader.loadConfigurationUpdatedForcefully();
    }
  }

  public void deleteCache() {
    loader.deleteCaches();
  }

  public ConfigurationLoader loader() {
    return loader;
  }

  public YamlConfiguration configuration() {
    return loader.configuration();
  }

  public String configurationKey() {
    return configurationKey;
  }
}
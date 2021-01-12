package de.jpx3.intave.config;

import com.google.common.collect.ImmutableMap;
import de.jpx3.intave.access.IntaveException;
import de.jpx3.intave.detect.IntaveCheck;
import de.jpx3.intave.tools.MathHelper;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class CheckConfiguration {
  private final IntaveCheck check;
  private volatile CheckSettings settingsAccess;
  private Map<Integer, List<String>> thresholdCommands = ImmutableMap.of();
  private String trustFactorPreventionAccessKey;

  public CheckConfiguration(IntaveCheck check) {
    this.check = check;
  }

  public IntaveCheck check() {
    return check;
  }

  public CheckSettings settings() {
    return settingsAccess;
  }

  public void setSettings(Map<String, Object> settings) {
    this.settingsAccess = new CheckSettings(settings, this);
  }

  public Map<Integer, List<String>> thresholdCommands() {
    return thresholdCommands;
  }

  public void setThresholdCommands(Map<Integer, List<String>> thresholdCommands) {
    this.thresholdCommands = thresholdCommands;
  }

  public String trustFactorPreventionLevelKey() {
    return trustFactorPreventionAccessKey;
  }

  public void setTrustFactorPreventionAccessKey(String trustFactorPreventionAccessKey) {
    this.trustFactorPreventionAccessKey = trustFactorPreventionAccessKey;
  }

  public void deleteCaches() {
    thresholdCommands = ImmutableMap.of();
  }

  public static class CheckSettings {
    private final Map<String, Object> access;
    private final CheckConfiguration configurationCache;

    public CheckSettings(
      Map<String, Object> access,
      CheckConfiguration configurationCache
    ) {
      this.access = ImmutableMap.copyOf(access);
      this.configurationCache = configurationCache;
    }

    public Map<Integer, List<String>> defaultThresholds() {
      return thresholdsBy("def-threshold");
    }

    public Map<Integer, List<String>> thresholdsBy(String key) {
      ConfigurationSection configurationSection = (ConfigurationSection) access.get(key);
      Map<Integer, List<String>> thresholdMap = new HashMap<>();
      for (String configurationSectionKey : configurationSection.getKeys(false)) {
        List<String> output = new ArrayList<>();
        if (configurationSection.isList(configurationSectionKey)) {
          output.addAll(configurationSection.getStringList(configurationSectionKey));
        } else {
          output.add(configurationSection.getString(configurationSectionKey));
        }
        thresholdMap.put(Integer.parseInt(configurationSectionKey), output);
      }
      return thresholdMap;
    }

    public List<String> stringListBy(String key) {
      //noinspection unchecked
      return (List<String>) uncheckedResolveOrDefault(key, new ArrayList<String>());
    }

    public String stringBy(String key) {
      return stringBy(key, "");
    }

    public String stringBy(String key, String def) {
      return (String) uncheckedResolveOrDefault(key, def);
    }

    public boolean boolBy(String key) {
      return boolBy(key, false);
    }

    public boolean boolBy(String key, boolean def) {
      try {
        return (boolean) uncheckedResolveOrDefault(key, def);
      } catch (ClassCastException exception) {
        throw new IntaveException(new InvalidConfigurationException("Expected " + key + " in check " + configurationCache.check().name() + " to be a boolean expression", exception));
      }
    }

    public double doubleBy(String key) {
      return doubleBy(key, 0);
    }

    public double doubleInBoundsBy(String key, double min, double max) {
      return doubleInBoundsBy(key, min, max, 0);
    }

    public double doubleInBoundsBy(String key, double min, double max, double def) {
      return MathHelper.minmax(doubleBy(key, def), min, max);
    }

    public double doubleBy(String key, double def) {
      try {
        return (double) uncheckedResolveOrDefault(key, def);
      } catch (ClassCastException exception) {
        return Double.parseDouble(String.valueOf(intBy(key, (int) def)));
      }
    }

    public long longBy(String key) {
      return longBy(key, 0);
    }

    public long longInBoundsBy(String key, long min, long max) {
      return longInBoundsBy(key, min, max, 0);
    }

    public long longInBoundsBy(String key, long min, long max, long def) {
      return MathHelper.minmax(longBy(key, def), min, max);
    }

    public long longBy(String key, long def) {
      try {
        return (long) uncheckedResolveOrDefault(key, def);
      } catch (ClassCastException exception) {
        return Long.parseLong(String.valueOf(intBy(key, (int) def)));
      }
    }

    public int intBy(String key) {
      return intBy(key, 0);
    }

    public int intInBoundsBy(String key, int min, int max) {
      return intInBoundsBy(key, min, max, 0);
    }

    public int intInBoundsBy(String key, int min, int max, int def) {
      return MathHelper.minmax(intBy(key, def), min, max);
    }

    public int intBy(String key, int def) {
      try {
        return (int) uncheckedResolveOrDefault(key, def);
      } catch (ClassCastException exception) {
        throw new IntaveException(new InvalidConfigurationException("Expected " + key + " in check " + configurationCache.check().name() + " to be a numeric expression", exception));
      }
    }

    private Object uncheckedResolveOrDefault(String key, Object def) {
      return access.getOrDefault(key, def);
    }
  }

  public static class CheckViolationDecaySettings {
    private volatile int amount;
    private volatile long delay;
    private volatile long wait;

    public CheckViolationDecaySettings(int amount, long delay, long wait) {
      this.amount = amount;
      this.delay = delay;
      this.wait = wait;
    }

    public int amount() {
      return amount;
    }

    public void setAmount(int amount) {
      this.amount = amount;
    }

    public long delay() {
      return delay;
    }

    public void setDelay(long delay) {
      this.delay = delay;
    }

    public long waitTime() {
      return wait;
    }

    public void setWait(long wait) {
      this.wait = wait;
    }

    public static CheckViolationDecaySettings ofMap(Map<String, Integer> settings) {
      return new CheckViolationDecaySettings(
        settings.getOrDefault("amount", 0),
        settings.getOrDefault("delay", 0),
        settings.getOrDefault("wait", 0)
      );
    }

    public static CheckViolationDecaySettings empty() {
      return new CheckViolationDecaySettings(0,0,0);
    }
  }
}

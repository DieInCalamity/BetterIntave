package de.jpx3.intave.trustfactor;

import de.jpx3.intave.access.IntaveInternalException;
import de.jpx3.intave.access.TrustFactor;
import de.jpx3.intave.logging.IntaveLogger;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.*;

public final class YamlTrustFactorConfiguration implements TrustFactorConfiguration {
  private final Map<String, EnumMap<TrustFactor, Integer>> settingsMap = new HashMap<>();

  public YamlTrustFactorConfiguration(YamlConfiguration configuration) {
    apply(configuration);
  }

  private void apply(YamlConfiguration trustFactorSettings) {
    for (Map.Entry<String, Object> configEntry : trustFactorSettings.getValues(true).entrySet()) {
      if(configEntry.getValue() instanceof ArrayList) {
        List<?> values = (List<?>) configEntry.getValue();
        apply(configEntry.getKey(), (List<Integer>) values);
      }
    }
  }

  private void apply(String key, List<Integer> values) {
    EnumMap<TrustFactor, Integer> enumMap = new EnumMap<>(TrustFactor.class);
    TrustFactor[] trustFactors = TrustFactor.values();
    for (int j = 0; j < trustFactors.length; j++) {
      TrustFactor value = trustFactors[j];
      enumMap.put(value, values.get(j));
    }
    settingsMap.put(key, enumMap);
  }

  @Override
  public int resolveSetting(String key, TrustFactor trustFactor) {
    EnumMap<TrustFactor, Integer> trustFactorIntegerEnumMap = settingsMap.get(key.toLowerCase(Locale.ROOT));
    if(trustFactorIntegerEnumMap == null) {
      throw new IntaveInternalException("Unable to find trust-factor setting for " + key);
    }
    try {
      return trustFactorIntegerEnumMap.get(trustFactor);
    } catch (NullPointerException exception) {
      IntaveLogger.logger().globalPrintLn(key + " " + settingsMap);
      throw exception;
    }
  }
}

package de.jpx3.intave.detect;

import de.jpx3.intave.IntavePlugin;
import de.jpx3.intave.user.User;
import de.jpx3.intave.user.UserRepository;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public abstract class IntaveCheck implements EventProcessor {
  private final IntavePlugin plugin;
  private final String checkName;
  private final String configurationKey;
  private final CheckStatistics statistics;
  public final CheckConfiguration checkConfiguration;

  public final List<IntaveCheckPart<?>> checkParts = new ArrayList<>();

  public IntaveCheck(String checkName, String configurationKey) {
    this.plugin = IntavePlugin.singletonInstance();
    this.checkName = checkName;
    this.configurationKey = configurationKey;
    this.statistics = new CheckStatistics();
    this.checkConfiguration = new CheckConfiguration(this);

    plugin.checkService().enterConfiguration(checkConfiguration);
  }

  protected User userOf(Player player) {
    return UserRepository.userOf(player);
  }

  protected void appendCheckPart(IntaveCheckPart<?> checkPart) {
    checkParts.add(checkPart);
  }

  public CheckStatistics statistics() {
    return statistics;
  }

  public int trustFactorSetting(String key, Player player) {
    String checkKey = configurationKey + "." + key;
    return plugin.trustFactorService().trustFactorSetting(checkKey, player);
  }

  public String name() {
    return checkName;
  }

  public String configurationKey() {
    return configurationKey;
  }

  public List<IntaveCheckPart<?>> checkParts() {
    return checkParts;
  }

  public boolean enabled() {
    return true;
  }
}
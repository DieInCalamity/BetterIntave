package de.jpx3.intave.detect;

import de.jpx3.intave.IntavePlugin;
import de.jpx3.intave.config.CheckConfiguration;
import de.jpx3.intave.user.User;
import de.jpx3.intave.user.UserRepository;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public abstract class IntaveCheck implements EventProcessor {
  private final IntavePlugin plugin;
  private final String checkName;
  private final String configurationName;
  public final CheckConfiguration checkConfiguration;

  public final List<IntaveCheckPart<?>> checkParts = new ArrayList<>();

  public IntaveCheck(String checkName, String configurationName) {
    this.plugin = IntavePlugin.singletonInstance();
    this.checkName = checkName;
    this.configurationName = configurationName;
    this.checkConfiguration = new CheckConfiguration(this);

  }

  protected User userOf(Player player) {
    return UserRepository.userOf(player);
  }

  protected void appendCheckPart(IntaveCheckPart<?> checkPart) {
    checkParts.add(checkPart);
  }

  public int trustFactorSetting(String key, Player player) {
    String checkKey = configurationName + "." + key;
    return plugin.trustFactorService().trustFactorSetting(checkKey, player);
  }

  public List<IntaveCheckPart<?>> checkParts() {
    return checkParts;
  }

  public boolean enabled() {
    return true;
  }

  public String name() {
    return checkName;
  }
}
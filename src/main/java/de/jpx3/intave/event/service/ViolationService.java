package de.jpx3.intave.event.service;

import de.jpx3.intave.IntavePlugin;
import de.jpx3.intave.access.TrustFactor;
import de.jpx3.intave.detect.IntaveCheck;
import de.jpx3.intave.tools.MathHelper;
import de.jpx3.intave.tools.sync.Synchronizer;
import de.jpx3.intave.user.User;
import de.jpx3.intave.user.UserRepository;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Locale;
import java.util.Map;

public final class ViolationService {
  private final IntavePlugin plugin;

  // 100 ~ kick

  public ViolationService(IntavePlugin plugin) {
    this.plugin = plugin;
  }

  public boolean processViolation(Player detectedPlayer, double vl, String checkName, String details) {
    return processViolation(detectedPlayer, vl, checkName, details, "thresholds");
  }

  public boolean processViolation(Player detectedPlayer, double vl, String checkName, String details, String thresholdsKey) {
    checkName = checkName.toLowerCase(Locale.ROOT);

    String thresholdsConfigKey = "checks." + checkName + "." + thresholdsKey;
    IntaveCheck check = plugin.checkService().searchCheck(checkName);

    double oldVl = violationMapOf(detectedPlayer).computeIfAbsent(checkName, s -> 0d);
    double newVl = MathHelper.minmax(0, oldVl + vl, 1000);

    double preventionActivation = resolvePreventionActivationThreshold(checkName, detectedPlayer);

    sendMessageToAdministrators(detectedPlayer, vl, newVl, checkName, details);

    violationMapOf(detectedPlayer).put(checkName, newVl);
    return preventionActivation > newVl;
  }

  private Map<String, Double> violationMapOf(Player player) {
    return UserRepository.userOf(player).meta().violationLevelData().violationLevel;
  }

  private double resolvePreventionActivationThreshold(String checkName, Player player) {
    return plugin.trustFactorService().trustFactorSetting(checkName + ".prevention-activation", player);
  }

  private void sendMessageToAdministrators(Player detectedPlayer, double vl, double newVL, String checkName, String details) {
    Synchronizer.synchronizeDelayed(() -> {
      for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
        if (onlinePlayer.isOp()) {
          String message = resolveFlagMessage(detectedPlayer, vl, newVL, checkName, details);
          onlinePlayer.sendMessage(message);
        }
      }
    }, 0);
  }

  private final static String PREFIX = "&8[&c&lIntave&8]&7 ";
  private final static String VERBOSE_FORMAT = "%s&c%s&7/%s&7 &7%s &7(%s / +%s -> %s)";

  private String resolveFlagMessage(Player player, double vl, double newVL, String checkName, String details) {
    User user = UserRepository.userOf(player);
    TrustFactor trustFactor = user.trustFactor();

    String trustfactorStringified = trustFactor.chatColor() + trustFactor.name().toLowerCase(Locale.ROOT).replace("_", "");

    String message = String.format(
      VERBOSE_FORMAT,
      PREFIX, player.getName(), trustfactorStringified, details, checkName.toLowerCase(Locale.ROOT), MathHelper.formatDouble(vl, 2), MathHelper.formatDouble(newVL, 2)
    );
    return ChatColor.translateAlternateColorCodes('&', message);
  }
}
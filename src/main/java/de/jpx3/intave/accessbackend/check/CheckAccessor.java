package de.jpx3.intave.accessbackend.check;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import de.jpx3.intave.IntavePlugin;
import de.jpx3.intave.access.CheckAccess;
import de.jpx3.intave.access.CheckStatisticsAccess;
import de.jpx3.intave.access.UnknownCheckException;
import de.jpx3.intave.access.UnknownPlayerException;
import de.jpx3.intave.detect.IntaveCheck;
import de.jpx3.intave.user.UserRepository;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class CheckAccessor {
  private final Map<String, CheckAccess> checkAccessCache = Maps.newConcurrentMap();
  private final IntavePlugin plugin;
  private final CheckStatisticsAccessor statisticsAccessor;

  public CheckAccessor(IntavePlugin plugin) {
    this.plugin = plugin;
    this.statisticsAccessor = new CheckStatisticsAccessor(plugin);
  }

  public synchronized CheckAccess checkMirrorOf(String name) {
    Preconditions.checkNotNull(name);
    return checkAccessCache.computeIfAbsent(name, x -> newCheckMirrorOf(tryGetCheck(name)));
  }

  private IntaveCheck tryGetCheck(String name) {
    try {
      return plugin.checkService().searchCheck(name);
    } catch (NullPointerException nullptr) {
      throw new UnknownCheckException("Could not find check " + name);
    }
  }

  private final static Map<String, Double> DEFAULT_RETURN = new HashMap<>();

  private CheckAccess newCheckMirrorOf(IntaveCheck check) {
    return new CheckAccess() {

      @Override
      public String name() {
        return check.name();
      }

      @Override
      public boolean enabled() {
        return check.enabled();
      }

      @Override
      public double violationLevelOf(Player player, String threshold) {
        if(!UserRepository.hasUser(player)) {
          throw new UnknownPlayerException("Player " + player.getName() + " couldn't be found");
        }

        Map<String, Map<String, Double>> violationLevel = UserRepository.userOf(player).meta().violationLevelData().violationLevel;
        return violationLevel.getOrDefault(check.name().toLowerCase(), DEFAULT_RETURN).getOrDefault(threshold, 0d);
      }

      @Override
      public void addViolationPoints(Player player, String threshold, double amount) {
        if(!UserRepository.hasUser(player)) {
          throw new UnknownPlayerException("Player " + player.getName() + " couldn't be found");
        }
        Map<String, Map<String, Double>> violationLevel = UserRepository.userOf(player).meta().violationLevelData().violationLevel;
        violationLevel.getOrDefault(check.name().toLowerCase(), DEFAULT_RETURN).put(threshold, violationLevelOf(player, threshold) + amount);
      }

      @Override
      public void resetViolationLevel(Player player, String threshold) {
        if(!UserRepository.hasUser(player)) {
          throw new UnknownPlayerException("Player " + player.getName() + " couldn't be found");
        }
        Map<String, Map<String, Double>> violationLevel = UserRepository.userOf(player).meta().violationLevelData().violationLevel;
        violationLevel.getOrDefault(check.name().toLowerCase(), DEFAULT_RETURN).remove(threshold);
      }

      @Override
      public Map<Integer, List<String>> commandsOf(String threshold) {
        return check.checkConfiguration.settings().thresholdsBy(threshold);
      }

      @Override
      public CheckStatisticsAccess statistics() {
        return null;
      }
    };
  }
}

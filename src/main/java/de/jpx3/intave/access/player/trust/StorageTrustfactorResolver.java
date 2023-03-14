package de.jpx3.intave.access.player.trust;

import de.jpx3.intave.IntavePlugin;
import de.jpx3.intave.annotate.Native;
import de.jpx3.intave.user.User;
import de.jpx3.intave.user.UserRepository;
import de.jpx3.intave.user.storage.*;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

public final class StorageTrustfactorResolver implements TrustFactorResolver {
  @Override
  @Native
  public void resolve(Player player, Consumer<TrustFactor> callback) {
    User user = UserRepository.userOf(player);
    user.onStorageReady(storage -> callback.accept(calculateTrustfactorFor(storage)));
  }

  @Native
  private TrustFactor calculateTrustfactorFor(Storage storage) {
    try {
      PlayerStorage playerStorage = (PlayerStorage) storage;
      PlaytimeStorage playtimeStorage = playerStorage.storageOf(PlaytimeStorage.class);
      long joins = playtimeStorage.totalJoins();
      long hoursPlayed = playtimeStorage.minutesPlayed() / 60;
      long hoursAfk = playtimeStorage.minutesAfk() / 60;
      TrustFactor factor;
      if (hoursPlayed > 500 && joins > 1000) {
        factor = TrustFactor.GREEN;
      } else if (hoursPlayed > 100 && joins > 100) {
        factor = TrustFactor.YELLOW;
      } else if (hoursPlayed > 5 && joins > 10) {
        factor = TrustFactor.ORANGE;
      } else if (hoursPlayed >= 1 && joins >= 1) {
        factor = TrustFactor.RED;
      } else {
        factor = TrustFactor.RED; // TrustFactor.DARK_RED;
      }
      ViolationStorage violationStorage = playerStorage.storageOf(ViolationStorage.class);

      StorageViolationEvents violations = violationStorage.violations();
      for (StorageViolationEvent violation : violations) {
        long timePassedSince = violation.timePassedSince();
        if ("heuristics".equals(violation.checkName())) {
          factor = factor.unsafer();
        } else if (timePassedSince < 1000 * 60 * 15) {
          factor = factor.unsafer();
        }
      }

      return factor;
    } catch (Exception exception) {
      return IntavePlugin.singletonInstance().trustFactorService().defaultTrustFactor();
    }
  }

  @Override
  public String toString() {
    return "AutoTrustfactor";
  }
}

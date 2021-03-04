package de.jpx3.intave.event.punishment;

import de.jpx3.intave.logging.IntaveLogger;
import de.jpx3.intave.reflect.ReflectiveHandleAccess;
import de.jpx3.intave.tools.sync.Synchronizer;
import de.jpx3.intave.user.User;
import de.jpx3.intave.user.UserMetaPunishmentData;
import de.jpx3.intave.user.UserRepository;
import io.netty.util.internal.ThreadLocalRandom;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;

public final class EntityNoDamageTickChanger {
  private static boolean hitDelayLinkageError = false;

  public static void applyHurtTimeChangeTo(Player player, int durationTicks) {
    if (hitDelayLinkageError) {
      return;
    }

    User user = UserRepository.userOf(player);
    UserMetaPunishmentData punishmentData = user.meta().punishmentData();

    // Already changed
    if (punishmentData.damageTicksBefore != -1) {
      return;
    }

    int noDamageTicksBefore = resolveNoDamageTicksOf(player);
    int newNoDamageTicks = calculateNewNoDamageTicks(noDamageTicksBefore);
    punishmentData.damageTicksBefore = noDamageTicksBefore;
    setNoDamageTicksOf(player, newNoDamageTicks);
    Synchronizer.synchronizeDelayed(new Runnable() {
      @Override
      public void run() {
        removeNoDamageTickChangeOf(user);
      }
    }, durationTicks);
  }

  private static int calculateNewNoDamageTicks(int noDamageTicks) {
    return Math.max(0, noDamageTicks - ThreadLocalRandom.current().nextInt(1, 2));
  }

  private static void removeNoDamageTickChangeOf(User user) {
    Player player = user.player();
    UserMetaPunishmentData punishmentData = user.meta().punishmentData();
    int noDamageTicksBefore = punishmentData.damageTicksBefore;
    int expectedPlayerNoDamageTicks = calculateNewNoDamageTicks(noDamageTicksBefore);
    if (expectedPlayerNoDamageTicks != resolveNoDamageTicksOf(player)) {
      // The server has changed the noDamageTicks field, do not override
      punishmentData.damageTicksBefore = -1;
      return;
    }
    setNoDamageTicksOf(player, noDamageTicksBefore);
    punishmentData.damageTicksBefore = -1;
  }

  private static int resolveNoDamageTicksOf(Player player) {
    try {
      Object handle = ReflectiveHandleAccess.handleOf(player);
      Field maxDamageTicks = handle.getClass().getField("maxNoDamageTicks");
      return (int) maxDamageTicks.get(handle);
    } catch (NoSuchFieldException | IllegalAccessException e) {
      IntaveLogger.logger().error("Intave has problems accessing an entity field");
      hitDelayLinkageError = true;
    }
    return -1;
  }

  private static void setNoDamageTicksOf(Player player, int noDamageTicks) {
    try {
      Object handle = ReflectiveHandleAccess.handleOf(player);
      Field maxDamageTicks = handle.getClass().getField("maxNoDamageTicks");
      maxDamageTicks.set(handle, noDamageTicks);
    } catch (NoSuchFieldException | IllegalAccessException e) {
      IntaveLogger.logger().error("Intave has problems accessing an entity field");
      hitDelayLinkageError = true;
    }
  }
}
package de.jpx3.intave.user;

import com.google.common.collect.Lists;
import de.jpx3.intave.event.punishment.AttackCancelType;
import de.jpx3.intave.event.punishment.EntityNoDamageTickChanger;
import de.jpx3.intave.tools.AccessHelper;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

public final class UserMetaPunishmentData {
  public final static long DAMAGE_CANCEL_LIGHT_DURATION = 20_000;
  private final static long DAMAGE_CANCEL_MEDIUM_DURATION = 20_000;
  private final static long DAMAGE_CANCEL_HEAVY_DURATION = 5_000;
  private final static long BLOCKING_DAMAGE_CANCEL_DURATION = 5_000;

  private final List<DamageCancel> damageCancels;

  public int damageTicksBefore = -1;
  public int attackCount;

  public UserMetaPunishmentData(Player player) {
    this.damageCancels = Lists.newArrayList(
      new DamageCancel(AttackCancelType.DCRH, DAMAGE_CANCEL_HEAVY_DURATION, (event) -> event.setCancelled(true)),
      new DamageCancel(AttackCancelType.DCRM, DAMAGE_CANCEL_MEDIUM_DURATION, (event) -> {
        attackCount++;
        if (attackCount % 10 == 0 || attackCount % ThreadLocalRandom.current().nextInt(1, 5) == 0) {
          event.setDamage(0);
        }
        // Perform hurt-time change
        EntityNoDamageTickChanger.applyHurtTimeChangeTo(player, (int) (DAMAGE_CANCEL_MEDIUM_DURATION / 50));
      }),
      new DamageCancel(AttackCancelType.DCRL, DAMAGE_CANCEL_LIGHT_DURATION, (event) -> {
        // Perform hurt-time change
        EntityNoDamageTickChanger.applyHurtTimeChangeTo(player, (int) (DAMAGE_CANCEL_LIGHT_DURATION / 50));
      }),
      new DamageCancel(AttackCancelType.DCRB, BLOCKING_DAMAGE_CANCEL_DURATION, (event) -> {
        double blockingDamageAbsorption = event.getDamage(EntityDamageEvent.DamageModifier.BLOCKING);
        if (blockingDamageAbsorption != 0) {
          event.setDamage(EntityDamageEvent.DamageModifier.BLOCKING, 0);
        }
      })
    );
  }

  public List<DamageCancel> damageCancels() {
    return damageCancels;
  }

  public DamageCancel damageCancelOfType(AttackCancelType type) {
    for (DamageCancel damageCancel : damageCancels) {
      if (damageCancel.type == type) {
        return damageCancel;
      }
    }
    throw new IllegalStateException();
  }

  public static final class DamageCancel {
    private final AttackCancelType type;
    private final Consumer<EntityDamageByEntityEvent> executor;
    private final long duration;
    private long activated;

    public DamageCancel(
      AttackCancelType type,
      long duration,
      Consumer<EntityDamageByEntityEvent> executor
    ) {
      this.type = type;
      this.duration = duration;
      this.executor = executor;
    }

    public void activate() {
      activated = AccessHelper.now();
    }

    public boolean active() {
      return AccessHelper.now() - activated < duration;
    }

    public Consumer<EntityDamageByEntityEvent> executor() {
      return executor;
    }

    public String name() {
      return type.typeName();
    }
  }
}
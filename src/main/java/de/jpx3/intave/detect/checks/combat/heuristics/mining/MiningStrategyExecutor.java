package de.jpx3.intave.detect.checks.combat.heuristics.mining;

import de.jpx3.intave.IntavePlugin;
import de.jpx3.intave.detect.checks.combat.Heuristics;
import de.jpx3.intave.detect.checks.combat.heuristics.Anomaly;
import de.jpx3.intave.detect.checks.combat.heuristics.Confidence;
import de.jpx3.intave.detect.checks.combat.heuristics.MiningStrategy;
import de.jpx3.intave.fakeplayer.FakePlayer;
import de.jpx3.intave.tools.AccessHelper;
import de.jpx3.intave.user.User;
import de.jpx3.intave.user.UserMetaAttackData;
import de.jpx3.intave.user.UserMetaMovementData;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public abstract class MiningStrategyExecutor {
  private final User user;
  private final Heuristics heuristicsCheck;
  private final long added;

  private long lastHitOnBot;
  public double threshold;

  public MiningStrategyExecutor(User user) {
    this.user = user;
    this.heuristicsCheck = IntavePlugin.singletonInstance().checkService().searchCheck(Heuristics.class);
    this.added = AccessHelper.now();
    this.registerExecutor();
    this.setup();
  }

  private void registerExecutor() {
    UserMetaAttackData attackData = user.meta().attackData();
    attackData.activeMiningStrategy = new MiningStrategyContainer(miningStrategy(), this);
  }

  protected void setup() {
  }

  protected void stopStrategy() {
  }

  public void receiveAttackOfPlayer(EntityDamageByEntityEvent event) {
  }

  public boolean expired() {
    int duration = miningStrategy().duration();
    return duration > 0 && AccessHelper.now() - added > duration;
  }

  public void saveAnomalyWithID(int id) {
    UserMetaAttackData attackData = user.meta().attackData();
    UserMetaMovementData movementData = user.meta().movementData();
    FakePlayer fakePlayer = attackData.fakePlayer();
    if (fakePlayer == null) {
      return;
    }
    if (AccessHelper.now() - lastHitOnBot > 40) {
      if (AccessHelper.now() - lastHitOnBot < 200) {
        ++threshold;
        if (threshold >= 2) {
          fakePlayer.moveOnTopOfPlayer();
        }
        boolean reportedBotOnTop = attackData.fakePlayerLastReportedY - movementData.positionY > 1.8;
        if (reportedBotOnTop && threshold >= 4) {
          Anomaly anomaly = createAnomaly(id);
          heuristicsCheck.saveAnomaly(user().player(), anomaly);
          this.unregisterStrategy();
        }
      }
      lastHitOnBot = AccessHelper.now();
    }
  }

  private Anomaly createAnomaly(int id) {
    String key = "31" + id;
    String description = "attacked a bot";
    return Anomaly.anomalyOf(key, Confidence.CERTAIN, Anomaly.Type.KILLAURA, description);
  }

  public void unregisterStrategy() {
    this.stopStrategy();
    UserMetaAttackData attackData = user.meta().attackData();
    attackData.lastMiningStrategy = miningStrategy();
    attackData.activeMiningStrategy = null;
  }

  public User user() {
    return user;
  }

  public abstract MiningStrategy miningStrategy();
}
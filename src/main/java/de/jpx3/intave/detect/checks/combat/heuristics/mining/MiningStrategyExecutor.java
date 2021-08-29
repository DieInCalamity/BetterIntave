package de.jpx3.intave.detect.checks.combat.heuristics.mining;

import de.jpx3.intave.IntavePlugin;
import de.jpx3.intave.detect.checks.combat.Heuristics;
import de.jpx3.intave.detect.checks.combat.heuristics.Anomaly;
import de.jpx3.intave.detect.checks.combat.heuristics.Confidence;
import de.jpx3.intave.detect.checks.combat.heuristics.MiningStrategy;
import de.jpx3.intave.event.AccessHelper;
import de.jpx3.intave.fakeplayer.FakePlayer;
import de.jpx3.intave.user.User;
import de.jpx3.intave.user.meta.AttackMetadata;
import de.jpx3.intave.user.meta.MovementMetadata;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public abstract class MiningStrategyExecutor {
  private final User user;
  private final Heuristics heuristicsCheck;
  private final long added;

  private long lastHitOnBot;
  public double threshold;

  public MiningStrategyExecutor(User user) {
    this.user = user;
    this.heuristicsCheck = IntavePlugin.singletonInstance().checks().searchCheck(Heuristics.class);
    this.added = AccessHelper.now();
    this.registerExecutor();
    this.setup();
  }

  private void registerExecutor() {
    AttackMetadata attackData = user.meta().attack();
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
    AttackMetadata attackData = user.meta().attack();
    MovementMetadata movementData = user.meta().movement();
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
    return Anomaly.anomalyOf(key, Confidence.CERTAIN, Anomaly.Type.KILLAURA, description, Anomaly.AnomalyOption.FORCE_APPLY);
  }

  public void unregisterStrategy() {
    this.stopStrategy();
    AttackMetadata attackData = user.meta().attack();
    attackData.lastMiningStrategy = miningStrategy();
    attackData.activeMiningStrategy = null;
  }

  public User user() {
    return user;
  }

  public abstract MiningStrategy miningStrategy();
}
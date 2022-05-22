package de.jpx3.intave.module.nayoro;

import de.jpx3.intave.check.combat.heuristics.Confidence;
import de.jpx3.intave.module.mitigate.AttackNerfStrategy;
import de.jpx3.intave.shade.Position;
import de.jpx3.intave.shade.Rotation;
import de.jpx3.intave.user.User;
import de.jpx3.intave.user.meta.CheckCustomMetadata;
import org.bukkit.GameMode;

import java.util.function.Consumer;

/**
 * Class generated using IntelliJ IDEA
 * Created by Richard Strunk 2022
 */

public interface PlayerContainer {
  Environment environment();
  void setEnvironment(Environment environment);
  int id();
  int version();
  boolean outdatedClient();
  <T extends CheckCustomMetadata> T meta(Class<T> metaClass);

  Rotation rotation();
  float yaw();
  float pitch();

  Rotation lastRotation();
  float lastYaw();
  float lastPitch();

  Position position();

  boolean cursorUponEntity(int id, float expansion);
  boolean notTeleportedIn(int ticks);
  boolean inGameMode(GameMode gameMode);
  boolean recentlyAttacked(long millis);
  boolean recentlySwitchedEntity(long millis);

  void debug(String message);
  void nerf(AttackNerfStrategy strategy, String originCode);
  void noteAnomaly(String key, Confidence confidence, String description);
  void applyIfUserPresent(Consumer<User> action);
}

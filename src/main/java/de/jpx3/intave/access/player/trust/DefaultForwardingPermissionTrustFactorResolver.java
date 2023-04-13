package de.jpx3.intave.access.player.trust;

import de.jpx3.intave.user.permission.BukkitPermissionCheck;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Consumer;

public final class DefaultForwardingPermissionTrustFactorResolver implements TrustFactorResolver {
  private final TrustFactorResolver forward;

  public DefaultForwardingPermissionTrustFactorResolver(TrustFactorResolver forward) {
    this.forward = forward;
  }

  @Override
  public void resolve(Player player, Consumer<TrustFactor> callback) {
    Optional<TrustFactor> resolvedTrustFactor =
      Arrays.stream(TrustFactor.values())
        .filter(trustFactor -> hasPermissionFor(player, trustFactor))
        .findFirst();

    if (resolvedTrustFactor.isPresent()) {
      callback.accept(resolvedTrustFactor.get());
    } else {
      forward.resolve(player, callback);
    }
  }

  private boolean hasPermissionFor(Player player, TrustFactor trustFactor) {
    return BukkitPermissionCheck.permissionCheck(player, trustFactor.permission());
  }

  @Override
  public String toString() {
    return "PermissionCheck, defaulting to " + forward.toString();
  }
}

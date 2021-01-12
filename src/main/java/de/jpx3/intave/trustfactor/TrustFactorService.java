package de.jpx3.intave.trustfactor;

import de.jpx3.intave.IntavePlugin;
import de.jpx3.intave.access.DefaultForwardingPermissionTrustFactorResolver;
import de.jpx3.intave.access.TrustFactor;
import de.jpx3.intave.access.TrustFactorResolver;
import de.jpx3.intave.event.bukkit.BukkitEventSubscriber;
import de.jpx3.intave.event.bukkit.BukkitEventSubscription;
import de.jpx3.intave.user.User;
import de.jpx3.intave.user.UserRepository;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;

public final class TrustFactorService implements BukkitEventSubscriber {
  private final static boolean USE_FILE_MAPPINGS = true;

  private final IntavePlugin plugin;
  private TrustFactorResolver trustFactorResolver;
  private TrustFactorConfiguration trustFactorConfiguration;
  private TrustFactor defaultTrustFactor = TrustFactor.YELLOW;

  public TrustFactorService(IntavePlugin plugin) {
    this.plugin = plugin;
  }

  public void setup() {
    TrustFactorLoader trustFactorLoader = USE_FILE_MAPPINGS ? new DefaultTrustFactorLoader() : new DownloadingTrustFactorLoader();
    trustFactorConfiguration = trustFactorLoader.fetch();
    trustFactorResolver = new DefaultForwardingPermissionTrustFactorResolver(new DefaultTrustFactorResolver());

    plugin.eventLinker().registerEventsIn(this);
    resolveTrustFactorForAll();
  }

  @BukkitEventSubscription(priority = EventPriority.MONITOR)
  public void on(PlayerJoinEvent join) {
    Player player = join.getPlayer();
    resolveTrustFactorFor(player);
  }

  private void resolveTrustFactorForAll() {
    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
      resolveTrustFactorFor(onlinePlayer);
    }
  }

  private void resolveTrustFactorFor(Player player) {
    User user = UserRepository.userOf(player);
    user.setTrustFactor(defaultTrustFactor);
    trustFactorResolver.resolveTrustFactor(player, user::setTrustFactor);
  }

  public int trustFactorSetting(String key, Player player) {
    return trustFactorConfiguration.resolveSetting(key, UserRepository.userOf(player).trustFactor());
  }

  public TrustFactor defaultTrustFactor() {
    return defaultTrustFactor;
  }

  public void setDefaultTrustFactor(TrustFactor defaultTrustFactor) {
    this.defaultTrustFactor = defaultTrustFactor;
  }

  public TrustFactorResolver trustFactorResolver() {
    return trustFactorResolver;
  }

  public void setTrustFactorResolver(TrustFactorResolver trustFactorResolver) {
    this.trustFactorResolver = trustFactorResolver;
  }

  public TrustFactorConfiguration trustFactorConfiguration() {
    return trustFactorConfiguration;
  }
}

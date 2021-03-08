package de.jpx3.intave.accessbackend;

import de.jpx3.intave.IntavePlugin;
import de.jpx3.intave.access.*;
import de.jpx3.intave.accessbackend.check.CheckAccessor;
import de.jpx3.intave.accessbackend.player.PlayerAccessor;
import de.jpx3.intave.accessbackend.server.ServerAccessor;
import de.jpx3.intave.logging.IntaveLogger;
import org.bukkit.entity.Player;

import java.io.PrintStream;

/**
 * Created by Jpx3 on 01.12.2017.
 */

public final class IntaveAccessService {
  private final IntavePlugin plugin;
  private final CheckAccessor checkAccessor;
  private final PlayerAccessor playerAccessor;
  private final ServerAccessor serverAccessor;

  public IntaveAccessService(IntavePlugin plugin) {
    this.plugin = plugin;
    this.checkAccessor = new CheckAccessor(plugin);
    this.playerAccessor = new PlayerAccessor(plugin);
    this.serverAccessor = new ServerAccessor(plugin);
  }

  public void setup() {
    plugin.setAccess(newIntaveAccess());
  }

  public void fireEvent(AbstractIntaveExternalEvent externalEvent) {
    plugin.eventLinker().fireEvent(externalEvent);
  }

  private IntaveAccess newIntaveAccess() {
    return new IntaveAccess() {
      @Override
      public void setTrustFactorResolver(TrustFactorResolver resolver) {
        plugin.trustFactorService().setTrustFactorResolver(resolver);
      }

      @Override
      public void setDefaultTrustFactor(TrustFactor defaultTrustFactor) {
        plugin.trustFactorService().setDefaultTrustFactor(defaultTrustFactor);
      }

      @Override
      public void subscribeOutputStream(PrintStream stream) {
        IntaveLogger.logger().addOutputStream(stream);
      }

      @Override
      public void unsubscribeOutputStream(PrintStream stream) {
        IntaveLogger.logger().removeOutputStream(stream);
      }

      @Override
      public PlayerAccess player(Player player) {
        return playerAccessor.playerAccessOf(player);
      }

      @Override
      public ServerAccess server() {
        return serverAccessor.serverAccess();
      }

      @Override
      public CheckAccess check(String checkName) throws UnknownCheckException {
        return checkAccessor.checkMirrorOf(checkName);
      }
    };
  }

  public PlayerAccessor playerAccessor() {
    return playerAccessor;
  }

  public ServerAccessor serverAccessor() {
    return serverAccessor;
  }
}

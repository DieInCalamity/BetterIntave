package de.jpx3.intave.accessbackend;

import de.jpx3.intave.IntavePlugin;
import de.jpx3.intave.access.*;
import de.jpx3.intave.accessbackend.internal.IntaveInternalAccessor;
import de.jpx3.intave.accessbackend.player.PlayerAccessor;
import de.jpx3.intave.accessbackend.server.ServerAccessor;
import org.bukkit.entity.Player;

/**
 * Created by Jpx3 on 01.12.2017.
 */

public final class IntaveAccessService {
  private final IntavePlugin plugin;
  private final IntaveInternalAccessor internalAccessor;
  private final PlayerAccessor playerAccessor;
  private final ServerAccessor serverAccessor;

  public IntaveAccessService(IntavePlugin plugin) {
    this.plugin = plugin;
    this.internalAccessor = new IntaveInternalAccessor(plugin);
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
      public PlayerAccess player(Player player) {
        return playerAccessor.playerAccessOf(player);
      }

      @Override
      public IntaveInternalAccess internal() {
        return internalAccessor.internalAccess();
      }

      @Override
      public ServerAccess server() {
        return serverAccessor.serverAccess();
      }
    };
  }

  public IntaveInternalAccessor internalAccessor() {
    return internalAccessor;
  }

  public PlayerAccessor playerAccessor() {
    return playerAccessor;
  }

  public ServerAccessor serverAccessor() {
    return serverAccessor;
  }
}

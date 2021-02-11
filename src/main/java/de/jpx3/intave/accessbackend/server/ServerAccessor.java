package de.jpx3.intave.accessbackend.server;

import de.jpx3.intave.IntavePlugin;
import de.jpx3.intave.access.ServerAccess;

public final class ServerAccessor {
  private final IntavePlugin plugin;
  private ServerAccess serverAccess;
  private final ServerStatisticAccessor serverStatisticAccessor;

  public ServerAccessor(IntavePlugin plugin) {
    this.plugin = plugin;
    this.serverStatisticAccessor = new ServerStatisticAccessor(plugin);
  }

  public synchronized ServerAccess serverAccess() {
    if(serverAccess == null) {
      serverAccess = newServerAccess();
    }
    return serverAccess;
  }

  private ServerAccess newServerAccess() {
    return serverStatisticAccessor::serverStatisticAccess;
  }

  public void pluginShutdown() {
    serverStatisticAccessor.shutdownScheduler();
  }
}

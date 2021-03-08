package de.jpx3.intave.access;

import org.bukkit.entity.Player;

import java.io.PrintStream;

public interface IntaveAccess {
  void setTrustFactorResolver(TrustFactorResolver resolver);
  void setDefaultTrustFactor(TrustFactor defaultTrustFactor);

  void subscribeOutputStream(PrintStream stream);
  void unsubscribeOutputStream(PrintStream stream);

  PlayerAccess player(Player player);
  ServerAccess server();
  CheckAccess check(String checkName) throws UnknownCheckException;
}

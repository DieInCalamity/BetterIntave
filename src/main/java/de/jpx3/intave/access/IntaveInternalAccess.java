package de.jpx3.intave.access;

import java.io.PrintStream;

public interface IntaveInternalAccess {
  void setTrustFactorResolver(TrustFactorResolver resolver);
  void setDefaultTrustFactor(TrustFactor defaultTrustFactor);

  void subscribeOutputStream(PrintStream stream);
  void unsubscribeOutputStream(PrintStream stream);

  CheckAccess accessCheck(String checkName) throws UnknownCheckException;
}
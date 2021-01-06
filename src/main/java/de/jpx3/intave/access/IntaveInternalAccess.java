package de.jpx3.intave.access;

import java.util.List;

public interface IntaveInternalAccess {
  void setPermissionProcessor(IntavePermissionCheck resolver);

  void setTrustFactorResolver(TrustFactorResolver resolver);
  void setDefaultTrustFactor(TrustFactor defaultTrustFactor);

  void overrideBreakPermissionCheck(BlockBreakPermissionCheck check);
  void overridePlacePermissionCheck(BlockPlacePermissionCheck check);

  IntaveCheckAccess accessCheck(String checkName) throws UnknownCheckException;
  List<String> loadedCheckNames();
  @Deprecated
  void restart();
  void reload();
}
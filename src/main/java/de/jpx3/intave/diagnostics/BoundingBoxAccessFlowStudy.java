package de.jpx3.intave.diagnostics;

public final class BoundingBoxAccessFlowStudy {
  public static int REQUEST;
  public static int LOOKUP;
  public static int DYNAMIC;

  public static String output() {
    return REQUEST + " requests required " + LOOKUP + " lookups, " + DYNAMIC + " dynamically";
  }
}

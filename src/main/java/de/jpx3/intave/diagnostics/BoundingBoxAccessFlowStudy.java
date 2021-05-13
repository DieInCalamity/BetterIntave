package de.jpx3.intave.diagnostics;

public final class BoundingBoxAccessFlowStudy {

  // total number of bb access
  public static int requests;

  // total number of required lookups
  public static int lookups;

  // total number of lookups, that didn't need to "ask the server"
  public static int dynamic;

  // NB gc = global cache
  //
  // lookup flow,
  //   green = lookup via gc
  //   yellow = created new gc entry, manual lookup
  //   red = cache denied, manual lookup
  public static int green, yellow, red;

  public static void incremRequests() {
    requests++;
  }

  public static void incremLookups() {
    lookups++;
  }

  public static void incremDynamic() {
    dynamic++;
  }

  public static void incremGreenLookups() {
    green++;
  }

  public static void incremYellowLookups() {
    yellow++;
  }

  public static void incremRedLookups() {
    red++;
  }

  public static String output() {
    return requests + " requests required " + lookups + " lookups, " + green + " green, " + yellow + " yellow, " + red + " red, " + dynamic + " dynamic";
  }
}

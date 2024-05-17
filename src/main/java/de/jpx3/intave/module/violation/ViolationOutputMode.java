package de.jpx3.intave.module.violation;

public enum ViolationOutputMode {
  SIMPLE("verbose"),
  FINE("verbose"),

  ;

  private final String layoutPath;

  ViolationOutputMode(String layoutPath) {
    this.layoutPath = layoutPath;
  }

  public String layoutPath() {
    return layoutPath;
  }
}

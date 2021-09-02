package de.jpx3.intave.clazz.locate;

abstract class Location {
  private final String key;
  private final IntegerMatcher versionMatcher;

  public Location(String key, IntegerMatcher versionMatcher) {
    this.key = key;
    this.versionMatcher = versionMatcher;
  }

  public String key() {
    return key;
  }

  public IntegerMatcher versionMatcher() {
    return versionMatcher;
  }
}

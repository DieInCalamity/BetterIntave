package de.jpx3.intave.event.punishment;

public enum AttackNerfStrategy {
  CANCEL("cancel"),
  CANCEL_FIRST_HIT("cancel/first"),
  DMG_MEDIUM("dmg/medium"),
  DMG_LIGHT("dmg/light"),
  HT_MEDIUM("ht/medium"),
  HT_LIGHT("ht/light"),
  BLOCKING("blocking");

  private final String typeName;

  AttackNerfStrategy(String name) {
    this.typeName = name;
  }

  public String typeName() {
    return typeName;
  }
}
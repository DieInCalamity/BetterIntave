package de.jpx3.intave.module.mitigate;

public enum AttackNerfStrategy {
  CANCEL("cancel"),
  CANCEL_FIRST_HIT("cancel/first"),
  DMG_HIGH("dmg/high"),
  DMG_MEDIUM("dmg/medium"),
  DMG_LIGHT("dmg/light"),
  DMG_ARMOR("dmg/armor"),
  @Deprecated
  HT_MEDIUM("ht/medium"),
  HT_LIGHT("ht/light"),
  GARBAGE_HITS("ht/jitter"),
  BLOCKING("dmg/blocking"),
  CRITICALS("dmg/criticals"),
  BURN_LONGER("burn-longer"),
  WALK_SLOW("walk-slower");

  private final String typeName;

  AttackNerfStrategy(String name) {
    this.typeName = name;
  }

  public String typeName() {
    return typeName;
  }
}
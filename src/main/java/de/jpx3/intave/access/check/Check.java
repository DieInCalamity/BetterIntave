package de.jpx3.intave.access.check;

/**
 * Class generated using IntelliJ IDEA
 * Created by Richard Strunk 2021
 */

public enum Check {
  ABILITY_SPEED_LIMITER("abilitySpeedLimiter"),
  ATTACK_RAYTRACE("attackRaytrace"),
  BREAK_SPEED_LIMITER("breakSpeedLimiter"),
  CLICK_SPEED_LIMITER("clickSpeedLimiter"),
  HEURISTICS("heuristics"),
  INTERACTION_RAYTRACE("interactionRaytrace"),
  INVENTORY_CLICK_ANALYSIS("inventoryClickAnalysis"),
  PHYSICS("physics"),
  PROTOCOL_SCANNER("protocolScanner"),
  PLACEMENT_ANALYSIS("placementAnalysis"),
  TIMER("timer"),

  ;

  private final String typeName;

  Check(String name) {
    this.typeName = name;
  }

  public static Check fromString(String name) {
    for (Check value : values()) {
      if (value.typeName.equalsIgnoreCase(name)) {
        return value;
      }
    }
    return null;
  }

  public String typeName() {
    return typeName;
  }
}

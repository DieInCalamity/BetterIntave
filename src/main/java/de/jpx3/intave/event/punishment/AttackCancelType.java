package de.jpx3.intave.event.punishment;

public enum AttackCancelType {
  DCRH("heavy"), // Damage Cancel Request Heavy
  DCRM("medium"), // Damage Cancel Request Medium
  DCRL("light"), // Damage Cancel Request Light
  DCRB("blocking"); // Damage Cancel Request Blocking

  private final String typeName;

  AttackCancelType(String name) {
    this.typeName = name;
  }

  public String typeName() {
    return typeName;
  }
}
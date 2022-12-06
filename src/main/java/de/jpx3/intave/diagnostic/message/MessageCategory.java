package de.jpx3.intave.diagnostic.message;

import de.jpx3.intave.annotate.KeepEnumInternalNames;

@KeepEnumInternalNames
public enum MessageCategory {
  HERAN("HEUristic ANomlies"),
  SIMFLT("SIMulation FauLTs"),
  SIMFUL("SIMulation REPORTs"),
  PKBF("PAcket BUFFers"),
  MKLG("MiKro LaG"),
  PKDL("PAcket DELay"),
  ATLALI("ATtack LAtency LImiting"),
  ATRAFLT("ATtack RAytrace FauLTs"),
  TRUSTSET("TRUST SETs"),

  ;

  private final String description;

  MessageCategory(String description) {
    this.description = description;
  }

  public String description() {
    return description;
  }
}

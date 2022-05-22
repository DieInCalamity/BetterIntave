package de.jpx3.intave.check.combat.heuristics;

import java.util.ArrayList;
import java.util.List;

public enum Confidence implements Comparable<Confidence> {
  CERTAIN("certain", "!!", 960),
  ALMOST_CERTAIN("almost certain", "!", 320),
  VERY_LIKELY("very likely", "?!", 160),
  LIKELY("likely", "?", 80),
  PROBABLE("probable", "??", 40),
  MAYBE("maybe", "-", 10),
  NONE("none", "-", 0),

  ;

  final String name;
  final String output;
  final int level;

  Confidence(String name, String output, int level) {
    this.name = name;
    this.output = output;
    this.level = level;
  }

  public int level() {
    return level;
  }

  public String confidenceName() {
    return name;
  }

  public String output() {
    return output;
  }

  public boolean atLeast(Confidence confidence) {
    return level() >= confidence.level();
  }

  public static int levelFrom(Confidence... confidences) {
    int sum = 0;
    for (Confidence confidence : confidences) {
      sum += confidence.level();
    }
    return sum;
  }

  public static Confidence confidenceFrom(int level) {
    Confidence highest = Confidence.NONE;
    for (Confidence value : Confidence.values()) {
      if (value.level > highest.level && value.level <= level) {
        highest = value;
      }
    }
    return highest;
  }

  public static List<Confidence> confidencesStackingTo(int level) {
    List<Confidence> result = new ArrayList<>();
    while (level > MAYBE.level()) {
      Confidence confidence = confidenceFrom(level);
      result.add(confidence);
      level -= confidence.level();
    }
    return result;
  }
}

package de.jpx3.intave.version;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class IntaveVersion {
  private final String version;
  private final int versionInteger;
  private final long release;
  private final Status typeClassifier;

  public IntaveVersion(String version, long release, Status typeClassifier) {
    this.version = version;
    this.release = release;
    this.typeClassifier = typeClassifier;
    int val = 0;
    try {
      val = parseVersion(version);
    } catch (Exception exception) {
      exception.printStackTrace();
    }
    this.versionInteger = val;
  }

  private int parseVersion(String version) {
    int build = 0;
    if (version.contains("-b")) {
      String[] split = version.split("-b");
      version = split[0];
      build = Integer.parseInt(split[1]);
    } else if (version.contains("-u")) {
      String[] split = version.split("-u");
      version = split[0];
      build = Integer.parseInt(split[1]);
    } else if (version.contains("-pre")) {
      String[] split = version.split("-pre");
      version = split[0];
      build = Integer.parseInt(split[1]);
    }
    String[] parts = version.split("\\.");
    int result = 0;
    for (String part : parts) {
      result = result * 10 + Integer.parseInt(part);
    }
    result = result * 10 + build;
    return result;
  }

  public String version() {
    return version;
  }

  public long release() {
    return release;
  }

  public Status typeClassifier() {
    return typeClassifier;
  }

  public boolean outdated() {
    return typeClassifier == Status.OUTDATED;
  }

  public enum Status {
    OUTDATED("OUTDATED"),
    LATEST("LATEST"),
    STABLE("STABLE"),
    DISABLED("DISABLED"),
    INVALID("");

    private final static Map<String, Status> map = new HashMap<>();
    private final String typeName;

    static {
      for (Status value : values()) {
        map.put(value.typeName(), value);
      }
    }

    public static Status fromName(String name) {
      Status statusLookup = map.get(name.toUpperCase(Locale.ROOT));
      return statusLookup == null ? Status.INVALID : statusLookup;
    }

    Status(String typeName) {
      this.typeName = typeName;
    }

    public String typeName() {
      return typeName;
    }
  }
}

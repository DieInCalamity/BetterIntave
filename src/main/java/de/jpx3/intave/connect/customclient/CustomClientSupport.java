package de.jpx3.intave.connect.customclient;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public final class CustomClientSupport {
  private final boolean legacySneakHeight;

  private CustomClientSupport(boolean legacySneakHeight) {
    this.legacySneakHeight = legacySneakHeight;
  }

  public boolean isLegacySneakHeight() {
    return legacySneakHeight;
  }

  public static CustomClientSupport createDefault() {
    return new CustomClientSupport(false);
  }

  public static CustomClientSupport createFrom(JsonElement jsonElement) {
    JsonObject object = jsonElement.getAsJsonObject();
    boolean read = readBoolean(object, "legacySneakHeight", false);
    return new CustomClientSupport(read);
  }

  private static boolean readBoolean(JsonObject object, String key, boolean def) {
    JsonElement element = object.get(key);
    return element == null ? def : element.getAsBoolean();
  }

  @Override
  public String toString() {
    return "CustomClientSupport{" +
      "legacySneakHeight=" + legacySneakHeight +
      '}';
  }
}

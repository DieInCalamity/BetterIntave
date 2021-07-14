package de.jpx3.intave.reflect.locate;

import de.jpx3.intave.access.IntaveInternalException;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class Locator {
  private final static ClassAndFieldLocationFileCompiler fileCompiler = new ClassAndFieldLocationFileCompiler();
  private final static ClassAndFieldLocations CLASS_AND_FIELD_LOCATIONS = fileCompiler.fromResource("/mappings/locate").reduced();
  private final static ClassLocations classLocation = CLASS_AND_FIELD_LOCATIONS.classLocations();
  private final static FieldLocations fieldLocations = CLASS_AND_FIELD_LOCATIONS.fieldLocations();
  private final static Map<String, ClassLocation> keyClassAccessCache = new ConcurrentHashMap<>();
  private final static Map<String, FieldLocation> fieldAccessCache = new ConcurrentHashMap<>();

  public static String patchyConvert(String input) {
    input = input.replace("/", ".");
    String output;
    if (input.startsWith("net.minecraft.server.v")) {
      output = classPathByKey(input.split("\\.")[4]);
    } else {
      output = input;
    }
    return output.replace(".", "/");
  }

  public static String patchyFieldCovert(String classInput, String fieldKey) {
    classInput = classInput.replace("/", ".");
    String output;
    if (classInput.startsWith("net.minecraft.server.v")) {
      output = fieldNameByKey(classInput.split("\\.")[4], fieldKey);
    } else {
      output = fieldKey;
    }
    return output;
  }

  public static Class<?> tryConvertByClassNameLookup(String name) {
    if (name.startsWith("net.minecraft.server.v")) {
      return classByKey(name.split("\\.")[4]);
    } else {
      try {
        return Class.forName(name);
      } catch (ClassNotFoundException e) {
        throw new IllegalArgumentException("Unsupported class " + name);
      }
    }
  }

  public static Field fieldByKey(String classKey, String fieldKey) {
    String key = classKey + "." + fieldKey;
    FieldLocation fieldLocation = fieldAccessCache.computeIfAbsent(key, s -> fieldLookupByKey(classKey, fieldKey));
    Class<?> aClass = classByKey(classKey);
    try {
      Field declaredField = aClass.getDeclaredField(fieldLocation.targetName());
      if (!declaredField.isAccessible()) {
        declaredField.setAccessible(true);
      }
      return declaredField;
    } catch (NoSuchFieldException exception) {
      throw new IntaveInternalException(exception);
    }
  }

  public static String fieldNameByKey(String classKey, String fieldKey) {
    String key = classKey + "." + fieldKey;
    FieldLocation fieldLocation = fieldAccessCache.computeIfAbsent(key, s -> fieldLookupByKey(classKey, fieldKey));
    return fieldLocation.targetName();
  }

  public static FieldLocation fieldLookupByKey(String classKey, String fieldKey) {
    return fieldLocations.filterByClassKey(classKey).filterByFieldKey(fieldKey)
      .stream().findAny().orElseGet(() -> FieldLocation.defaultFor(classKey, fieldKey));
  }

  public static String classPathByKey(String name) {
    return classLocationByKey(name).compiledLocation();
  }

  public static Class<?> classByKey(String name) {
    return classLocationByKey(name).access();
  }

  public static ClassLocation classLocationByKey(String key) {
    return keyClassAccessCache.computeIfAbsent(key, Locator::classLocationLookupByKey);
  }

  private static ClassLocation classLocationLookupByKey(String key) {
    return classLocation.filterByKey(key).stream().findAny()
      .orElseGet(() -> ClassLocation.nmsDefaultFor(key));
  }

  public static void setup() {
    // nothing!
  }

  public static void close() {
    keyClassAccessCache.clear();
  }
}

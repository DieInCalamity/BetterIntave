package de.jpx3.intave.reflect.locate;

import java.lang.reflect.Field;

public final class FieldLocation extends Location {
  private final String classKey;
  private final String target;

  public FieldLocation(String classKey, String key, IntegerMatcher versionMatcher, String target) {
    super(key, versionMatcher);
    this.classKey = classKey;
    this.target = target;
  }

  public Field access(Class<?> owner) {
    try {
      Field declaredField = owner.getDeclaredField(target);
      if (!declaredField.isAccessible()) {
        declaredField.setAccessible(true);
      }
      return declaredField;
    } catch (Exception exception) {
      throw new IllegalStateException(exception);
    }
  }

  public String targetName() {
    return target;
  }

  public String classKey() {
    return classKey;
  }

  @Override
  public String toString() {
    return "FieldLocation{"+classKey+"/"+ key()+" -> "+target+" @"+versionMatcher()+"}";
  }

  public static FieldLocation defaultFor(String classKey, String fieldKey) {
    return new FieldLocation(classKey, fieldKey, IntegerMatcher.anything(), fieldKey);
  }
}

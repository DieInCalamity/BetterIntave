package de.jpx3.intave.clazz.locate;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;

public final class FieldLocation extends Location {
  private final static Reference<Field> EMPTY_FIELD_REFERENCE = new WeakReference<>(null);

  private final String classKey;
  private final String target;
  private Reference<Field> fieldCache = EMPTY_FIELD_REFERENCE;

  public FieldLocation(String classKey, String key, IntegerMatcher versionMatcher, String target) {
    super(key, versionMatcher);
    this.classKey = classKey;
    this.target = target;
  }

  public Field access(Class<?> owner) {
    Field field = fieldCache.get();
    if (field == null) {
      field = compile(owner);
      fieldCache = new WeakReference<>(field);
    }
    return field;
  }

  private Field compile(Class<?> owner) {
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
    return new FieldLocation(classKey, fieldKey, IntegerMatcher.any(), fieldKey);
  }
}

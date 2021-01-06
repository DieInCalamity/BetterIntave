package de.jpx3.dynref.annotate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DynRefCustomFieldTranslation {
  DynRefVersionFieldReference[] value();
  DynRefUnknownVersionPolicy unknownVersionPolicy() default DynRefUnknownVersionPolicy.USE_NEXT_LOWER;
}

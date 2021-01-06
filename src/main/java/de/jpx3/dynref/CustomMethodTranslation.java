package de.jpx3.dynref;

import com.google.common.collect.ImmutableList;
import de.jpx3.dynref.annotate.DynRefUnknownVersionPolicy;
import de.jpx3.intave.lib.asm.tree.AnnotationNode;
import de.jpx3.intave.tools.annotate.Natify;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class CustomMethodTranslation {
  private DynRefUnknownVersionPolicy versionPolicy;
  private List<VersionMethodReference> versionMethodReferences = new ArrayList<>();
  private VersionMethodReference compiledTranslation;

  @Natify
  public VersionMethodReference selectedTranslationOf(VersionMethodReference original) {
    if (compiledTranslation == null) {
      compiledTranslation = compileTranslation(original);
    }
    return compiledTranslation;
  }

  @Natify
  private VersionMethodReference compileTranslation(VersionMethodReference original) {
    String version = DynRefTranslationConfiguration.selectSuitableVersion(this, original);
    for (VersionMethodReference versionMethodReference : versionMethodReferences) {
      if (versionMethodReference.version().equals(version)) {
        return versionMethodReference;
      }
    }
    throw new IllegalStateException("Something went wrong");
  }

  public DynRefUnknownVersionPolicy versionPolicy() {
    return versionPolicy;
  }

  public List<VersionMethodReference> versionMethodDescriptors() {
    return versionMethodReferences;
  }

  @Natify
  public static CustomMethodTranslation buildFrom(AnnotationNode annotationNode) {
    if (!DynRefTranslationConfiguration.className(annotationNode).equals(DynRefTranslationConfiguration.CUSTOM_METHOD_TRANSLATION_ANNOTATION_PATH)) {
      throw new IllegalArgumentException("Invalid annotation type");
    }
    CustomMethodTranslation customMethodTranslation = new CustomMethodTranslation();
    Map<String, Object> stringObjectMap = DynRefTranslationConfiguration.buildAnnotationMap(annotationNode.values);
    if (stringObjectMap.containsKey("unknownVersionPolicy")) {
      customMethodTranslation.versionPolicy = Enum.valueOf(DynRefUnknownVersionPolicy.class, ((String[]) stringObjectMap.get("unknownVersionPolicy"))[1]);
    } else {
      customMethodTranslation.versionPolicy = DynRefUnknownVersionPolicy.USE_NEXT_LOWER;
    }
    //noinspection unchecked
    for (AnnotationNode value : (List<AnnotationNode>) stringObjectMap.get("value")) {
      customMethodTranslation.versionMethodReferences.add(VersionMethodReference.buildFrom(value));
    }
    customMethodTranslation.versionMethodReferences = ImmutableList.copyOf(customMethodTranslation.versionMethodReferences);
    return customMethodTranslation;
  }
}

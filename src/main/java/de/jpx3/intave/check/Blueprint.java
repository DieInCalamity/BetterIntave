package de.jpx3.intave.check;

import de.jpx3.intave.user.meta.CheckCustomMetadata;

/**
 * A {@link Blueprint} is a template for check parts.
 * <br>
 * <br>
 * A quick example on how this would look:<br>
 * The defining blueprint:
 * <pre> {@code
 * abstract class ClickBlueprint<M extends ClickBlueprintMeta>
 *   extends Blueprint<ClickPatterns, ClickBlueprintMeta, M> {
 *   private final int sampleSize;
 *
 *   public ClickBlueprint(
 *     ClickPatterns parentCheck,
 *     Class<M> blueprintMetaClass,
 *     int sampleSize
 *   ) {
 *     super(parentCheck, blueprintMetaClass);
 *     this.sampleSize = sampleSize;
 *   }
 * }
 * }</pre>
 * The blueprint meta class:
 * <pre> {@code
 * abstract class ClickBlueprintMeta extends CheckCustomMetadata {
 *   public String specialString = "";
 * }
 * }</pre>
 * A blueprint-implementation:
 * <pre> {@code
 * final class Kurtosis extends ClickBlueprint<KurtosisMeta> {
 *   public Kurtosis(ClickPatterns parentCheck) {
 *     super(parentCheck, KurtosisMeta.class, 4);
 *   }
 *
 *   public void execute() {
 *     KurtosisMeta meta = metaOf(userOf(null));
 *     meta.specialString = "";
 *   }
 *
 *   public static class KurtosisMeta extends ClickBlueprintMeta {
 *     public String test;
 *   }
 * }
 * }</pre>
 *
 * @param <PARENT>         The parent check.
 * @param <BLUEPRINT_META> The blueprint meta class.
 * @param <CHECK_META>     The check meta class.
 * @author Jpx3
 * @see de.jpx3.intave.check.CheckPart
 * @see de.jpx3.intave.check.MetaCheckPart
 */
public abstract class Blueprint
  <PARENT extends Check,
    BLUEPRINT_META extends CheckCustomMetadata,
    CHECK_META extends BLUEPRINT_META>
  extends MetaCheckPart<PARENT, CHECK_META> {
  public Blueprint(PARENT parentCheck, Class<CHECK_META> metaClass) {
    super(parentCheck, metaClass);
  }
}

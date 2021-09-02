package de.jpx3.intave.block.fluid;

import de.jpx3.intave.access.IntaveInternalException;
import de.jpx3.intave.annotate.KeepEnumInternalNames;
import de.jpx3.intave.clazz.Lookup;

@KeepEnumInternalNames
public enum FluidTag {
  WATER(true),
  LAVA(true),
  EMPTY(false);

  private final boolean real;

  FluidTag(boolean real) {
    this.real = real;
  }

  private Object nativeTag;

  public Object nativeTag() {
    if (!this.real) {
      throw new IntaveInternalException("Cannot resolve actual fluid tag");
    }
    if (this.nativeTag == null) {
      this.nativeTag = resolveNativeTag();
    }
    return this.nativeTag;
  }

  private Object resolveNativeTag() {
    try {
      return Lookup.serverField("TagsFluid", name()).get(null);
    } catch (IllegalAccessException e) {
      throw new IntaveInternalException("Cannot access fluid tag", e);
    }
  }
}
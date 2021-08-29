package de.jpx3.intave.world.blockshape;

import de.jpx3.intave.IntaveControl;
import de.jpx3.intave.diagnostic.MemoryTraced;
import de.jpx3.intave.event.AccessHelper;
import de.jpx3.intave.world.blockaccess.BlockVariantRegister;
import de.jpx3.intave.world.wrapper.WrappedAxisAlignedBB;
import org.bukkit.Material;

import java.util.List;
import java.util.Objects;

/**
 * A {@link BlockShape} serves as a block-snapshot by capturing the bounding box,
 * the type and variant index of a block. It is primarily used for block-caching and
 * block-overrides.
 *
 * @see BlockShapeAccess
 * @see WrappedAxisAlignedBB
 * @see Material
 * @see BlockVariantRegister
 */
public final class BlockShape extends MemoryTraced {
  private final List<WrappedAxisAlignedBB> boxes;
  private final Material type;
  private final int variant;
  private final long creation = AccessHelper.now();

  public BlockShape(List<WrappedAxisAlignedBB> boxes, Material type, int variant) {
    this.boxes = boxes;
    this.type = type;
    this.variant = variant;
  }

  /**
   * Retrieve the blocks bounding boxes
   * @return the blocks bounding boxes
   */
  public List<WrappedAxisAlignedBB> boundingBoxes() {
    return boxes;
  }

  /**
   * Retrieve the blocks type
   * @return the blocks type
   */
  public Material type() {
    return type;
  }

  /**
   * Retrieve the blocks variant
   * @return the blocks variant
   */
  public int variant() {
    return variant;
  }

  /**
   * Check if the entry effectively expired.
   * Expires don't have to be acknowledged or followed - this only serves as a basic indicator
   * @return whether the shape is expired
   */
  public boolean expired() {
    return !IntaveControl.IGNORE_CACHE_REFRESH_ON_SIMULATION_FAULT && AccessHelper.now() - creation > 10000;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    BlockShape that = (BlockShape) o;
    if (variant != that.variant) return false;
    if (creation != that.creation) return false;
    if (!Objects.equals(boxes, that.boxes)) return false;
    return type == that.type;
  }

  @Override
  public int hashCode() {
    int result = boxes != null ? boxes.hashCode() : 0;
    result = 31 * result + (type != null ? type.hashCode() : 0);
    result = 31 * result + variant;
    result = 31 * result + (int) (creation ^ (creation >>> 32));
    return result;
  }
}

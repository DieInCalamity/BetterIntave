package de.jpx3.intave.block.shape;

import de.jpx3.intave.user.User;

/**
 * A override cached block shape access - or {@link OCBlockShapeAccess} - merges
 * {@link CachedBlockShapeAccess} featuring methods for type caching and
 * {@link OverrideBlockShapeAccess}, featuring methods for type override and
 * {@link BlockShapeAccess} for basic access.
 *
 * @see User
 * @see BlockShape
 * @see BlockShapeAccess
 * @see OverrideBlockShapeAccess
 * @see CachedBlockShapeAccess
 * @see MultiChunkKeyOCBlockShapeAccess
 */
public interface OCBlockShapeAccess extends OverrideBlockShapeAccess, CachedBlockShapeAccess, BlockShapeAccess {
}

package de.jpx3.intave.detect.checks.movement.physics;

import de.jpx3.intave.tools.wrapper.WrappedAxisAlignedBB;
import de.jpx3.intave.tools.wrapper.WrappedBlockPosition;
import de.jpx3.intave.user.User;
import de.jpx3.intave.user.UserMetaMovementData;
import de.jpx3.intave.world.BlockAccessor;
import de.jpx3.intave.world.collision.Collision;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.List;

public final class CollisionHelper {
  public static boolean nearBySolidBlock(Location location, double expansion) {
    for (double x = -expansion; x <= expansion; x += expansion) {
      for (double z = -expansion; z <= expansion; z += expansion) {
        Block block = BlockAccessor.blockAccess(location.clone().add(x, 0.0, z));
        if (block.getType().isSolid()) {
          return true;
        }
      }
    }
    return false;
  }

  public static WrappedAxisAlignedBB boundingBoxOf(
    User user,
    double positionX, double positionY, double positionZ
  ) {
    UserMetaMovementData movementData = user.meta().movementData();
    double width = movementData.widthRounded;
    float height = movementData.height;
    // 0.000000001 accuracy
    double newYMax = Math.round((positionY + height) * 1000000000d) / 1000000000d;
    return new WrappedAxisAlignedBB(
      positionX - width, positionY, positionZ - width,
      positionX + width, newYMax, positionZ + width
    );
  }

  public static WrappedAxisAlignedBB boundingBoxOf(
    User user, double width,
    double positionX, double positionY, double positionZ
  ) {
    UserMetaMovementData movementData = user.meta().movementData();
    double height = movementData.height;
    return new WrappedAxisAlignedBB(
      positionX - width, positionY, positionZ - width,
      positionX + width, positionY + height, positionZ + width
    );
  }

  public static WrappedAxisAlignedBB boundingBoxOf(User user, Location location) {
    return boundingBoxOf(user, location.getX(), location.getY(), location.getZ());
  }

  public static WrappedAxisAlignedBB boundingBoxOf(Location center) {
    return boundingBoxOf(center.getX(), center.getY(), center.getZ());
  }

  public static WrappedAxisAlignedBB boundingBoxOf(WrappedBlockPosition position) {
    return boundingBoxOf(position.xCoord, position.yCoord, position.zCoord);
  }

  private final static float PLAYER_HEIGHT = 1.8f;
  private final static double HALF_WIDTH = 0.3;

  @Deprecated
  public static WrappedAxisAlignedBB boundingBoxOf(
    double positionX, double positionY, double positionZ
  ) {
    return new WrappedAxisAlignedBB(
      positionX - HALF_WIDTH, positionY, positionZ - HALF_WIDTH,
      positionX + HALF_WIDTH, positionY + PLAYER_HEIGHT, positionZ + HALF_WIDTH
    );
  }

  public static CollisionResult resolveQuickCollisions(
    Player player,
    double positionX, double positionY, double positionZ,
    double motionX, double motionY, double motionZ
  ) {
    WrappedAxisAlignedBB boundingBox = CollisionHelper.boundingBoxOf(positionX, positionY, positionZ);
    List<WrappedAxisAlignedBB> collisionBoxes = Collision.resolve(
      player,
      boundingBox.addCoord(motionX, motionY, motionZ)
    );
    double startMotionY = motionY;
    for (WrappedAxisAlignedBB collisionBox : collisionBoxes) {
      motionY = collisionBox.calculateYOffset(boundingBox, motionY);
    }
    boundingBox = (boundingBox.offset(0.0D, motionY, 0.0D));
    boolean onGround = startMotionY != motionY && startMotionY < 0.0D;
    for (WrappedAxisAlignedBB collisionBox : collisionBoxes) {
      motionX = collisionBox.calculateXOffset(boundingBox, motionX);
    }
    boundingBox = boundingBox.offset(motionX, 0.0D, 0.0D);
    for (WrappedAxisAlignedBB collisionBox : collisionBoxes) {
      motionZ = collisionBox.calculateZOffset(boundingBox, motionZ);
    }
    return new CollisionResult(motionX, motionY, motionZ, onGround, startMotionY != motionY);
  }

  public static boolean checkBoundingBoxIntersection(User user, WrappedAxisAlignedBB boundingBox) {
    return !Collision.resolve(user.player(), boundingBox).isEmpty();
  }

  public static class CollisionResult {
    private final double motionX, motionY, motionZ;
    private final boolean onGround, collidedVertically;

    public CollisionResult(double motionX, double motionY, double motionZ, boolean onGround, boolean collidedVertically) {
      this.motionX = motionX;
      this.motionY = motionY;
      this.motionZ = motionZ;
      this.onGround = onGround;
      this.collidedVertically = collidedVertically;
    }

    public double motionX() {
      return motionX;
    }

    public double motionY() {
      return motionY;
    }

    public double motionZ() {
      return motionZ;
    }

    public boolean onGround() {
      return onGround;
    }

    public boolean collidedVertically() {
      return collidedVertically;
    }
  }
}
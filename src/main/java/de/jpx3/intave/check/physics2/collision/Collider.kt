package de.jpx3.intave.check.physics2.collision

import de.jpx3.intave.block.collision.Collision
import de.jpx3.intave.check.physics2.Vec
import de.jpx3.intave.share.Direction
import de.jpx3.intave.user.meta.PhysicsMetadata

object Collider {
    private val WEB_FRICTION = Vec(0.25, 0.05F.toDouble(), 0.25)

    fun move(env: PhysicsMetadata, preVel: Vec, postVel: Vec): ColliderResult {
        if (env.inWeb) {
            env.inWeb = false
            preVel * WEB_FRICTION
            postVel.nullify()
        }

        val startPreVel = preVel.copy()

        val clampOnEdge = env.onGround && env.sneaking
        if (clampOnEdge) {
            // TODO: Implement this
        }

        var box = env.box
        val collisionShape = Collision.collisionShape(
            env.player,
            box.expand(preVel)
        )

        collisionShape.allowedOffset(Direction.Axis.Y_AXIS, box, preVel.y)
        box = box.offset(0.0, preVel.y, 0.0)
        collisionShape.allowedOffset(Direction.Axis.X_AXIS, box, preVel.x)
        box = box.offset(preVel.x, 0.0, 0.0)
        collisionShape.allowedOffset(Direction.Axis.Z_AXIS, box, preVel.z)
        box = box.offset(0.0, 0.0, preVel.z)

        val step = env.onGround || startPreVel.y != preVel.y && startPreVel.y < 0.0
        if (step) {
            // TODO: Implement this
        }

        val collidedHorizontally = startPreVel.x != preVel.x || startPreVel.z != preVel.z
        val collidedVertically = startPreVel.y != preVel.y
        val onGround = collidedVertically && startPreVel.y < 0.0
        val isCollided = collidedVertically || collidedHorizontally

        if (startPreVel.x != preVel.x) {
            postVel.x = 0.0
        }

        if (startPreVel.z != preVel.z) {
            postVel.z = 0.0
        }

        val collidedPreVel = box.centerToVec() - env.pos

        // TODO: bad onBlockLanded implementation
        if (onGround) {
            postVel.y = 0.0
        }

        return ColliderResult(
            collidedPreVel,
            collidedHorizontally,
            collidedVertically,
            onGround
        )
    }
}

data class ColliderResult(
    val collidedPreVel: Vec,
    val collidedHorizontally: Boolean,
    val collidedVertically: Boolean,
    val onGround: Boolean
)

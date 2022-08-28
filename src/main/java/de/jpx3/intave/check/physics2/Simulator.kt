package de.jpx3.intave.check.physics2

import com.comphenix.protocol.events.PacketEvent
import de.jpx3.intave.check.Check
import de.jpx3.intave.check.physics2.collision.Collider
import de.jpx3.intave.module.linker.packet.ListenerPriority
import de.jpx3.intave.module.linker.packet.PacketId
import de.jpx3.intave.module.linker.packet.PacketSubscription
import de.jpx3.intave.share.BoundingBox
import de.jpx3.intave.share.ClientMathHelper
import de.jpx3.intave.user.UserRepository
import de.jpx3.intave.user.meta.PhysicsMetadata
import org.bukkit.Bukkit
import kotlin.math.max

private val GRAVITY = Vec(0.0, -0.08, 0.0)

class Simulator : Check("Physics2", "physics2") {
    @PacketSubscription(
        priority = ListenerPriority.NORMAL,
        packetsIn = [PacketId.Client.POSITION, PacketId.Client.POSITION_LOOK]
    )
    fun receiveMovement(event: PacketEvent) {
        val player = event.player
        val user = UserRepository.userOf(player)
        val physics = user.meta().physics()

        val movement = user.meta().movement()
        val pos = Vec(movement.positionX, movement.positionY, movement.positionZ)
        val lastPos = Vec(movement.lastPositionX, movement.lastPositionY, movement.lastPositionZ)

        physics.pos = pos
        physics.lastPos = lastPos

        val result = simulate(physics.postVelocity, physics)

        physics.box = BoundingBox.fromPosition(user, movement.position())
        physics.onGround = result.onGround
        physics.postVelocity = result.postVelocity
    }

    fun simulate(postVelocity: Vec, env: PhysicsMetadata): Result {
        // TODO: Threshold not associated with client version
        val threshold = 0.005
        postVelocity.clamp(threshold)

        val input = Input.inputs[0]

        val preVelocity = simulatePreVelocity(postVelocity, input, env)
        val (collidedPreVel, collidedHorizontally, collidedVertically, onGround) =
            Collider.move(env, preVelocity, postVelocity)

        // We check if c(preVelocity) matches 𝛥p
        val positionDelta = env.pos - env.lastPos
        val dist = collidedPreVel.distance(positionDelta)

        Bukkit.broadcastMessage(
            "dist=${dist.format(5)}, 𝛥p=${positionDelta.format(3)}, c(v)=${collidedPreVel.format(3)}, pv=${
            postVelocity.format(
                3
            )
            }"
        )

        // TODO postVelocity += simulatePostVelocity(preVelocity, env)
        postVelocity += simulatePostVelocity(positionDelta, env)

        // If c(preVelocity) is less than or equal to 0.03, clients
        // do not send movement packets
        val flyingPacket = collidedPreVel.length() <= 0.03

        return Result(postVelocity, flyingPacket, onGround)
    }

    private fun simulatePreVelocity(postVelocity: Vec, input: Vec, env: PhysicsMetadata): Vec {
        // TODO: InputFriction depends on use item and sneaking
        val inputFriction = 0.98
        input *= inputFriction

        val friction = if (env.onGround) {
            if (env.sprinting) 0.13 else 0.1
        } else {
            0.02 + (if (env.sprinting) 0.02 * 0.03 else 0.0)
        }

        // TODO: jumpVelocity is not added on preVelocity
        val strafeVelocity = simulateStrafing(input, env.yaw, friction)

        return postVelocity + strafeVelocity
    }

    private fun simulateStrafing(input: Vec, yaw: Float, preFriction: Double): Vec {
        return if (input.length() < 0.01) {
            Vec()
        } else {
            val coefficient = preFriction / max(1.0, input.length())
            val theta = yaw * DEGREE_TO_RAD

            val sin = coefficient * ClientMathHelper.sin(theta)
            val cos = coefficient * ClientMathHelper.cos(theta)

            val forward = input.x
            val strafe = input.z

            Vec(
                strafe * cos - forward * sin,
                0.0,
                forward * cos + strafe * sin
            )
        }
    }

    private fun simulatePostVelocity(preVelocity: Vec, env: PhysicsMetadata): Vec {
        val postFriction = if (env.onGround) {
            // TODO: blockFriction * 0.91
            0.6 * 0.91
        } else {
            0.91
        }

        val frictionVec = Vec(postFriction, 0.98, postFriction)
        return frictionVec * (preVelocity + GRAVITY)
    }

    override fun enabled(): Boolean {
        return true
    }

    override fun performLinkage(): Boolean {
        return true
    }
}

data class Result(val postVelocity: Vec, val flyingPacket: Boolean, val onGround: Boolean)

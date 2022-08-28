package de.jpx3.intave.user.meta

import de.jpx3.intave.check.physics2.Vec
import de.jpx3.intave.check.physics2.toVec
import de.jpx3.intave.share.BoundingBox
import de.jpx3.intave.user.UserRepository
import org.bukkit.entity.Player

class PhysicsMetadata(val player: Player?) {
    lateinit var box: BoundingBox
    var onGround = false
    var sneaking = false
    var sprinting = false
    var yaw = 0.0f
    var inWeb = false

    // TODO: This should be stored somewhere else
    var postVelocity = Vec()
    lateinit var pos: Vec
    lateinit var lastPos: Vec

    fun setup() {
        val player = this.player ?: return
        val user = UserRepository.userOf(player)
        box = BoundingBox.fromPosition(user, player.location)
        onGround = player.isOnGround
        sneaking = player.isSneaking
        sprinting = player.isSprinting
        yaw = player.location.yaw
        inWeb = false
        pos = player.location.toVec()
        lastPos = pos.copy()
    }
}

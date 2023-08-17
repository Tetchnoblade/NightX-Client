package net.aspw.client.features.module.impl.movement.speeds.ncp

import net.aspw.client.Client
import net.aspw.client.event.EventState
import net.aspw.client.event.MotionEvent
import net.aspw.client.event.MoveEvent
import net.aspw.client.features.module.impl.movement.Speed
import net.aspw.client.features.module.impl.movement.speeds.SpeedMode
import net.aspw.client.features.module.impl.player.Scaffold
import net.aspw.client.util.MovementUtils
import net.minecraft.potion.Potion

class NCPSemiStrafe : SpeedMode("NCPSemiStrafe") {
    override fun onDisable() {
        val scaffold = Client.moduleManager.getModule(Scaffold::class.java)

        if (!mc.thePlayer.isSneaking && !scaffold!!.state) {
            mc.thePlayer.motionX = 0.0
            mc.thePlayer.motionZ = 0.0
        }
    }

    override fun onUpdate() {}
    override fun onMotion() {}
    override fun onMove(event: MoveEvent) {}
    override fun onMotion(eventMotion: MotionEvent) {
        val speed = Client.moduleManager.getModule(
            Speed::class.java
        )
        if (speed == null || eventMotion.eventState !== EventState.PRE || mc.thePlayer.isInWater) return
        if (MovementUtils.isMoving()) {
            if (mc.thePlayer.onGround) {
                mc.thePlayer.motionY = 0.41999998688698
            } else {
                if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
                    MovementUtils.strafe(0.265f)
                } else {
                    MovementUtils.strafe(0.145f)
                }
                mc.thePlayer.jumpMovementFactor = 0.14f
            }
        }
    }
}
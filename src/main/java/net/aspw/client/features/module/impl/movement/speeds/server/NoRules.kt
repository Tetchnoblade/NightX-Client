package net.aspw.client.features.module.impl.movement.speeds.server

import net.aspw.client.Launch
import net.aspw.client.event.JumpEvent
import net.aspw.client.event.MotionEvent
import net.aspw.client.event.MoveEvent
import net.aspw.client.features.module.impl.movement.speeds.SpeedMode
import net.aspw.client.features.module.impl.player.Scaffold
import net.aspw.client.utils.MovementUtils.getBaseMoveSpeed
import net.aspw.client.utils.MovementUtils.isMoving
import net.aspw.client.utils.MovementUtils.strafe
import kotlin.math.max

class NoRules : SpeedMode("NoRules") {

    override fun onJump(event: JumpEvent) {
        if (mc.thePlayer != null && isMoving())
            event.cancelEvent()
    }

    override fun onUpdate() {
        if (mc.thePlayer.onGround) {
            mc.thePlayer.motionY = -0.04
            strafe()
        }
    }

    override fun onMotion() {}

    override fun onMotion(event: MotionEvent) {
        val thePlayer = mc.thePlayer ?: return

        if (isMoving() && thePlayer.onGround) {
            mc.thePlayer.motionY = -0.04

            strafe(
                (max(
                    0.55,
                    getBaseMoveSpeed(0.2873)
                )).toFloat()
            )

            mc.timer.timerSpeed = 1.5f
        } else {
            mc.timer.timerSpeed = 1.0f
        }
    }

    override fun onMove(event: MoveEvent) {}

    override fun onDisable() {
        val scaffoldModule = Launch.moduleManager.getModule(Scaffold::class.java)

        if (!mc.thePlayer.isSneaking && !scaffoldModule!!.state)
            strafe(0.2f)
    }
}
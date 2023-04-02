package net.aspw.client.features.module.impl.movement.speeds.verus

import net.aspw.client.event.JumpEvent
import net.aspw.client.event.MotionEvent
import net.aspw.client.event.MoveEvent
import net.aspw.client.features.module.impl.movement.speeds.SpeedMode
import net.aspw.client.utils.MovementUtils

class VerusFloat : SpeedMode("VerusFloat") {
    private var ticks = 0
    private var verusBypass = false
    private var isFloating = false
    override fun onJump(event: JumpEvent) {
        if (mc.thePlayer != null && MovementUtils.isMoving())
            event.cancelEvent()
    }

    override fun onDisable() {}
    override fun onTick() {}
    override fun onMotion(event: MotionEvent) {}
    override fun onMotion() {}
    override fun onUpdate() {
        ticks++
        if (mc.thePlayer.onGround) {
            ticks = 0
            MovementUtils.strafe(0.44f)
            mc.thePlayer.motionY = 0.42
            mc.timer.timerSpeed = 2.1f
            isFloating = true
        } else if (isFloating) {
            if (ticks >= 10) {
                verusBypass = true
                MovementUtils.strafe(0.2865f)
                isFloating = false
            }

            if (verusBypass) {
                if (ticks <= 1) {
                    MovementUtils.strafe(0.45f)
                }

                if (ticks >= 2) {
                    MovementUtils.strafe(0.69f - (ticks - 2) * 0.019f)
                }
            }

            mc.thePlayer.motionY = 0.0
            mc.timer.timerSpeed = 0.9f

            mc.thePlayer.onGround = true
        }
    }

    override fun onMove(event: MoveEvent) {}
    override fun onEnable() {
        verusBypass = false
    }
}
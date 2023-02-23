package net.aspw.client.features.module.modules.movement.speeds.matrix

import net.aspw.client.event.MoveEvent
import net.aspw.client.features.module.modules.movement.speeds.SpeedMode
import net.aspw.client.utils.MovementUtils.isMoving
import net.aspw.client.utils.timer.TickTimer

class ShotBow : SpeedMode("ShotBow") {
    private val tickTimer = TickTimer()
    override fun onDisable() {
        tickTimer.reset()
        mc.timer.timerSpeed = 1f
    }

    override fun onTick() {}
    override fun onMotion() {}
    override fun onUpdate() {
        if (mc.thePlayer!!.isInWater) return
        if (isMoving()) {
            tickTimer.update()
            if (mc.thePlayer!!.onGround) {
                mc.thePlayer!!.jump()
            }
            if (tickTimer.hasTimePassed(8)) {
                mc.timer.timerSpeed = 1.0f
            }
            if (tickTimer.hasTimePassed(17)) {
                mc.timer.timerSpeed = 1.12f
                tickTimer.reset()
            }
        } else {
            tickTimer.reset()
        }
    }

    override fun onMove(event: MoveEvent) {}
    override fun onEnable() {
    }
}
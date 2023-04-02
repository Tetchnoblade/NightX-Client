package net.aspw.client.features.module.impl.movement.speeds.matrix

import net.aspw.client.event.MoveEvent
import net.aspw.client.features.module.impl.movement.speeds.SpeedMode
import net.aspw.client.utils.MovementUtils.isMoving

class MatrixYPort : SpeedMode("MatrixYPort") {
    override fun onDisable() {
        mc.timer.timerSpeed = 1f
    }

    override fun onTick() {}
    override fun onMotion() {}
    override fun onUpdate() {
        if (mc.thePlayer!!.isInWater) return

        if (isMoving()) {
            if (mc.thePlayer!!.onGround) {
                mc.thePlayer!!.jump()
            }
            mc.timer.timerSpeed = 10f
        } else {
            mc.timer.timerSpeed = 1f
        }
    }

    override fun onMove(event: MoveEvent) {}
    override fun onEnable() {
        mc.timer.timerSpeed = 1f
    }
}
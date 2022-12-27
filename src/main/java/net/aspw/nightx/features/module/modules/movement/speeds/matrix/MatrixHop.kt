package net.aspw.nightx.features.module.modules.movement.speeds.matrix

import net.aspw.nightx.event.MoveEvent
import net.aspw.nightx.features.module.modules.movement.speeds.SpeedMode
import net.aspw.nightx.utils.MovementUtils.isMoving

class MatrixHop : SpeedMode("MatrixHop") {
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
            if (mc.thePlayer!!.fallDistance > 0.7 && mc.thePlayer!!.fallDistance < 1.3) {
                mc.timer.timerSpeed = 1.8f
            }
            if (mc.thePlayer!!.fallDistance >= 1.3) {
                mc.timer.timerSpeed = 1f
            }
        }
    }

    override fun onMove(event: MoveEvent) {}
    override fun onEnable() {}
}
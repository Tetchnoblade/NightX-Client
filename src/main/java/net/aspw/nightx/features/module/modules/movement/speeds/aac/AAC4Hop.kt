package net.aspw.nightx.features.module.modules.movement.speeds.aac

import net.aspw.nightx.event.MoveEvent
import net.aspw.nightx.features.module.modules.movement.speeds.SpeedMode
import net.aspw.nightx.utils.MovementUtils.isMoving

class AAC4Hop : SpeedMode("AAC4Hop") {
    override fun onDisable() {
        mc.timer.timerSpeed = 1f
        mc.thePlayer!!.speedInAir = 0.02f
    }

    override fun onTick() {}
    override fun onMotion() {}
    override fun onUpdate() {
        if (mc.thePlayer!!.isInWater) return

        if (isMoving()) {
            if (mc.thePlayer!!.onGround) {
                mc.thePlayer!!.jump()
                mc.thePlayer!!.speedInAir = 0.0201f
                mc.timer.timerSpeed = 0.94f
            }
            if (mc.thePlayer!!.fallDistance > 0.7 && mc.thePlayer!!.fallDistance < 1.3) {
                mc.thePlayer!!.speedInAir = 0.02f
                mc.timer.timerSpeed = 1.8f
            }
            if (mc.thePlayer!!.fallDistance >= 1.3) {
                mc.timer.timerSpeed = 1f
                mc.thePlayer!!.speedInAir = 0.02f
            }
        } else {
            mc.thePlayer!!.motionX = 0.0
            mc.thePlayer!!.motionZ = 0.0
        }
    }

    override fun onMove(event: MoveEvent) {}
    override fun onEnable() {}
}
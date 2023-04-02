package net.aspw.client.features.module.impl.movement.speeds.aac

import net.aspw.client.event.MoveEvent
import net.aspw.client.features.module.impl.movement.speeds.SpeedMode
import net.aspw.client.utils.MovementUtils.isMoving

class AAC4SlowHop : SpeedMode("AAC4SlowHop") {
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
                mc.gameSettings.keyBindJump.pressed = false
                mc.thePlayer!!.jump()
            }
            if (!mc.thePlayer!!.onGround && mc.thePlayer!!.fallDistance <= 0.1) {
                mc.thePlayer!!.speedInAir = 0.02f
                mc.timer.timerSpeed = 1.4f
            }
            if (mc.thePlayer!!.fallDistance > 0.1 && mc.thePlayer!!.fallDistance < 1.3) {
                mc.thePlayer!!.speedInAir = 0.0205f
                mc.timer.timerSpeed = 0.65f
            }
            if (mc.thePlayer!!.fallDistance >= 1.3) {
                mc.timer.timerSpeed = 1f
                mc.thePlayer!!.speedInAir = 0.02f
            }
        }
    }

    override fun onMove(event: MoveEvent) {}
    override fun onEnable() {}
}
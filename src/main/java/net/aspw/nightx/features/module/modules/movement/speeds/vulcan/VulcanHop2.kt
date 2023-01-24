package net.aspw.nightx.features.module.modules.movement.speeds.vulcan

import net.aspw.nightx.NightX
import net.aspw.nightx.event.MotionEvent
import net.aspw.nightx.event.MoveEvent
import net.aspw.nightx.features.module.modules.movement.speeds.SpeedMode
import net.aspw.nightx.features.module.modules.world.Scaffold
import net.aspw.nightx.utils.MovementUtils.*

class VulcanHop2 : SpeedMode("VulcanHop2") {

    private var jumpTicks = 0

    override fun onUpdate() {

        jumpTicks += 1

        if (isMoving()) {
            if (mc.thePlayer.onGround) {
                mc.thePlayer.jump()

                jumpTicks = 0
            } else {
                if (jumpTicks > 3)
                    mc.thePlayer.motionY = (mc.thePlayer.motionY - 0.08) * 0.98

                strafe(getSpeed() * (1.01 - (Math.random() / 500)).toFloat())
            }
        } else {
            mc.timer.timerSpeed = 1.00f
            mc.thePlayer.motionX = 0.0
            mc.thePlayer.motionZ = 0.0
        }
    }

    override fun onMotion() {}

    override fun onMotion(event: MotionEvent) {}

    override fun onDisable() {
        val scaffoldModule = NightX.moduleManager.getModule(Scaffold::class.java)

        if (!mc.thePlayer.isSneaking && !scaffoldModule!!.state)
            strafe(0.3f)
    }

    override fun onMove(event: MoveEvent) {
    }
}
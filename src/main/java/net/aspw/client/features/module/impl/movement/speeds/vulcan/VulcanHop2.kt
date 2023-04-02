package net.aspw.client.features.module.impl.movement.speeds.vulcan

import net.aspw.client.Client
import net.aspw.client.event.MotionEvent
import net.aspw.client.event.MoveEvent
import net.aspw.client.features.module.impl.movement.speeds.SpeedMode
import net.aspw.client.features.module.impl.player.Scaffold
import net.aspw.client.utils.MovementUtils.*

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
        }
    }

    override fun onMotion() {}

    override fun onMotion(event: MotionEvent) {}

    override fun onDisable() {
        val scaffoldModule = Client.moduleManager.getModule(Scaffold::class.java)

        if (!mc.thePlayer.isSneaking && !scaffoldModule!!.state)
            strafe(0.2f)
    }

    override fun onMove(event: MoveEvent) {
    }
}
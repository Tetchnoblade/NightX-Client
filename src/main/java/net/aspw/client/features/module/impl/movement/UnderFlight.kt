package net.aspw.client.features.module.impl.movement

import net.aspw.client.event.EventState
import net.aspw.client.event.EventTarget
import net.aspw.client.event.MotionEvent
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.util.MovementUtils

@ModuleInfo(name = "UnderFlight", spacedName = "Under Flight", description = "", category = ModuleCategory.MOVEMENT)
class UnderFlight : Module() {
    private var bmcSpeed = 0.0
    private var started = false

    override fun onEnable() {
        bmcSpeed = 0.0
        started = false
    }

    override fun onDisable() {
        mc.thePlayer.motionX = 0.0
        mc.thePlayer.motionZ = 0.0
        bmcSpeed = 0.0
        started = false
    }

    @EventTarget
    fun onMotion(event: MotionEvent) {
        if (event.eventState === EventState.PRE) {
            val bb = mc.thePlayer.entityBoundingBox.offset(0.0, 1.0, 0.0)

            if (started) {
                chat("Applied funny motion (?)")
                mc.thePlayer.motionY += 0.025
                MovementUtils.strafe(0.935f.let { bmcSpeed *= it; bmcSpeed }.toFloat())
                if (mc.thePlayer.motionY < -0.5 && !MovementUtils.isBlockUnder()) {
                    toggle()
                }
            }

            if (mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, bb).isEmpty() && !started) {
                started = true
                mc.thePlayer.jump()
                MovementUtils.strafe(9.also { bmcSpeed = it.toDouble() }.toFloat())
            }
        }
    }
}
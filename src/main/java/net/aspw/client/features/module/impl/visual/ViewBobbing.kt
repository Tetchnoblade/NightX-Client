package net.aspw.client.features.module.impl.visual

import net.aspw.client.event.EventTarget
import net.aspw.client.event.MotionEvent
import net.aspw.client.event.UpdateEvent
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.util.MovementUtils
import net.aspw.client.value.BoolValue
import net.aspw.client.value.FloatValue

@ModuleInfo(name = "ViewBobbing", spacedName = "View Bobbing", description = "", category = ModuleCategory.VISUAL)
class ViewBobbing : Module() {
    private val noBob = BoolValue("NoBob", false)
    private val customBobbing = BoolValue("CustomBobbing", true)
    private val bobbingAmount = FloatValue("BobbingAmount", 0.03f, -0.5f, 0.5f) { customBobbing.get() }

    @EventTarget
    fun onMotion(event: MotionEvent?) {
        if (customBobbing.get() && mc.thePlayer.onGround && MovementUtils.isMoving()) {
            mc.thePlayer.cameraYaw = bobbingAmount.value
        }
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (noBob.get()) {
            mc.thePlayer.distanceWalkedModified = 0f
        }
    }
}
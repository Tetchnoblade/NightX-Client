package net.aspw.client.features.module.impl.visual

import net.aspw.client.event.EventTarget
import net.aspw.client.event.MotionEvent
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo

@ModuleInfo(name = "RealBobbing", spacedName = "Real Bobbing", category = ModuleCategory.VISUAL)
class RealBobbing : Module() {
    @EventTarget
    fun onMotion(event: MotionEvent?) {
        if (mc.thePlayer.onGround) {
            mc.thePlayer.cameraYaw = 0.03f
        }
    }
}
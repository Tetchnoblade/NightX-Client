package net.aspw.nightx.features.module.modules.movement

import net.aspw.nightx.event.EventTarget
import net.aspw.nightx.event.UpdateEvent
import net.aspw.nightx.features.module.Module
import net.aspw.nightx.features.module.ModuleCategory
import net.aspw.nightx.features.module.ModuleInfo

@ModuleInfo(name = "Bhop", category = ModuleCategory.MOVEMENT)
class Bhop : Module() {
    @EventTarget
    fun onUpdate(event: UpdateEvent?) {
        mc.thePlayer.jumpMovementFactor = 0.16f
    }
}
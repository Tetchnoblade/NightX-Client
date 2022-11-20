package net.aspw.nightx.features.module.modules.movement

import net.aspw.nightx.event.EventTarget
import net.aspw.nightx.event.UpdateEvent
import net.aspw.nightx.features.module.Module
import net.aspw.nightx.features.module.ModuleCategory
import net.aspw.nightx.features.module.ModuleInfo

@ModuleInfo(name = "HorseJump", "Horse Jump", category = ModuleCategory.MOVEMENT)
class HorseJump : Module() {

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        mc.thePlayer.horseJumpPowerCounter = 9
        mc.thePlayer.horseJumpPower = 1f
    }
}

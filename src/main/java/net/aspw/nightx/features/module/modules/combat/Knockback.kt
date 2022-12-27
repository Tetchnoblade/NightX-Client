package net.aspw.nightx.features.module.modules.combat

import net.aspw.nightx.event.EventTarget
import net.aspw.nightx.event.MotionEvent
import net.aspw.nightx.features.module.Module
import net.aspw.nightx.features.module.ModuleCategory
import net.aspw.nightx.features.module.ModuleInfo

@ModuleInfo(
    name = "Knockback", spacedName = "Knock back",
    category = ModuleCategory.COMBAT
)
class Knockback : Module() {
    private var ticks = 0

    @EventTarget
    fun onMotion(event: MotionEvent) {
        if (ticks <= 6) {
            mc.gameSettings.keyBindForward.pressed = false
        } else if (ticks == 7) {
            mc.gameSettings.keyBindForward.pressed = true
        }
    }
}
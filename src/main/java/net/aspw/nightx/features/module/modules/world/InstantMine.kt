package net.aspw.nightx.features.module.modules.world

import net.aspw.nightx.event.EventTarget
import net.aspw.nightx.event.MotionEvent
import net.aspw.nightx.features.module.Module
import net.aspw.nightx.features.module.ModuleCategory
import net.aspw.nightx.features.module.ModuleInfo

@ModuleInfo(name = "InstantMine", spacedName = "Instant Mine", category = ModuleCategory.WORLD)
class InstantMine : Module() {
    @EventTarget
    fun onUpdate(event: MotionEvent) {
        mc.playerController.blockHitDelay = 0
        if (mc.playerController.curBlockDamageMP > 0) {
            mc.playerController.curBlockDamageMP = 1F
        }
    }
}
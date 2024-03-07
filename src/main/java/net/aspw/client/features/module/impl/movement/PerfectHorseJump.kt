package net.aspw.client.features.module.impl.movement

import net.aspw.client.event.EventTarget
import net.aspw.client.event.UpdateEvent
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo

@ModuleInfo(name = "PerfectHorseJump", "Perfect Horse Jump", category = ModuleCategory.MOVEMENT)
class PerfectHorseJump : Module() {

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        mc.thePlayer.horseJumpPowerCounter = 9
        mc.thePlayer.horseJumpPower = 1f
    }
}
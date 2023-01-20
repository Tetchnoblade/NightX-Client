package net.aspw.nightx.features.module.modules.misc

import net.aspw.nightx.event.EventTarget
import net.aspw.nightx.event.UpdateEvent
import net.aspw.nightx.features.module.Module
import net.aspw.nightx.features.module.ModuleCategory
import net.aspw.nightx.features.module.ModuleInfo
import net.aspw.nightx.utils.timer.MSTimer
import net.minecraft.item.ItemFishingRod

@ModuleInfo(name = "AutoFish", spacedName = "Auto Fish", category = ModuleCategory.MISC)
class AutoFish : Module() {

    private val rodOutTimer = MSTimer()

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (mc.thePlayer.heldItem == null || mc.thePlayer.heldItem.item !is ItemFishingRod)
            return

        if (rodOutTimer.hasTimePassed(500L) && mc.thePlayer.fishEntity == null || (mc.thePlayer.fishEntity != null && mc.thePlayer.fishEntity.motionX == 0.0 && mc.thePlayer.fishEntity.motionZ == 0.0 && mc.thePlayer.fishEntity.motionY != 0.0)) {
            mc.rightClickMouse()
            rodOutTimer.reset()
        }
    }
}

package net.aspw.nightx.features.module.modules.movement

import net.aspw.nightx.event.EventTarget
import net.aspw.nightx.event.UpdateEvent
import net.aspw.nightx.features.module.Module
import net.aspw.nightx.features.module.ModuleCategory
import net.aspw.nightx.features.module.ModuleInfo
import net.minecraft.client.settings.GameSettings

@ModuleInfo(name = "AutoJump", spacedName = "Auto Jump", category = ModuleCategory.MOVEMENT)
class AutoJump : Module() {

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        mc.gameSettings.keyBindJump.pressed = true
    }

    override fun onDisable() {
        if (!GameSettings.isKeyDown(mc.gameSettings.keyBindForward))
            mc.gameSettings.keyBindJump.pressed = false
    }
}

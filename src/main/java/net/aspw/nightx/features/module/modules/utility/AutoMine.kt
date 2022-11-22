package net.aspw.nightx.features.module.modules.utility

import net.aspw.nightx.event.EventTarget
import net.aspw.nightx.event.UpdateEvent
import net.aspw.nightx.features.module.Module
import net.aspw.nightx.features.module.ModuleCategory
import net.aspw.nightx.features.module.ModuleInfo
import net.minecraft.client.settings.GameSettings
import net.minecraft.init.Blocks

@ModuleInfo(name = "AutoMine", spacedName = "Auto Mine", category = ModuleCategory.UTILITY)
class AutoMine : Module() {

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        mc.gameSettings.keyBindAttack.pressed =
            mc.theWorld.getBlockState(mc.objectMouseOver.blockPos).block != Blocks.air
    }

    override fun onDisable() {
            mc.gameSettings.keyBindAttack.pressed = false
    }
}
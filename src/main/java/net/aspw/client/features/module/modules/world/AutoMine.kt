package net.aspw.client.features.module.modules.world

import net.aspw.client.event.EventTarget
import net.aspw.client.event.UpdateEvent
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.minecraft.init.Blocks

@ModuleInfo(name = "AutoMine", spacedName = "Auto Mine", category = ModuleCategory.WORLD)
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
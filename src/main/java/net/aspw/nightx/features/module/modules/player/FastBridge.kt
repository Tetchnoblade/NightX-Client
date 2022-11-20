package net.aspw.nightx.features.module.modules.player

import net.aspw.nightx.event.EventTarget
import net.aspw.nightx.event.UpdateEvent
import net.aspw.nightx.features.module.Module
import net.aspw.nightx.features.module.ModuleCategory
import net.aspw.nightx.features.module.ModuleInfo
import net.minecraft.client.settings.GameSettings
import net.minecraft.init.Blocks
import net.minecraft.util.BlockPos

@ModuleInfo(name = "FastBridge", spacedName = "Fast Bridge", category = ModuleCategory.PLAYER)
class FastBridge : Module() {

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        val shouldEagle = mc.theWorld.getBlockState(
            BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.0, mc.thePlayer.posZ)
        ).block === Blocks.air
        mc.gameSettings.keyBindSneak.pressed = shouldEagle
    }

    override fun onDisable() {
        if (mc.thePlayer == null)
            return

        if (!GameSettings.isKeyDown(mc.gameSettings.keyBindSneak))
            mc.gameSettings.keyBindSneak.pressed = false
    }
}

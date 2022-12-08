package net.aspw.nightx.features.module.modules.ghost

import net.aspw.nightx.event.EventTarget
import net.aspw.nightx.event.UpdateEvent
import net.aspw.nightx.features.module.Module
import net.aspw.nightx.features.module.ModuleCategory
import net.aspw.nightx.features.module.ModuleInfo
import net.minecraft.client.settings.GameSettings
import net.minecraft.client.settings.KeyBinding
import net.minecraft.init.Blocks
import net.minecraft.util.BlockPos

@ModuleInfo(name = "FastBridge", spacedName = "Fast Bridge", category = ModuleCategory.GHOST)
class FastBridge : Module() {

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        val shouldEagle = mc.theWorld.getBlockState(
            BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.0, mc.thePlayer.posZ)
        ).block === Blocks.air
        mc.gameSettings.keyBindSneak.pressed = shouldEagle

        if (mc.gameSettings.keyBindUseItem.isKeyDown && shouldEagle || mc.gameSettings.keyBindUseItem.isKeyDown && !mc.thePlayer.onGround) {
            KeyBinding.onTick(mc.gameSettings.keyBindUseItem.keyCode)
        }
    }

    override fun onDisable() {
        if (mc.thePlayer == null)
            return

        if (!GameSettings.isKeyDown(mc.gameSettings.keyBindSneak))
            mc.gameSettings.keyBindSneak.pressed = false
    }
}

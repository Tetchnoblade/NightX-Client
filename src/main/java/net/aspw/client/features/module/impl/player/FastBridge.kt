package net.aspw.client.features.module.impl.player

import net.aspw.client.event.EventTarget
import net.aspw.client.event.UpdateEvent
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.utils.timer.TickTimer
import net.aspw.client.value.IntegerValue
import net.minecraft.client.settings.GameSettings
import net.minecraft.client.settings.KeyBinding
import net.minecraft.init.Blocks
import net.minecraft.util.BlockPos

@ModuleInfo(name = "FastBridge", spacedName = "Fast Bridge", category = ModuleCategory.PLAYER)
class FastBridge : Module() {
    private val speedValue = IntegerValue("Place-Speed", 0, 0, 20)
    private val tickTimer = TickTimer()

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        tickTimer.update()

        val shouldEagle = mc.theWorld.getBlockState(
            BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.0, mc.thePlayer.posZ)
        ).block === Blocks.air
        mc.gameSettings.keyBindSneak.pressed = shouldEagle

        if (tickTimer.hasTimePassed(0 + speedValue.get()) && mc.gameSettings.keyBindUseItem.isKeyDown && shouldEagle || mc.gameSettings.keyBindUseItem.isKeyDown && !mc.thePlayer.onGround) {
            KeyBinding.onTick(mc.gameSettings.keyBindUseItem.keyCode)
            tickTimer.reset()
        }
    }

    override fun onDisable() {
        if (mc.thePlayer == null)
            return

        if (!GameSettings.isKeyDown(mc.gameSettings.keyBindSneak))
            mc.gameSettings.keyBindSneak.pressed = false
    }
}

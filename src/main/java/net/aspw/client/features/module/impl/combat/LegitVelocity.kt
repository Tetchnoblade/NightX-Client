package net.aspw.client.features.module.impl.combat

import net.aspw.client.event.EventTarget
import net.aspw.client.event.UpdateEvent
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.minecraft.client.settings.GameSettings

@ModuleInfo(name = "LegitVelocity", spacedName = "Legit Velocity", category = ModuleCategory.COMBAT)
class LegitVelocity : Module() {

    override fun onDisable() {
        if (GameSettings.isKeyDown(mc.gameSettings.keyBindForward))
            mc.gameSettings.keyBindForward.pressed = true
        if (GameSettings.isKeyDown(mc.gameSettings.keyBindJump))
            mc.gameSettings.keyBindJump.pressed = true
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (mc.thePlayer.hurtTime >= 8) {
            mc.gameSettings.keyBindJump.pressed = true
        }
        if (mc.thePlayer.hurtTime >= 7) {
            mc.gameSettings.keyBindForward.pressed = true
        } else if (mc.thePlayer.hurtTime >= 4) {
            mc.gameSettings.keyBindJump.pressed = false
            mc.gameSettings.keyBindForward.pressed = false
        } else if (mc.thePlayer.hurtTime > 1) {
            mc.gameSettings.keyBindForward.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindForward)
            mc.gameSettings.keyBindJump.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindJump)
        }
    }
}
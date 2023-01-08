package net.aspw.nightx.features.module.modules.player

import net.aspw.nightx.NightX
import net.aspw.nightx.event.EventTarget
import net.aspw.nightx.event.UpdateEvent
import net.aspw.nightx.features.module.Module
import net.aspw.nightx.features.module.ModuleCategory
import net.aspw.nightx.features.module.ModuleInfo
import net.aspw.nightx.features.module.modules.movement.Speed
import net.aspw.nightx.utils.MovementUtils
import org.lwjgl.input.Keyboard

@ModuleInfo(name = "NoClip", spacedName = "No Clip", category = ModuleCategory.PLAYER)
class NoClip : Module() {

    override fun onDisable() {
        val speed = NightX.moduleManager.getModule(Speed::class.java)

        if (speed != null) {
            if (!speed.state) {
                MovementUtils.strafe(0.2f)
            }
        }

        mc.thePlayer?.noClip = false
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        mc.thePlayer.noClip = true
        mc.thePlayer.onGround = false

        mc.thePlayer.capabilities.isFlying = false
        mc.thePlayer.motionX = 0.0
        mc.thePlayer.motionY = 0.0
        mc.thePlayer.motionZ = 0.0

        MovementUtils.strafe(1f)
        if (mc.gameSettings.keyBindJump.isKeyDown) {
            mc.thePlayer.motionY += 0.6f
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
            mc.thePlayer.motionY -= 0.6f
            mc.gameSettings.keyBindSneak.pressed = false
        }
    }
}
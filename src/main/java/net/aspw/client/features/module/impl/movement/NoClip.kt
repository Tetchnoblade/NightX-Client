package net.aspw.client.features.module.impl.movement

import net.aspw.client.Client
import net.aspw.client.event.EventTarget
import net.aspw.client.event.UpdateEvent
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.utils.MovementUtils
import org.lwjgl.input.Keyboard

@ModuleInfo(name = "NoClip", spacedName = "No Clip", category = ModuleCategory.MOVEMENT)
class NoClip : Module() {

    override fun onDisable() {
        val speed = Client.moduleManager.getModule(Speed::class.java)

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
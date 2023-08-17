package net.aspw.client.features.module.impl.movement.speeds.intave

import net.aspw.client.Client
import net.aspw.client.event.MoveEvent
import net.aspw.client.features.module.impl.movement.Speed
import net.aspw.client.features.module.impl.movement.speeds.SpeedMode
import net.aspw.client.util.MovementUtils
import net.minecraft.client.settings.GameSettings

class IntaveHop : SpeedMode("IntaveHop") {

    override fun onUpdate() {
        mc.gameSettings.keyBindJump.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindJump)
        if (MovementUtils.isMoving()) {
            if (mc.thePlayer.onGround) {
                mc.gameSettings.keyBindJump.pressed = false
                mc.timer.timerSpeed = 1.0f
                mc.thePlayer.jump()
            }

            if (mc.thePlayer.motionY > 0.003) {
                mc.thePlayer.motionX *= 1.0015
                mc.thePlayer.motionZ *= 1.0015
                mc.timer.timerSpeed = 1.06f
            }
        }
    }

    override fun onEnable() {
        Client.moduleManager.getModule(
            Speed::class.java
        ) ?: return
        super.onEnable()
    }

    override fun onMotion() {}
    override fun onMove(event: MoveEvent) {}
}
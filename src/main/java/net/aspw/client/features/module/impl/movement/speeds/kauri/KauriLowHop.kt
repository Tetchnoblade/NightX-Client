package net.aspw.client.features.module.impl.movement.speeds.kauri

import net.aspw.client.Client
import net.aspw.client.event.MoveEvent
import net.aspw.client.features.module.impl.movement.Speed
import net.aspw.client.features.module.impl.movement.speeds.SpeedMode
import net.aspw.client.util.MovementUtils
import net.minecraft.client.settings.GameSettings

class KauriLowHop : SpeedMode("KauriLowHop") {

    private var ticks = 0

    override fun onUpdate() {
        ticks++
        if (!mc.thePlayer.onGround && ticks == 3) {
            mc.thePlayer.motionY = 0.00
        }
        mc.gameSettings.keyBindJump.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindJump)
        if (mc.thePlayer.onGround && MovementUtils.isMoving()) {
            mc.gameSettings.keyBindJump.pressed = false
            mc.thePlayer.jump()
            mc.thePlayer.motionY = 0.3001145141919810
            if (MovementUtils.getSpeed() < 0.22) {
                MovementUtils.strafe(0.22f)
            } else {
                MovementUtils.strafe(0.48f)
            }
        }
        if (!MovementUtils.isMoving()) {
            mc.thePlayer.motionX = 0.0
            mc.thePlayer.motionZ = 0.0
        }
    }

    override fun onDisable() {
        ticks = 0
        mc.timer.timerSpeed = 1f
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
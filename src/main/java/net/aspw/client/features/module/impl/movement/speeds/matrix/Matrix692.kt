package net.aspw.client.features.module.impl.movement.speeds.matrix

import net.aspw.client.event.MoveEvent
import net.aspw.client.features.module.impl.movement.speeds.SpeedMode
import net.aspw.client.utils.MovementUtils
import net.minecraft.client.settings.GameSettings

class Matrix692 : SpeedMode("Matrix6.9.2") {
    private var wasTimer = false
    override fun onDisable() {
        wasTimer = false
        mc.timer.timerSpeed = 1f
    }

    override fun onTick() {}
    override fun onMotion() {}
    override fun onUpdate() {
        if (mc.thePlayer!!.isInWater) return

        if (wasTimer) {
            wasTimer = false
            mc.timer.timerSpeed = 1.0f
        }
        mc.thePlayer.motionY -= 0.00348
        mc.thePlayer.jumpMovementFactor = 0.026f
        mc.gameSettings.keyBindJump.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindJump)
        if (MovementUtils.isMoving() && mc.thePlayer.onGround) {
            mc.gameSettings.keyBindJump.pressed = false
            mc.timer.timerSpeed = 1.35f
            wasTimer = true
            mc.thePlayer.jump()
            MovementUtils.strafe()
        } else if (MovementUtils.getSpeed() < 0.215) {
            MovementUtils.strafe(0.215f)
        }
    }

    override fun onMove(event: MoveEvent) {}
    override fun onEnable() {}
}
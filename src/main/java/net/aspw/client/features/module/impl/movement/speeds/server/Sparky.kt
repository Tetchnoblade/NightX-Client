package net.aspw.client.features.module.impl.movement.speeds.server

import net.aspw.client.event.JumpEvent
import net.aspw.client.event.MotionEvent
import net.aspw.client.event.MoveEvent
import net.aspw.client.features.module.impl.movement.speeds.SpeedMode
import net.aspw.client.utils.MovementUtils.isMoving

class Sparky : SpeedMode("Sparky") {

    override fun onJump(event: JumpEvent) {}

    override fun onUpdate() {
        if (isMoving()) {
            if (mc.thePlayer.onGround) {
                mc.gameSettings.keyBindJump.pressed = false
                mc.thePlayer.jump()
            } else {
                if (mc.thePlayer.fallDistance > 0.7f && mc.thePlayer.fallDistance <= 0.8f) {
                    mc.thePlayer.setPositionAndUpdate(mc.thePlayer.posX, mc.thePlayer.posY - 0.16, mc.thePlayer.posZ)
                    mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.16, mc.thePlayer.posZ)
                }
            }
        }
    }

    override fun onMotion() {}
    override fun onMotion(event: MotionEvent) {}
    override fun onMove(event: MoveEvent) {}
    override fun onDisable() {}
}
package net.aspw.client.features.module.impl.movement.speeds.vulcan

import net.aspw.client.Client
import net.aspw.client.event.MotionEvent
import net.aspw.client.event.MoveEvent
import net.aspw.client.features.module.impl.movement.speeds.SpeedMode
import net.aspw.client.features.module.impl.player.Scaffold
import net.aspw.client.utils.MovementUtils.*
import net.minecraft.client.settings.GameSettings

class VulcanHop1 : SpeedMode("VulcanHop1") {

    private var wasTimer = false

    override fun onUpdate() {
        if (wasTimer) {
            mc.timer.timerSpeed = 1.00f
            wasTimer = false
        }
        if (Math.abs(mc.thePlayer.movementInput.moveStrafe) < 0.1f) {
            mc.thePlayer.jumpMovementFactor = 0.026499f
        } else {
            mc.thePlayer.jumpMovementFactor = 0.0244f
        }
        mc.gameSettings.keyBindJump.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindJump)

        if (getSpeed() < 0.215f && !mc.thePlayer.onGround) {
            strafe(0.215f)
        }
        if (mc.thePlayer.onGround && isMoving()) {
            mc.gameSettings.keyBindJump.pressed = false
            mc.thePlayer.jump()
            if (!mc.thePlayer.isAirBorne) {
                return
            }
            mc.timer.timerSpeed = 1.25f
            wasTimer = true
            strafe()
            if (getSpeed() < 0.5f) {
                strafe(0.4849f)
            }
        } else if (!isMoving()) {
            mc.timer.timerSpeed = 1.00f
        }
    }

    override fun onMotion() {}

    override fun onMotion(event: MotionEvent) {}

    override fun onDisable() {
        val scaffoldModule = Client.moduleManager.getModule(Scaffold::class.java)

        if (!mc.thePlayer.isSneaking && !scaffoldModule!!.state)
            strafe(0.2f)
    }

    override fun onMove(event: MoveEvent) {
    }
}
package net.aspw.nightx.features.module.modules.movement.speeds.vulcan

import net.aspw.nightx.NightX
import net.aspw.nightx.event.MotionEvent
import net.aspw.nightx.event.MoveEvent
import net.aspw.nightx.features.module.modules.movement.speeds.SpeedMode
import net.aspw.nightx.features.module.modules.world.Scaffold
import net.aspw.nightx.utils.MovementUtils.*
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
            mc.thePlayer.motionX = 0.0
            mc.thePlayer.motionZ = 0.0
        }
    }

    override fun onMotion() {}

    override fun onMotion(event: MotionEvent) {}

    override fun onDisable() {
        val scaffoldModule = NightX.moduleManager.getModule(Scaffold::class.java)

        if (!mc.thePlayer.isSneaking && !scaffoldModule!!.state)
            strafe(0.3f)
    }

    override fun onMove(event: MoveEvent) {
    }
}
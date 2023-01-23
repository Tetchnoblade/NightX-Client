package net.aspw.nightx.features.module.modules.movement.speeds.vulcan

import net.aspw.nightx.NightX
import net.aspw.nightx.event.MotionEvent
import net.aspw.nightx.event.MoveEvent
import net.aspw.nightx.features.module.modules.movement.speeds.SpeedMode
import net.aspw.nightx.features.module.modules.world.Scaffold
import net.aspw.nightx.utils.MovementUtils.*
import net.minecraft.client.settings.GameSettings

class VulcanYPort : SpeedMode("VulcanYPort") {

    private var wasTimer = false
    private var ticks = 0

    override fun onUpdate() {
        ticks++
        if (wasTimer) {
            mc.timer.timerSpeed = 1.00f
            wasTimer = false
        }
        mc.thePlayer.jumpMovementFactor = 0.0245f
        if (!mc.thePlayer.onGround && ticks > 3 && mc.thePlayer.motionY > 0) {
            mc.thePlayer.motionY = -0.27
        }

        mc.gameSettings.keyBindJump.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindJump)
        if (getSpeed() < 0.215f && !mc.thePlayer.onGround) {
            strafe(0.215f)
        }
        if (mc.thePlayer.onGround && isMoving()) {
            ticks = 0
            mc.gameSettings.keyBindJump.pressed = false
            mc.thePlayer.jump()
            if (!mc.thePlayer.isAirBorne) {
                return
            }
            mc.timer.timerSpeed = 1.2f
            wasTimer = true
            if (getSpeed() < 0.48f) {
                strafe(0.48f)
            } else {
                strafe((getSpeed() * 0.985).toFloat())
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
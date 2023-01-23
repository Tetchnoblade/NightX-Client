package net.aspw.nightx.features.module.modules.movement.speeds.watchdog

import net.aspw.nightx.NightX
import net.aspw.nightx.event.JumpEvent
import net.aspw.nightx.event.MotionEvent
import net.aspw.nightx.event.MoveEvent
import net.aspw.nightx.features.module.modules.movement.Speed
import net.aspw.nightx.features.module.modules.movement.speeds.SpeedMode
import net.aspw.nightx.features.module.modules.world.Scaffold
import net.aspw.nightx.features.module.modules.world.Timer
import net.aspw.nightx.utils.MovementUtils.*
import kotlin.math.max

class WatchdogCustom : SpeedMode("WatchdogCustom") {

    override fun onJump(event: JumpEvent) {
        if (mc.thePlayer != null && isMoving())
            event.cancelEvent()
    }

    override fun onUpdate() {}

    override fun onMotion() {}

    override fun onMotion(event: MotionEvent) {
        val thePlayer = mc.thePlayer ?: return

        val speedModule = NightX.moduleManager.getModule(Speed::class.java)!!
        val scaffoldModule = NightX.moduleManager.getModule(Scaffold::class.java)
        val timer = NightX.moduleManager.getModule(Timer::class.java)

        if (isMoving()) {
            when {
                thePlayer.onGround && thePlayer.isCollidedVertically -> {
                    thePlayer.motionY = getJumpBoostModifier(
                        if (scaffoldModule!!.state) 0.41999 else speedModule.motionYValue.get().toDouble(), true
                    )

                    if (scaffoldModule.state) {
                        strafe(0.37F)
                    } else {
                        strafe(
                            (max(
                                speedModule.customSpeedValue.get() + getSpeedEffect() * 0.1,
                                getBaseMoveSpeed(0.2873)
                            )).toFloat()
                        )
                    }
                }

                else -> {
                    if (!timer!!.state && speedModule.timerValue.get())
                        mc.timer.timerSpeed = 1.07f

                    setMotion(getSpeed().toDouble(), speedModule.smoothStrafe.get())
                }
            }
        } else {
            thePlayer.motionX *= 0.0
            thePlayer.motionZ *= 0.0
        }
    }

    override fun onDisable() {
        val scaffoldModule = NightX.moduleManager.getModule(Scaffold::class.java)

        if (!mc.thePlayer.isSneaking && !scaffoldModule!!.state)
            strafe(0.3f)
    }

    override fun onMove(event: MoveEvent) {
        val thePlayer = mc.thePlayer ?: return
        val speedModule = NightX.moduleManager.getModule(Speed::class.java)!!

        if (isMoving()) {
            when {
                thePlayer.isCollidedHorizontally -> {
                    setMotion(event, getBaseMoveSpeed(0.258), 1.0, speedModule.smoothStrafe.get())
                }
            }
        }
    }
}
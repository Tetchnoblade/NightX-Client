package net.ccbluex.liquidbounce.features.module.modules.movement.speeds.blocksmc;

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.JumpEvent
import net.ccbluex.liquidbounce.event.MotionEvent
import net.ccbluex.liquidbounce.event.MoveEvent
import net.ccbluex.liquidbounce.features.module.modules.movement.Speed
import net.ccbluex.liquidbounce.features.module.modules.movement.speeds.SpeedMode
import net.ccbluex.liquidbounce.features.module.modules.world.Scaffold
import net.ccbluex.liquidbounce.utils.MovementUtils.*
import kotlin.math.max

class BlocksMC : SpeedMode("BlocksMC") {

    override fun onJump(event: JumpEvent) {
        if (mc.thePlayer != null && isMoving())
            event.cancelEvent()
    }

    override fun onUpdate() {}

    override fun onMotion() {}

    override fun onMotion(event: MotionEvent) {
        val thePlayer = mc.thePlayer ?: return

        val speedModule = LiquidBounce.moduleManager.getModule(Speed::class.java)!!
        val scaffoldModule = LiquidBounce.moduleManager.getModule(Scaffold::class.java)

        if (isMoving()) {
            when {
                thePlayer.onGround && thePlayer.isCollidedVertically -> {
                    thePlayer.motionY = getJumpBoostModifier(
                        if (scaffoldModule!!.state) 0.41999 else 0.42, true
                    )

                    if (scaffoldModule.state) {
                        strafe(0.37F)
                    } else {
                        strafe(
                            (max(
                                0.48 + getSpeedEffect() * 0.1,
                                getBaseMoveSpeed(0.2873)
                            )).toFloat()
                        )
                    }
                }

                else -> {
                    setMotion(getSpeed().toDouble(), speedModule.strafing.get())
                }
            }
        } else {
            thePlayer.motionX *= 0.0
            thePlayer.motionZ *= 0.0
        }
    }

    override fun onMove(event: MoveEvent) {
        val thePlayer = mc.thePlayer ?: return
        val speedModule = LiquidBounce.moduleManager.getModule(Speed::class.java)!!

        if (isMoving()) {
            when {
                thePlayer.isCollidedHorizontally -> {
                    setMotion(event, getBaseMoveSpeed(0.258), 1.0, speedModule.strafing.get())
                }
            }
        }
    }

}
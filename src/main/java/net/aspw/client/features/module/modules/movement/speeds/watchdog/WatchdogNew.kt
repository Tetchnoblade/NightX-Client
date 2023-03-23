package net.aspw.client.features.module.modules.movement.speeds.watchdog

import net.aspw.client.Client
import net.aspw.client.event.EventState
import net.aspw.client.event.JumpEvent
import net.aspw.client.event.MotionEvent
import net.aspw.client.event.MoveEvent
import net.aspw.client.features.module.modules.movement.Speed
import net.aspw.client.features.module.modules.movement.speeds.SpeedMode
import net.aspw.client.utils.MovementUtils
import net.minecraft.potion.Potion

class WatchdogNew : SpeedMode("WatchdogNew") {
    private var groundTick = 0
    private var ticks = 0
    override fun onJump(event: JumpEvent) {
        if (mc.thePlayer != null && MovementUtils.isMoving())
            event.cancelEvent()
    }

    override fun onMotion(eventMotion: MotionEvent) {
        if (mc.thePlayer.hurtTime > 6) {
            mc.thePlayer.jumpMovementFactor = 0.04f
        }
        ticks++
        val speed = Client.moduleManager.getModule(
            Speed::class.java
        )
        if (speed == null || eventMotion.eventState !== EventState.PRE || mc.thePlayer.isInWater) return
        if (MovementUtils.isMoving()) {
            groundTick++
            if (mc.thePlayer.onGround) {
                mc.timer.timerSpeed = 1.01f
                if (groundTick >= 0) {
                    mc.thePlayer.motionY = 0.41999998688698
                }
                if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
                    MovementUtils.strafe(0.578f)
                } else {
                    MovementUtils.strafe(0.428f)
                }
            } else if (ticks > 3 && mc.thePlayer.motionY > 0) {
                mc.thePlayer.jumpMovementFactor = 0.022f
            }
        } else {
            groundTick = 0
        }
    }

    override fun onEnable() {
        Client.moduleManager.getModule(
            Speed::class.java
        ) ?: return
        super.onEnable()
    }

    override fun onUpdate() {}
    override fun onMotion() {}
    override fun onMove(event: MoveEvent) {}
}
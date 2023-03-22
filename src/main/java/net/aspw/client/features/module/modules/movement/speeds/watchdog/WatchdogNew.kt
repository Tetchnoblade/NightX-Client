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
    override fun onJump(event: JumpEvent) {
        if (mc.thePlayer != null && MovementUtils.isMoving())
            event.cancelEvent()
    }

    override fun onMotion(eventMotion: MotionEvent) {
        val speed = Client.moduleManager.getModule(
            Speed::class.java
        )
        if (speed == null || eventMotion.eventState !== EventState.PRE || mc.thePlayer.isInWater) return
        if (MovementUtils.isMoving()) {
            if (mc.thePlayer.onGround) {
                if (groundTick >= 0) {
                    mc.thePlayer.motionY = 0.41999998688698
                }
                if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
                    MovementUtils.strafe(0.57f)
                } else {
                    MovementUtils.strafe(0.42f)
                }
            }
            groundTick++
        } else {
            groundTick = 0
            mc.thePlayer.motionY += -0.021 * 0.021
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
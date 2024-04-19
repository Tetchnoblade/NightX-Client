package net.aspw.client.features.module.impl.movement.speeds.watchdog

import net.aspw.client.Launch
import net.aspw.client.event.EventState
import net.aspw.client.event.JumpEvent
import net.aspw.client.event.MotionEvent
import net.aspw.client.event.MoveEvent
import net.aspw.client.features.module.impl.movement.Speed
import net.aspw.client.features.module.impl.movement.speeds.SpeedMode
import net.aspw.client.utils.MovementUtils
import net.minecraft.potion.Potion
import net.minecraft.stats.StatList

class WatchdogGround : SpeedMode("WatchdogGround") {

    override fun onJump(event: JumpEvent) {
        if (mc.thePlayer != null && MovementUtils.isMoving())
            event.cancelEvent()
    }

    override fun onMotion(eventMotion: MotionEvent) {
        val speed = Launch.moduleManager.getModule(
            Speed::class.java
        )
        if (speed == null || eventMotion.eventState !== EventState.PRE || mc.thePlayer.isInWater) return
        if (mc.thePlayer.onGround) {
            if (MovementUtils.isMoving()) {
                mc.thePlayer.motionY = 0.41999998688698
                if (mc.thePlayer.isPotionActive(Potion.jump))
                    mc.thePlayer.motionY += ((mc.thePlayer.getActivePotionEffect(Potion.jump).amplifier + 1).toFloat() * 0.1f).toDouble()
                mc.thePlayer.isAirBorne = true
                mc.thePlayer.triggerAchievement(StatList.jumpStat)
                val baseSpeed = 0.482f
                if (mc.thePlayer.isPotionActive(Potion.moveSpeed))
                    MovementUtils.strafe(baseSpeed + ((mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).amplifier + 1).toFloat() * 0.0575f))
                else MovementUtils.strafe(baseSpeed)
            }
        } else {
            if (mc.thePlayer.hurtTime >= 8)
                MovementUtils.strafe(MovementUtils.getSpeed() * 0.75f)
            if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
                mc.thePlayer.motionX *= (1.0002 + 0.0008 * (mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).amplifier + 1))
                mc.thePlayer.motionZ *= (1.0002 + 0.0008 * (mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).amplifier + 1))
                mc.thePlayer.speedInAir =
                    0.02f + 0.0003f * (mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).amplifier + 1).toFloat()
            }
        }
    }

    override fun onUpdate() {}
    override fun onMotion() {}
    override fun onMove(event: MoveEvent) {}
}
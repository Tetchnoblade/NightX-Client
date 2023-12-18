package net.aspw.client.features.module.impl.minigames

import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.event.*
import net.aspw.client.util.MovementUtils
import net.aspw.client.util.timer.TickTimer
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.potion.Potion
import net.minecraft.stats.StatList

@ModuleInfo(name = "HypixelStrafe", spacedName = "Hypixel Strafe Beta", description = "", category = ModuleCategory.MINIGAMES)
class HypixelStrafe : Module() {

    override val tag: String
        get() = tickTimer.tick.toString()

    private val tickTimer = TickTimer()
    private var strafing = false

    override fun onDisable() {
        tickTimer.reset()
        strafing = false
    }

    @EventTarget
    fun onJump(event: JumpEvent) {
        if (MovementUtils.isMoving() && !mc.thePlayer.isInWater) {
            event.cancelEvent()
        }
    }

    @EventTarget
    fun onMotion(event: MotionEvent) {
        if (MovementUtils.isMoving() && !mc.thePlayer.isInWater) {
            tickTimer.update()
            if (!mc.thePlayer.onGround && tickTimer.hasTimePassed(12)) {
                strafing = true
                MovementUtils.strafe()
                tickTimer.reset()
            } else if (mc.thePlayer.onGround) {
                strafing = false
                tickTimer.reset()
                mc.thePlayer.motionY = 0.41999998688698
                mc.thePlayer.isAirBorne = true
                mc.thePlayer.triggerAchievement(StatList.jumpStat)
                if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
                    MovementUtils.strafe(0.57f)
                } else {
                    MovementUtils.strafe(0.425f)
                }
            }
        } else {
            strafing = false
            tickTimer.reset()
        }
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet
        if (strafing) {
            if (packet is C03PacketPlayer) {
                event.cancelEvent()
                chat("Nigger Porn Exploit: " + tickTimer.tick.toString())
            }
        }
    }
}
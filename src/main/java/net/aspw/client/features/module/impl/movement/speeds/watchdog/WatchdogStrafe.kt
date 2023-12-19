package net.aspw.client.features.module.impl.movement.speeds.watchdog

import net.aspw.client.Client
import net.aspw.client.event.JumpEvent
import net.aspw.client.event.MotionEvent
import net.aspw.client.event.MoveEvent
import net.aspw.client.event.PacketEvent
import net.aspw.client.features.module.impl.movement.Speed
import net.aspw.client.features.module.impl.movement.speeds.SpeedMode
import net.aspw.client.features.module.impl.player.Scaffold
import net.aspw.client.util.MovementUtils
import net.aspw.client.util.PacketUtils
import net.aspw.client.util.timer.TickTimer
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.network.play.client.C0DPacketCloseWindow
import net.minecraft.potion.Potion
import net.minecraft.stats.StatList

class WatchdogStrafe : SpeedMode("WatchdogStrafe") {

    private val tickTimer = TickTimer()
    private var strafing = false

    override fun onJump(event: JumpEvent) {
        if (mc.thePlayer != null && MovementUtils.isMoving())
            event.cancelEvent()
    }

    override fun onPacket(event: PacketEvent) {
        val packet = event.packet
        if (strafing) {
            if (packet is C03PacketPlayer) {
                PacketUtils.sendPacketNoEvent(C0DPacketCloseWindow())
            }
        }
    }

    override fun onMotion(eventMotion: MotionEvent) {
        Client.moduleManager.getModule(
            Speed::class.java
        ) ?: return

        if (MovementUtils.isMoving() && !mc.thePlayer.isInWater) {
            tickTimer.update()
            if (!mc.thePlayer.onGround && tickTimer.hasTimePassed(7)) {
                strafing = true
                if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
                    MovementUtils.strafe(0.21f)
                } else {
                    MovementUtils.strafe(0.16f)
                }
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

    override fun onEnable() {
        Client.moduleManager.getModule(
            Speed::class.java
        ) ?: return
        super.onEnable()
    }

    override fun onDisable() {
        tickTimer.reset()
        strafing = false

        val scaffoldModule = Client.moduleManager.getModule(Scaffold::class.java)

        if (!mc.thePlayer.isSneaking && !scaffoldModule!!.state)
            MovementUtils.strafe(0.2f)
    }

    override fun onUpdate() {}
    override fun onMotion() {}
    override fun onMove(event: MoveEvent) {}
}
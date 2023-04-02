package net.aspw.client.features.module.impl.movement.speeds.vulcan

import net.aspw.client.Client
import net.aspw.client.event.MotionEvent
import net.aspw.client.event.MoveEvent
import net.aspw.client.event.PacketEvent
import net.aspw.client.features.module.impl.movement.Speed
import net.aspw.client.features.module.impl.movement.speeds.SpeedMode
import net.aspw.client.utils.MovementUtils.isMoving
import net.aspw.client.utils.MovementUtils.strafe
import net.minecraft.network.play.client.C03PacketPlayer

class VulcanGround : SpeedMode("VulcanGround") {

    private var jumped = false
    private var jumpCount = 0
    private var yMotion = 0.0

    override fun onUpdate() {
        if (jumped) {
            mc.thePlayer.motionY = -0.1
            mc.thePlayer.onGround = false
            jumped = false
            yMotion = 0.0
        }
        mc.thePlayer.jumpMovementFactor = 0.025f
        if (mc.thePlayer.onGround && isMoving()) {
            if (mc.thePlayer.isCollidedHorizontally || mc.gameSettings.keyBindJump.pressed) {
                if (!mc.gameSettings.keyBindJump.pressed) {
                    mc.thePlayer.jump()
                }
                return
            }
            mc.thePlayer.jump()
            mc.thePlayer.motionY = 0.0
            yMotion = 0.1 + Math.random() * 0.03
            strafe(0.48f + jumpCount * 0.001f)
            jumpCount++
            jumped = true
        } else if (isMoving()) {
            strafe(0.27f + jumpCount * 0.0018f)
        }
    }

    override fun onMotion() {}

    override fun onMotion(event: MotionEvent) {}

    override fun onDisable() {
        mc.timer.timerSpeed = 1f
    }

    override fun onEnable() {
        mc.timer.timerSpeed = 1f
    }

    fun onPacket(event: PacketEvent) {
        val packet = event.packet
        if (packet is C03PacketPlayer) {
            packet.y += yMotion
        }
    }

    override fun onMove(event: MoveEvent) {
        if (jumpCount >= Client.moduleManager.getModule(Speed::class.java)!!.boostDelayValue.get() && Client.moduleManager.getModule(
                Speed::class.java
            )!!.boostSpeedValue.get()
        ) {
            event.x *= 1.7181145141919810
            event.z *= 1.7181145141919810
            jumpCount = 0
        } else if (!Client.moduleManager.getModule(Speed::class.java)!!.boostSpeedValue.get()) {
            jumpCount = 4
        }
    }
}
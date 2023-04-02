package net.aspw.client.features.module.impl.movement.speeds.matrix

import net.aspw.client.event.MoveEvent
import net.aspw.client.event.PacketEvent
import net.aspw.client.features.module.impl.movement.speeds.SpeedMode
import net.aspw.client.utils.MovementUtils
import net.minecraft.client.settings.GameSettings
import net.minecraft.network.play.server.S12PacketEntityVelocity
import kotlin.math.abs

class Matrix670 : SpeedMode("Matrix6.7.0") {
    private var noVelocityY = 0
    override fun onDisable() {
        mc.timer.timerSpeed = 1f
        noVelocityY = 0
    }

    override fun onTick() {}
    override fun onMotion() {}
    override fun onUpdate() {
        if (mc.thePlayer!!.isInWater) return

        if (noVelocityY >= 0) {
            noVelocityY -= 1
        }
        if (!mc.thePlayer.onGround && noVelocityY <= 0) {
            if (mc.thePlayer.motionY > 0) {
                mc.thePlayer.motionY -= 0.0005
            }
            mc.thePlayer.motionY -= 0.009400114514191982
        }
        if (!mc.thePlayer.onGround) {
            mc.gameSettings.keyBindJump.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindJump)
            if (MovementUtils.getSpeed() < 0.2177 && noVelocityY < 8) {
                MovementUtils.strafe(0.2177f)
            }
        }
        if (abs(mc.thePlayer.movementInput.moveStrafe) < 0.1) {
            mc.thePlayer.jumpMovementFactor = 0.026f
        } else {
            mc.thePlayer.jumpMovementFactor = 0.0247f
        }
        if (mc.thePlayer.onGround && MovementUtils.isMoving()) {
            mc.gameSettings.keyBindJump.pressed = false
            mc.thePlayer.jump()
            mc.thePlayer.motionY = 0.4105000114514192
            if (abs(mc.thePlayer.movementInput.moveStrafe) < 0.1) {
                MovementUtils.strafe()
            }
        }
        if (!MovementUtils.isMoving()) {
            mc.thePlayer.motionX = 0.0
            mc.thePlayer.motionZ = 0.0
        }
    }

    fun onPacket(event: PacketEvent) {
        val packet = event.packet
        if (packet is S12PacketEntityVelocity) {
            if (mc.thePlayer == null || (mc.theWorld?.getEntityByID(packet.entityID) ?: return) != mc.thePlayer) {
                return
            }
            noVelocityY = 10
        }
    }

    override fun onMove(event: MoveEvent) {}
    override fun onEnable() {}
}
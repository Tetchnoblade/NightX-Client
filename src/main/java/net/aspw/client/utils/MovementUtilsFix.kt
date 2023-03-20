package net.aspw.client.utils

import net.minecraft.client.Minecraft
import kotlin.math.cos
import kotlin.math.sin

object MovementUtilsFix : MinecraftInstance() {
    val direction: Double
        get() {
            var rotationYaw = Minecraft.getMinecraft().thePlayer.rotationYaw
            if (Minecraft.getMinecraft().thePlayer.moveForward < 0f) rotationYaw += 180f
            var forward = 1f
            if (Minecraft.getMinecraft().thePlayer.moveForward < 0f) forward =
                -0.5f else if (Minecraft.getMinecraft().thePlayer.moveForward > 0f) forward = 0.5f
            if (Minecraft.getMinecraft().thePlayer.moveStrafing > 0f) rotationYaw -= 90f * forward
            if (Minecraft.getMinecraft().thePlayer.moveStrafing < 0f) rotationYaw += 90f * forward
            return Math.toRadians(rotationYaw.toDouble())
        }

    fun setMotion(speed: Double) {
        var forward = mc.thePlayer.movementInput.moveForward.toDouble()
        var strafe = mc.thePlayer.movementInput.moveStrafe.toDouble()
        var yaw = mc.thePlayer.rotationYaw
        if (forward == 0.0 && strafe == 0.0) {
            mc.thePlayer.motionX = 0.0
            mc.thePlayer.motionZ = 0.0
        } else {
            if (forward != 0.0) {
                if (strafe > 0.0) {
                    yaw += (if (forward > 0.0) -45 else 45).toFloat()
                } else if (strafe < 0.0) {
                    yaw += (if (forward > 0.0) 45 else -45).toFloat()
                }
                strafe = 0.0
                if (forward > 0.0) {
                    forward = 1.0
                } else if (forward < 0.0) {
                    forward = -1.0
                }
            }
            val cos = cos(Math.toRadians((yaw + 90.0f).toDouble()))
            val sin = sin(Math.toRadians((yaw + 90.0f).toDouble()))
            mc.thePlayer.motionX = (forward * speed * cos +
                    strafe * speed * sin)
            mc.thePlayer.motionZ = (forward * speed * sin -
                    strafe * speed * cos)
        }
    }

    var bps = 0.0
        private set
    private var lastX = 0.0
    private var lastY = 0.0
    private var lastZ = 0.0

    fun updateBlocksPerSecond() {
        if (mc.thePlayer == null || mc.thePlayer.ticksExisted < 1) {
            bps = 0.0
        }
        val distance = mc.thePlayer.getDistance(lastX, lastY, lastZ)
        lastX = mc.thePlayer.posX
        lastY = mc.thePlayer.posY
        lastZ = mc.thePlayer.posZ
        bps = distance * (20 * mc.timer.timerSpeed)
    }

    val movingYaw: Float
        get() = (direction * 180f / Math.PI).toFloat()
}

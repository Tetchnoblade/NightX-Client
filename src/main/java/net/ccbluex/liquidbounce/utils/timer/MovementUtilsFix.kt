package net.ccbluex.liquidbounce.utils.timer

import net.ccbluex.liquidbounce.utils.MinecraftInstance
import net.minecraft.client.Minecraft

object MovementUtilsFix : MinecraftInstance() {
    val direction: Double
        get() {
            var rotationYaw = Minecraft.getMinecraft().thePlayer.rotationYaw
            if (Minecraft.getMinecraft().thePlayer.moveForward < 0f) rotationYaw += 180f
            var forward = 1f
            if (Minecraft.getMinecraft().thePlayer.moveForward < 0f) forward = -0.5f else if (Minecraft.getMinecraft().thePlayer.moveForward > 0f) forward = 0.5f
            if (Minecraft.getMinecraft().thePlayer.moveStrafing > 0f) rotationYaw -= 90f * forward
            if (Minecraft.getMinecraft().thePlayer.moveStrafing < 0f) rotationYaw += 90f * forward
            return Math.toRadians(rotationYaw.toDouble())
        }

    val movingYaw: Float
        get() = (direction * 180f / Math.PI).toFloat()
}

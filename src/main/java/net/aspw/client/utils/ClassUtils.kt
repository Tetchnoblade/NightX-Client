package net.aspw.client.utils

import net.minecraft.client.Minecraft
import net.minecraft.util.AxisAlignedBB

object ClassUtils {

    private val cachedClasses = mutableMapOf<String, Boolean>()

    /**
     * Allows you to check for existing classes with the [className]
     */
    @JvmStatic
    fun hasClass(className: String): Boolean {
        return if (cachedClasses.containsKey(className))
            cachedClasses[className]!!
        else try {
            Class.forName(className)
            cachedClasses[className] = true

            true
        } catch (e: ClassNotFoundException) {
            cachedClasses[className] = false

            false
        }
    }

    fun hasForge() = hasClass("net.minecraftforge.common.MinecraftForge")

    fun isBlockUnder(): Boolean {
        if (Minecraft.getMinecraft().thePlayer.posY < 0) return false
        var off = 0
        while (off < Minecraft.getMinecraft().thePlayer.posY.toInt() + 2) {
            val bb: AxisAlignedBB = Minecraft.getMinecraft().thePlayer.entityBoundingBox
                .offset(0.0, -off.toDouble(), 0.0)
            if (Minecraft.getMinecraft().theWorld.getCollidingBoundingBoxes(
                    Minecraft.getMinecraft().thePlayer,
                    bb
                ).isNotEmpty()
            ) {
                return true
            }
            off += 2
        }
        return false
    }
}
package net.aspw.client.util

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
        if (MinecraftInstance.mc.thePlayer.posY < 0) return false
        var off = 0
        while (off < MinecraftInstance.mc.thePlayer.posY.toInt() + 2) {
            val bb: AxisAlignedBB = MinecraftInstance.mc.thePlayer.entityBoundingBox
                .offset(0.0, -off.toDouble(), 0.0)
            if (MinecraftInstance.mc.theWorld.getCollidingBoundingBoxes(
                    MinecraftInstance.mc.thePlayer,
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
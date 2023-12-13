package net.aspw.client.features.module.impl.other

import net.aspw.client.event.EventTarget
import net.aspw.client.event.MotionEvent
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.value.BoolValue
import net.aspw.client.value.FloatValue

/**
 * The type Player edit.
 */
@ModuleInfo(name = "PlayerEdit", spacedName = "Player Edit", description = "", category = ModuleCategory.OTHER)
class PlayerEdit : Module() {

    companion object {
        @JvmField
        val editPlayerSizeValue = BoolValue("PlayerSize", false)

        /**
         * The constant playerSizeValue.
         */
        @JvmField
        val playerSizeValue = FloatValue("PlayerSize", 1.5f, 0.5f, 2.5f, "m") { editPlayerSizeValue.get() }

        /**
         * The constant rotatePlayer.
         */
        @JvmField
        val rotatePlayer = BoolValue("PlayerRotate", true)

        @JvmField
        val xRot = FloatValue("X-Rotation", 90.0f, -180.0f, 180.0f) { rotatePlayer.get() }

        @JvmField
        val yPos = FloatValue("Y-Position", 0.0f, -5.0f, 5.0f) { rotatePlayer.get() }
    }

    @EventTarget
    fun onMotion(event: MotionEvent?) {
        if (editPlayerSizeValue.get()) mc.thePlayer.eyeHeight =
            playerSizeValue.get() + 0.62f else mc.thePlayer.eyeHeight = mc.thePlayer.defaultEyeHeight
    }

    override fun onDisable() {
        mc.thePlayer.eyeHeight = mc.thePlayer.defaultEyeHeight
    }
}
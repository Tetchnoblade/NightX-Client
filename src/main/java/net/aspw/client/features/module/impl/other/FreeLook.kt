package net.aspw.client.features.module.impl.other

import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.minecraft.client.Minecraft
import org.lwjgl.input.Keyboard
import org.lwjgl.opengl.Display

@ModuleInfo(name = "FreeLook", spacedName = "Free Look", category = ModuleCategory.OTHER, keyBind = Keyboard.KEY_F9)
class FreeLook : Module() {
    override fun onEnable() {
        perspectiveToggled = !perspectiveToggled
        cameraYaw = mc.thePlayer.rotationYaw
        cameraPitch = mc.thePlayer.rotationPitch
        if (perspectiveToggled) {
            previousPerspective = mc.gameSettings.thirdPersonView
            mc.gameSettings.thirdPersonView = 1
        } else {
            mc.gameSettings.thirdPersonView = previousPerspective
        }
    }

    override fun onDisable() {
        resetPerspective()
    }

    companion object {
        private val mc = Minecraft.getMinecraft()

        @JvmField
        var perspectiveToggled = false

        @JvmField
        var cameraYaw = 0f

        @JvmField
        var cameraPitch = 0f
        private var previousPerspective = 0

        @JvmStatic
        fun overrideMouse(): Boolean {
            if (mc.inGameHasFocus && Display.isActive()) {
                if (!perspectiveToggled) {
                    return true
                }
                mc.mouseHelper.mouseXYChange()
                val f1 = mc.gameSettings.mouseSensitivity * 0.6f + 0.2f
                val f2 = f1 * f1 * f1 * 8.0f
                val f3 = mc.mouseHelper.deltaX.toFloat() * f2
                val f4 = mc.mouseHelper.deltaY.toFloat() * f2
                cameraYaw += f3 * 0.15f
                cameraPitch += f4 * 0.15f
                if (cameraPitch > 90) cameraPitch = 90f
                if (cameraPitch < -90) cameraPitch = -90f
            }
            return false
        }

        fun resetPerspective() {
            perspectiveToggled = false
            mc.gameSettings.thirdPersonView = previousPerspective
        }
    }
}
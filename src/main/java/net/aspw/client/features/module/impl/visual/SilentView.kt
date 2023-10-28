package net.aspw.client.features.module.impl.visual

import net.aspw.client.event.EventTarget
import net.aspw.client.event.MotionEvent
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.util.RotationUtils
import net.aspw.client.value.BoolValue
import net.aspw.client.value.ListValue

@ModuleInfo(
    name = "SilentView",
    spacedName = "Silent View",
    description = "",
    category = ModuleCategory.VISUAL,
    array = false
)
class SilentView : Module() {
    val rotationMode = ListValue("Mode", arrayOf("Normal", "Silent"), "Normal")
    val rotatingCheckValue = BoolValue("RotatingCheck", true) { rotationMode.get().equals("normal", true) }
    val bodyLockValue = BoolValue("BodyLock", true) { rotationMode.get().equals("normal", true) }

    var playerYaw: Float? = null

    companion object {
        @JvmStatic
        var prevHeadPitch = 0f

        @JvmStatic
        var headPitch = 0f

        @JvmStatic
        fun lerp(tickDelta: Float, old: Float, new: Float): Float {
            return old + (new - old) * tickDelta
        }
    }

    @EventTarget
    fun onMotion(event: MotionEvent) {
        val thePlayer = mc.thePlayer
        if (thePlayer == null || RotationUtils.targetRotation == null && rotatingCheckValue.get()) {
            playerYaw = null
            headPitch = 0f
            prevHeadPitch = 0f
            return
        }
        prevHeadPitch = headPitch
        headPitch = RotationUtils.serverRotation?.pitch!!
        playerYaw = RotationUtils.serverRotation?.yaw!!
    }

    init {
        state = true
    }
}
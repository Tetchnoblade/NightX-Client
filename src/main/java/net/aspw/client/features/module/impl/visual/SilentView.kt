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
    val rotationMode = ListValue("Mode", arrayOf("Normal", "Old"), "Normal")
    val rotatingCheckValue = BoolValue("RotatingCheck", false)
    val bodyLockValue = BoolValue("BodyLock", false)

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
        if (mc.thePlayer == null) return
        if (RotationUtils.targetRotation == null && rotatingCheckValue.get() || !rotatingCheckValue.get()) {
            when (rotationMode.get().lowercase()) {
                "normal" -> {
                    prevHeadPitch = headPitch
                    headPitch = RotationUtils.serverRotation?.pitch!!
                    playerYaw = RotationUtils.serverRotation?.yaw!!
                }

                "old" -> {
                    if (mc.thePlayer.ticksExisted % 10 == 0) {
                        prevHeadPitch = headPitch
                        headPitch = RotationUtils.serverRotation?.pitch!!
                        playerYaw = RotationUtils.serverRotation?.yaw!!
                    }
                }
            }
        }
    }
}
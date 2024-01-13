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
    private val rotationMode = ListValue("Mode", arrayOf("Fast", "Smooth"), "Fast")
    private val bodyLockValue = BoolValue("BodyLock", false) { rotationMode.get().equals("smooth", true) }

    var playerYaw: Float? = null
    var pitchRotating = false
    var bodyLocked = false
    var oldRotating = false

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

    override fun onDisable() {
        playerYaw = null
        pitchRotating = false
        bodyLocked = false
        oldRotating = false
        headPitch = 0f
        prevHeadPitch = 0f
    }

    @EventTarget
    fun onMotion(event: MotionEvent) {
        if (mc.thePlayer == null || RotationUtils.targetRotation == null) {
            playerYaw = null
            pitchRotating = false
            bodyLocked = false
            oldRotating = false
            headPitch = 0f
            prevHeadPitch = 0f
            return
        }

        when (rotationMode.get().lowercase()) {
            "fast" -> {
                prevHeadPitch = headPitch
                headPitch = RotationUtils.serverRotation?.pitch!!
                pitchRotating = true
                oldRotating = true
                mc.thePlayer.renderYawOffset = RotationUtils.serverRotation?.yaw!!
                mc.thePlayer.rotationYawHead = RotationUtils.serverRotation?.yaw!!
            }

            "smooth" -> {
                prevHeadPitch = headPitch
                headPitch = RotationUtils.serverRotation?.pitch!!
                pitchRotating = true
                if (bodyLockValue.get())
                    bodyLocked = true
                playerYaw = RotationUtils.serverRotation?.yaw!!
            }
        }
    }
}
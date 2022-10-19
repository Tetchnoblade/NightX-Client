package net.ccbluex.liquidbounce.features.module.modules.misc

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.Rotation
import net.ccbluex.liquidbounce.utils.RotationUtils
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.ListValue

@ModuleInfo(name = "Annoy", category = ModuleCategory.MISC)
class Annoy : Module() {
    private val yawModeValue = ListValue("YawMove", arrayOf("Jitter", "Spin", "Back"), "Spin")
    private val pitchModeValue = ListValue("PitchMode", arrayOf("Down", "Up", "Jitter"), "Down")
    private val rotateValue = BoolValue("SilentRotate", true)

    private var yaw = 0f
    private var pitch = 0f

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        when (yawModeValue.get().lowercase()) {
            "spin" -> {
                yaw += 20.0f
                if (yaw > 180.0f) {
                    yaw = -180.0f
                } else if (yaw < -180.0f) {
                    yaw = 180.0f
                }
            }

            "jitter" -> {
                yaw = mc.thePlayer.rotationYaw + if (mc.thePlayer.ticksExisted % 2 == 0) 90F else -90F
            }

            "back" -> {
                yaw = mc.thePlayer.rotationYaw + 180f
            }
        }

        when (pitchModeValue.get().lowercase()) {
            "up" -> {
                pitch = -90.0f
            }

            "down" -> {
                pitch = 90.0f
            }

            "jitter" -> {
                pitch += 30.0f
                if (pitch > 90.0f) {
                    pitch = -90.0f
                } else if (pitch < -90.0f) {
                    pitch = 90.0f
                }
            }
        }

        if (rotateValue.get()) {
            RotationUtils.setTargetRotation(Rotation(yaw, pitch))
        } else {
            mc.thePlayer.rotationYaw = yaw
            mc.thePlayer.rotationPitch = pitch
        }
    }
}
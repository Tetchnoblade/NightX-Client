package net.aspw.client.features.module.impl.other

import net.aspw.client.event.EventTarget
import net.aspw.client.event.UpdateEvent
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.util.Rotation
import net.aspw.client.util.RotationUtils
import net.aspw.client.value.BoolValue
import net.aspw.client.value.ListValue

@ModuleInfo(name = "Annoy", description = "", category = ModuleCategory.OTHER)
class Annoy : Module() {
    private val yawModeValue = ListValue("YawMove", arrayOf("None", "Jitter", "Spin", "Back"), "Spin")
    private val pitchModeValue = ListValue("PitchMode", arrayOf("None", "Down", "Up", "Jitter"), "Down")
    val rotateValue = BoolValue("SilentRotate", true)

    private var yaw = 0f
    private var pitch = 0f

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        when (yawModeValue.get().lowercase()) {
            "none" -> {
                yaw = mc.thePlayer.rotationYaw
            }

            "spin" -> {
                yaw += 20.0f
                if (yaw > 175.0f) {
                    yaw = -180.0f
                } else if (yaw < -175.0f) {
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
            "none" -> {
                pitch = mc.thePlayer.rotationPitch
            }

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
package net.aspw.client.features.module.impl.other

import net.aspw.client.event.EventTarget
import net.aspw.client.event.UpdateEvent
import net.aspw.client.event.WorldEvent
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.utils.Rotation
import net.aspw.client.utils.RotationUtils
import net.aspw.client.value.IntegerValue
import net.aspw.client.value.ListValue

@ModuleInfo(name = "Annoy", category = ModuleCategory.OTHER)
class Annoy : Module() {
    private val yawModeValue = ListValue("YawMove", arrayOf("None", "Jitter", "Spin", "Back"), "Spin")
    private val pitchModeValue = ListValue("PitchMode", arrayOf("None", "Down", "Up", "Jitter"), "Down")
    private val spinSpeed = IntegerValue("SpinSpeed", 20, 0, 40) { yawModeValue.get().equals("spin", true) }

    private var yaw = 0f
    private var pitch = 0f

    override fun onDisable() {
        yaw = 0f
        pitch = 0f
    }

    @EventTarget
    fun onWorld(event: WorldEvent) {
        yaw = 0f
        pitch = 0f
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        when (yawModeValue.get().lowercase()) {
            "none" -> {
                yaw = RotationUtils.cameraYaw
            }

            "spin" -> {
                yaw += spinSpeed.get()
            }

            "jitter" -> {
                yaw = RotationUtils.cameraYaw + if (mc.thePlayer.ticksExisted % 2 == 0) 90F else -90F
            }

            "back" -> {
                yaw = RotationUtils.cameraYaw + 180f
            }
        }

        when (pitchModeValue.get().lowercase()) {
            "none" -> {
                pitch = RotationUtils.cameraPitch
            }

            "up" -> {
                pitch = -90.0f
            }

            "down" -> {
                pitch = 90.0f
            }

            "jitter" -> {
                pitch += 30.0f
                if (pitch > 80.0f) {
                    pitch = -80.0f
                } else if (pitch < -80.0f) {
                    pitch = 80.0f
                }
            }
        }

        RotationUtils.setTargetRotation(Rotation(yaw, pitch))
    }
}
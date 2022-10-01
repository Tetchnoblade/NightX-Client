package net.ccbluex.liquidbounce.features.module.modules.render

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.Render3DEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import java.util.*

@ModuleInfo(name = "Rotate", spacedName = "Rotate", description = "", category = ModuleCategory.RENDER)
class Rotate : Module() {
    private val yawMode = ListValue("Yaw", arrayOf("Static", "Offset", "Random", "Jitter", "Spin", "None"), "None")
    val pitchMode = ListValue("Pitch", arrayOf("Static", "Offset", "Random", "Jitter", "None"), "Static")
    private val static_offsetYaw = FloatValue("Static/Offset-Yaw", 0F, -180F, 180F, "°")
    private val static_offsetPitch = FloatValue("Static/Offset-Pitch", 0F, -90F, 90F, "°")
    private val yawJitterTimer = IntegerValue("YawJitterTimer", 40, 1, 40, " tick(s)")
    private val pitchJitterTimer = IntegerValue("PitchJitterTimer", 1, 1, 40, " tick(s)")
    private val yawSpinSpeed = FloatValue("YawSpinSpeed", 5F, -90F, 90F, "°")

    var pitch = 0F
    private var lastSpin = 0F
    private var yawTimer = 0
    private var pitchTimer = 0

    override fun onDisable() {
        pitch = -4.9531336E7f
        lastSpin = 0.0f
        yawTimer = 0
        pitchTimer = 0
    }

    @EventTarget
    fun onRender3D(event: Render3DEvent) {
        mc.thePlayer ?: return

        if (!yawMode.get().equals("none", true)) {
            var yaw = 0F
            when (yawMode.get().lowercase(Locale.getDefault())) {
                "static" -> yaw = static_offsetYaw.get()
                "offset" -> yaw = mc.thePlayer.rotationYaw + static_offsetYaw.get()
                "random" -> yaw = Math.floor(Math.random() * 360.0 - 180.0).toFloat()
                "jitter" -> {
                    if (yawTimer++ % (yawJitterTimer.get() * 2) >= yawJitterTimer.get())
                        yaw = mc.thePlayer.rotationYaw
                    else
                        yaw = mc.thePlayer.rotationYaw - 180F
                }

                "spin" -> {
                    lastSpin += yawSpinSpeed.get()
                    yaw = lastSpin
                }
            }
            mc.thePlayer.renderYawOffset = yaw
            mc.thePlayer.rotationYawHead = yaw
            lastSpin = yaw
        }
        when (pitchMode.get().lowercase(Locale.getDefault())) {
            "static" -> pitch = static_offsetPitch.get()
            "offset" -> pitch = mc.thePlayer.rotationPitch + static_offsetPitch.get()
            "random" -> pitch = Math.floor(Math.random() * 180.0 - 90.0).toFloat()
            "jitter" -> {
                if (pitchTimer++ % (pitchJitterTimer.get() * 2) >= pitchJitterTimer.get())
                    pitch = 90F
                else
                    pitch = -90F
            }
        }
    }

    override val tag: String
        get() = "${yawMode.get()}, ${pitchMode.get()}"
}

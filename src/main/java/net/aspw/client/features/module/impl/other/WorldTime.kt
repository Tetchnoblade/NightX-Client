package net.aspw.client.features.module.impl.other

import net.aspw.client.event.EventTarget
import net.aspw.client.event.PacketEvent
import net.aspw.client.event.UpdateEvent
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.value.FloatValue
import net.aspw.client.value.IntegerValue
import net.aspw.client.value.ListValue
import net.minecraft.network.play.server.S03PacketTimeUpdate

@ModuleInfo(name = "WorldTime", spacedName = "World Time", category = ModuleCategory.OTHER)
class WorldTime : Module() {
    val timeModeValue = ListValue("Time", arrayOf("Static", "Cycle"), "Static")
    val cycleSpeedValue = IntegerValue("CycleSpeed", 30, -30, 100, { timeModeValue.get().equals("cycle", true) })
    val staticTimeValue = IntegerValue("StaticTime", 18000, 0, 24000, { timeModeValue.get().equals("static", true) })
    val weatherModeValue = ListValue("Weather", arrayOf("Clear", "Rain", "NoModification"), "Clear")
    val rainStrengthValue = FloatValue("RainStrength", 1F, 0.01F, 1F, { weatherModeValue.get().equals("rain", true) })

    private var timeCycle = 0L

    override fun onEnable() {
        timeCycle = 0L
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        if (event.packet is S03PacketTimeUpdate)
            event.cancelEvent()
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (timeModeValue.get().equals("static", true))
            mc.theWorld.worldTime = staticTimeValue.get().toLong()
        else {
            mc.theWorld.worldTime = timeCycle
            timeCycle += (cycleSpeedValue.get() * 10).toLong()

            if (timeCycle > 24000L) timeCycle = 0L
            if (timeCycle < 0L) timeCycle = 24000L
        }

        if (!weatherModeValue.get().equals("nomodification", true))
            mc.theWorld.setRainStrength(
                if (weatherModeValue.get().equals("clear", true)) 0F else rainStrengthValue.get()
            )
    }
}
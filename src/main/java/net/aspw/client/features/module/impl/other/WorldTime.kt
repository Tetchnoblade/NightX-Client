package net.aspw.client.features.module.impl.other

import net.aspw.client.event.EventTarget
import net.aspw.client.event.PacketEvent
import net.aspw.client.event.UpdateEvent
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.value.IntegerValue
import net.aspw.client.value.ListValue
import net.minecraft.network.play.server.S03PacketTimeUpdate

@ModuleInfo(name = "WorldTime", spacedName = "World Time", description = "", category = ModuleCategory.OTHER)
class WorldTime : Module() {
    private val timeModeValue = ListValue("Time", arrayOf("Static", "Cycle"), "Static")
    private val cycleSpeedValue = IntegerValue("CycleSpeed", 30, -30, 100) { timeModeValue.get().equals("cycle", true) }
    private val staticTimeValue = IntegerValue("StaticTime", 18000, 0, 24000) {
        timeModeValue.get().equals("static", true)
    }

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
    }
}
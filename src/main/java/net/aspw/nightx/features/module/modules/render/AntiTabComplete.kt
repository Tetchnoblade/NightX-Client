package net.aspw.nightx.features.module.modules.render

import net.aspw.nightx.event.EventTarget
import net.aspw.nightx.event.PacketEvent
import net.aspw.nightx.features.module.Module
import net.aspw.nightx.features.module.ModuleCategory
import net.aspw.nightx.features.module.ModuleInfo
import net.minecraft.network.play.client.C14PacketTabComplete
import net.minecraft.network.play.server.S3APacketTabComplete

@ModuleInfo(name = "AntiTabComplete", spacedName = "Anti Tab Complete", category = ModuleCategory.RENDER)
class AntiTabComplete : Module() {

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet

        if (packet is C14PacketTabComplete || packet is S3APacketTabComplete) {
            event.cancelEvent()
        }
    }

    init {
        state = true
    }
}
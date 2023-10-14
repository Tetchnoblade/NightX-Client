package net.aspw.client.features.module.impl.other

import net.aspw.client.event.EventTarget
import net.aspw.client.event.PacketEvent
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.minecraft.network.play.server.S2DPacketOpenWindow
import java.util.*

@ModuleInfo(name = "AntiFrozen", spacedName = "Anti Frozen", description = "", category = ModuleCategory.OTHER)
class AntiFrozen : Module() {

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet

        if (packet is S2DPacketOpenWindow && packet.windowTitle.unformattedText.lowercase(Locale.getDefault())
                .contains("frozen")
        )
            event.cancelEvent()
    }
}
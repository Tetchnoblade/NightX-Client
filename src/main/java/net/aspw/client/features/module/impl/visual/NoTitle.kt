package net.aspw.client.features.module.impl.visual

import net.aspw.client.event.EventTarget
import net.aspw.client.event.PacketEvent
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.minecraft.network.play.server.S45PacketTitle

@ModuleInfo(name = "NoTitle", spacedName = "No Title", category = ModuleCategory.VISUAL, array = false)
class NoTitle : Module() {
    @EventTarget
    fun onPacket(event: PacketEvent) {
        if (event.packet is S45PacketTitle)
            event.cancelEvent()
    }
}
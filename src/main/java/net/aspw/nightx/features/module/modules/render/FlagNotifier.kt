package net.aspw.nightx.features.module.modules.render

import net.aspw.nightx.event.EventTarget
import net.aspw.nightx.event.PacketEvent
import net.aspw.nightx.features.module.Module
import net.aspw.nightx.features.module.ModuleCategory
import net.aspw.nightx.features.module.ModuleInfo
import net.minecraft.network.play.server.S08PacketPlayerPosLook

@ModuleInfo(name = "FlagNotifier", spacedName = "Flag Notifier", category = ModuleCategory.RENDER)
class FlagNotifier : Module() {
    @EventTarget
    fun onPacket(event: PacketEvent) {
        if (event.packet is S08PacketPlayerPosLook) {
            chat("§fFlag")
        }
    }
}
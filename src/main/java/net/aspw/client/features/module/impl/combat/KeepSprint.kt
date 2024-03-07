package net.aspw.client.features.module.impl.combat

import net.aspw.client.event.PacketEvent
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.minecraft.network.play.client.C0BPacketEntityAction

@ModuleInfo(name = "KeepSprint", spacedName = "Keep Sprint", category = ModuleCategory.COMBAT)
class KeepSprint : Module() {
    fun onPacket(event: PacketEvent) {
        val packet = event.packet
        if (packet is C0BPacketEntityAction)
            if (packet.action == C0BPacketEntityAction.Action.STOP_SPRINTING)
                event.cancelEvent()
    }
}
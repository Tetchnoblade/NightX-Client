package net.aspw.nightx.features.module.modules.movement

import net.aspw.nightx.event.PacketEvent
import net.aspw.nightx.features.module.Module
import net.aspw.nightx.features.module.ModuleCategory
import net.aspw.nightx.features.module.ModuleInfo
import net.minecraft.network.play.client.C0BPacketEntityAction

@ModuleInfo(name = "KeepSprint", spacedName = "Keep Sprint", category = ModuleCategory.MOVEMENT)
class KeepSprint : Module() {
    fun onPacket(event: PacketEvent) {
        val packet = event.packet
        if (packet is C0BPacketEntityAction)
            if (packet.action == C0BPacketEntityAction.Action.STOP_SPRINTING)
                event.cancelEvent()
    }
}
package net.ccbluex.liquidbounce.features.module.modules.movement

import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
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
package net.aspw.client.features.module.impl.other

import net.aspw.client.event.EventTarget
import net.aspw.client.event.PacketEvent
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.minecraft.network.play.server.S14PacketEntity
import net.minecraft.network.play.server.S1DPacketEntityEffect

@ModuleInfo(name = "AntiVanish", spacedName = "Anti Vanish", category = ModuleCategory.OTHER, array = false)
class AntiVanish : Module() {

    private var lastNotify = -1L

    override fun onInitialize() {
        state = true
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        if (mc.theWorld == null || mc.thePlayer == null) return
        if (event.packet is S1DPacketEntityEffect) {
            if (mc.theWorld.getEntityByID(event.packet.entityId) == null) {
                vanish()
            }
        } else if (event.packet is S14PacketEntity) {
            if (event.packet.getEntity(mc.theWorld) == null) {
                vanish()
            }
        }
    }

    private fun vanish() {
        lastNotify = System.currentTimeMillis()
    }
}
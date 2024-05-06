package net.aspw.client.features.module.impl.other

import net.aspw.client.event.EventTarget
import net.aspw.client.event.PacketEvent
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.value.BoolValue
import net.minecraft.network.play.client.C00PacketKeepAlive
import net.minecraft.network.play.client.C0FPacketConfirmTransaction

@ModuleInfo(name = "PacketTracker", spacedName = "Packet Tracker", category = ModuleCategory.OTHER)
class PacketTracker : Module() {
    private val transactionIDValue = BoolValue("Transaction-ID", true)
    private val keepAliveKeyValue = BoolValue("KeepAlive-Key", false)

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet

        if (transactionIDValue.get() && packet is C0FPacketConfirmTransaction)
            chat("§c[Transaction ID]: §r" + packet.uid.toString())

        if (keepAliveKeyValue.get() && packet is C00PacketKeepAlive)
            chat("§c[KeepAlive Key]: §r" + packet.key.toString())
    }
}
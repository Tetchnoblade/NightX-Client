package net.aspw.client.features.module.impl.visual

import net.aspw.client.event.EventTarget
import net.aspw.client.event.PacketEvent
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.value.BoolValue
import net.minecraft.network.play.client.C03PacketPlayer

@ModuleInfo(name = "SilentView", spacedName = "Silent View", category = ModuleCategory.VISUAL, array = false)
class SilentView : Module() {
    val lockValue = BoolValue("Lock", false)
    var playerYaw: Float? = null

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val thePlayer = mc.thePlayer

        if (thePlayer == null) {
            playerYaw = null
            return
        }

        val packet = event.packet

        if (packet is C03PacketPlayer.C06PacketPlayerPosLook || packet is C03PacketPlayer.C05PacketPlayerLook) {
            val packetPlayer = packet as C03PacketPlayer

            playerYaw = packetPlayer.yaw

            thePlayer.rotationYawHead = packetPlayer.yaw
        } else {
            thePlayer.rotationYawHead = this.playerYaw!!
        }
    }

    init {
        state = true
    }
}
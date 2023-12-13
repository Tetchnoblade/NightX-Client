package net.aspw.client.features.module.impl.other

import net.aspw.client.Client
import net.aspw.client.event.EventTarget
import net.aspw.client.event.PacketEvent
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.util.ClientUtils
import net.aspw.client.value.BoolValue
import net.aspw.client.visual.hud.element.elements.Notification
import net.minecraft.network.play.server.S2CPacketSpawnGlobalEntity
import kotlin.math.roundToInt

@ModuleInfo(
    name = "ThunderNotifier",
    spacedName = "Thunder Notifier",
    description = "",
    category = ModuleCategory.OTHER
)
class ThunderNotifier : Module() {
    private val chatValue = BoolValue("Chat", true)
    private val notifyValue = BoolValue("Notification", false)

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet
        if (packet is S2CPacketSpawnGlobalEntity && packet.func_149053_g() == 1) {
            val x = packet.func_149051_d() / 32.0
            val y = packet.func_149050_e() / 32.0
            val z = packet.func_149049_f() / 32.0
            val dist = mc.thePlayer.getDistance(x, mc.thePlayer.entityBoundingBox.minY, z).roundToInt()

            if (chatValue.get())
                ClientUtils.displayChatMessage(Client.CLIENT_CHAT + "§fDetected thunder at ${x.toInt()} ${y.toInt()} ${z.toInt()} ($dist blocks away)")
            if (notifyValue.get())
                Client.hud.addNotification(
                    Notification(
                        "Detected thunder at ${x.toInt()} ${y.toInt()} ${z.toInt()} ($dist blocks away)",
                        Notification.Type.INFO,
                        3000L
                    )
                )
        }
    }
}
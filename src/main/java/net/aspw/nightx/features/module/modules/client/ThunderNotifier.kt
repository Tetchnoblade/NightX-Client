package net.aspw.nightx.features.module.modules.client

import net.aspw.nightx.NightX
import net.aspw.nightx.event.EventTarget
import net.aspw.nightx.event.PacketEvent
import net.aspw.nightx.features.module.Module
import net.aspw.nightx.features.module.ModuleCategory
import net.aspw.nightx.features.module.ModuleInfo
import net.aspw.nightx.utils.ClientUtils
import net.aspw.nightx.value.BoolValue
import net.aspw.nightx.visual.hud.element.elements.Notification
import net.minecraft.network.play.server.S2CPacketSpawnGlobalEntity
import kotlin.math.roundToInt

@ModuleInfo(name = "ThunderNotifier", spacedName = "Thunder Notifier", category = ModuleCategory.CLIENT)
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
                ClientUtils.displayChatMessage(NightX.CLIENT_CHAT + "§fDetected thunder at [§7X: $x, Y: $y, Z: $z§f]")
            if (notifyValue.get())
                NightX.hud.addNotification(
                    Notification(
                        "Detected thunder at [X: $x, Y: $y, Z: $z]",
                        Notification.Type.INFO,
                        3000L
                    )
                )
        }
    }

    init {
        state = true
    }
}
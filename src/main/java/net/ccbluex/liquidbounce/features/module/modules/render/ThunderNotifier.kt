package net.ccbluex.liquidbounce.features.module.modules.render

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.Notification
import net.ccbluex.liquidbounce.utils.ClientUtils
import net.ccbluex.liquidbounce.value.BoolValue
import net.minecraft.network.play.server.S2CPacketSpawnGlobalEntity
import kotlin.math.roundToInt

@ModuleInfo(name = "ThunderNotifier", description = "", category = ModuleCategory.RENDER)
class ThunderNotifier : Module() {
    val chatValue = BoolValue("Chat", true)
    val notifValue = BoolValue("Notification", false)

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet
        if (packet is S2CPacketSpawnGlobalEntity && packet.func_149053_g() == 1) {
            val x = packet.func_149051_d() / 32.0
            val y = packet.func_149050_e() / 32.0
            val z = packet.func_149049_f() / 32.0
            val dist = mc.thePlayer.getDistance(x, mc.thePlayer.entityBoundingBox.minY, z).roundToInt()

            if (chatValue.get())
                ClientUtils.displayChatMessage("§c>> §fDetected thunder at [§7X: $x, Y: $y, Z: $z§f]")

            if (notifValue.get())
                LiquidBounce.hud.addNotification(
                    Notification(
                        "Detected thunder at [X: $x, Y: $y, Z: $z]",
                        Notification.Type.WARNING,
                        3000L
                    )
                )
        }
    }

    init {
        state = true
    }
}
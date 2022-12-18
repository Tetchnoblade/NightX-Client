package net.aspw.nightx.features.module.modules.misc

import net.aspw.nightx.NightX
import net.aspw.nightx.event.EventTarget
import net.aspw.nightx.event.PacketEvent
import net.aspw.nightx.features.module.Module
import net.aspw.nightx.features.module.ModuleCategory
import net.aspw.nightx.features.module.ModuleInfo
import net.aspw.nightx.value.IntegerValue
import net.aspw.nightx.visual.hud.element.elements.Notification
import net.minecraft.network.play.server.S14PacketEntity
import net.minecraft.network.play.server.S1DPacketEntityEffect

@ModuleInfo(name = "AntiVanish", spacedName = "Anti Vanish", category = ModuleCategory.MISC)
class AntiVanish : Module() {
    private var lastNotify = -1L

    private val notifyLast = IntegerValue("Notification-Seconds", 2, 1, 30)

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
        if ((System.currentTimeMillis() - lastNotify) > 5000) {
            NightX.hud.addNotification(
                Notification(
                    "Found a vanished entity!",
                    Notification.Type.WARNING,
                    notifyLast.get().toLong() * 1000L
                )
            )
        }
        lastNotify = System.currentTimeMillis()

    }
}
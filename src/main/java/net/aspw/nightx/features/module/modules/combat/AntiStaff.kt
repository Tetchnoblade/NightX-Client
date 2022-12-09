package net.aspw.nightx.features.module.modules.combat

//import net.minecraft.network.play.server.S02PacketChat

import net.aspw.nightx.NightX
import net.aspw.nightx.event.EventTarget
import net.aspw.nightx.event.PacketEvent
import net.aspw.nightx.event.WorldEvent
import net.aspw.nightx.features.module.Module
import net.aspw.nightx.features.module.ModuleCategory
import net.aspw.nightx.features.module.ModuleInfo
import net.aspw.nightx.ui.client.hud.element.elements.Notification
import net.aspw.nightx.utils.timer.MSTimer
import net.minecraft.network.play.server.*
import kotlin.concurrent.thread

@ModuleInfo(name = "AntiStaff", spacedName = "Anti Staff", category = ModuleCategory.COMBAT)
class AntiStaff : Module() {

    private var obStaffs = "_"
    private var detected = false
    private var totalCount = 0
    private var finishedCheck = false

    private var updater = MSTimer()

    override fun onInitialize() {
        thread {
            totalCount = obStaffs.filter { it.isWhitespace() }.count()
            println("[Staff/fallback] ${obStaffs}")
        }
    }

    override fun onEnable() {
        detected = false
    }

    @EventTarget
    fun onWorld(e: WorldEvent) {
        detected = false
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        if (mc.theWorld == null || mc.thePlayer == null) return

        val packet = event.packet // smart convert
        if (packet is S1DPacketEntityEffect) {
            val entity = mc.theWorld.getEntityByID(packet.entityId)
            if (entity != null && (obStaffs.contains(entity.name) || obStaffs.contains(entity.displayName.unformattedText))) {
                if (!detected) {
                    NightX.hud.addNotification(Notification("${entity.name} / effect.", Notification.Type.ERROR))
                    mc.thePlayer.sendChatMessage("/leave")
                    detected = true
                }
            }
        }
        if (packet is S18PacketEntityTeleport) {
            val entity = mc.theWorld.getEntityByID(packet.entityId)
            if (entity != null && (obStaffs.contains(entity.name) || obStaffs.contains(entity.displayName.unformattedText))) {
                if (!detected) {
                    NightX.hud.addNotification(
                        Notification(
                            "${entity.name} / teleport.",
                            Notification.Type.ERROR
                        )
                    )
                    mc.thePlayer.sendChatMessage("/leave")
                    detected = true
                }
            }
        }
        if (packet is S20PacketEntityProperties) {
            val entity = mc.theWorld.getEntityByID(packet.entityId)
            if (entity != null && (obStaffs.contains(entity.name) || obStaffs.contains(entity.displayName.unformattedText))) {
                if (!detected) {
                    NightX.hud.addNotification(
                        Notification(
                            "${entity.name} / properties.",
                            Notification.Type.ERROR
                        )
                    )
                    mc.thePlayer.sendChatMessage("/leave")
                    detected = true
                }
            }
        }
        if (packet is S0BPacketAnimation) {
            val entity = mc.theWorld.getEntityByID(packet.entityID)
            if (entity != null && (obStaffs.contains(entity.name) || obStaffs.contains(entity.displayName.unformattedText))) {
                if (!detected) {
                    NightX.hud.addNotification(
                        Notification(
                            "${entity.name} / animation.",
                            Notification.Type.ERROR
                        )
                    )
                    mc.thePlayer.sendChatMessage("/leave")
                    detected = true
                }
            }
        }
        if (packet is S14PacketEntity) {
            val entity = packet.getEntity(mc.theWorld)

            if (entity != null && (obStaffs.contains(entity.name) || obStaffs.contains(entity.displayName.unformattedText))) {
                if (!detected) {
                    NightX.hud.addNotification(Notification("${entity.name} / update.", Notification.Type.ERROR))
                    mc.thePlayer.sendChatMessage("/leave")
                    detected = true
                }
            }
        }
        if (packet is S19PacketEntityStatus) {
            val entity = packet.getEntity(mc.theWorld)

            if (entity != null && (obStaffs.contains(entity.name) || obStaffs.contains(entity.displayName.unformattedText))) {
                if (!detected) {
                    NightX.hud.addNotification(Notification("${entity.name} / status.", Notification.Type.ERROR))
                    mc.thePlayer.sendChatMessage("/leave")
                    detected = true
                }
            }
        }
        if (packet is S19PacketEntityHeadLook) {
            val entity = packet.getEntity(mc.theWorld)

            if (entity != null && (obStaffs.contains(entity.name) || obStaffs.contains(entity.displayName.unformattedText))) {
                if (!detected) {
                    NightX.hud.addNotification(Notification("${entity.name} / head.", Notification.Type.ERROR))
                    mc.thePlayer.sendChatMessage("/leave")
                    detected = true
                }
            }
        }
        if (packet is S49PacketUpdateEntityNBT) {
            val entity = packet.getEntity(mc.theWorld)

            if (entity != null && (obStaffs.contains(entity.name) || obStaffs.contains(entity.displayName.unformattedText))) {
                if (!detected) {
                    NightX.hud.addNotification(Notification("${entity.name} / nbt.", Notification.Type.ERROR))
                    mc.thePlayer.sendChatMessage("/leave")
                    detected = true
                }
            }
        }
    }
}
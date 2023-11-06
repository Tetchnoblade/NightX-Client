package net.aspw.client.features.module.impl.combat

//import net.minecraft.network.play.server.S02PacketChat

import net.aspw.client.Client
import net.aspw.client.event.EventTarget
import net.aspw.client.event.PacketEvent
import net.aspw.client.event.UpdateEvent
import net.aspw.client.event.WorldEvent
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.util.EntityUtils
import net.aspw.client.util.network.CheckConnection
import net.aspw.client.util.render.ColorUtils
import net.aspw.client.value.BoolValue
import net.aspw.client.value.ListValue
import net.aspw.client.visual.hud.element.elements.Notification
import net.minecraft.network.play.server.*
import kotlin.concurrent.thread

@ModuleInfo(name = "AntiStaff", spacedName = "Anti Staff", description = "", category = ModuleCategory.COMBAT)
class AntiStaff : Module() {
    private val modeValue = ListValue("Mode", arrayOf("Anywhere", "BlocksMC"), "Anywhere")
    private val leaveValue = BoolValue("Leave", true) { modeValue.get().equals("blocksmc", true) }

    private var obStaffs = "_"
    private var detected = false
    private var totalCount = 0

    private var staffs = mutableListOf<String>()
    private var staffsInWorld = mutableListOf<String>()

    override val tag: String
        get() = modeValue.get()

    override fun onEnable() {
        thread {
            totalCount = obStaffs.count { it.isWhitespace() }
            staffs.addAll(CheckConnection.stafflist.split(","))
        }
        detected = false
        staffsInWorld.clear()
    }

    @EventTarget
    fun onWorld(e: WorldEvent) {
        detected = false
        staffsInWorld.clear()
    }

    private fun warn(name: String) {
        if (name in staffsInWorld)
            return
        Client.hud.addNotification(Notification("Staff Detected!", Notification.Type.INFO))
        if (leaveValue.get())
            mc.thePlayer.sendChatMessage("/leave")
        staffsInWorld.add(name)
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (modeValue.get().equals("blocksmc", true)) {
            for (networkPlayerInfo in mc.netHandler.playerInfoMap) {
                val networkName = ColorUtils.stripColor(EntityUtils.getName(networkPlayerInfo)) ?: continue
                if (networkName in staffs)
                    warn(networkName)
            }
        }
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        if (mc.theWorld == null || mc.thePlayer == null) return
        val packet = event.packet

        when (modeValue.get().lowercase()) {
            "anywhere" -> {
                if (mc.thePlayer.ticksExisted % 3 == 0 && detected) detected = false
                if (packet is S1DPacketEntityEffect) {
                    val entity = mc.theWorld.getEntityByID(packet.entityId)
                    if (entity != null && (obStaffs.contains(entity.name) || obStaffs.contains(entity.displayName.unformattedText))) {
                        if (!detected) {
                            Client.hud.addNotification(Notification("Staff Detected!", Notification.Type.INFO))
                            detected = true
                        }
                    }
                }
                if (packet is S18PacketEntityTeleport) {
                    val entity = mc.theWorld.getEntityByID(packet.entityId)
                    if (entity != null && (obStaffs.contains(entity.name) || obStaffs.contains(entity.displayName.unformattedText))) {
                        if (!detected) {
                            Client.hud.addNotification(Notification("Staff Detected!", Notification.Type.INFO))
                            detected = true
                        }
                    }
                }
                if (packet is S20PacketEntityProperties) {
                    val entity = mc.theWorld.getEntityByID(packet.entityId)
                    if (entity != null && (obStaffs.contains(entity.name) || obStaffs.contains(entity.displayName.unformattedText))) {
                        if (!detected) {
                            Client.hud.addNotification(Notification("Staff Detected!", Notification.Type.INFO))
                            detected = true
                        }
                    }
                }
                if (packet is S0BPacketAnimation) {
                    val entity = mc.theWorld.getEntityByID(packet.entityID)
                    if (entity != null && (obStaffs.contains(entity.name) || obStaffs.contains(entity.displayName.unformattedText))) {
                        if (!detected) {
                            Client.hud.addNotification(Notification("Staff Detected!", Notification.Type.INFO))
                            detected = true
                        }
                    }
                }
                if (packet is S14PacketEntity) {
                    val entity = packet.getEntity(mc.theWorld)

                    if (entity != null && (obStaffs.contains(entity.name) || obStaffs.contains(entity.displayName.unformattedText))) {
                        if (!detected) {
                            Client.hud.addNotification(Notification("Staff Detected!", Notification.Type.INFO))
                            detected = true
                        }
                    }
                }
                if (packet is S19PacketEntityStatus) {
                    val entity = packet.getEntity(mc.theWorld)

                    if (entity != null && (obStaffs.contains(entity.name) || obStaffs.contains(entity.displayName.unformattedText))) {
                        if (!detected) {
                            Client.hud.addNotification(Notification("Staff Detected!", Notification.Type.INFO))
                            detected = true
                        }
                    }
                }
                if (packet is S19PacketEntityHeadLook) {
                    val entity = packet.getEntity(mc.theWorld)

                    if (entity != null && (obStaffs.contains(entity.name) || obStaffs.contains(entity.displayName.unformattedText))) {
                        if (!detected) {
                            Client.hud.addNotification(Notification("Staff Detected!", Notification.Type.INFO))
                            detected = true
                        }
                    }
                }
                if (packet is S49PacketUpdateEntityNBT) {
                    val entity = packet.getEntity(mc.theWorld)

                    if (entity != null && (obStaffs.contains(entity.name) || obStaffs.contains(entity.displayName.unformattedText))) {
                        if (!detected) {
                            Client.hud.addNotification(Notification("Staff Detected!", Notification.Type.INFO))
                            detected = true
                        }
                    }
                }
            }

            "blocksmc" -> {
                if (packet is S1DPacketEntityEffect) {
                    val entity = mc.theWorld.getEntityByID(packet.entityId) ?: return
                    if (staffs.contains(entity.name) || staffs.contains(entity.displayName.unformattedText)) {
                        warn(entity.name)
                    }
                }
                if (packet is S18PacketEntityTeleport) {
                    val entity = mc.theWorld.getEntityByID(packet.entityId) ?: return
                    if (staffs.contains(entity.name) || staffs.contains(entity.displayName.unformattedText)) {
                        warn(entity.name)
                    }
                }
                if (packet is S20PacketEntityProperties) {
                    val entity = mc.theWorld.getEntityByID(packet.entityId) ?: return
                    if (staffs.contains(entity.name) || staffs.contains(entity.displayName.unformattedText)) {
                        warn(entity.name)
                    }
                }
                if (packet is S0BPacketAnimation) {
                    val entity = mc.theWorld.getEntityByID(packet.entityID) ?: return
                    if (staffs.contains(entity.name) || staffs.contains(entity.displayName.unformattedText)) {
                        warn(entity.name)
                    }
                }
                if (packet is S14PacketEntity) {
                    val entity = packet.getEntity(mc.theWorld) ?: return
                    if (staffs.contains(entity.name) || staffs.contains(entity.displayName.unformattedText)) {
                        warn(entity.name)
                    }
                }
                if (packet is S19PacketEntityStatus) {
                    val entity = packet.getEntity(mc.theWorld) ?: return
                    if (staffs.contains(entity.name) || staffs.contains(entity.displayName.unformattedText)) {
                        warn(entity.name)

                    }
                }
                if (packet is S19PacketEntityHeadLook) {
                    val entity = packet.getEntity(mc.theWorld) ?: return
                    if (staffs.contains(entity.name) || staffs.contains(entity.displayName.unformattedText)) {
                        warn(entity.name)
                    }
                }
                if (packet is S49PacketUpdateEntityNBT) {
                    val entity = packet.getEntity(mc.theWorld) ?: return
                    if (staffs.contains(entity.name) || staffs.contains(entity.displayName.unformattedText)) {
                        warn(entity.name)
                    }
                }
            }
        }
    }
}
package net.aspw.client.features.module.impl.other

import net.aspw.client.Client
import net.aspw.client.event.EventTarget
import net.aspw.client.event.PacketEvent
import net.aspw.client.event.UpdateEvent
import net.aspw.client.event.WorldEvent
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.util.EntityUtils
import net.aspw.client.util.network.Access
import net.aspw.client.util.render.ColorUtils
import net.aspw.client.value.BoolValue
import net.aspw.client.value.ListValue
import net.aspw.client.visual.hud.element.elements.Notification
import net.minecraft.entity.Entity
import net.minecraft.network.play.server.*
import kotlin.concurrent.thread

@ModuleInfo(
    name = "StaffProtection",
    spacedName = "Staff Protection",
    description = "",
    category = ModuleCategory.OTHER
)
class StaffProtection : Module() {
    private val modeValue = ListValue("Mode", arrayOf("BlocksMC", "MushMC"), "BlocksMC")
    private val leaveValue = BoolValue("Leave", true) { modeValue.get().equals("blocksmc", true) }

    private var obStaffs = "_"
    private var detected = false
    private var totalCount = 0

    private var staffs = mutableListOf<String>()
    private var staffsInWorld = mutableListOf<String>()

    private var mushmcstaffs = mutableListOf<String>()

    override val tag: String
        get() = modeValue.get()

    override fun onEnable() {
        thread {
            totalCount = obStaffs.count { it.isWhitespace() }
            staffs.addAll(Access.bmcstafflist.split(","))
            mushmcstaffs.addAll(Access.mushstafflist.split(","))
        }
        detected = false
        staffsInWorld.clear()
    }

    @EventTarget
    fun onWorld(event: WorldEvent) {
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

    private fun isStaff(entity: Entity): Boolean {
        when (modeValue.get().lowercase()) {
            "blocksmc" -> {
                return entity.name in staffs || entity.displayName.unformattedText in staffs
            }

            "mushmc" -> {
                return entity.name in mushmcstaffs || entity.displayName.unformattedText in mushmcstaffs
            }
        }

        return false
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (mc.thePlayer.ticksExisted % 3 == 0 && detected) detected = false

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
            "blocksmc" -> {
                when (packet) {
                    is S0CPacketSpawnPlayer -> {
                        val entity = mc.theWorld.getEntityByID(packet.entityID) ?: return
                        if (isStaff(entity))
                            warn(entity.name)
                    }

                    is S1EPacketRemoveEntityEffect -> {
                        val entity = mc.theWorld.getEntityByID(packet.entityId) ?: return
                        if (isStaff(entity))
                            warn(entity.name)
                    }

                    is S01PacketJoinGame -> {
                        val entity = mc.theWorld.getEntityByID(packet.entityId) ?: return
                        if (isStaff(entity))
                            warn(entity.name)
                    }

                    is S04PacketEntityEquipment -> {
                        val entity = mc.theWorld.getEntityByID(packet.entityID) ?: return
                        if (isStaff(entity))
                            warn(entity.name)
                    }

                    is S1CPacketEntityMetadata -> {
                        val entity = mc.theWorld.getEntityByID(packet.entityId) ?: return
                        if (isStaff(entity))
                            warn(entity.name)
                    }

                    is S1DPacketEntityEffect -> {
                        val entity = mc.theWorld.getEntityByID(packet.entityId) ?: return
                        if (isStaff(entity))
                            warn(entity.name)
                    }

                    is S18PacketEntityTeleport -> {
                        val entity = mc.theWorld.getEntityByID(packet.entityId) ?: return
                        if (isStaff(entity))
                            warn(entity.name)
                    }

                    is S20PacketEntityProperties -> {
                        val entity = mc.theWorld.getEntityByID(packet.entityId) ?: return
                        if (isStaff(entity))
                            warn(entity.name)
                    }

                    is S0BPacketAnimation -> {
                        val entity = mc.theWorld.getEntityByID(packet.entityID) ?: return
                        if (isStaff(entity))
                            warn(entity.name)
                    }

                    is S14PacketEntity -> {
                        val entity = packet.getEntity(mc.theWorld) ?: return
                        if (isStaff(entity))
                            warn(entity.name)
                    }

                    is S19PacketEntityStatus -> {
                        val entity = packet.getEntity(mc.theWorld) ?: return
                        if (isStaff(entity))
                            warn(entity.name)
                    }

                    is S19PacketEntityHeadLook -> {
                        val entity = packet.getEntity(mc.theWorld) ?: return
                        if (isStaff(entity))
                            warn(entity.name)
                    }

                    is S49PacketUpdateEntityNBT -> {
                        val entity = packet.getEntity(mc.theWorld) ?: return
                        if (isStaff(entity))
                            warn(entity.name)
                    }
                }
            }

            "mushmc" -> {
                when (packet) {
                    is S0CPacketSpawnPlayer -> {
                        val entity = mc.theWorld.getEntityByID(packet.entityID) ?: return
                        if (isStaff(entity))
                            warn(entity.name)
                    }

                    is S1EPacketRemoveEntityEffect -> {
                        val entity = mc.theWorld.getEntityByID(packet.entityId) ?: return
                        if (isStaff(entity))
                            warn(entity.name)
                    }

                    is S01PacketJoinGame -> {
                        val entity = mc.theWorld.getEntityByID(packet.entityId) ?: return
                        if (isStaff(entity))
                            warn(entity.name)
                    }

                    is S04PacketEntityEquipment -> {
                        val entity = mc.theWorld.getEntityByID(packet.entityID) ?: return
                        if (isStaff(entity))
                            warn(entity.name)
                    }

                    is S1CPacketEntityMetadata -> {
                        val entity = mc.theWorld.getEntityByID(packet.entityId) ?: return
                        if (isStaff(entity))
                            warn(entity.name)
                    }

                    is S1DPacketEntityEffect -> {
                        val entity = mc.theWorld.getEntityByID(packet.entityId) ?: return
                        if (isStaff(entity))
                            warn(entity.name)
                    }

                    is S18PacketEntityTeleport -> {
                        val entity = mc.theWorld.getEntityByID(packet.entityId) ?: return
                        if (isStaff(entity))
                            warn(entity.name)
                    }

                    is S20PacketEntityProperties -> {
                        val entity = mc.theWorld.getEntityByID(packet.entityId) ?: return
                        if (isStaff(entity))
                            warn(entity.name)
                    }

                    is S0BPacketAnimation -> {
                        val entity = mc.theWorld.getEntityByID(packet.entityID) ?: return
                        if (isStaff(entity))
                            warn(entity.name)
                    }

                    is S14PacketEntity -> {
                        val entity = packet.getEntity(mc.theWorld) ?: return
                        if (isStaff(entity))
                            warn(entity.name)
                    }

                    is S19PacketEntityStatus -> {
                        val entity = packet.getEntity(mc.theWorld) ?: return
                        if (isStaff(entity))
                            warn(entity.name)
                    }

                    is S19PacketEntityHeadLook -> {
                        val entity = packet.getEntity(mc.theWorld) ?: return
                        if (isStaff(entity))
                            warn(entity.name)
                    }

                    is S49PacketUpdateEntityNBT -> {
                        val entity = packet.getEntity(mc.theWorld) ?: return
                        if (isStaff(entity))
                            warn(entity.name)
                    }
                }
            }
        }
    }
}
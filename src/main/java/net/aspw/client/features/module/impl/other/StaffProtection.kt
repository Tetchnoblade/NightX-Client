package net.aspw.client.features.module.impl.other

import net.aspw.client.event.EventTarget
import net.aspw.client.event.PacketEvent
import net.aspw.client.event.UpdateEvent
import net.aspw.client.event.WorldEvent
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.utils.Access
import net.aspw.client.utils.EntityUtils
import net.aspw.client.utils.render.ColorUtils
import net.aspw.client.value.BoolValue
import net.aspw.client.value.ListValue
import net.minecraft.entity.Entity
import net.minecraft.network.play.server.S01PacketJoinGame
import net.minecraft.network.play.server.S04PacketEntityEquipment
import net.minecraft.network.play.server.S0BPacketAnimation
import net.minecraft.network.play.server.S0CPacketSpawnPlayer
import net.minecraft.network.play.server.S14PacketEntity
import net.minecraft.network.play.server.S18PacketEntityTeleport
import net.minecraft.network.play.server.S19PacketEntityHeadLook
import net.minecraft.network.play.server.S19PacketEntityStatus
import net.minecraft.network.play.server.S1CPacketEntityMetadata
import net.minecraft.network.play.server.S1DPacketEntityEffect
import net.minecraft.network.play.server.S1EPacketRemoveEntityEffect
import net.minecraft.network.play.server.S20PacketEntityProperties
import net.minecraft.network.play.server.S49PacketUpdateEntityNBT
import kotlin.concurrent.thread

@ModuleInfo(
    name = "StaffProtection",
    spacedName = "Staff Protection",
    category = ModuleCategory.OTHER
)
class StaffProtection : Module() {
    private val modeValue = ListValue("Mode", arrayOf("Hypixel", "BlocksMC", "MushMC"), "Hypixel")
    private val leaveValue = BoolValue("Leave", true)

    private var obStaffs = "_"
    private var detected = false
    private var totalCount = 0

    private var staffsInWorld = mutableListOf<String>()

    private var blocksmcstaffs = mutableListOf<String>()
    private var mushmcstaffs = mutableListOf<String>()
    private var hypixelstaffs = mutableListOf<String>()

    override val tag: String
        get() = modeValue.get()

    override fun onEnable() {
        thread {
            totalCount = obStaffs.count { it.isWhitespace() }
            blocksmcstaffs.addAll(Access.bmcstafflist.split(","))
            mushmcstaffs.addAll(Access.mushstafflist.split(","))
            hypixelstaffs.addAll(Access.hypixelstafflist.split(","))
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
        chat("Staff Detected!")
        if (leaveValue.get())
            mc.thePlayer.sendChatMessage("/leave")
        staffsInWorld.add(name)
    }

    private fun isStaff(entity: Entity): Boolean {
        when (modeValue.get().lowercase()) {
            "blocksmc" -> {
                return entity.name in blocksmcstaffs || entity.displayName.unformattedText in blocksmcstaffs
            }

            "mushmc" -> {
                return entity.name in mushmcstaffs || entity.displayName.unformattedText in mushmcstaffs
            }

            "hypixel" -> {
                return entity.name in hypixelstaffs || entity.displayName.unformattedText in hypixelstaffs
            }
        }

        return false
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (mc.thePlayer.ticksExisted % 3 == 0 && detected) detected = false

        for (networkPlayerInfo in mc.netHandler.playerInfoMap) {
            val networkName = ColorUtils.stripColor(EntityUtils.getName(networkPlayerInfo)) ?: continue
            when (modeValue.get().lowercase()) {
                "blocksmc" -> {
                    if (networkName in blocksmcstaffs)
                        warn(networkName)
                }

                "mushmc" -> {
                    if (networkName in mushmcstaffs)
                        warn(networkName)
                }

                "hypixel" -> {
                    if (networkName in hypixelstaffs)
                        warn(networkName)
                }
            }
        }
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        if (mc.theWorld == null || mc.thePlayer == null) return

        when (val packet = event.packet) {
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
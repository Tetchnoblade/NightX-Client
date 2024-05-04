package net.aspw.client.features.module.impl.other

import net.aspw.client.event.EventTarget
import net.aspw.client.event.PacketEvent
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.utils.PacketUtils
import net.aspw.client.utils.pathfinder.MainPathFinder
import net.aspw.client.utils.pathfinder.Vec3
import net.aspw.client.value.BoolValue
import net.minecraft.network.play.client.C02PacketUseEntity
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition
import net.minecraft.network.play.client.C07PacketPlayerDigging
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement
import net.minecraft.util.BlockPos
import kotlin.math.max

@ModuleInfo(
    name = "InfiniteReach",
    spacedName = "Infinite Reach",
    category = ModuleCategory.OTHER
)
class InfiniteReach : Module() {
    private val attackValue = BoolValue("Attack", true)
    private val breakValue = BoolValue("Break", true)
    private val placeValue = BoolValue("Place", true)

    val maxRange: Float
        get() = max(200f, 200f)

    @EventTarget
    fun onPacket(event: PacketEvent) {
        if (mc.thePlayer == null) return
        val packet = event.packet

        val x = mc.thePlayer.posX
        val y = mc.thePlayer.posY
        val z = mc.thePlayer.posZ

        if (attackValue.get() && packet is C02PacketUseEntity) {
            event.cancelEvent()
            val position = packet.getEntityFromWorld(mc.theWorld).position
            val actualPos = BlockPos(position.x.toDouble(), position.y - 0.5, position.z.toDouble())
            Thread {
                val path: ArrayList<Vec3> = MainPathFinder.computePath(
                    Vec3(x, y, z),
                    Vec3(actualPos.x.toDouble(), actualPos.y.toDouble(), actualPos.z.toDouble())
                )
                for (vec in path) PacketUtils.sendPacketNoEvent(
                    C04PacketPlayerPosition(
                        vec.x,
                        vec.y,
                        vec.z,
                        true
                    )
                )
                PacketUtils.sendPacketNoEvent(packet)
                path.reverse()
                for (vec in path) PacketUtils.sendPacketNoEvent(
                    C04PacketPlayerPosition(
                        vec.x,
                        vec.y,
                        vec.z,
                        true
                    )
                )
            }.start()
        }

        if (breakValue.get() && packet is C07PacketPlayerDigging) {
            if (!(packet.status == C07PacketPlayerDigging.Action.DROP_ITEM || packet.status == C07PacketPlayerDigging.Action.DROP_ALL_ITEMS)) {
                event.cancelEvent()
                val pos = packet.position
                Thread {
                    val path: ArrayList<Vec3> =
                        MainPathFinder.computePath(
                            Vec3(x, y, z),
                            Vec3(pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble())
                        )
                    for (vec in path) PacketUtils.sendPacketNoEvent(
                        C04PacketPlayerPosition(
                            vec.x,
                            vec.y,
                            vec.z,
                            true
                        )
                    )
                    PacketUtils.sendPacketNoEvent(packet)
                    path.reverse()
                    for (vec in path) PacketUtils.sendPacketNoEvent(
                        C04PacketPlayerPosition(
                            vec.x,
                            vec.y,
                            vec.z,
                            true
                        )
                    )
                }.start()
            }
        }

        if (placeValue.get() && packet is C08PacketPlayerBlockPlacement) {
            event.cancelEvent()
            val pos = packet.position
            Thread {
                val path: ArrayList<Vec3> =
                    MainPathFinder.computePath(
                        Vec3(x, y, z),
                        Vec3(pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble())
                    )
                for (vec in path) PacketUtils.sendPacketNoEvent(
                    C04PacketPlayerPosition(
                        vec.x,
                        vec.y,
                        vec.z,
                        true
                    )
                )
                PacketUtils.sendPacketNoEvent(packet)
                path.reverse()
                for (vec in path) PacketUtils.sendPacketNoEvent(
                    C04PacketPlayerPosition(
                        vec.x,
                        vec.y,
                        vec.z,
                        true
                    )
                )
            }.start()
        }
    }
}
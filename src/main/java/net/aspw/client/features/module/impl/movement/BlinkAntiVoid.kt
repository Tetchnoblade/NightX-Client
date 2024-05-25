package net.aspw.client.features.module.impl.movement

import net.aspw.client.event.EventTarget
import net.aspw.client.event.PacketEvent
import net.aspw.client.event.Render3DEvent
import net.aspw.client.event.TeleportEvent
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.utils.PredictUtils
import net.aspw.client.utils.timer.TickTimer
import net.minecraft.block.BlockAir
import net.minecraft.network.Packet
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.util.BlockPos
import java.util.concurrent.LinkedBlockingQueue
import kotlin.math.abs

@ModuleInfo(name = "BlinkAntiVoid", spacedName = "Blink Anti Void", category = ModuleCategory.MOVEMENT)
class BlinkAntiVoid : Module() {

    private var packets = LinkedBlockingQueue<Packet<*>>()
    private var safeTimer = TickTimer()
    private var togglePrevent = false
    private var disableLogger = false
    private var preX: Double? = null
    private var preY: Double? = null
    private var preZ: Double? = null
    private var preYaw: Float? = null
    private var prePitch: Float? = null

    override fun onDisable() {
        reset()
    }

    @EventTarget
    fun onRender3D(event: Render3DEvent) {
        togglePrevent = PredictUtils.checkVoid(7)
        if (togglePrevent) {
            if (abs(mc.thePlayer.posY - preY!!) > 8) {
                mc.thePlayer.setPositionAndRotation(preX!!, preY!!, preZ!!, preYaw!!, prePitch!!)
                mc.thePlayer.motionX = 0.0
                mc.thePlayer.motionY = 0.0
                mc.thePlayer.motionZ = 0.0
                reset()
            } else if (safeTimer.hasTimePassed(20) || shouldSync(0.8f) || shouldSync(1.8f))
                sync()
        } else sync()
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet
        if (mc.thePlayer == null || disableLogger || !togglePrevent) return
        if (packet is C03PacketPlayer) {
            if (preX == null)
                preX = packet.x
            if (preY == null)
                preY = packet.y
            if (preZ == null)
                preZ = packet.z
            if (preYaw == null)
                preYaw = packet.yaw
            if (prePitch == null)
                prePitch = packet.pitch
            packets.add(packet)
            safeTimer.update()
            event.cancelEvent()
        }
    }

    @EventTarget
    fun onTeleport(event: TeleportEvent) {
        if (togglePrevent) {
            if (preX == null)
                preX = event.posX
            if (preY == null)
                preY = event.posY
            if (preZ == null)
                preZ = event.posZ
            if (preYaw == null)
                preYaw = event.yaw
            if (prePitch == null)
                prePitch = event.pitch
        }
    }

    private fun shouldSync(y: Float): Boolean {
        return mc.theWorld.getBlockState(
            BlockPos(
                mc.thePlayer.posX,
                mc.thePlayer.posY - y,
                mc.thePlayer.posZ
            )
        ).block !is BlockAir || mc.theWorld.getBlockState(
            BlockPos(
                mc.thePlayer.posX + 0.2,
                mc.thePlayer.posY - y,
                mc.thePlayer.posZ + 0.2
            )
        ).block !is BlockAir || mc.theWorld.getBlockState(
            BlockPos(
                mc.thePlayer.posX + 0.2,
                mc.thePlayer.posY - y,
                mc.thePlayer.posZ - 0.2
            )
        ).block !is BlockAir || mc.theWorld.getBlockState(
            BlockPos(
                mc.thePlayer.posX - 0.2,
                mc.thePlayer.posY - y,
                mc.thePlayer.posZ + 0.2
            )
        ).block !is BlockAir || mc.theWorld.getBlockState(
            BlockPos(
                mc.thePlayer.posX - 0.2,
                mc.thePlayer.posY - y,
                mc.thePlayer.posZ - 0.2
            )
        ).block !is BlockAir
    }

    private fun sync() {
        try {
            disableLogger = true
            while (packets.isNotEmpty()) {
                mc.netHandler.networkManager.sendPacket(packets.take())
            }
            reset()
        } catch (_: Exception) {
        }
    }

    private fun reset() {
        if (packets.isNotEmpty())
            packets.clear()
        safeTimer.reset()
        togglePrevent = false
        disableLogger = false
        preX = null
        preY = null
        preZ = null
        preYaw = null
        prePitch = null
    }
}
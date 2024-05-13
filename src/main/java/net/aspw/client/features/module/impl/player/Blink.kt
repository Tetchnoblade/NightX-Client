package net.aspw.client.features.module.impl.player

import net.aspw.client.event.EventTarget
import net.aspw.client.event.PacketEvent
import net.aspw.client.event.Render3DEvent
import net.aspw.client.event.UpdateEvent
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.utils.render.RenderUtils
import net.aspw.client.utils.timer.MSTimer
import net.aspw.client.value.BoolValue
import net.aspw.client.value.IntegerValue
import net.minecraft.client.entity.EntityOtherPlayerMP
import net.minecraft.network.Packet
import net.minecraft.network.play.client.*
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition
import net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook
import net.minecraft.util.Vec3
import java.util.*
import java.util.concurrent.LinkedBlockingQueue

@ModuleInfo(name = "Blink", category = ModuleCategory.PLAYER)
class Blink : Module() {
    private val pulseValue = BoolValue("Pulse", true)
    private val packets = LinkedBlockingQueue<Packet<*>>()
    private val positions = LinkedList<Vec3>()
    private val c0FValue = BoolValue("C0FCancel", false)
    private val pulseDelayValue = IntegerValue("PulseDelay", 400, 10, 5000, "ms") { pulseValue.get() }
    private val pulseTimer = MSTimer()
    private var fakePlayer: EntityOtherPlayerMP? = null
    private var disableLogger = false

    override fun onEnable() {
        if (mc.thePlayer == null) return
        fakePlayer = EntityOtherPlayerMP(mc.theWorld, mc.thePlayer.gameProfile)
        fakePlayer!!.clonePlayer(mc.thePlayer, true)
        fakePlayer!!.copyLocationAndAnglesFrom(mc.thePlayer)
        fakePlayer!!.rotationYawHead = mc.thePlayer.rotationYawHead
        mc.theWorld.addEntityToWorld(-1337, fakePlayer)
        synchronized(positions) {
            positions.add(
                Vec3(
                    mc.thePlayer.posX,
                    mc.thePlayer.entityBoundingBox.minY + mc.thePlayer.getEyeHeight() / 2,
                    mc.thePlayer.posZ
                )
            )
            positions.add(Vec3(mc.thePlayer.posX, mc.thePlayer.entityBoundingBox.minY, mc.thePlayer.posZ))
        }
        pulseTimer.reset()
    }

    override fun onDisable() {
        if (mc.thePlayer == null) return
        blink()
        if (fakePlayer != null) {
            mc.theWorld.removeEntityFromWorld(fakePlayer!!.entityId)
            fakePlayer = null
        }
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet
        if (mc.thePlayer == null || disableLogger) return
        if (packet is C03PacketPlayer)
            event.cancelEvent()
        if (packet is C04PacketPlayerPosition || packet is C06PacketPlayerPosLook ||
            packet is C08PacketPlayerBlockPlacement ||
            packet is C0APacketAnimation ||
            packet is C0BPacketEntityAction || packet is C02PacketUseEntity || c0FValue.get() && packet is C0FPacketConfirmTransaction
        ) {
            event.cancelEvent()
            packets.add(packet)
        }
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent?) {
        synchronized(positions) {
            positions.add(
                Vec3(
                    mc.thePlayer.posX,
                    mc.thePlayer.entityBoundingBox.minY,
                    mc.thePlayer.posZ
                )
            )
        }
        if (pulseValue.get() && pulseTimer.hasTimePassed(pulseDelayValue.get().toLong())) {
            fakePlayer = EntityOtherPlayerMP(mc.theWorld, mc.thePlayer.gameProfile)
            fakePlayer!!.clonePlayer(mc.thePlayer, true)
            fakePlayer!!.copyLocationAndAnglesFrom(mc.thePlayer)
            fakePlayer!!.rotationYawHead = mc.thePlayer.rotationYawHead
            mc.theWorld.addEntityToWorld(-1337, fakePlayer)
            blink()
            pulseTimer.reset()
        }
    }

    @EventTarget
    fun onRender3D(event: Render3DEvent?) {
        RenderUtils.renderLine(positions)
    }

    override val tag: String
        get() = packets.size.toString()

    private fun blink() {
        try {
            disableLogger = true
            while (!packets.isEmpty()) {
                mc.netHandler.networkManager.sendPacket(packets.take())
            }
            disableLogger = false
        } catch (e: Exception) {
            e.printStackTrace()
            disableLogger = false
        }
        synchronized(positions) { positions.clear() }
    }
}
package net.aspw.client.features.module.impl.movement

import net.aspw.client.event.EventTarget
import net.aspw.client.event.PacketEvent
import net.aspw.client.event.Render3DEvent
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.utils.PredictUtils
import net.aspw.client.utils.timer.TickTimer
import net.minecraft.network.Packet
import net.minecraft.network.play.client.C03PacketPlayer
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
        togglePrevent = PredictUtils.checkVoid(5)
        if (togglePrevent) {
            if (abs(mc.thePlayer.posY - preY!!) > 8) {
                mc.thePlayer.setPositionAndRotation(preX!!, preY!!, preZ!!, preYaw!!, prePitch!!)
                mc.thePlayer.motionX = 0.0
                mc.thePlayer.motionY = 0.0
                mc.thePlayer.motionZ = 0.0
                reset()
            } else if (safeTimer.hasTimePassed(60) || mc.thePlayer.onGround) {
                sync()
            }
        } else sync()
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet
        if (mc.thePlayer == null || disableLogger) return
        if (packet is C03PacketPlayer && togglePrevent) {
            if (preX == null)
                preX = mc.thePlayer.posX
            if (preY == null)
                preY = mc.thePlayer.posY
            if (preZ == null)
                preZ = mc.thePlayer.posZ
            if (preYaw == null)
                preYaw = mc.thePlayer.rotationYaw
            if (prePitch == null)
                prePitch = mc.thePlayer.rotationPitch
            packets.add(packet)
            safeTimer.update()
            event.cancelEvent()
        }
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
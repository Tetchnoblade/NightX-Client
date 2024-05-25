package net.aspw.client.features.module.impl.visual

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

@ModuleInfo(name = "Test", spacedName = "Test", category = ModuleCategory.VISUAL)
class Test : Module() {

    private var packets = LinkedBlockingQueue<Packet<*>>()
    private var safeTimer = TickTimer()
    private var togglePrevent = false
    private var disableLogger = false
    private var preX: Double? = null
    private var preY: Double? = null
    private var preZ: Double? = null

    override fun onDisable() {
        reset()
    }

    @EventTarget
    fun onRender3D(event: Render3DEvent) {
        togglePrevent = PredictUtils.checkVoid()
        if (togglePrevent) {
            if (abs(mc.thePlayer.posY - preY!!) > 8) {
                mc.thePlayer.motionX = 0.0
                mc.thePlayer.motionZ = 0.0
                mc.thePlayer.setPositionAndUpdate(preX!!, preY!!, preZ!!)
                chat("set back")
                reset()
            } else if (safeTimer.hasTimePassed(60)) {
                sync()
                chat("realsync")
            }
        } else sync()
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet
        if (mc.thePlayer == null || disableLogger) return
        if (packet is C03PacketPlayer && togglePrevent) {
            chat("CANCEL")
            if (preX == null)
                preX = mc.thePlayer.posX
            if (preY == null)
                preY = mc.thePlayer.posY
            if (preZ == null)
                preZ = mc.thePlayer.posZ
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
    }
}
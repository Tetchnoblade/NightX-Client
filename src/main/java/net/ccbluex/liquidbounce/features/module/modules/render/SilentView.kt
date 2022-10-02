package net.ccbluex.liquidbounce.features.module.modules.render

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.event.Render3DEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.module.modules.combat.KillAura
import net.ccbluex.liquidbounce.features.module.modules.exploit.Disabler
import net.ccbluex.liquidbounce.features.module.modules.misc.Annoy
import net.ccbluex.liquidbounce.features.module.modules.world.Scaffold
import net.ccbluex.liquidbounce.utils.RotationUtils
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.network.play.client.C03PacketPlayer

@ModuleInfo(name = "SilentView", description = "", category = ModuleCategory.RENDER)
class SilentView : Module() {

    var mode = ListValue("Mode", arrayOf("Head", "Full"), "Full")

    var playerYaw: Float? = null

    @EventTarget
    fun onRender3D(event: Render3DEvent) {
        if (RotationUtils.serverRotation != null && mode.get().equals("Head"))
            mc.thePlayer.rotationYawHead = RotationUtils.serverRotation.yaw
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        if (!mode.get().equals("Full") || !shouldRotate() || mc.thePlayer == null)
            return

        val packet = event.packet
        if (packet is C03PacketPlayer.C06PacketPlayerPosLook || packet is C03PacketPlayer.C05PacketPlayerLook) {
            playerYaw = (packet as C03PacketPlayer).yaw
            mc.thePlayer.renderYawOffset = packet.getYaw()
            mc.thePlayer.rotationYawHead = packet.getYaw()
        } else {
            if (playerYaw != null)
                mc.thePlayer.renderYawOffset = this.playerYaw!!
            mc.thePlayer.rotationYawHead = mc.thePlayer.renderYawOffset
        }
    }

    private fun getState(module: Class<out Module>) = LiquidBounce.moduleManager[module]!!.state

    fun shouldRotate(): Boolean {
        val killAura = LiquidBounce.moduleManager.getModule(KillAura::class.java) as KillAura
        val disabler = LiquidBounce.moduleManager.getModule(Disabler::class.java) as Disabler
        return getState(Scaffold::class.java) ||
                (getState(KillAura::class.java) && killAura.target != null) ||
                (getState(Disabler::class.java) && disabler.canRenderInto3D) ||
                getState(Annoy::class.java)
    }

    init {
        state = true
    }
}
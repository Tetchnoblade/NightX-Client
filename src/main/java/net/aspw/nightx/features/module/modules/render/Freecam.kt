package net.aspw.nightx.features.module.modules.render

import net.aspw.nightx.event.EventTarget
import net.aspw.nightx.event.MotionEvent
import net.aspw.nightx.event.PacketEvent
import net.aspw.nightx.event.UpdateEvent
import net.aspw.nightx.features.module.Module
import net.aspw.nightx.features.module.ModuleCategory
import net.aspw.nightx.features.module.ModuleInfo
import net.aspw.nightx.utils.MovementUtils
import net.aspw.nightx.value.BoolValue
import net.aspw.nightx.value.FloatValue
import net.minecraft.client.entity.EntityOtherPlayerMP
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.network.play.client.C0BPacketEntityAction
import org.lwjgl.input.Keyboard

@ModuleInfo(name = "Freecam", category = ModuleCategory.RENDER, keyBind = Keyboard.KEY_F8)
class Freecam : Module() {
    private val speedValue = FloatValue("Speed", 1f, 0.1f, 2f, "m")
    private val flyValue = BoolValue("Fly", true)
    private val noClipValue = BoolValue("NoClip", true)
    private var fakePlayer: EntityOtherPlayerMP? = null
    private var oldX = 0.0
    private var oldY = 0.0
    private var oldZ = 0.0
    override fun onEnable() {
        if (mc.thePlayer == null) return
        oldX = mc.thePlayer.posX
        oldY = mc.thePlayer.posY
        oldZ = mc.thePlayer.posZ
        fakePlayer = EntityOtherPlayerMP(mc.theWorld, mc.thePlayer.gameProfile)
        fakePlayer!!.clonePlayer(mc.thePlayer, true)
        fakePlayer!!.rotationYawHead = mc.thePlayer.rotationYawHead
        fakePlayer!!.copyLocationAndAnglesFrom(mc.thePlayer)
        mc.theWorld.addEntityToWorld(-1000, fakePlayer)
        if (noClipValue.get()) mc.thePlayer.noClip = true
    }

    override fun onDisable() {
        if (mc.thePlayer == null || fakePlayer == null) return
        mc.thePlayer.setPositionAndRotation(oldX, oldY, oldZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch)
        mc.theWorld.removeEntityFromWorld(fakePlayer!!.entityId)
        fakePlayer = null
        mc.thePlayer.motionX = 0.0
        mc.thePlayer.motionY = 0.0
        mc.thePlayer.motionZ = 0.0
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent?) {
        if (noClipValue.get()) mc.thePlayer.noClip = true
        mc.thePlayer.fallDistance = 0f
        if (flyValue.get()) {
            val value = speedValue.get()
            mc.thePlayer.motionY = 0.0
            mc.thePlayer.motionX = 0.0
            mc.thePlayer.motionZ = 0.0
            if (mc.gameSettings.keyBindJump.isKeyDown) mc.thePlayer.motionY += value.toDouble()
            if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
                mc.thePlayer.motionY -= value.toDouble()
                mc.gameSettings.keyBindSneak.pressed = false
            }
            MovementUtils.strafe(value)
        }
    }

    @EventTarget
    fun onMotion(event: MotionEvent) {
        mc.thePlayer.cameraPitch = 0f
        mc.thePlayer.cameraYaw = 0f
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet
        if (packet is C03PacketPlayer || packet is C0BPacketEntityAction) event.cancelEvent()
    }
}
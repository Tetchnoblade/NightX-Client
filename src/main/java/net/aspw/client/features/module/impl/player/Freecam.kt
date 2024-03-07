package net.aspw.client.features.module.impl.player

import net.aspw.client.event.EventTarget
import net.aspw.client.event.MotionEvent
import net.aspw.client.event.PacketEvent
import net.aspw.client.event.UpdateEvent
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.utils.MovementUtils
import net.aspw.client.utils.PacketUtils
import net.aspw.client.value.BoolValue
import net.aspw.client.value.FloatValue
import net.minecraft.client.entity.EntityOtherPlayerMP
import net.minecraft.client.settings.GameSettings
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.network.play.client.C0BPacketEntityAction
import org.lwjgl.input.Keyboard

@ModuleInfo(name = "Freecam", category = ModuleCategory.PLAYER, keyBind = Keyboard.KEY_F8)
class Freecam : Module() {
    private val speedValue = FloatValue("Speed", 1f, 0f, 2f, "m")
    private val noClipValue = BoolValue("NoClip", true)
    private var fakePlayer: EntityOtherPlayerMP? = null
    private var oldX = 0.0
    private var oldY = 0.0
    private var oldZ = 0.0
    private var oldYaw = 0f
    private var oldPitch = 0f
    private var oldGround = false

    override fun onEnable() {
        if (mc.thePlayer == null) return
        oldX = mc.thePlayer.posX
        oldY = mc.thePlayer.posY
        oldZ = mc.thePlayer.posZ
        oldYaw = mc.thePlayer.rotationYaw
        oldPitch = mc.thePlayer.rotationPitch
        oldGround = mc.thePlayer.onGround
        fakePlayer = EntityOtherPlayerMP(mc.theWorld, mc.thePlayer.gameProfile)
        fakePlayer!!.clonePlayer(mc.thePlayer, true)
        fakePlayer!!.rotationYawHead = mc.thePlayer.rotationYawHead
        fakePlayer!!.copyLocationAndAnglesFrom(mc.thePlayer)
        mc.theWorld.addEntityToWorld(-1000, fakePlayer)
        if (noClipValue.get()) mc.thePlayer.noClip = true
    }

    override fun onDisable() {
        if (mc.thePlayer == null || fakePlayer == null) return
        mc.thePlayer.setPositionAndRotation(oldX, oldY, oldZ, oldYaw, oldPitch)
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
        val value = speedValue.get()
        mc.thePlayer.motionY = 0.0
        mc.thePlayer.motionX = 0.0
        mc.thePlayer.motionZ = 0.0
        if (GameSettings.isKeyDown(mc.gameSettings.keyBindJump)) mc.thePlayer.motionY += value.toDouble()
        if (GameSettings.isKeyDown(mc.gameSettings.keyBindSneak)) {
            mc.thePlayer.motionY -= value.toDouble()
            mc.gameSettings.keyBindSneak.pressed = false
        }
        MovementUtils.strafe(value)
    }

    @EventTarget
    fun onMotion(event: MotionEvent) {
        mc.thePlayer.cameraPitch = 0f
        mc.thePlayer.cameraYaw = 0f
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet
        if (packet is C03PacketPlayer || packet is C0BPacketEntityAction) {
            event.cancelEvent()
            PacketUtils.sendPacketNoEvent(
                C03PacketPlayer.C06PacketPlayerPosLook(
                    oldX,
                    oldY,
                    oldZ,
                    oldYaw,
                    oldPitch,
                    oldGround
                )
            )
        }
    }
}
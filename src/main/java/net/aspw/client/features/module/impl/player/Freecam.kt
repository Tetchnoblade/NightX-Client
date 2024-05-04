package net.aspw.client.features.module.impl.player

import net.aspw.client.event.EventTarget
import net.aspw.client.event.PacketEvent
import net.aspw.client.event.UpdateEvent
import net.aspw.client.event.WorldEvent
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.utils.PacketUtils
import net.aspw.client.value.BoolValue
import net.minecraft.client.entity.EntityOtherPlayerMP
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook
import org.lwjgl.input.Keyboard

@ModuleInfo(name = "Freecam", category = ModuleCategory.PLAYER, keyBind = Keyboard.KEY_F8)
class Freecam : Module() {
    private val noClipValue = BoolValue("NoClip", true)

    var fakePlayer: EntityOtherPlayerMP? = null
    private var packetCount = 0
    private var oldX = 0.0
    private var oldY = 0.0
    private var oldZ = 0.0
    private var oldYaw = 0f
    private var oldPitch = 0f
    private var oldFlying = false

    override fun onEnable() {
        if (mc.thePlayer == null) return
        oldX = mc.thePlayer.posX
        oldY = mc.thePlayer.posY
        oldZ = mc.thePlayer.posZ
        oldYaw = mc.thePlayer.rotationYaw
        oldPitch = mc.thePlayer.rotationPitch
        oldFlying = mc.thePlayer.capabilities.isFlying
        mc.thePlayer.motionY += 0.42f
        fakePlayer = EntityOtherPlayerMP(mc.theWorld, mc.thePlayer.gameProfile)
        fakePlayer!!.clonePlayer(mc.thePlayer, true)
        fakePlayer!!.copyLocationAndAnglesFrom(mc.thePlayer)
        fakePlayer!!.rotationYawHead = mc.thePlayer.rotationYawHead
        mc.theWorld.addEntityToWorld(-1000, fakePlayer)
    }

    override fun onDisable() {
        if (mc.thePlayer == null || fakePlayer == null) return
        packetCount = 0
        mc.thePlayer.setPositionAndRotation(oldX, oldY, oldZ, oldYaw, oldPitch)
        mc.theWorld.removeEntityFromWorld(fakePlayer!!.entityId)
        fakePlayer = null
        mc.thePlayer.motionX = 0.0
        mc.thePlayer.motionY = 0.0
        mc.thePlayer.motionZ = 0.0
        mc.thePlayer.capabilities.isFlying = oldFlying
    }

    @EventTarget
    fun onWorld(event: WorldEvent) {
        state = false
        chat("Freecam was disabled")
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent?) {
        if (noClipValue.get()) mc.thePlayer.noClip = true
        mc.thePlayer.onGround = false
        mc.thePlayer.capabilities.isFlying = true
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet

        if (packet is C03PacketPlayer && (packet.rotating || packet.isMoving)) {
            if (packetCount >= 20) {
                packetCount = 0
                PacketUtils.sendPacketNoEvent(
                    C06PacketPlayerPosLook(
                        fakePlayer?.posX!!,
                        fakePlayer?.posY!!,
                        fakePlayer?.posZ!!,
                        fakePlayer?.rotationYaw!!,
                        fakePlayer?.rotationPitch!!,
                        fakePlayer?.onGround!!
                    )
                )
            } else {
                packetCount++
                PacketUtils.sendPacketNoEvent(C03PacketPlayer(fakePlayer?.onGround!!))
            }
            event.cancelEvent()
        }
    }
}
package net.aspw.client.features.module.impl.player

import net.aspw.client.event.*
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.utils.MovementUtils
import net.aspw.client.utils.PacketUtils
import net.aspw.client.value.FloatValue
import net.minecraft.client.entity.EntityOtherPlayerMP
import net.minecraft.client.settings.GameSettings
import net.minecraft.network.play.client.C03PacketPlayer
import kotlin.math.cos
import kotlin.math.sin

@ModuleInfo(name = "ReverseFreecam", "Reverse Freecam", category = ModuleCategory.PLAYER)
class ReverseFreecam : Module() {
    private var speedValue = FloatValue("Speed", 0.5f, 0f, 1f)
    private var vSpeedValue = FloatValue("V-Speed", 0.5f, 0f, 1f)

    private var fakePlayer: EntityOtherPlayerMP? = null
    private var startX: Double? = null
    private var startY: Double? = null
    private var startZ: Double? = null

    override fun onDisable() {
        reset()
    }

    private fun reset() {
        if (fakePlayer != null) {
            mc.theWorld.removeEntityFromWorld(fakePlayer!!.entityId)
            fakePlayer = null
        }
        if (startX != null && startY != null && startZ != null) {
            startX = null
            startY = null
            startZ = null
        }
        if (GameSettings.isKeyDown(mc.gameSettings.keyBindSneak))
            mc.gameSettings.keyBindSneak.pressed = true
    }

    @EventTarget
    fun onWorld(event: WorldEvent) {
        state = false
        chat("ReverseFreecam was disabled")
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (startX != null && startY != null && startZ != null) {
            if (MovementUtils.isMoving()) {
                startX = startX!! - sin(MovementUtils.getDirection()) * speedValue.get()
                startZ = startZ!! + cos(MovementUtils.getDirection()) * speedValue.get()
            }
            if (GameSettings.isKeyDown(mc.gameSettings.keyBindJump))
                startY = startY!! + vSpeedValue.get()
            if (GameSettings.isKeyDown(mc.gameSettings.keyBindSneak)) {
                startY = startY!! - vSpeedValue.get()
                mc.gameSettings.keyBindSneak.pressed = false
            }
        }
    }

    @EventTarget
    fun onMotion(event: MotionEvent) {
        if (startX == null)
            startX = mc.thePlayer.posX
        if (startY == null)
            startY = mc.thePlayer.posY
        if (startZ == null)
            startZ = mc.thePlayer.posZ
        mc.thePlayer.cameraPitch = 0f
        mc.thePlayer.cameraYaw = 0f
    }

    @EventTarget
    fun onTeleport(event: TeleportEvent) {
        startX = event.posX
        startY = event.posY
        startZ = event.posZ
        event.cancelEvent()
    }

    @EventTarget
    fun onMove(event: MoveEvent) {
        event.zero()
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet

        if (packet is C03PacketPlayer) {
            fakePlayer = EntityOtherPlayerMP(mc.theWorld, mc.thePlayer.gameProfile)
            fakePlayer!!.clonePlayer(mc.thePlayer, true)
            fakePlayer!!.copyLocationAndAnglesFrom(mc.thePlayer)
            fakePlayer!!.prevRotationYaw = mc.thePlayer.prevRotationYaw
            fakePlayer!!.prevRotationPitch = mc.thePlayer.prevRotationPitch
            fakePlayer!!.prevRenderYawOffset = mc.thePlayer.prevRenderYawOffset
            fakePlayer!!.prevRotationYawHead = mc.thePlayer.prevRotationYawHead
            fakePlayer!!.rotationYaw = mc.thePlayer.rotationYaw
            fakePlayer!!.rotationPitch = mc.thePlayer.rotationPitch
            fakePlayer!!.renderYawOffset = mc.thePlayer.renderYawOffset
            fakePlayer!!.rotationYawHead = mc.thePlayer.rotationYawHead
            fakePlayer!!.posX = startX!!
            fakePlayer!!.posY = startY!!
            fakePlayer!!.posZ = startZ!!
            mc.theWorld.addEntityToWorld(-1337, fakePlayer)
            event.cancelEvent()
            if (startX != null && startY != null && startZ != null)
                PacketUtils.sendPacketNoEvent(
                    C03PacketPlayer.C06PacketPlayerPosLook(
                        startX!!,
                        startY!!,
                        startZ!!,
                        packet.yaw,
                        packet.pitch,
                        packet.onGround
                    )
                )
        }
    }
}
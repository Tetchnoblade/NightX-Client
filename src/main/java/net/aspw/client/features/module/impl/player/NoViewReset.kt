package net.aspw.client.features.module.impl.player

import net.aspw.client.event.EventTarget
import net.aspw.client.event.PacketEvent
import net.aspw.client.event.TeleportEvent
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.util.RotationUtils
import net.aspw.client.value.BoolValue
import net.aspw.client.value.ListValue
import net.minecraft.network.play.client.C03PacketPlayer.C05PacketPlayerLook
import net.minecraft.network.play.server.S08PacketPlayerPosLook

@ModuleInfo(name = "NoViewReset", spacedName = "No View Reset", description = "", category = ModuleCategory.PLAYER)
class NoViewReset : Module() {

    private val modeValue = ListValue("Mode", arrayOf("New", "Old"), "New")
    private val confirmValue = BoolValue("Confirm", true) { modeValue.get().equals("Old", true) }
    private val illegalRotationValue = BoolValue("ConfirmIllegalRotation", true) { modeValue.get().equals("Old", true) }
    private val noZeroValue = BoolValue("NoZero", false) { modeValue.get().equals("Old", true) }

    override val tag: String
        get() = modeValue.get()

    @EventTarget
    fun onTeleport(event: TeleportEvent) {
        if (modeValue.get().equals("New", true)) {
            event.yaw = mc.thePlayer.rotationYaw
            event.pitch = mc.thePlayer.rotationPitch
        }
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        if (modeValue.get().equals("Old", true)) {
            val packet = event.packet

            mc.thePlayer ?: return

            if (packet is S08PacketPlayerPosLook) {
                if (noZeroValue.get() && packet.getYaw() == 0F && packet.getPitch() == 0F)
                    return

                if (illegalRotationValue.get() || packet.getPitch() <= 90 && packet.getPitch() >= -90 &&
                    RotationUtils.serverRotation != null && packet.getYaw() != RotationUtils.serverRotation!!.yaw &&
                    packet.getPitch() != RotationUtils.serverRotation!!.pitch
                ) {

                    if (confirmValue.get())
                        mc.netHandler.addToSendQueue(
                            C05PacketPlayerLook(
                                packet.getYaw(),
                                packet.getPitch(),
                                mc.thePlayer.onGround
                            )
                        )
                }

                packet.yaw = mc.thePlayer.rotationYaw
                packet.pitch = mc.thePlayer.rotationPitch
            }
        }
    }
}
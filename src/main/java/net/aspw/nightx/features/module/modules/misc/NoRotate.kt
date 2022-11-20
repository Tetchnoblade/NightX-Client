package net.aspw.nightx.features.module.modules.misc

import net.aspw.nightx.event.EventTarget
import net.aspw.nightx.event.PacketEvent
import net.aspw.nightx.features.module.Module
import net.aspw.nightx.features.module.ModuleCategory
import net.aspw.nightx.features.module.ModuleInfo
import net.aspw.nightx.utils.RotationUtils
import net.aspw.nightx.value.BoolValue
import net.minecraft.network.play.client.C03PacketPlayer.C05PacketPlayerLook
import net.minecraft.network.play.server.S08PacketPlayerPosLook

@ModuleInfo(name = "NoRotate", spacedName = "No Rotate", category = ModuleCategory.MISC)
class NoRotate : Module() {

    private val confirmValue = BoolValue("Confirm", true)
    private val illegalRotationValue = BoolValue("ConfirmIllegalRotation", true)
    private val noZeroValue = BoolValue("NoZero", false)

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet

        mc.thePlayer ?: return

        if (packet is S08PacketPlayerPosLook) {
            if (noZeroValue.get() && packet.getYaw() == 0F && packet.getPitch() == 0F)
                return

            if (illegalRotationValue.get() || packet.getPitch() <= 90 && packet.getPitch() >= -90 &&
                RotationUtils.serverRotation != null && packet.getYaw() != RotationUtils.serverRotation.yaw &&
                packet.getPitch() != RotationUtils.serverRotation.pitch
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
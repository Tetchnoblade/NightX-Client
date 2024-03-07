package net.aspw.client.features.module.impl.movement

import net.aspw.client.event.EventTarget
import net.aspw.client.event.PacketEvent
import net.aspw.client.event.UpdateEvent
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.utils.MovementUtils
import net.aspw.client.utils.Rotation
import net.aspw.client.utils.RotationUtils
import net.aspw.client.value.BoolValue
import net.minecraft.network.play.client.C0BPacketEntityAction

@ModuleInfo(name = "Sprint", category = ModuleCategory.MOVEMENT)
class Sprint : Module() {

    private val noPacketPatchValue = BoolValue("Silent", false)

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet
        if (noPacketPatchValue.get()) {
            if (packet is C0BPacketEntityAction && (packet.action == C0BPacketEntityAction.Action.STOP_SPRINTING || packet.action == C0BPacketEntityAction.Action.START_SPRINTING)) {
                event.cancelEvent()
            }
        }
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (!MovementUtils.isMoving() || mc.thePlayer.isSneaking || RotationUtils.targetRotation != null &&
            RotationUtils.getRotationDifference(
                Rotation(
                    mc.thePlayer.rotationYaw,
                    mc.thePlayer.rotationPitch
                )
            ) > 30F
        ) {
            mc.thePlayer.isSprinting = false
            return
        }

        if (mc.thePlayer.movementInput.moveForward >= 0.8F)
            mc.thePlayer.isSprinting = true
    }
}

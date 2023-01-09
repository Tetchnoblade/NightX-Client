package net.aspw.nightx.features.module.modules.player

import net.aspw.nightx.event.EventTarget
import net.aspw.nightx.event.JumpEvent
import net.aspw.nightx.event.PacketEvent
import net.aspw.nightx.event.UpdateEvent
import net.aspw.nightx.features.module.Module
import net.aspw.nightx.features.module.ModuleCategory
import net.aspw.nightx.features.module.ModuleInfo
import net.aspw.nightx.utils.MovementUtils
import net.aspw.nightx.utils.Rotation
import net.aspw.nightx.utils.RotationUtils
import net.aspw.nightx.value.BoolValue
import net.minecraft.network.play.client.C0BPacketEntityAction

@ModuleInfo(name = "Sprint", category = ModuleCategory.PLAYER)
class Sprint : Module() {

    val allDirectionsValue = BoolValue("Multi", true)
    val noPacketPatchValue = BoolValue("Silent", false)

    private var modified = false

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
    fun onJump(event: JumpEvent) {
        if (allDirectionsValue.get() && !modified && !mc.isIntegratedServerRunning) {
            event.cancelEvent()
            val prevYaw = mc.thePlayer.rotationYaw
            mc.thePlayer.rotationYaw = MovementUtils.getRawDirection()
            modified = true
            mc.thePlayer.jump()
            mc.thePlayer.rotationYaw = prevYaw
            modified = false
        }
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (!MovementUtils.isMoving() || mc.thePlayer.isSneaking || !allDirectionsValue.get() && RotationUtils.targetRotation != null &&
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

        if (allDirectionsValue.get() || mc.thePlayer.movementInput.moveForward >= 0.8F)
            mc.thePlayer.isSprinting = true
    }

}

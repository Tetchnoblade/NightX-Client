package net.aspw.client.features.module.impl.movement

import net.aspw.client.Client
import net.aspw.client.event.*
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.features.module.impl.combat.KillAura
import net.aspw.client.util.MovementUtils
import net.aspw.client.util.Rotation
import net.aspw.client.util.RotationUtils
import net.aspw.client.value.BoolValue
import net.minecraft.network.play.client.C0BPacketEntityAction

@ModuleInfo(name = "Sprint", description = "", category = ModuleCategory.MOVEMENT)
class Sprint : Module() {

    val allDirectionsValue = BoolValue("Multi", true)
    private val noPacketPatchValue = BoolValue("Silent", false)
    private val rot = BoolValue("Rotations", false)

    @EventTarget
    fun onMotion(event: MotionEvent) {
        if (rot.get() && event.eventState == EventState.PRE && MovementUtils.isMoving() && Client.moduleManager.getModule(
                KillAura::class.java
            )?.target == null
        ) {
            event.yaw = MovementUtils.getRawDirection()
            RotationUtils.setTargetRotation(Rotation(event.yaw, event.pitch))
        }
    }

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

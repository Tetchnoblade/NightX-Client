package net.aspw.client.features.module.impl.movement

import net.aspw.client.Client
import net.aspw.client.event.*
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.features.module.impl.combat.KillAura
import net.aspw.client.utils.MovementUtils
import net.aspw.client.utils.Rotation
import net.aspw.client.utils.RotationUtils
import net.aspw.client.value.BoolValue
import net.minecraft.network.play.client.C0BPacketEntityAction

@ModuleInfo(name = "Sprint", category = ModuleCategory.MOVEMENT)
class Sprint : Module() {

    val allDirectionsValue = BoolValue("Multi", true)
    val noPacketPatchValue = BoolValue("Silent", false)
    val rot = BoolValue("Rotations", false)
    val wallValue = BoolValue("No-WallsCheck", false)

    private var modified = false

    @EventTarget
    fun onMotion(event: MotionEvent) {
        if (rot.get() && event.eventState == EventState.PRE && MovementUtils.isMoving() && Client.moduleManager.getModule(
                KillAura::class.java
            )?.target == null
        ) {
            event.yaw = MovementUtils.getPredictionYaw(event.x, event.z) - 90F
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

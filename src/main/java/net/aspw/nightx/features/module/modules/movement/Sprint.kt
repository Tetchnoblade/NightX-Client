package net.aspw.nightx.features.module.modules.movement

import net.aspw.nightx.NightX
import net.aspw.nightx.event.EventTarget
import net.aspw.nightx.event.JumpEvent
import net.aspw.nightx.event.PacketEvent
import net.aspw.nightx.event.UpdateEvent
import net.aspw.nightx.features.module.Module
import net.aspw.nightx.features.module.ModuleCategory
import net.aspw.nightx.features.module.ModuleInfo
import net.aspw.nightx.features.module.modules.combat.KillAura
import net.aspw.nightx.utils.MovementUtils
import net.aspw.nightx.utils.Rotation
import net.aspw.nightx.utils.RotationUtils
import net.aspw.nightx.value.BoolValue
import net.minecraft.network.play.client.C0BPacketEntityAction
import net.minecraft.potion.Potion

@ModuleInfo(name = "Sprint", category = ModuleCategory.MOVEMENT)
class Sprint : Module() {

    val allDirectionsValue = BoolValue("AllDirections", true)
    val moveDirPatchValue = BoolValue("AllDirections-MoveDirPatch", true, { allDirectionsValue.get() })
    val jumpDirPatchValue =
        BoolValue("MoveDirPatch-JumpOnly", true, { allDirectionsValue.get() && moveDirPatchValue.get() })
    val blindnessValue = BoolValue("Blindness", false)
    val foodValue = BoolValue("Food", false)

    val checkServerSide = BoolValue("CheckServerSide", false)
    val checkServerSideGround = BoolValue("CheckServerSideOnlyGround", false)
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
        if (allDirectionsValue.get() && moveDirPatchValue.get() && jumpDirPatchValue.get() && !modified && !mc.isIntegratedServerRunning()) {
            event.cancelEvent()
            var prevYaw = mc.thePlayer.rotationYaw
            mc.thePlayer.rotationYaw = MovementUtils.getRawDirection()
            modified = true
            mc.thePlayer.jump()
            mc.thePlayer.rotationYaw = prevYaw
            modified = false
        }
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        val killAura = NightX.moduleManager.getModule(KillAura::class.java)!!

        if (!MovementUtils.isMoving() || mc.thePlayer.isSneaking ||
            (blindnessValue.get() && mc.thePlayer.isPotionActive(Potion.blindness)) ||
            (foodValue.get() && !(mc.thePlayer.foodStats
                .foodLevel > 6.0F || mc.thePlayer.capabilities.allowFlying))
            || (checkServerSide.get() && (mc.thePlayer.onGround || !checkServerSideGround.get())
                    && !allDirectionsValue.get() && RotationUtils.targetRotation != null &&
                    RotationUtils.getRotationDifference(
                        Rotation(
                            mc.thePlayer.rotationYaw,
                            mc.thePlayer.rotationPitch
                        )
                    ) > 30F)
        ) {
            mc.thePlayer.isSprinting = false
            return
        }

        if (allDirectionsValue.get() || mc.thePlayer.movementInput.moveForward >= 0.8F)
            mc.thePlayer.isSprinting = true

        if (allDirectionsValue.get() && moveDirPatchValue.get() && !jumpDirPatchValue.get() && killAura.target == null)
            RotationUtils.setTargetRotation(Rotation(MovementUtils.getRawDirection(), mc.thePlayer.rotationPitch))
    }

}

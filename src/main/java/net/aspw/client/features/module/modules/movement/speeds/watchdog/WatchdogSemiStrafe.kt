package net.aspw.client.features.module.modules.movement.speeds.watchdog

import net.aspw.client.Client
import net.aspw.client.event.EventState
import net.aspw.client.event.JumpEvent
import net.aspw.client.event.MotionEvent
import net.aspw.client.event.MoveEvent
import net.aspw.client.features.module.modules.movement.Speed
import net.aspw.client.features.module.modules.movement.speeds.SpeedMode
import net.aspw.client.utils.MovementUtils
import net.aspw.client.utils.PacketUtils
import net.minecraft.network.play.client.C07PacketPlayerDigging
import net.minecraft.potion.Potion
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing

class WatchdogSemiStrafe : SpeedMode("WatchdogSemiStrafe") {
    private var groundTick = 0
    override fun onJump(event: JumpEvent) {
        if (mc.thePlayer != null && MovementUtils.isMoving())
            event.cancelEvent()
    }

    override fun onEnable() {
        Client.moduleManager.getModule(
            Speed::class.java
        ) ?: return
        super.onEnable()
    }

    override fun onMotion(eventMotion: MotionEvent) {
        if (mc.thePlayer.hurtTime > 6) {
            mc.thePlayer.jumpMovementFactor = 0.04f
        }
        val speed = Client.moduleManager.getModule(
            Speed::class.java
        )
        if (speed == null || eventMotion.eventState !== EventState.PRE || mc.thePlayer.isInWater) return
        if (MovementUtils.isMoving()) {
            if (!mc.thePlayer.onGround) {
                PacketUtils.sendPacketNoEvent(
                    C07PacketPlayerDigging(
                        C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK,
                        BlockPos(-1, -1, -1),
                        EnumFacing.UP
                    )
                )
                if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
                    MovementUtils.strafe(0.24f)
                } else {
                    MovementUtils.strafe(0.14f)
                }
                mc.thePlayer.jumpMovementFactor = 0.12f
            }
            groundTick++
            if (mc.thePlayer.onGround) {
                if (groundTick >= 0) {
                    mc.thePlayer.motionY = 0.41999998688698
                }
                if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
                    MovementUtils.strafe(0.578f)
                } else {
                    MovementUtils.strafe(0.428f)
                }
            }
        } else {
            groundTick = 0
        }
    }

    override fun onUpdate() {}
    override fun onMotion() {}
    override fun onMove(event: MoveEvent) {}
}
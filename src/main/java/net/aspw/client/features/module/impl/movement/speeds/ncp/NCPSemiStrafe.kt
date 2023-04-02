package net.aspw.client.features.module.impl.movement.speeds.ncp

import net.aspw.client.Client
import net.aspw.client.event.EventState
import net.aspw.client.event.MotionEvent
import net.aspw.client.event.MoveEvent
import net.aspw.client.features.module.impl.movement.Speed
import net.aspw.client.features.module.impl.movement.speeds.SpeedMode
import net.aspw.client.features.module.impl.player.Scaffold
import net.aspw.client.utils.MovementUtils
import net.aspw.client.utils.PacketUtils
import net.minecraft.network.play.client.C07PacketPlayerDigging
import net.minecraft.potion.Potion
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing

class NCPSemiStrafe : SpeedMode("NCPSemiStrafe") {
    override fun onDisable() {
        val scaffold = Client.moduleManager.getModule(Scaffold::class.java)

        if (!mc.thePlayer.isSneaking && !scaffold!!.state) MovementUtils.strafe(0.2f)
    }

    override fun onUpdate() {}
    override fun onMotion() {}
    override fun onMove(event: MoveEvent) {}
    override fun onMotion(eventMotion: MotionEvent) {
        if (mc.thePlayer.hurtTime > 6) {
            mc.thePlayer.jumpMovementFactor = 0.04f
        }
        val speed = Client.moduleManager.getModule(
            Speed::class.java
        )
        if (speed == null || eventMotion.eventState !== EventState.PRE || mc.thePlayer.isInWater) return
        if (MovementUtils.isMoving()) {
            PacketUtils.sendPacketNoEvent(
                C07PacketPlayerDigging(
                    C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK,
                    BlockPos(-1, -1, -1),
                    EnumFacing.UP
                )
            )
            if (mc.thePlayer.onGround) {
                mc.thePlayer.motionY = 0.41999998688698
            } else {
                if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
                    MovementUtils.strafe(0.27f)
                } else {
                    MovementUtils.strafe(0.15f)
                }
                mc.thePlayer.jumpMovementFactor = 0.14f
            }
        }
    }
}
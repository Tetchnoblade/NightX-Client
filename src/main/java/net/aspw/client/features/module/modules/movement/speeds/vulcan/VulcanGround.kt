package net.aspw.client.features.module.modules.movement.speeds.vulcan

import net.aspw.client.event.MotionEvent
import net.aspw.client.event.MoveEvent
import net.aspw.client.event.PacketEvent
import net.aspw.client.features.module.modules.movement.speeds.SpeedMode
import net.aspw.client.utils.MovementUtils.isMoving
import net.aspw.client.utils.MovementUtils.strafe
import net.aspw.client.utils.PacketUtils
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.network.play.client.C07PacketPlayerDigging
import net.minecraft.potion.Potion
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing

class VulcanGround : SpeedMode("VulcanGround") {

    private var jumped = false
    private var jumpCount = 0
    private var yMotion = 0.0
    private var c03Counter = 0

    override fun onUpdate() {
        if (jumped) {
            mc.thePlayer.motionY = -0.1
            mc.thePlayer.onGround = false
            jumped = false
            yMotion = 0.0
        }
        mc.thePlayer.jumpMovementFactor = 0.025f
        if (mc.thePlayer.onGround && isMoving()) {
            if (mc.thePlayer.isCollidedHorizontally || mc.gameSettings.keyBindJump.pressed) {
                if (!mc.gameSettings.keyBindJump.pressed) {
                    mc.thePlayer.jump()
                }
                return
            }
            mc.thePlayer.jump()
            mc.thePlayer.motionY = 0.0
            yMotion = 0.1 + Math.random() * 0.03
            if (mc.thePlayer.onGround && !mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
                strafe(0.47f + jumpCount * 0.001f)
            }
            if (mc.thePlayer.onGround && mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
                strafe(0.57f + jumpCount * 0.001f)
            }
            jumpCount++
            jumped = true
        } else if (isMoving() && mc.thePlayer.onGround) {
            strafe(0.26f + jumpCount * 0.0018f)
        } else if (isMoving() && mc.thePlayer.onGround && mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
            strafe(0.36f + jumpCount * 0.0018f)
        }
    }

    override fun onMotion() {}

    override fun onMotion(event: MotionEvent) {}

    override fun onDisable() {
        mc.timer.timerSpeed = 1f
    }

    override fun onEnable() {
        c03Counter = -15
        mc.timer.timerSpeed = 1f
    }

    fun onWorld() {
        c03Counter = -15
    }

    fun onPacket(event: PacketEvent) {
        val packet = event.packet
        if (packet is C03PacketPlayer) {
            packet.y += yMotion;
            c03Counter++
            if (packet.isMoving) {
                if (c03Counter >= 7) {
                    PacketUtils.sendPacketNoEvent(
                        C07PacketPlayerDigging(
                            C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK,
                            BlockPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ),
                            EnumFacing.DOWN
                        )
                    )
                    c03Counter = 0
                } else if (c03Counter == 7 - 2) {
                    PacketUtils.sendPacketNoEvent(
                        C07PacketPlayerDigging(
                            C07PacketPlayerDigging.Action.START_DESTROY_BLOCK,
                            BlockPos.ORIGIN, EnumFacing.DOWN
                        )
                    )
                }
            }
        }
    }

    override fun onMove(event: MoveEvent) {
        jumpCount = 4
    }
}
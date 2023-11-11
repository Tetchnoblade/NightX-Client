package net.aspw.client.features.module.impl.minigames

import net.aspw.client.event.*
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.util.MovementUtils
import net.aspw.client.util.misc.RandomUtils
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.network.play.client.C0BPacketEntityAction
import net.minecraft.network.play.server.S12PacketEntityVelocity

@ModuleInfo(name = "TestModule2", spacedName = "Test Module 2", description = "", category = ModuleCategory.MINIGAMES)
class TestModule2 : Module() {
    override val tag: String
        get() = "Verus Flagless DMG HighJump Beta"

    var started = false

    override fun onEnable() {
    }

    override fun onDisable() {
        mc.thePlayer.motionX = 0.0
        mc.thePlayer.motionY = 0.0
        mc.thePlayer.motionZ = 0.0
        mc.timer.timerSpeed = 1.0f
        started = false
    }

    @EventTarget
    fun onWorld(event: WorldEvent) {
    }

    @EventTarget
    fun onTeleport(event: TeleportEvent) {
    }

    @EventTarget
    fun onMotion(event: MotionEvent) {
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet
        if (mc.thePlayer.hurtTime > 0 && (packet is C03PacketPlayer || packet is C0BPacketEntityAction)) {
            event.cancelEvent()
        }
        if (packet is S12PacketEntityVelocity) {
            event.cancelEvent()
        }
    }

    @EventTarget
    fun onKey(event: KeyEvent) {
    }

    @EventTarget
    fun onMove(event: MoveEvent) {
        if (mc.thePlayer.hurtTime > 0) {
            started = true
            if (mc.thePlayer.hurtTime > 3) {
                event.y += 0.7
                event.zeroXZ()
                return
            }
            chat("AcceptVelo-" + RandomUtils.randomNumber(9))
            if (mc.thePlayer.hurtTime > 2)
                MovementUtils.strafe(2f)
        } else {
            mc.timer.timerSpeed = 1.0f
            started = false
        }
        if (started) {
            mc.timer.timerSpeed = 0.3f
            event.y += 1.2
        } else {
            mc.timer.timerSpeed = 1.0f
        }
    }

    @EventTarget
    fun onStrafe(event: StrafeEvent) {
    }

    @EventTarget
    fun onSlowDown(event: SlowDownEvent) {
    }

    @EventTarget
    fun onClickBlock(event: ClickBlockEvent) {
    }

    @EventTarget
    fun onAttack(event: AttackEvent) {
    }

    @EventTarget
    fun onJump(event: JumpEvent) {
    }

    @EventTarget
    fun onRender2D(event: Render2DEvent) {
    }

    @EventTarget
    fun onRender3D(event: Render3DEvent) {
    }
}
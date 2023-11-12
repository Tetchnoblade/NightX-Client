package net.aspw.client.features.module.impl.minigames

import net.aspw.client.event.*
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.util.MovementUtils
import net.aspw.client.util.misc.RandomUtils
import net.aspw.client.value.BoolValue
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.network.play.client.C0BPacketEntityAction
import net.minecraft.network.play.server.S12PacketEntityVelocity

@ModuleInfo(name = "TestModule2", spacedName = "Test Module 2", description = "", category = ModuleCategory.MINIGAMES)
class TestModule2 : Module() {
    private val timerBoostValue = BoolValue("TimerBoost", true)

    override val tag: String
        get() = "VeruSUSLongJump"

    private var started = false
    var stage = 0
    var fakeY = 0.0
    private var jumpWaiting = false
    private var jumped = false

    override fun onEnable() {
    }

    override fun onDisable() {
        mc.thePlayer.motionX = 0.0
        mc.thePlayer.motionZ = 0.0
        mc.timer.timerSpeed = 1.0f
        started = false
        jumpWaiting = false
        jumped = false
        stage = 0
        fakeY = 0.0
    }

    @EventTarget
    fun onWorld(event: WorldEvent) {
    }

    @EventTarget
    fun onTeleport(event: TeleportEvent) {
    }

    @EventTarget
    fun onMotion(event: MotionEvent) {
        if (stage < 6) {
            mc.thePlayer.cameraPitch = 0f
            mc.thePlayer.cameraYaw = 0f
        }
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (stage == 0)
            fakeY = mc.thePlayer.posY
        if (stage < 6 && timerBoostValue.get())
            mc.timer.timerSpeed = 4f
        else mc.timer.timerSpeed = 1f
        if (!mc.thePlayer.onGround || jumpWaiting || stage == 7) return
        mc.timer.timerSpeed = 1f
        if (stage < 5) {
            stage += 1
            if (stage < 4)
                mc.thePlayer.jump()
            chat(stage.toString())
        }
        if (stage == 5) {
            jumpWaiting = true
        }
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet
        if (mc.thePlayer.hurtTime > 0 && (packet is C03PacketPlayer || packet is C0BPacketEntityAction)) {
            event.cancelEvent()
        }
        if (packet is S12PacketEntityVelocity && stage <= 7) {
            event.cancelEvent()
        }
        if (stage == 7) return
        if (!jumpWaiting) {
            if (packet is C03PacketPlayer) {
                packet.onGround = false
            }
        } else if (packet is C03PacketPlayer && mc.thePlayer.onGround) {
            packet.onGround = true
            jumped = true
            if (stage == 5)
                chat("6")
            stage = 6
        }
    }

    @EventTarget
    fun onKey(event: KeyEvent) {
    }

    @EventTarget
    fun onMove(event: MoveEvent) {
        if (stage > 7 && jumped || !jumped || stage == 6 && jumpWaiting || stage == 7 && mc.thePlayer.onGround) {
            event.zeroXZ()
        }
        if (mc.thePlayer.hurtTime > 0) {
            started = true
            if (mc.thePlayer.hurtTime > 3) {
                event.y += 0.7
                event.zeroXZ()
                return
            }
            chat("AcceptVelo-" + RandomUtils.randomNumber(9))
            if (mc.thePlayer.hurtTime > 2)
                MovementUtils.strafe(2.8f)
            if (mc.thePlayer.hurtTime > 1)
                MovementUtils.strafe(1f)
        } else {
            started = false
        }
        if (started) {
            stage = 7
            chat("Motion-" + RandomUtils.randomNumber(7))
            mc.timer.timerSpeed = 0.3f
            event.y += 1.2
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
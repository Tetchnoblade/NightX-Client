// LiquidBounce:legacy Implementation

package net.aspw.client.features.module.impl.combat

import net.aspw.client.event.*
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.util.MovementUtils.isMoving
import net.aspw.client.util.PacketUtils.sendPacket
import net.aspw.client.util.PacketUtils.sendPackets
import net.aspw.client.util.timing.MSTimer
import net.aspw.client.util.timing.TimeUtils.randomDelay
import net.aspw.client.value.BoolValue
import net.aspw.client.value.IntegerValue
import net.aspw.client.value.ListValue
import net.minecraft.entity.EntityLivingBase
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.network.play.client.C0BPacketEntityAction
import net.minecraft.network.play.client.C0BPacketEntityAction.Action.*

@ModuleInfo(
    name = "WTap", description = "",
    category = ModuleCategory.COMBAT
)
object WTap : Module() {
    private val delay by IntegerValue("Delay", 0, 0, 500)
    private val hurtTime by IntegerValue("HurtTime", 10, 0, 10)
    private val mode by ListValue("Mode", arrayOf("SprintTap", "WTap", "Old", "Silent", "Packet", "SneakPacket"), "Old")
    private val reSprintMaxTicks: IntegerValue = object : IntegerValue("ReSprintMaxTicks", 2, 1..5) {
        override fun isSupported() = mode == "WTap"

        override fun onChange(oldValue: Int, newValue: Int) = newValue.coerceAtLeast(reSprintMinTicks.get())
    }
    private val reSprintMinTicks: IntegerValue = object : IntegerValue("ReSprintMinTicks", 1, 1..5) {
        override fun isSupported() = mode == "WTap"

        override fun onChange(oldValue: Int, newValue: Int) = newValue.coerceAtMost(reSprintMaxTicks.get())
    }

    private val onlyGround by BoolValue("OnlyGround", false)

    val onlyMove by BoolValue("OnlyMove", true)
    val onlyMoveForward by BoolValue("OnlyMoveForward", true) { onlyMove }

    private var ticks = 0
    private var forceSprintState = 0
    private val timer = MSTimer()

    // WTap
    private var blockInput = false
    private var allowInputTicks = randomDelay(reSprintMinTicks.get(), reSprintMaxTicks.get())
    private var ticksElapsed = 0

    override fun onToggle(state: Boolean) {
        // Make sure the user won't have their input forever blocked
        blockInput = false
    }

    @EventTarget
    fun onAttack(event: AttackEvent) {
        val player = mc.thePlayer ?: return

        if (event.targetEntity !is EntityLivingBase) return

        if (event.targetEntity.hurtTime > hurtTime || !timer.hasTimePassed(delay) || (onlyGround && !mc.thePlayer.onGround)) return

        if (onlyMove && (!isMoving || (onlyMoveForward && mc.thePlayer.movementInput.moveStrafe != 0f))) return

        when (mode) {
            "Old" -> {
                // Users reported that this mode is better than the other ones

                if (mc.thePlayer.isSprinting) {
                    sendPacket(C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING))
                }

                sendPackets(
                    C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING),
                    C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING),
                    C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING)
                )
                mc.thePlayer.isSprinting = true
                mc.thePlayer.serverSprintState = true
            }

            "SprintTap", "Silent" -> if (player.isSprinting && player.serverSprintState) ticks = 2

            "Packet" -> {
                sendPackets(
                    C0BPacketEntityAction(mc.thePlayer, STOP_SPRINTING),
                    C0BPacketEntityAction(mc.thePlayer, START_SPRINTING)
                )
            }

            "SneakPacket" -> {
                sendPackets(
                    C0BPacketEntityAction(mc.thePlayer, STOP_SPRINTING),
                    C0BPacketEntityAction(mc.thePlayer, START_SNEAKING),
                    C0BPacketEntityAction(mc.thePlayer, START_SPRINTING),
                    C0BPacketEntityAction(mc.thePlayer, STOP_SNEAKING)
                )
            }

            "WTap" -> {
                // We want the player to be sprinting before we block inputs
                if (player.isSprinting && player.serverSprintState) {
                    blockInput = true
                    allowInputTicks = randomDelay(reSprintMinTicks.get(), reSprintMaxTicks.get())
                }
            }
        }

        timer.reset()
    }

    @EventTarget
    fun onPostSprintUpdate(event: PostSprintUpdateEvent) {
        if (mode == "SprintTap") {
            if (ticks == 2) {
                mc.thePlayer.isSprinting = false
                forceSprintState = 2
                ticks--
            } else if (ticks == 1) {
                if (mc.thePlayer.movementInput.moveForward > 0.8) {
                    mc.thePlayer.isSprinting = true
                }
                forceSprintState = 1
                ticks--
            } else {
                forceSprintState = 0
            }
        }
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (mode == "WTap" && blockInput) {
            if (ticksElapsed >= allowInputTicks) {
                blockInput = false
                ticksElapsed = 0
            } else {
                ticksElapsed++
            }
        }
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet
        if (packet is C03PacketPlayer && mode == "Silent") {
            if (ticks == 2) {
                sendPacket(C0BPacketEntityAction(mc.thePlayer, STOP_SPRINTING))
                ticks--
            } else if (ticks == 1) {
                sendPacket(C0BPacketEntityAction(mc.thePlayer, START_SPRINTING))
                ticks--
            }
        }
    }

    fun shouldBlockInput() = handleEvents() && mode == "WTap" && blockInput

    override val tag
        get() = mode

    fun breakSprint() = handleEvents() && forceSprintState == 2 && mode == "SprintTap"
    fun startSprint() = handleEvents() && forceSprintState == 1 && mode == "SprintTap"
}

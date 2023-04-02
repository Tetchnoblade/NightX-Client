package net.aspw.client.features.module.impl.player

import net.aspw.client.Client
import net.aspw.client.event.*
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.utils.MovementUtils
import net.aspw.client.utils.PacketUtils
import net.aspw.client.utils.render.RenderUtils
import net.aspw.client.value.BoolValue
import net.aspw.client.value.FloatValue
import net.aspw.client.value.IntegerValue
import net.aspw.client.visual.font.Fonts
import net.aspw.client.visual.hud.element.elements.Notification
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.init.Items
import net.minecraft.item.ItemBow
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.network.play.client.C03PacketPlayer.C05PacketPlayerLook
import net.minecraft.network.play.client.C07PacketPlayerDigging
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement
import net.minecraft.network.play.client.C09PacketHeldItemChange
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing
import java.awt.Color

@ModuleInfo(name = "BowLongJump", spacedName = "Bow Long Jump", category = ModuleCategory.PLAYER)
class BowLongJump : Module() {
    private val boostValue = FloatValue("Boost", 0.96f, 0f, 10f, "x")
    private val heightValue = FloatValue("Height", 0.58f, 0f, 10f, "m")
    private val timerValue = FloatValue("Timer", 1f, 0.1f, 10f, "x")
    private val delayBeforeLaunch = IntegerValue("DelayBeforeArrowLaunch", 1, 1, 20, " tick")
    private val autoDisable = BoolValue("AutoDisable", true)
    private val bobbingValue = BoolValue("Bobbing", false)
    private val bobbingAmountValue = FloatValue("BobbingAmount", 0.07f, 0f, 1f) { bobbingValue.get() }
    private val renderValue = BoolValue("RenderStatus", false)
    private var bowState = 0
    private var lastPlayerTick: Long = 0
    private var lastSlot = -1
    override fun onEnable() {
        if (mc.thePlayer == null) return
        bowState = 0
        lastPlayerTick = -1
        lastSlot = mc.thePlayer.inventory.currentItem
        MovementUtils.strafe(0f)
    }

    @EventTarget
    fun onMotion(event: MotionEvent?) {
        if (bobbingValue.get()) {
            mc.thePlayer.cameraYaw = bobbingAmountValue.get()
        }
    }

    @EventTarget
    fun onMove(event: MoveEvent) {
        if (mc.thePlayer.onGround && bowState < 3) event.cancelEvent()
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        if (event.packet is C09PacketHeldItemChange) {
            lastSlot = event.packet.slotId
            event.cancelEvent()
        }
        if (event.packet is C03PacketPlayer) {
            if (bowState < 3) event.packet.isMoving = false
        }
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent?) {
        mc.timer.timerSpeed = 1f
        var forceDisable = false
        when (bowState) {
            0 -> {
                val slot = bowSlot
                if (slot < 0 || !mc.thePlayer.inventory.hasItem(Items.arrow)) {
                    Client.hud.addNotification(
                        Notification(
                            "No arrows or bow found in your inventory!",
                            Notification.Type.ERROR
                        )
                    )
                    forceDisable = true
                    bowState = 5
                } else if (lastPlayerTick == -1L) {
                    val stack = mc.thePlayer.inventoryContainer.getSlot(slot + 36).stack
                    if (lastSlot != slot) PacketUtils.sendPacketNoEvent(C09PacketHeldItemChange(slot))
                    PacketUtils.sendPacketNoEvent(
                        C08PacketPlayerBlockPlacement(
                            BlockPos(-1, -1, -1),
                            255,
                            mc.thePlayer.inventoryContainer.getSlot(slot + 36).stack,
                            0f,
                            0f,
                            0f
                        )
                    )
                    lastPlayerTick = mc.thePlayer.ticksExisted.toLong()
                    bowState = 1
                }
            }

            1 -> {
                val reSlot = bowSlot
                if (mc.thePlayer.ticksExisted - lastPlayerTick > delayBeforeLaunch.get()) {
                    PacketUtils.sendPacketNoEvent(
                        C05PacketPlayerLook(
                            mc.thePlayer.rotationYaw,
                            -90f,
                            mc.thePlayer.onGround
                        )
                    )
                    PacketUtils.sendPacketNoEvent(
                        C07PacketPlayerDigging(
                            C07PacketPlayerDigging.Action.RELEASE_USE_ITEM,
                            BlockPos.ORIGIN,
                            EnumFacing.DOWN
                        )
                    )
                    if (lastSlot != reSlot) PacketUtils.sendPacketNoEvent(C09PacketHeldItemChange(lastSlot))
                    bowState = 2
                }
            }

            2 -> if (mc.thePlayer.hurtTime > 0) bowState = 3
            3 -> {
                MovementUtils.strafe(boostValue.get())
                mc.thePlayer.motionY = heightValue.get().toDouble()
                bowState = 4
                lastPlayerTick = mc.thePlayer.ticksExisted.toLong()
            }

            4 -> {
                mc.timer.timerSpeed = timerValue.get()
                if (mc.thePlayer.onGround && mc.thePlayer.ticksExisted - lastPlayerTick >= 1) bowState = 5
            }
        }
        if (bowState < 3) {
            mc.thePlayer.movementInput.moveForward = 0f
            mc.thePlayer.movementInput.moveStrafe = 0f
        }
        if (bowState == 5 && (autoDisable.get() || forceDisable)) state = false
    }

    @EventTarget
    fun onWorld(event: WorldEvent?) {
        state = false //prevent weird things
    }

    override fun onDisable() {
        mc.timer.timerSpeed = 1.0f
        mc.thePlayer.speedInAir = 0.02f
        if (!mc.thePlayer.isSneaking) {
            MovementUtils.strafe(0.2f)
        }
    }

    private val bowSlot: Int
        private get() {
            for (i in 36..44) {
                val stack = mc.thePlayer.inventoryContainer.getSlot(i).stack
                if (stack != null && stack.item is ItemBow) {
                    return i - 36
                }
            }
            return -1
        }

    @EventTarget
    fun onRender2D(event: Render2DEvent?) {
        if (!renderValue.get()) return
        val scaledRes = ScaledResolution(mc)
        val width = bowState.toFloat() / 5f * 60f
        Fonts.fontSFUI40.drawCenteredString(
            bowStatus,
            scaledRes.scaledWidth / 2f,
            scaledRes.scaledHeight / 2f + 14f,
            -1,
            true
        )
        RenderUtils.drawRect(
            scaledRes.scaledWidth / 2f - 31f,
            scaledRes.scaledHeight / 2f + 25f,
            scaledRes.scaledWidth / 2f + 31f,
            scaledRes.scaledHeight / 2f + 29f,
            -0x60000000
        )
        RenderUtils.drawRect(
            scaledRes.scaledWidth / 2f - 30f,
            scaledRes.scaledHeight / 2f + 26f,
            scaledRes.scaledWidth / 2f - 30f + width,
            scaledRes.scaledHeight / 2f + 28f,
            statusColor
        )
    }

    val bowStatus: String
        get() = when (bowState) {
            0 -> "Idle..."
            1 -> "Preparing..."
            2 -> "Waiting..."
            3, 4 -> "Successfully!"
            else -> "Task completed."
        }
    val statusColor: Color
        get() = when (bowState) {
            0 -> Color(21, 21, 21)
            1 -> Color(48, 48, 48)
            2 -> Color.yellow
            3, 4 -> Color.green
            else -> Color(0, 111, 255)
        }
}
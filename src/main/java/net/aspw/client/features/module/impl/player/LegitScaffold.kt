package net.aspw.client.features.module.impl.player

import net.aspw.client.Launch
import net.aspw.client.event.*
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.features.module.impl.visual.SilentRotations
import net.aspw.client.utils.InventoryUtils
import net.aspw.client.utils.Rotation
import net.aspw.client.utils.RotationUtils
import net.aspw.client.utils.misc.RandomUtils
import net.aspw.client.utils.timer.TickTimer
import net.aspw.client.value.BoolValue
import net.aspw.client.value.FloatValue
import net.aspw.client.value.IntegerValue
import net.minecraft.client.settings.GameSettings
import net.minecraft.client.settings.KeyBinding
import net.minecraft.init.Blocks
import net.minecraft.item.ItemBlock
import net.minecraft.util.BlockPos

@ModuleInfo(name = "LegitScaffold", spacedName = "Legit Scaffold", category = ModuleCategory.PLAYER)
class LegitScaffold : Module() {

    private val sneakValue = BoolValue("AutoSneak", true)
    private val autoSwitchValue = BoolValue("AutoSwitch", true)
    private val safeWalkValue = BoolValue("SafeWalk", true)
    private val stopSprintValue = BoolValue("StopSprint", true)
    private val delayValue = IntegerValue("PlaceDelay", 0, 0, 30)
    private val maxTurnSpeed: FloatValue =
        object : FloatValue("MaxTurnSpeed", 80f, 0f, 180f, "°") {
            override fun onChanged(oldValue: Float, newValue: Float) {
                val i = minTurnSpeed.get()
                if (i > newValue) set(i)
            }
        }
    private val minTurnSpeed: FloatValue =
        object : FloatValue("MinTurnSpeed", 40f, 0f, 180f, "°") {
            override fun onChanged(oldValue: Float, newValue: Float) {
                val i = maxTurnSpeed.get()
                if (i < newValue) set(i)
            }
        }

    private val tickTimer = TickTimer()
    var lastSlot = 0

    override fun onEnable() {
        lastSlot = mc.thePlayer.inventory.currentItem
    }

    override fun onDisable() {
        if (mc.thePlayer == null)
            return

        if (autoSwitchValue.get()) {
            mc.thePlayer.inventory.currentItem = lastSlot
            mc.playerController.updateController()
        }

        if (!GameSettings.isKeyDown(mc.gameSettings.keyBindSneak))
            mc.gameSettings.keyBindSneak.pressed = false
    }

    @EventTarget
    fun onMotion(event: MotionEvent) {
        if (GameSettings.isKeyDown(mc.gameSettings.keyBindSneak)) {
            tickTimer.reset()
            mc.gameSettings.keyBindSneak.pressed = true
            return
        }

        if (event.eventState == EventState.POST) {
            try {
                if (autoSwitchValue.get() && mc.thePlayer.inventory.currentItem != mc.thePlayer.inventoryContainer.getSlot(
                        InventoryUtils.findAutoBlockBlock()
                    ).slotIndex
                ) {
                    mc.thePlayer.inventory.currentItem = InventoryUtils.findAutoBlockBlock() - 36
                    mc.playerController.updateController()
                }
            } catch (ignored: Exception) {
            }
        }

        tickTimer.update()

        val shouldEagle = mc.theWorld.getBlockState(
            BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.0, mc.thePlayer.posZ)
        ).block === Blocks.air

        if (shouldEagle && (tickTimer.hasTimePassed(delayValue.get()) || !mc.thePlayer.onGround)) {
            if (mc.thePlayer.heldItem != null && mc.thePlayer.heldItem.item is ItemBlock)
                KeyBinding.onTick(mc.gameSettings.keyBindUseItem.keyCode)
            tickTimer.reset()
        }
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (GameSettings.isKeyDown(mc.gameSettings.keyBindSneak)) {
            tickTimer.reset()
            mc.gameSettings.keyBindSneak.pressed = true
            return
        }

        val shouldEagle = mc.theWorld.getBlockState(
            BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.0, mc.thePlayer.posZ)
        ).block === Blocks.air

        if (sneakValue.get() && shouldEagle || GameSettings.isKeyDown(mc.gameSettings.keyBindSneak))
            mc.gameSettings.keyBindSneak.pressed = true
        else if (!GameSettings.isKeyDown(mc.gameSettings.keyBindSneak))
            mc.gameSettings.keyBindSneak.pressed = false

        if (stopSprintValue.get())
            mc.thePlayer.isSprinting = false

        RotationUtils.setTargetRotation(
            RotationUtils.limitAngleChange(
                RotationUtils.serverRotation!!,
                Rotation(
                    RotationUtils.cameraYaw - 180f,
                    (if (!mc.thePlayer.isSneaking) 80.4f else 80f) + if (mc.thePlayer.isSprinting) 0.2f else 0.0f
                ),
                RandomUtils.nextFloat(minTurnSpeed.get(), maxTurnSpeed.get())
            )
        )
    }

    @EventTarget
    fun onStrafe(event: StrafeEvent) {
        if (!Launch.moduleManager.getModule(SilentRotations::class.java)?.customStrafe?.get()!!)
            event.yaw = RotationUtils.serverRotation?.yaw!! - 180f
    }

    @EventTarget
    fun onJump(event: JumpEvent) {
        if (!Launch.moduleManager.getModule(SilentRotations::class.java)?.customStrafe?.get()!!)
            event.yaw = RotationUtils.serverRotation?.yaw!! - 180f
    }

    @EventTarget
    fun onMove(event: MoveEvent) {
        if (mc.thePlayer.onGround && safeWalkValue.get())
            event.isSafeWalk = true
    }
}
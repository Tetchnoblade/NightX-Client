package net.aspw.client.features.module.impl.combat

import net.aspw.client.event.EventTarget
import net.aspw.client.event.Render3DEvent
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.util.CooldownHelper
import net.aspw.client.util.timer.TickTimer
import net.aspw.client.util.timer.TimeUtils
import net.aspw.client.value.BoolValue
import net.aspw.client.value.IntegerValue
import net.minecraft.client.settings.KeyBinding

@ModuleInfo(name = "AutoClicker", spacedName = "Auto Clicker", description = "", category = ModuleCategory.COMBAT)
class AutoClicker : Module() {
    private val coolDownCheck = BoolValue("Cooldown-Check", false)
    private val leftValue = BoolValue("Left", true)
    private val leftmaxCPSValue: IntegerValue =
        object : IntegerValue("Left-MaxCPS", 12, 1, 20, { leftValue.get() && !coolDownCheck.get() }) {
            override fun onChanged(oldValue: Int, newValue: Int) {
                val leftminCPS = leftminCPSValue.get()
                if (leftminCPS > newValue)
                    set(leftminCPS)
            }
        }
    private val leftminCPSValue: IntegerValue =
        object : IntegerValue("Left-MinCPS", 8, 1, 20, { leftValue.get() && !coolDownCheck.get() }) {
            override fun onChanged(oldValue: Int, newValue: Int) {
                val leftmaxCPS = leftmaxCPSValue.get()
                if (leftmaxCPS < newValue)
                    set(leftmaxCPS)
            }
        }
    private val rightValue = BoolValue("Right", false)
    private val rightmaxCPSValue: IntegerValue =
        object : IntegerValue("Right-MaxCPS", 12, 1, 20, { rightValue.get() }) {
            override fun onChanged(oldValue: Int, newValue: Int) {
                val rightminCPS = rightminCPSValue.get()
                if (rightminCPS > newValue)
                    set(rightminCPS)
            }
        }
    private val rightminCPSValue: IntegerValue = object : IntegerValue("Right-MinCPS", 8, 1, 20, { rightValue.get() }) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val rightmaxCPS = rightmaxCPSValue.get()
            if (rightmaxCPS < newValue)
                set(rightmaxCPS)
        }
    }

    private var rightDelay = TimeUtils.randomClickDelay(rightminCPSValue.get(), rightmaxCPSValue.get())
    private var rightLastSwing = 0L
    private var leftDelay = if (coolDownCheck.get())
        TimeUtils.randomClickDelay(20, 20)
    else TimeUtils.randomClickDelay(leftminCPSValue.get(), leftmaxCPSValue.get())
    private var leftLastSwing = 0L
    private val tickTimer = TickTimer()

    @EventTarget
    fun onRender(event: Render3DEvent) {
        // Right click
        if (mc.gameSettings.keyBindUseItem.isKeyDown && !mc.thePlayer.isUsingItem && rightValue.get() &&
            System.currentTimeMillis() - rightLastSwing >= rightDelay
        ) {
            KeyBinding.onTick(mc.gameSettings.keyBindUseItem.keyCode) // Minecraft Click Handling

            rightLastSwing = System.currentTimeMillis()
            rightDelay = TimeUtils.randomClickDelay(rightminCPSValue.get(), rightmaxCPSValue.get())
        }

        // Left click
        if (mc.gameSettings.keyBindAttack.isKeyDown && leftValue.get() &&
            System.currentTimeMillis() - leftLastSwing >= leftDelay && mc.playerController.curBlockDamageMP == 0F
        ) {
            if (coolDownCheck.get() && CooldownHelper.getAttackCooldownProgress() < 1f)
                return
            KeyBinding.onTick(mc.gameSettings.keyBindAttack.keyCode) // Minecraft Click Handling

            leftLastSwing = System.currentTimeMillis()
            leftDelay = TimeUtils.randomClickDelay(leftminCPSValue.get(), leftmaxCPSValue.get())
        }
    }

    override fun onEnable() {
        tickTimer.update()
    }

    override fun onDisable() {
        tickTimer.reset()
    }
}
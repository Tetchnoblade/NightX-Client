package net.aspw.client.features.module.impl.combat

import net.aspw.client.event.EventTarget
import net.aspw.client.event.Render3DEvent
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.utils.CooldownHelper
import net.aspw.client.utils.EntityUtils
import net.aspw.client.utils.timer.TimeUtils
import net.aspw.client.value.BoolValue
import net.aspw.client.value.IntegerValue

@ModuleInfo(name = "TriggerBot", spacedName = "Trigger Bot", category = ModuleCategory.COMBAT)
class TriggerBot : Module() {
    private val coolDownCheck = BoolValue("Cooldown-Check", false)
    private val maxCPS: IntegerValue = object : IntegerValue("MaxCPS", 12, 1, 20, { !coolDownCheck.get() }) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val i = minCPS.get()
            if (i > newValue) set(i)
            delay = TimeUtils.randomClickDelay(minCPS.get(), this.get())
        }
    }

    private val minCPS: IntegerValue = object : IntegerValue("MinCPS", 10, 1, 20, { !coolDownCheck.get() }) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val i = maxCPS.get()
            if (i < newValue) set(i)
            delay = TimeUtils.randomClickDelay(this.get(), maxCPS.get())
        }
    }

    private var delay = if (coolDownCheck.get())
        TimeUtils.randomClickDelay(20, 20)
    else TimeUtils.randomClickDelay(minCPS.get(), maxCPS.get())
    private var lastSwing = 0L

    override fun onDisable() {
        lastSwing = 0L
    }

    @EventTarget
    fun onRender(event: Render3DEvent) {
        if (coolDownCheck.get() && CooldownHelper.getAttackCooldownProgress() < 1f)
            return
        val objectMouseOver = mc.objectMouseOver

        if (objectMouseOver != null && System.currentTimeMillis() - lastSwing >= delay &&
            EntityUtils.isSelected(objectMouseOver.entityHit, true)
        ) {
            net.minecraft.client.settings.KeyBinding.onTick(mc.gameSettings.keyBindAttack.keyCode)

            lastSwing = System.currentTimeMillis()
            delay = if (coolDownCheck.get())
                TimeUtils.randomClickDelay(20, 20)
            else TimeUtils.randomClickDelay(minCPS.get(), maxCPS.get())
        }
    }
}
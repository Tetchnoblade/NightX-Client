package net.aspw.nightx.features.module.modules.world

import net.aspw.nightx.event.EventTarget
import net.aspw.nightx.event.UpdateEvent
import net.aspw.nightx.features.module.Module
import net.aspw.nightx.features.module.ModuleCategory
import net.aspw.nightx.features.module.ModuleInfo
import net.aspw.nightx.utils.timer.TickTimer
import net.aspw.nightx.value.FloatValue

@ModuleInfo(name = "TickTimer", spacedName = "Tick Timer", category = ModuleCategory.WORLD)
class TickTimer : Module() {
    private val tickTimerValue = FloatValue("Speed", 2F, 0.1F, 10F, "x")
    private val tickTimer = TickTimer()
    override fun onEnable() {
        if (mc.thePlayer == null) return
        tickTimer.reset()
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent?) {
        tickTimer.update()
        if (tickTimer.hasTimePassed(10)) {
            mc.timer.timerSpeed = 1.0f
        }
        if (tickTimer.hasTimePassed(20)) {
            mc.timer.timerSpeed = tickTimerValue.get().toFloat()
            tickTimer.reset()
        }
    }

    override fun onDisable() {
        mc.timer.timerSpeed = 1.0f
        tickTimer.reset()
    }
}
package net.aspw.client.features.module.impl.player

import net.aspw.client.event.EventTarget
import net.aspw.client.event.UpdateEvent
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.utils.timer.TickTimer
import net.aspw.client.value.FloatValue

@ModuleInfo(name = "TickTimer", spacedName = "Tick Timer", category = ModuleCategory.PLAYER)
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
            mc.timer.timerSpeed = tickTimerValue.get()
            tickTimer.reset()
        }
    }

    override fun onDisable() {
        mc.timer.timerSpeed = 1.0f
        tickTimer.reset()
    }
}
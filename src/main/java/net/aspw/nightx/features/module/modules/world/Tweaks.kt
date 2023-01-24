package net.aspw.nightx.features.module.modules.world

import net.aspw.nightx.event.EventTarget
import net.aspw.nightx.event.UpdateEvent
import net.aspw.nightx.features.module.Module
import net.aspw.nightx.features.module.ModuleCategory
import net.aspw.nightx.features.module.ModuleInfo
import net.aspw.nightx.utils.timer.TickTimer
import net.aspw.nightx.value.IntegerValue

@ModuleInfo(name = "Tweaks", category = ModuleCategory.WORLD)
class Tweaks : Module() {
    private val speedValue = IntegerValue("Sneak-Speed", 0, 0, 20)

    private val tickTimer = TickTimer()

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        tickTimer.update()

        if (tickTimer.hasTimePassed(1 + speedValue.get())) {
            mc.gameSettings.keyBindSneak.pressed = true
        }

        if (tickTimer.hasTimePassed(2 + speedValue.get())) {
            mc.gameSettings.keyBindSneak.pressed = false
            tickTimer.reset()
        }
    }

    override fun onEnable() {
        tickTimer.reset()
    }

    override fun onDisable() {
        mc.gameSettings.keyBindSneak.pressed = false
    }
}

package net.aspw.client.features.module.impl.other

import net.aspw.client.event.EventTarget
import net.aspw.client.event.UpdateEvent
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.utils.timer.TickTimer
import net.aspw.client.value.IntegerValue

@ModuleInfo(name = "Tweaks", category = ModuleCategory.OTHER)
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

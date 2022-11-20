package net.aspw.nightx.features.module.modules.world

import net.aspw.nightx.event.EventTarget
import net.aspw.nightx.event.UpdateEvent
import net.aspw.nightx.event.WorldEvent
import net.aspw.nightx.features.module.Module
import net.aspw.nightx.features.module.ModuleCategory
import net.aspw.nightx.features.module.ModuleInfo
import net.aspw.nightx.utils.MovementUtils
import net.aspw.nightx.value.BoolValue
import net.aspw.nightx.value.FloatValue

@ModuleInfo(name = "Timer", category = ModuleCategory.WORLD)
class Timer : Module() {

    private val speedValue = FloatValue("Speed", 2F, 0.1F, 10F, "x")
    private val onMoveValue = BoolValue("OnMove", false)
    private val autoDisableValue = BoolValue("AutoDisable", false)

    override fun onDisable() {
        mc.timer.timerSpeed = 1F
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (mc.thePlayer == null || mc.theWorld == null) return

        if (MovementUtils.isMoving() || !onMoveValue.get()) {
            mc.timer.timerSpeed = speedValue.get()
            return
        }

        mc.timer.timerSpeed = 1F
    }

    @EventTarget
    fun onWorld(event: WorldEvent) {
        if (event.worldClient != null)
            return

        if (autoDisableValue.get()) state = false
    }
}

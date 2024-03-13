package net.aspw.client.features.module.impl.player

import net.aspw.client.event.EventTarget
import net.aspw.client.event.TeleportEvent
import net.aspw.client.event.UpdateEvent
import net.aspw.client.event.WorldEvent
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.utils.MovementUtils
import net.aspw.client.value.BoolValue
import net.aspw.client.value.FloatValue
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

@ModuleInfo(name = "Timer", category = ModuleCategory.PLAYER)
class Timer : Module() {

    private val speedValue = FloatValue("Speed", 2F, 0.1F, 10F, "x")
    private val onMoveValue = BoolValue("OnMove", false)
    private val lagCheck = BoolValue("LagCheck", false)
    private val worldCheck = BoolValue("WorldCheck", true)

    private val decimalFormat = DecimalFormat("##0.00", DecimalFormatSymbols(Locale.ENGLISH))

    override val tag: String
        get() = decimalFormat.format(speedValue.get()) + "x"

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
        if (worldCheck.get()) {
            state = false
            chat("Timer was disabled")
        }
    }

    @EventTarget
    fun onTeleport(event: TeleportEvent) {
        if (lagCheck.get()) {
            state = false
            chat("Disabling Timer due to lag back")
        }
    }
}

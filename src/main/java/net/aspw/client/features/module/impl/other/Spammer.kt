package net.aspw.client.features.module.impl.other

import net.aspw.client.event.EventTarget
import net.aspw.client.event.UpdateEvent
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.util.misc.RandomUtils
import net.aspw.client.util.timer.MSTimer
import net.aspw.client.util.timer.TimeUtils
import net.aspw.client.value.IntegerValue
import java.util.*

@ModuleInfo(name = "Spammer", description = "", category = ModuleCategory.OTHER)
class Spammer : Module() {
    private val maxDelayValue: IntegerValue = object : IntegerValue("MaxDelay", 1500, 0, 5000, "ms") {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val minDelayValueObject = minDelayValue.get()
            if (minDelayValueObject > newValue) set(minDelayValueObject)
            delay = TimeUtils.randomDelay(minDelayValue.get(), this.get())
        }
    }
    private val minDelayValue: IntegerValue = object : IntegerValue("MinDelay", 1500, 0, 5000, "ms") {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val maxDelayValueObject = maxDelayValue.get()
            if (maxDelayValueObject < newValue) set(maxDelayValueObject)
            delay = TimeUtils.randomDelay(this.get(), maxDelayValue.get())
        }
    }
    private val msTimer = MSTimer()

    @EventTarget
    fun onUpdate(event: UpdateEvent?) {
        if (msTimer.hasTimePassed(delay)) {
            mc.thePlayer.sendChatMessage(
                "!NightX Client!" + " >" + RandomUtils.randomString(5 + Random().nextInt(5)) + "<"
            )
            msTimer.reset()
            delay = TimeUtils.randomDelay(minDelayValue.get(), maxDelayValue.get())
        }
    }

    private var delay = TimeUtils.randomDelay(minDelayValue.get(), maxDelayValue.get())
}
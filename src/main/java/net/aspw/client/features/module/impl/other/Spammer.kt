package net.aspw.client.features.module.impl.other

import net.aspw.client.event.EventTarget
import net.aspw.client.event.UpdateEvent
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.utils.misc.RandomUtils
import net.aspw.client.utils.timer.MSTimer
import net.aspw.client.value.BoolValue
import net.aspw.client.value.IntegerValue
import net.aspw.client.value.TextValue


@ModuleInfo(name = "Spammer", category = ModuleCategory.OTHER)
class Spammer : Module() {
    private val messageValue = TextValue("Message", "cocaine")
    private val delayValue = IntegerValue("Delay", 1000, 1000, 5000)
    private val randomValue = BoolValue("Random", true)

    private val spamTimer = MSTimer()

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (spamTimer.hasTimePassed(delayValue.get().toLong())) {
            if (randomValue.get())
                mc.thePlayer.sendChatMessage(messageValue.get() + " " + RandomUtils.randomString(3))
            else mc.thePlayer.sendChatMessage(messageValue.get())
            spamTimer.reset()
        }
    }
}
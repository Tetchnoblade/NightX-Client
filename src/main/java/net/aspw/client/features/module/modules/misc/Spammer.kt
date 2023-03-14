package net.aspw.client.features.module.modules.misc

import net.aspw.client.event.EventTarget
import net.aspw.client.event.UpdateEvent
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.utils.misc.RandomUtils
import net.aspw.client.utils.timer.MSTimer
import net.aspw.client.utils.timer.TimeUtils
import net.aspw.client.value.BoolValue
import net.aspw.client.value.IntegerValue
import net.aspw.client.value.TextValue
import java.util.*

@ModuleInfo(name = "Spammer", category = ModuleCategory.MISC)
class Spammer : Module() {
    private val customValue = BoolValue("Custom", false)
    private val maxDelayValue: IntegerValue = object : IntegerValue("MaxDelay", 1500, 0, 5000, "ms") {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val minDelayValueObject = minDelayValue.get()
            if (minDelayValueObject > newValue) set(minDelayValueObject)
            delay = TimeUtils.randomDelay(minDelayValue.get(), this.get())
        }
    }
    private val blankText = TextValue("Placeholder guide", "") { customValue.get() }
    private val guideFloat = TextValue("%f", "Random float") { customValue.get() }
    private val minDelayValue: IntegerValue = object : IntegerValue("MinDelay", 1500, 0, 5000, "ms") {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val maxDelayValueObject = maxDelayValue.get()
            if (maxDelayValueObject < newValue) set(maxDelayValueObject)
            delay = TimeUtils.randomDelay(this.get(), maxDelayValue.get())
        }
    }
    private val guideInt = TextValue("%i", "Random integer (max length 10000)") { customValue.get() }
    private val guideString = TextValue("%s", "Random string (max length 9)") { customValue.get() }
    private val guideShortString = TextValue("%ss", "Random short string (max length 5)") { customValue.get() }
    private val guideLongString = TextValue("%ls", "Random long string (max length 16)") { customValue.get() }
    private val msTimer = MSTimer()

    @EventTarget
    fun onUpdate(event: UpdateEvent?) {
        if (msTimer.hasTimePassed(delay)) {
            mc.thePlayer.sendChatMessage(
                if (customValue.get()) replace("!N.i.g.h.t.X. .C.l.i.e.n.t!") else "!N.i.g.h.t.X. .C.l.i.e.n.t!" + " >" + RandomUtils.randomString(
                    5 + Random().nextInt(5)
                ) + "<"
            )
            msTimer.reset()
            delay = TimeUtils.randomDelay(minDelayValue.get(), maxDelayValue.get())
        }
    }

    private fun replace(`object`: String): String {
        var `object` = `object`
        val r = Random()
        while (`object`.contains("%f")) `object` = `object`.substring(
            0,
            `object`.indexOf("%f")
        ) + r.nextFloat() + `object`.substring(`object`.indexOf("%f") + "%f".length)
        while (`object`.contains("%i")) `object` = `object`.substring(
            0,
            `object`.indexOf("%i")
        ) + r.nextInt(10000) + `object`.substring(`object`.indexOf("%i") + "%i".length)
        while (`object`.contains("%s")) `object` = `object`.substring(
            0,
            `object`.indexOf("%s")
        ) + RandomUtils.randomString(r.nextInt(8) + 1) + `object`.substring(`object`.indexOf("%s") + "%s".length)
        while (`object`.contains("%ss")) `object` = `object`.substring(
            0,
            `object`.indexOf("%ss")
        ) + RandomUtils.randomString(r.nextInt(4) + 1) + `object`.substring(`object`.indexOf("%ss") + "%ss".length)
        while (`object`.contains("%ls")) `object` = `object`.substring(
            0,
            `object`.indexOf("%ls")
        ) + RandomUtils.randomString(r.nextInt(15) + 1) + `object`.substring(`object`.indexOf("%ls") + "%ls".length)
        return `object`
    }

    private var delay = TimeUtils.randomDelay(minDelayValue.get(), maxDelayValue.get())
}
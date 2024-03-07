package net.aspw.client.features.module.impl.other

import net.aspw.client.Launch
import net.aspw.client.event.EventTarget
import net.aspw.client.event.TextEvent
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.utils.misc.StringUtils
import net.aspw.client.utils.render.ColorUtils.translateAlternateColorCodes
import net.aspw.client.value.TextValue

@ModuleInfo(name = "StreamerMode", spacedName = "Streamer Mode", category = ModuleCategory.OTHER)
class StreamerMode : Module() {
    private val fakeNameValue = TextValue("FakeName", "ElonMusk")

    @EventTarget
    fun onText(event: TextEvent) {
        if (mc.thePlayer == null || event.text!!.contains(Launch.CLIENT_CHAT + "§3") || event.text!!.startsWith("/") || event.text!!.startsWith(
                "."
            )
        ) return
        event.text = StringUtils.replace(
            event.text,
            mc.thePlayer.name,
            translateAlternateColorCodes(fakeNameValue.get())
        )
    }
}
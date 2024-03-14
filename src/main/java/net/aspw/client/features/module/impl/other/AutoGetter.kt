package net.aspw.client.features.module.impl.other

import net.aspw.client.event.EventState
import net.aspw.client.event.EventTarget
import net.aspw.client.event.MotionEvent
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.utils.timer.MSTimer
import net.aspw.client.value.BoolValue

@ModuleInfo(name = "AutoGetter", spacedName = "Auto Getter", category = ModuleCategory.OTHER)
class AutoGetter : Module() {
    private val shotBowXpSlaughterValue = BoolValue("ShotBowXP-Slaughter", false)
    private val purplePrisonAutoSellValue = BoolValue("PurplePrison-AutoSell", false)

    private val shotBowXpSlaughterTimer = MSTimer()
    private val purplePrisonAutoSellTimer = MSTimer()

    @EventTarget
    fun onMotion(event: MotionEvent) {
        if (shotBowXpSlaughterValue.get()) {
            if (event.eventState == EventState.PRE) {
                if (shotBowXpSlaughterTimer.hasTimePassed(8000L)) {
                    mc.thePlayer.sendChatMessage("/myxp")
                    shotBowXpSlaughterTimer.reset()
                }
            }
        }

        if (purplePrisonAutoSellValue.get()) {
            if (event.eventState == EventState.PRE) {
                if (purplePrisonAutoSellTimer.hasTimePassed(8000L)) {
                    mc.thePlayer.sendChatMessage("/sellall")
                    purplePrisonAutoSellTimer.reset()
                }
            }
        }
    }
}
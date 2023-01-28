package net.aspw.nightx.features.module.modules.misc

import com.google.gson.JsonParser
import net.aspw.nightx.NightX
import net.aspw.nightx.features.module.Module
import net.aspw.nightx.features.module.ModuleCategory
import net.aspw.nightx.features.module.ModuleInfo
import net.aspw.nightx.utils.misc.HttpUtils.get
import net.aspw.nightx.utils.timer.MSTimer
import net.aspw.nightx.value.BoolValue
import net.aspw.nightx.value.IntegerValue
import net.aspw.nightx.visual.hud.element.elements.Notification

@ModuleInfo(name = "BanNotifier", spacedName = "Ban Notifier", category = ModuleCategory.MISC)
class BanNotifier : Module() {
    val alertValue = BoolValue("Alert", true)
    val serverCheckValue = BoolValue("ServerCheck", true)
    val alertTimeValue = IntegerValue("Alert-Time", 10, 1, 50, " seconds")

    init {
        object : Thread("Hypixel-BanChecker") {
            override fun run() {
                val checkTimer = MSTimer()
                while (true) {
                    if (checkTimer.hasTimePassed(60000L)) {
                        try {
                            val apiContent = get(API_PUNISHMENT)
                            val jsonObject = JsonParser().parse(apiContent).asJsonObject
                            if (jsonObject["success"].asBoolean && jsonObject.has("record")) {
                                val objectAPI = jsonObject["record"].asJsonObject
                                WATCHDOG_BAN_LAST_MIN = objectAPI["watchdog_lastMinute"].asInt
                                var staffBanTotal = objectAPI["staff_total"].asInt
                                if (staffBanTotal < LAST_TOTAL_STAFF) staffBanTotal = LAST_TOTAL_STAFF
                                if (LAST_TOTAL_STAFF == -1) LAST_TOTAL_STAFF = staffBanTotal else {
                                    STAFF_BAN_LAST_MIN = staffBanTotal - LAST_TOTAL_STAFF
                                    LAST_TOTAL_STAFF = staffBanTotal
                                }
                                if (NightX.moduleManager.getModule(BanNotifier::class.java)!!.state && alertValue.get() && mc.thePlayer != null && (!serverCheckValue.get() || isOnHypixel)) if (STAFF_BAN_LAST_MIN > 0) NightX.hud.addNotification(
                                    Notification(
                                        "" + STAFF_BAN_LAST_MIN + " players got banned in the last minute!",
                                        if (STAFF_BAN_LAST_MIN > 3) Notification.Type.WARNING else Notification.Type.WARNING,
                                        alertTimeValue.get() * 1000L
                                    )
                                ) else NightX.hud.addNotification(
                                    Notification(
                                        "Didn't got banned any player in the last minute.",
                                        Notification.Type.SUCCESS,
                                        alertTimeValue.get() * 1000L
                                    )
                                )
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            if (NightX.moduleManager.getModule(BanNotifier::class.java)!!.state && alertValue.get() && mc.thePlayer != null && (!serverCheckValue.get() || isOnHypixel)) NightX.hud.addNotification(
                                Notification(
                                    "Didn't get banned any player in the last minute.",
                                    Notification.Type.ERROR,
                                    1000L
                                )
                            )
                        }
                        checkTimer.reset()
                    }
                }
            }
        }.start()
    }

    val isOnHypixel: Boolean
        get() = !mc.isIntegratedServerRunning && mc.currentServerData.serverIP.contains("hypixel.net")

    companion object {
        private val API_PUNISHMENT =
            aB("68747470733a2f2f6170692e706c616e636b652e696f2f6879706978656c2f76312f70756e6973686d656e745374617473")
        var WATCHDOG_BAN_LAST_MIN = 0
        var LAST_TOTAL_STAFF = -1
        var STAFF_BAN_LAST_MIN = 0
        fun aB(str: String): String {
            var result = ""
            val charArray = str.toCharArray()
            var i = 0
            while (i < charArray.size) {
                val st = "" + charArray[i] + "" + charArray[i + 1]
                val ch = st.toInt(16).toChar()
                result = result + ch
                i = i + 2
            }
            return result
        }
    }
}
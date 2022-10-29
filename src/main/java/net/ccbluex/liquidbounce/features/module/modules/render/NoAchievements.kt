package net.ccbluex.liquidbounce.features.module.modules.render

import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo

@ModuleInfo(name = "NoAchievements", spacedName = "No Achievements", category = ModuleCategory.RENDER, array = false)
class NoAchievements : Module() {
    init {
        state = true
    }
}
package net.aspw.nightx.features.module.modules.render

import net.aspw.nightx.features.module.Module
import net.aspw.nightx.features.module.ModuleCategory
import net.aspw.nightx.features.module.ModuleInfo

@ModuleInfo(name = "NoAchievements", spacedName = "No Achievements", category = ModuleCategory.RENDER, array = false)
class NoAchievements : Module() {
    init {
        state = true
    }
}
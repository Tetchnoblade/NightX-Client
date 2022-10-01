package net.ccbluex.liquidbounce.features.module.modules.render

import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.value.BoolValue

@ModuleInfo(name = "ShowInvis", spacedName = "Show Invis", description = "", category = ModuleCategory.RENDER)
class ShowInvis : Module() {
    val barriersValue = BoolValue("Barriers", false)
    val entitiesValue = BoolValue("Entities", true)
}
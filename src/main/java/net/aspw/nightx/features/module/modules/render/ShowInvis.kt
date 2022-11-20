package net.aspw.nightx.features.module.modules.render

import net.aspw.nightx.features.module.Module
import net.aspw.nightx.features.module.ModuleCategory
import net.aspw.nightx.features.module.ModuleInfo
import net.aspw.nightx.value.BoolValue

@ModuleInfo(name = "ShowInvis", spacedName = "Show Invis", category = ModuleCategory.RENDER)
class ShowInvis : Module() {
    val barriersValue = BoolValue("Barriers", false)
    val entitiesValue = BoolValue("Entities", true)
}
package net.aspw.client.features.module.impl.visual

import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.value.BoolValue

@ModuleInfo(name = "ShowInvis", spacedName = "Show Invis", category = ModuleCategory.VISUAL, array = false)
class ShowInvis : Module() {
    val barriersValue = BoolValue("Barrier", false)
    val entitiesValue = BoolValue("Entity", true)
}
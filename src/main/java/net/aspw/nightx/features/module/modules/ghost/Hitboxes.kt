package net.aspw.nightx.features.module.modules.ghost

import net.aspw.nightx.features.module.Module
import net.aspw.nightx.features.module.ModuleCategory
import net.aspw.nightx.features.module.ModuleInfo
import net.aspw.nightx.value.FloatValue

@ModuleInfo(name = "Hitboxes", category = ModuleCategory.GHOST)
class Hitboxes : Module() {
    val sizeValue = FloatValue("Size", 1F, 0F, 1F)
}
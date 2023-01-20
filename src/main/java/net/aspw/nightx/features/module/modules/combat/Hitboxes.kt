package net.aspw.nightx.features.module.modules.combat

import net.aspw.nightx.features.module.Module
import net.aspw.nightx.features.module.ModuleCategory
import net.aspw.nightx.features.module.ModuleInfo
import net.aspw.nightx.value.FloatValue

@ModuleInfo(name = "Hitboxes", category = ModuleCategory.COMBAT)
class Hitboxes : Module() {
    val sizeValue = FloatValue("Size", 1F, 0F, 1F)
}
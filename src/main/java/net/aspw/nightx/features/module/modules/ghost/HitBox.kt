package net.aspw.nightx.features.module.modules.ghost

import net.aspw.nightx.features.module.Module
import net.aspw.nightx.features.module.ModuleCategory
import net.aspw.nightx.features.module.ModuleInfo
import net.aspw.nightx.value.FloatValue

@ModuleInfo(name = "HitBox", spacedName = "Hit Box", category = ModuleCategory.GHOST)
class HitBox : Module() {
    val sizeValue = FloatValue("Size", 1F, 0F, 1F)
}
package net.aspw.nightx.features.module.modules.world

import net.aspw.nightx.features.module.Module
import net.aspw.nightx.features.module.ModuleCategory
import net.aspw.nightx.features.module.ModuleInfo
import net.aspw.nightx.value.IntegerValue

@ModuleInfo(name = "FastPlace", spacedName = "Fast Place", category = ModuleCategory.WORLD)
class FastPlace : Module() {
    val speedValue = IntegerValue("Speed", 0, 0, 4)
}

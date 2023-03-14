package net.aspw.client.features.module.modules.world

import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.value.IntegerValue

@ModuleInfo(name = "FastPlace", spacedName = "Fast Place", category = ModuleCategory.WORLD)
class FastPlace : Module() {
    val speedValue = IntegerValue("Speed", 0, 0, 4)
}

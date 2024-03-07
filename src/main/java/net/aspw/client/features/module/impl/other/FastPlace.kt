package net.aspw.client.features.module.impl.other

import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.value.IntegerValue

@ModuleInfo(name = "FastPlace", spacedName = "Fast Place", category = ModuleCategory.OTHER)
class FastPlace : Module() {
    val speedValue = IntegerValue("Speed", 0, 0, 4)
}

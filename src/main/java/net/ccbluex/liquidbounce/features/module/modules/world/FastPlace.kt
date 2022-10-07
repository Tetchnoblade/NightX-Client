package net.ccbluex.liquidbounce.features.module.modules.world

import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.value.IntegerValue

@ModuleInfo(name = "FastPlace", spacedName = "Fast Place", category = ModuleCategory.WORLD)
class FastPlace : Module() {
    val speedValue = IntegerValue("Speed", 0, 0, 4)
}

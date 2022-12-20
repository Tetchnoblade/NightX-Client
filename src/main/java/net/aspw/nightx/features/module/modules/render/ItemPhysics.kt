package net.aspw.nightx.features.module.modules.render

import net.aspw.nightx.features.module.Module
import net.aspw.nightx.features.module.ModuleCategory
import net.aspw.nightx.features.module.ModuleInfo
import net.aspw.nightx.value.FloatValue

@ModuleInfo(name = "ItemPhysics", spacedName = "Item Physics", category = ModuleCategory.RENDER)
class ItemPhysics : Module() {
    val xzValue = FloatValue("X-Z", 2F, 0F, 2F, "x")
}
package net.aspw.client.features.module.modules.render

import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.value.FloatValue

@ModuleInfo(name = "ItemPhysics", spacedName = "Item Physics", category = ModuleCategory.RENDER)
class ItemPhysics : Module() {
    val xzValue = FloatValue("X-Z", 2F, 0F, 2F, "x")
}
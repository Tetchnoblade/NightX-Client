package net.aspw.nightx.features.module.modules.render

import net.aspw.nightx.features.module.Module
import net.aspw.nightx.features.module.ModuleCategory
import net.aspw.nightx.features.module.ModuleInfo

@ModuleInfo(name = "ItemPhysics", spacedName = "Item Physics", category = ModuleCategory.RENDER, array = false)
class ItemPhysics : Module() {
    init {
        state = true
    }
}
package net.aspw.nightx.features.module.modules.client

import net.aspw.nightx.features.module.Module
import net.aspw.nightx.features.module.ModuleCategory
import net.aspw.nightx.features.module.ModuleInfo
import net.aspw.nightx.value.BoolValue

@ModuleInfo(
    name = "Gui",
    category = ModuleCategory.CLIENT,
    canEnable = false
)
class Fix : Module() {
    companion object {
        @JvmField
        val fixValue = BoolValue("Fix", true)
    }
}
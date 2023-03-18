package net.aspw.client.features.module.modules.client

import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.value.BoolValue

@ModuleInfo(
    name = "Gui",
    category = ModuleCategory.CLIENT,
    canEnable = false
)
class Fix : Module() {
    companion object {
        @JvmField
        val fixValue = BoolValue("Fix", false)
    }
}
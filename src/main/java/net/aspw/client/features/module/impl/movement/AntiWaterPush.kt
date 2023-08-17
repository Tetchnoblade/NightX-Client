package net.aspw.client.features.module.impl.movement

import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.value.BoolValue

@ModuleInfo(
    name = "AntiWaterPush",
    spacedName = "Anti Water Push",
    description = "",
    category = ModuleCategory.MOVEMENT
)
class AntiWaterPush : Module() {
    val waterValue = BoolValue("Water", true)
    val lavaValue = BoolValue("Lava", true)
}
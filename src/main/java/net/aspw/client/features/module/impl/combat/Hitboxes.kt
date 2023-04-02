package net.aspw.client.features.module.impl.combat

import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.value.FloatValue

@ModuleInfo(name = "Hitboxes", category = ModuleCategory.COMBAT)
class Hitboxes : Module() {
    val sizeValue = FloatValue("Size", 1F, 0F, 1F)
}
package net.ccbluex.liquidbounce.features.module.modules.render

import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.value.FloatValue

@ModuleInfo(name = "ItemPhysics", spacedName = "Item Physics", description = "", category = ModuleCategory.RENDER)
class ItemPhysics : Module() {
    val itemWeight = FloatValue("Weight", 1F, 0F, 1F, "x")
    override val tag: String
        get() = "${itemWeight.get()}"

    init {
        state = true
    }
}
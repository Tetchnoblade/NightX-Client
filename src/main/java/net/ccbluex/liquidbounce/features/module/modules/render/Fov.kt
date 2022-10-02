package net.ccbluex.liquidbounce.features.module.modules.render

import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.value.FloatValue

@ModuleInfo(name = "Fov", description = "", category = ModuleCategory.RENDER, array = false)
class Fov : Module() {
    val fovValue = FloatValue("FOV", 1.4f, 0f, 1.5f, "x")

    init {
        state = true
    }
}
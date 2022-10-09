package net.ccbluex.liquidbounce.features.module.modules.render

import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.value.BoolValue

@ModuleInfo(name = "SilentView", category = ModuleCategory.RENDER, forceNoSound = true, onlyEnable = true, array = false)
class SilentView : Module() {

    val headValue = BoolValue("Head", true)
    val bodyValue = BoolValue("Body", true)
}
package net.ccbluex.liquidbounce.features.module.modules.client

import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.EntityUtils

@ModuleInfo(name = "TargetMobs", category = ModuleCategory.CLIENT, array = false)
class TargetMobs : Module() {
    override fun onEnable() {
        super.onEnable()
        EntityUtils.targetMobs = true
    }

    override fun onDisable() {
        super.onDisable()
        EntityUtils.targetMobs = false
    }

    init {
        state = true
    }
}
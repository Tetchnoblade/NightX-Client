package net.ccbluex.liquidbounce.features.module.modules.client

import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.EntityUtils

@ModuleInfo(name = "TargetInvisible", category = ModuleCategory.CLIENT, array = false)
class TargetInvisible : Module() {
    override fun onEnable() {
        super.onEnable()
        EntityUtils.targetInvisible = true
    }

    override fun onDisable() {
        super.onDisable()
        EntityUtils.targetInvisible = false
    }

    init {
        state = true
    }
}
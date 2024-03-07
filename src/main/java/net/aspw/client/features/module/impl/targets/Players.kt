package net.aspw.client.features.module.impl.targets

import net.aspw.client.Launch
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.utils.EntityUtils

@ModuleInfo(name = "Players", category = ModuleCategory.TARGETS, array = false)
class Players : Module() {
    override fun onEnable() {
        EntityUtils.targetPlayer = true
    }

    override fun onDisable() {
        EntityUtils.targetPlayer = false
    }

    init {
        if (EntityUtils.targetPlayer != state)
            EntityUtils.targetPlayer = false
        if (!Launch.fileManager.modulesConfig.hasConfig() || !Launch.fileManager.valuesConfig.hasConfig())
            state = true
    }
}
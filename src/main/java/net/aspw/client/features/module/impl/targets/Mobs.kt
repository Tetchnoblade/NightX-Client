package net.aspw.client.features.module.impl.targets

import net.aspw.client.Launch
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.utils.EntityUtils

@ModuleInfo(name = "Mobs", category = ModuleCategory.TARGETS, array = false)
class Mobs : Module() {
    override fun onEnable() {
        EntityUtils.targetMobs = true
    }

    override fun onDisable() {
        EntityUtils.targetMobs = false
    }

    init {
        if (EntityUtils.targetMobs != state)
            EntityUtils.targetMobs = false
        if (!Launch.fileManager.modulesConfig.hasConfig() || !Launch.fileManager.valuesConfig.hasConfig())
            state = true
    }
}
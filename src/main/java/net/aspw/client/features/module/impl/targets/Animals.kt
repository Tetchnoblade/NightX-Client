package net.aspw.client.features.module.impl.targets

import net.aspw.client.Launch
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.utils.EntityUtils

@ModuleInfo(name = "Animals", category = ModuleCategory.TARGETS, array = false)
class Animals : Module() {
    override fun onEnable() {
        EntityUtils.targetAnimals = true
    }

    override fun onDisable() {
        EntityUtils.targetAnimals = false
    }

    init {
        if (EntityUtils.targetAnimals != state)
            EntityUtils.targetAnimals = false
        if (!Launch.fileManager.modulesConfig.hasConfig() || !Launch.fileManager.valuesConfig.hasConfig())
            state = true
    }
}
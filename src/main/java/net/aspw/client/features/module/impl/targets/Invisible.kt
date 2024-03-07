package net.aspw.client.features.module.impl.targets

import net.aspw.client.Launch
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.utils.EntityUtils

@ModuleInfo(name = "Invisible", category = ModuleCategory.TARGETS, array = false)
class Invisible : Module() {
    override fun onEnable() {
        EntityUtils.targetInvisible = true
    }

    override fun onDisable() {
        EntityUtils.targetInvisible = false
    }

    init {
        if (EntityUtils.targetInvisible != state)
            EntityUtils.targetInvisible = false
        if (!Launch.fileManager.modulesConfig.hasConfig() || !Launch.fileManager.valuesConfig.hasConfig())
            state = true
    }
}
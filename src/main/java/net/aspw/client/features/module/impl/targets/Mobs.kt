package net.aspw.client.features.module.impl.targets

import net.aspw.client.Client
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.util.EntityUtils

@ModuleInfo(name = "Mobs", description = "", category = ModuleCategory.TARGETS, array = false)
class Mobs : Module() {
    override fun onEnable() {
        EntityUtils.targetMobs = true
    }

    override fun onDisable() {
        EntityUtils.targetMobs = false
    }

    init {
        if (!Client.fileManager.modulesConfig.hasConfig() || !Client.fileManager.valuesConfig.hasConfig())
            state = true
    }
}
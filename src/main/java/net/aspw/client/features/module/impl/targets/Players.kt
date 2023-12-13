package net.aspw.client.features.module.impl.targets

import net.aspw.client.Client
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.util.EntityUtils

@ModuleInfo(name = "Players", description = "", category = ModuleCategory.TARGETS, array = false)
class Players : Module() {
    override fun onEnable() {
        EntityUtils.targetPlayer = true
    }

    override fun onDisable() {
        EntityUtils.targetPlayer = false
    }

    init {
        if (!Client.fileManager.modulesConfig.hasConfig() || !Client.fileManager.valuesConfig.hasConfig())
            state = true
    }
}
package net.aspw.client.features.module.impl.targets

import net.aspw.client.Client
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.util.EntityUtils

@ModuleInfo(name = "Invisible", description = "", category = ModuleCategory.TARGETS, array = false)
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
        if (!Client.fileManager.modulesConfig.hasConfig() || !Client.fileManager.valuesConfig.hasConfig())
            state = true
    }
}
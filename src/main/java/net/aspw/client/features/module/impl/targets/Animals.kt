package net.aspw.client.features.module.impl.targets

import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.utils.EntityUtils

@ModuleInfo(name = "Animals", category = ModuleCategory.TARGETS, array = false)
class Animals : Module() {
    override fun onEnable() {
        super.onEnable()
        EntityUtils.targetAnimals = true
    }

    override fun onDisable() {
        super.onDisable()
        EntityUtils.targetAnimals = false
    }

    init {
        state = true
    }
}
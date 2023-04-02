package net.aspw.client.features.module.impl.targets

import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.utils.EntityUtils

@ModuleInfo(name = "Mobs", category = ModuleCategory.TARGETS, array = false)
class Mobs : Module() {
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
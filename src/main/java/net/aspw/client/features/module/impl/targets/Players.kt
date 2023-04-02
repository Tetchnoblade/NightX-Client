package net.aspw.client.features.module.impl.targets

import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.utils.EntityUtils

@ModuleInfo(name = "Players", category = ModuleCategory.TARGETS, array = false)
class Players : Module() {
    override fun onEnable() {
        super.onEnable()
        EntityUtils.targetPlayer = true
    }

    override fun onDisable() {
        super.onDisable()
        EntityUtils.targetPlayer = false
    }

    init {
        state = true
    }
}
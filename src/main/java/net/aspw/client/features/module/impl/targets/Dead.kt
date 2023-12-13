package net.aspw.client.features.module.impl.targets

import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.util.EntityUtils

@ModuleInfo(name = "Dead", description = "", category = ModuleCategory.TARGETS, array = false)
class Dead : Module() {
    override fun onEnable() {
        EntityUtils.targetDead = true
    }

    override fun onDisable() {
        EntityUtils.targetDead = false
    }
}
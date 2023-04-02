package net.aspw.client.features.module.impl.targets

import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.utils.EntityUtils

@ModuleInfo(name = "Dead", category = ModuleCategory.TARGETS, array = false)
class Dead : Module() {
    override fun onEnable() {
        super.onEnable()
        EntityUtils.targetDead = true
    }

    override fun onDisable() {
        super.onDisable()
        EntityUtils.targetDead = false
    }
}
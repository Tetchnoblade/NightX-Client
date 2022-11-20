package net.aspw.nightx.features.module.modules.targets

import net.aspw.nightx.features.module.Module
import net.aspw.nightx.features.module.ModuleCategory
import net.aspw.nightx.features.module.ModuleInfo
import net.aspw.nightx.utils.EntityUtils

@ModuleInfo(name = "TargetInvisible", category = ModuleCategory.TARGETS, array = false)
class TargetInvisible : Module() {
    override fun onEnable() {
        super.onEnable()
        EntityUtils.targetInvisible = true
    }

    override fun onDisable() {
        super.onDisable()
        EntityUtils.targetInvisible = false
    }

    init {
        state = true
    }
}
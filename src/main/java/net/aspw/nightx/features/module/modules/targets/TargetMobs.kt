package net.aspw.nightx.features.module.modules.targets

import net.aspw.nightx.features.module.Module
import net.aspw.nightx.features.module.ModuleCategory
import net.aspw.nightx.features.module.ModuleInfo
import net.aspw.nightx.utils.EntityUtils

@ModuleInfo(name = "TargetMobs", category = ModuleCategory.TARGETS, array = false)
class TargetMobs : Module() {
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
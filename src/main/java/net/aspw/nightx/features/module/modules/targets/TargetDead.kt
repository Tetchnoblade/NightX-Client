package net.aspw.nightx.features.module.modules.targets

import net.aspw.nightx.features.module.Module
import net.aspw.nightx.features.module.ModuleCategory
import net.aspw.nightx.features.module.ModuleInfo
import net.aspw.nightx.utils.EntityUtils

@ModuleInfo(name = "TargetDead", category = ModuleCategory.TARGETS, array = false)
class TargetDead : Module() {
    override fun onEnable() {
        super.onEnable()
        EntityUtils.targetDead = true
    }

    override fun onDisable() {
        super.onDisable()
        EntityUtils.targetDead = false
    }
}
package net.aspw.nightx.features.module.modules.targets

import net.aspw.nightx.features.module.Module
import net.aspw.nightx.features.module.ModuleCategory
import net.aspw.nightx.features.module.ModuleInfo
import net.aspw.nightx.utils.EntityUtils

@ModuleInfo(name = "TargetAnimals", category = ModuleCategory.TARGETS, array = false)
class TargetAnimals : Module() {
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
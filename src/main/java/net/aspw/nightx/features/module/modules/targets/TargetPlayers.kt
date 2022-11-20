package net.aspw.nightx.features.module.modules.targets

import net.aspw.nightx.features.module.Module
import net.aspw.nightx.features.module.ModuleCategory
import net.aspw.nightx.features.module.ModuleInfo
import net.aspw.nightx.utils.EntityUtils

@ModuleInfo(name = "TargetPlayers", category = ModuleCategory.TARGETS, array = false)
class TargetPlayers : Module() {
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
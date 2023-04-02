package net.aspw.client.features.module.impl.targets

import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.utils.EntityUtils

@ModuleInfo(name = "Invisible", category = ModuleCategory.TARGETS, array = false)
class Invisible : Module() {
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
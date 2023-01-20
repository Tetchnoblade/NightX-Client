package net.aspw.nightx.features.module.modules.client

import net.aspw.nightx.features.module.Module
import net.aspw.nightx.features.module.ModuleCategory
import net.aspw.nightx.features.module.ModuleInfo
import net.aspw.nightx.value.BoolValue

@ModuleInfo(name = "AntiNausea", spacedName = "Anti Nausea", category = ModuleCategory.CLIENT, array = false)
class AntiNausea : Module() {
    val confusionEffect = BoolValue("Confusion", true)
    val pumpkinEffect = BoolValue("Pumpkin", true)
    val fireEffect = BoolValue("Fire", true)
    val scoreBoard = BoolValue("Scoreboard", false)
    val bossHealth = BoolValue("Boss-Health", false)
}
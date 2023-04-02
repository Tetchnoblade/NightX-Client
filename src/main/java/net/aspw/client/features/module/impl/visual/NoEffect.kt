package net.aspw.client.features.module.impl.visual

import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.value.BoolValue

@ModuleInfo(name = "NoEffect", spacedName = "No Effect", category = ModuleCategory.VISUAL, array = false)
class NoEffect : Module() {
    val confusionEffect = BoolValue("Confusion", true)
    val pumpkinEffect = BoolValue("Pumpkin", true)
    val fireEffect = BoolValue("Fire", true)
    val scoreBoard = BoolValue("Scoreboard", false)
    val bossHealth = BoolValue("Boss-Health", false)
}
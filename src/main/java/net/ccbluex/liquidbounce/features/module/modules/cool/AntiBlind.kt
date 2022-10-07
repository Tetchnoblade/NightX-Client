package net.ccbluex.liquidbounce.features.module.modules.cool

import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.value.BoolValue

@ModuleInfo(name = "AntiBlind", spacedName = "Anti Blind", category = ModuleCategory.COOL)
class AntiBlind : Module() {
    val confusionEffect = BoolValue("Confusion", true)
    val pumpkinEffect = BoolValue("Pumpkin", true)
    val fireEffect = BoolValue("Fire", true)
    val scoreBoard = BoolValue("Scoreboard", false)
    val bossHealth = BoolValue("Boss-Health", false)
}
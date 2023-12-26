package net.aspw.client.features.module.impl.visual

import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.value.BoolValue

@ModuleInfo(
    name = "VisualAbilities",
    spacedName = "Visual Abilities",
    description = "",
    category = ModuleCategory.VISUAL
)
class VisualAbilities : Module() {
    val cameraClip = BoolValue("CameraClip", true)
    val confusionEffect = BoolValue("Confusion", true)
    val pumpkinEffect = BoolValue("Pumpkin", true)
    val fireEffect = BoolValue("Fire", true)
    val scoreBoard = BoolValue("Scoreboard", false)
    val bossHealth = BoolValue("Boss-Health", false)
}
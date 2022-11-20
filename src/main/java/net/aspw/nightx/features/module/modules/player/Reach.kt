package net.aspw.nightx.features.module.modules.player

import net.aspw.nightx.features.module.Module
import net.aspw.nightx.features.module.ModuleCategory
import net.aspw.nightx.features.module.ModuleInfo
import net.aspw.nightx.value.FloatValue

@ModuleInfo(name = "Reach", category = ModuleCategory.PLAYER)
class Reach : Module() {

    val combatReachValue = FloatValue("CombatReach", 6f, 3f, 7f, "m")
    val buildReachValue = FloatValue("BuildReach", 6f, 4.5f, 7f, "m")

    val maxRange: Float
        get() {
            val combatRange = combatReachValue.get()
            val buildRange = buildReachValue.get()

            return if (combatRange > buildRange) combatRange else buildRange
        }
}

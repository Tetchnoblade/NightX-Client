package net.aspw.client.features.module.impl.combat

import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.value.FloatValue

@ModuleInfo(name = "Reach", category = ModuleCategory.COMBAT)
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

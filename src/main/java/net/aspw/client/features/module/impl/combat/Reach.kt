package net.aspw.client.features.module.impl.combat

import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.value.FloatValue
import kotlin.math.max

@ModuleInfo(name = "Reach", description = "", category = ModuleCategory.COMBAT)
class Reach : Module() {

    val combatReachValue = FloatValue("CombatReach", 6f, 3f, 6f, "m")
    val buildReachValue = FloatValue("BuildReach", 6f, 4.5f, 6f, "m")

    val maxRange: Float
        get() = max(combatReachValue.get(), buildReachValue.get())
}

package net.aspw.client.features.module.impl.movement

import net.aspw.client.event.EventTarget
import net.aspw.client.event.MoveEvent
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.value.BoolValue

@ModuleInfo(name = "SafeWalk", spacedName = "Safe Walk", category = ModuleCategory.MOVEMENT)
class SafeWalk : Module() {

    private val airSafeValue = BoolValue("AirSafe", false)

    @EventTarget
    fun onMove(event: MoveEvent) {
        if (airSafeValue.get() || mc.thePlayer.onGround)
            event.isSafeWalk = true
    }
}

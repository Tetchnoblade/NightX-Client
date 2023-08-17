package net.aspw.client.features.module.impl.premium

import net.aspw.client.event.EventTarget
import net.aspw.client.event.MoveEvent
import net.aspw.client.event.UpdateEvent
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.util.connection.LoginID

@ModuleInfo(name = "TestModule", spacedName = "Test Module", description = "", category = ModuleCategory.BETA)
class TestModule : Module() {

    @EventTarget
    fun onMove(event: MoveEvent) {
        event.y = 0.0
    }
}
package net.aspw.client.features.module.modules.client

import net.aspw.client.event.EventTarget
import net.aspw.client.event.Render3DEvent
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.utils.RenderWings
import net.aspw.client.value.BoolValue

@ModuleInfo(name = "Wings", category = ModuleCategory.CLIENT, array = false)
class Wings : Module() {
    private val onlyThirdPerson = BoolValue("Only-ThirdPerson", true)

    @EventTarget
    fun onRenderPlayer(event: Render3DEvent) {
        if (onlyThirdPerson.get() && mc.gameSettings.thirdPersonView == 0) return
        val renderWings = RenderWings()
        renderWings.renderWings(event.partialTicks)
    }
}
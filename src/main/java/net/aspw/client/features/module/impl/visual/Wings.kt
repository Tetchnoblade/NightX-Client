package net.aspw.client.features.module.impl.visual

import net.aspw.client.Client
import net.aspw.client.event.EventTarget
import net.aspw.client.event.Render3DEvent
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.util.RenderWings
import net.aspw.client.value.BoolValue
import java.util.*

@ModuleInfo(name = "Wings", description = "", category = ModuleCategory.VISUAL, array = false)
class Wings : Module() {
    private val onlyThirdPerson = BoolValue("Only-ThirdPerson", true)

    @EventTarget
    fun onRenderPlayer(event: Render3DEvent) {
        if (onlyThirdPerson.get() && mc.gameSettings.thirdPersonView == 0 || Objects.requireNonNull(
                Client.moduleManager.getModule(
                    SilentView::class.java
                )
            )?.state!! && Objects.requireNonNull(Client.moduleManager.getModule(SilentView::class.java))?.silentValue?.get()!! && Objects.requireNonNull(
                Client.moduleManager.getModule(SilentView::class.java)
            )?.shouldRotate()!!
        ) return
        val renderWings = RenderWings()
        renderWings.renderWings(event.partialTicks)
    }
}
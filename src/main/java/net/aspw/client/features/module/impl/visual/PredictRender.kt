package net.aspw.client.features.module.impl.visual

import net.aspw.client.event.EventTarget
import net.aspw.client.event.Render3DEvent
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.utils.PredictUtils
import net.aspw.client.utils.render.RenderUtils
import net.aspw.client.value.IntegerValue

@ModuleInfo(name = "PredictRender", spacedName = "Predict Render", category = ModuleCategory.VISUAL)
class PredictRender : Module() {
    private val rangeValue = IntegerValue("Range", 20, 0, 100)

    @EventTarget
    fun onRender3D(event: Render3DEvent) {
        val positions = PredictUtils.predict(rangeValue.get())
        RenderUtils.renderLine(positions)
    }
}
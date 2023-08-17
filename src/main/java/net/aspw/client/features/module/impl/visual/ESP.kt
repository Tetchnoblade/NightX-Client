package net.aspw.client.features.module.impl.visual

import net.aspw.client.event.EventTarget
import net.aspw.client.event.Render3DEvent
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.util.EntityUtils
import net.aspw.client.util.extensions.getDistanceToEntityBox
import net.aspw.client.util.render.RenderUtils
import java.awt.Color

@ModuleInfo(name = "ESP", description = "", category = ModuleCategory.VISUAL)
class ESP : Module() {
    @EventTarget
    fun onRender3D(event: Render3DEvent?) {
        val player = mc.thePlayer ?: return
        val selectedEntities = mc.theWorld.loadedEntityList.filter { entity ->
            entity != null && entity !== player && EntityUtils.isSelected(
                entity,
                false
            ) && mc.thePlayer!!.getDistanceToEntityBox(entity).toFloat() <= 42
        }

        for (entity in selectedEntities) {
            RenderUtils.drawEntityBox(
                entity,
                Color.BLACK,
                false
            )
        }
    }
}
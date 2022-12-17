package net.aspw.nightx.features.module.modules.render

import net.aspw.nightx.event.EventTarget
import net.aspw.nightx.event.Render2DEvent
import net.aspw.nightx.event.Render3DEvent
import net.aspw.nightx.features.module.Module
import net.aspw.nightx.features.module.ModuleCategory
import net.aspw.nightx.features.module.ModuleInfo
import net.aspw.nightx.utils.ClientUtils
import net.aspw.nightx.utils.render.ColorUtils.rainbow
import net.aspw.nightx.utils.render.RenderUtils
import net.aspw.nightx.utils.render.shader.shaders.OutlineShader
import net.aspw.nightx.value.BoolValue
import net.aspw.nightx.value.IntegerValue
import net.aspw.nightx.value.ListValue
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.projectile.EntityArrow
import java.awt.Color

@ModuleInfo(name = "ItemESP", spacedName = "Item ESP", category = ModuleCategory.RENDER, array = false)
class ItemESP : Module() {
    private val modeValue = ListValue("Mode", arrayOf("Box", "ShaderOutline"), "Box")
    private val colorRedValue = IntegerValue("R", 255, 0, 255)
    private val colorGreenValue = IntegerValue("G", 255, 0, 255)
    private val colorBlueValue = IntegerValue("B", 255, 0, 255)
    private val colorRainbow = BoolValue("Rainbow", false)

    @EventTarget
    fun onRender3D(event: Render3DEvent?) {
        if (modeValue.get().equals("Box", ignoreCase = true)) {
            val color = if (colorRainbow.get()) rainbow() else Color(
                colorRedValue.get(),
                colorGreenValue.get(),
                colorBlueValue.get()
            )
            for (entity in mc.theWorld.loadedEntityList) {
                if (!(entity is EntityItem || entity is EntityArrow)) continue
                RenderUtils.drawEntityBox(entity, color, true)
            }
        }
    }

    @EventTarget
    fun onRender2D(event: Render2DEvent) {
        if (modeValue.get().equals("ShaderOutline", ignoreCase = true)) {
            OutlineShader.OUTLINE_SHADER.startDraw(event.partialTicks)
            try {
                for (entity in mc.theWorld.loadedEntityList) {
                    if (!(entity is EntityItem || entity is EntityArrow)) continue
                    mc.renderManager.renderEntityStatic(entity, event.partialTicks, true)
                }
            } catch (ex: Exception) {
                ClientUtils.getLogger().error("An error occurred while rendering all item entities for shader esp", ex)
            }
            OutlineShader.OUTLINE_SHADER.stopDraw(
                if (colorRainbow.get()) rainbow() else Color(
                    colorRedValue.get(),
                    colorGreenValue.get(),
                    colorBlueValue.get()
                ), 1f, 1f
            )
        }
    }
}
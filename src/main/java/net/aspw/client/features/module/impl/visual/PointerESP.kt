package net.aspw.client.features.module.impl.visual

import net.aspw.client.event.EventTarget
import net.aspw.client.event.Render2DEvent
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.utils.EntityUtils
import net.aspw.client.utils.render.RenderUtils.drawTriAngle
import net.aspw.client.utils.render.RenderUtils.isInViewFrustrum
import net.aspw.client.value.BoolValue
import net.aspw.client.value.FloatValue
import net.aspw.client.value.IntegerValue
import net.aspw.client.value.ListValue
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.MathHelper
import java.awt.Color
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

@ModuleInfo(name = "PointerESP", spacedName = "Pointer ESP", category = ModuleCategory.VISUAL, array = false)
class PointerESP : Module() {
    private val modeValue = ListValue("Mode", arrayOf("Solid", "Line"), "Line")
    private val redValue = IntegerValue("Red", 255, 0, 255)
    private val greenValue = IntegerValue("Green", 0, 0, 255)
    private val blueValue = IntegerValue("Blue", 255, 0, 255)
    private val alphaValue = IntegerValue("Alpha", 200, 0, 255)
    private val sizeValue = IntegerValue("Size", 250, 50, 500)
    private val radiusValue = FloatValue("TriangleRadius", 3F, 1F, 10F, "m")
    private val noInViewValue = BoolValue("NoEntityInView", false)

    @EventTarget
    fun onRender2D(event: Render2DEvent) {
        val sr = ScaledResolution(mc)
        val color = Color(redValue.get(), greenValue.get(), blueValue.get(), alphaValue.get())

        GlStateManager.pushMatrix()
        //GlStateManager.pushAttrib()
        val size = 50 + sizeValue.get()
        val xOffset = sr.scaledWidth / 2 - 24.5 - sizeValue.get() / 2.0
        val yOffset = sr.scaledHeight / 2 - 25.2 - sizeValue.get() / 2.0
        val playerOffsetX = mc.thePlayer.posX
        val playerOffSetZ = mc.thePlayer.posZ

        for (entity in mc.theWorld.loadedEntityList) {
            if (EntityUtils.isSelected(entity, true) && (!noInViewValue.get() || !isInViewFrustrum(entity))) {
                val pos1 =
                    (((entity.posX + (entity.posX - entity.lastTickPosX) * mc.timer.renderPartialTicks) - playerOffsetX) * 0.2)
                val pos2 =
                    (((entity.posZ + (entity.posZ - entity.lastTickPosZ) * mc.timer.renderPartialTicks) - playerOffSetZ) * 0.2)
                val cos = cos(mc.thePlayer.rotationYaw * (Math.PI * 2 / 360))
                val sin = sin(mc.thePlayer.rotationYaw * (Math.PI * 2 / 360))
                val rotY = -(pos2 * cos - pos1 * sin)
                val rotX = -(pos1 * cos + pos2 * sin)
                val var7 = 0 - rotX
                val var9 = 0 - rotY
                if (MathHelper.sqrt_double(var7 * var7 + var9 * var9) < size / 2 - 4) {
                    val angle = (atan2(rotY - 0, rotX - 0) * 180 / Math.PI).toFloat()
                    val x = ((size / 2) * cos(Math.toRadians(angle.toDouble()))) + xOffset + size / 2
                    val y = ((size / 2) * sin(Math.toRadians(angle.toDouble()))) + yOffset + size / 2
                    GlStateManager.pushMatrix()
                    GlStateManager.translate(x, y, 0.0)
                    GlStateManager.rotate(angle, 0F, 0F, 1F)
                    GlStateManager.scale(1.0, 1.0, 1.0)
                    drawTriAngle(0F, 0F, radiusValue.get(), 3F, color, modeValue.get().equals("solid", true))
                    GlStateManager.popMatrix()
                }
            }
        }
        //GlStateManager.popAttrib()
        GlStateManager.popMatrix()
    }
}

package net.aspw.client.visual.client.clickgui.tab.value.impl

import net.aspw.client.util.MouseUtils
import net.aspw.client.util.newfont.FontLoaders
import net.aspw.client.util.render.RenderUtils
import net.aspw.client.value.ListValue
import net.aspw.client.visual.client.clickgui.tab.ColorManager
import net.aspw.client.visual.client.clickgui.tab.extensions.animSmooth
import net.aspw.client.visual.client.clickgui.tab.value.ValueElement
import net.aspw.client.visual.font.Fonts
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11.*
import java.awt.Color

class ListElement(val saveValue: ListValue) : ValueElement<String>(saveValue) {
    private var expandHeight = 0F
    private var expansion = false

    private val maxSubWidth =
        -(saveValue.values.map { -FontLoaders.SF20.getStringWidth(it) }.sorted().firstOrNull() ?: 0F).toFloat() + 20F

    companion object {
        val expanding = ResourceLocation("client/clickgui/expand.png")
    }

    override fun drawElement(
        mouseX: Int,
        mouseY: Int,
        x: Float,
        y: Float,
        width: Float,
        bgColor: Color,
        accentColor: Color
    ): Float {
        expandHeight = expandHeight.animSmooth(if (expansion) 16F * (saveValue.values.size - 1F) else 0F, 0.5F)
        val percent = expandHeight / (16F * (saveValue.values.size - 1F))
        FontLoaders.SF20.drawString(value.name, x + 10F, y + 10F - FontLoaders.SF20.height / 2F + 2F, -1)
        RenderUtils.originalRoundedRect(
            x + width - 18F - maxSubWidth,
            y + 2F,
            x + width - 10F,
            y + 18F + expandHeight,
            4F,
            ColorManager.button.rgb
        )
        GlStateManager.resetColor()
        glPushMatrix()
        glTranslatef(x + width - 20F, y + 10F, 0F)
        glPushMatrix()
        glRotatef(180F * percent, 0F, 0F, 1F)
        glColor4f(1F, 1F, 1F, 1F)
        RenderUtils.drawImage(expanding, -4, -4, 8, 8)
        glPopMatrix()
        glPopMatrix()
        FontLoaders.SF20.drawString(value.get(), x + width - 14F - maxSubWidth, y + 6F, -1)
        glPushMatrix()
        GlStateManager.translate(x + width - 14F - maxSubWidth, y + 7F, 0F)
        GlStateManager.scale(percent, percent, percent)
        var vertHeight = 0F
        if (percent > 0F) for (subV in unusedValues) {
            FontLoaders.SF20.drawString(
                subV,
                0F,
                (16F + vertHeight) * percent - 1F,
                Color(.5F, .5F, .5F, percent.coerceIn(0F, 1F)).rgb
            )
            vertHeight += 16F
        }
        glPopMatrix()
        valueHeight = 20F + expandHeight
        return valueHeight
    }

    override fun onClick(mouseX: Int, mouseY: Int, x: Float, y: Float, width: Float) {
        if (isDisplayable() && MouseUtils.mouseWithinBounds(mouseX, mouseY, x, y + 2F, x + width, y + 18F))
            expansion = !expansion
        if (expansion) {
            var vertHeight = 0F
            for (subV in unusedValues) {
                if (MouseUtils.mouseWithinBounds(
                        mouseX,
                        mouseY,
                        x + width - 14F - maxSubWidth,
                        y + 18F + vertHeight,
                        x + width - 10F,
                        y + 34F + vertHeight
                    )
                ) {
                    value.set(subV)
                    expansion = false
                    break
                }
                vertHeight += 16F
            }
        }
    }

    val unusedValues: List<String>
        get() = saveValue.values.filter { it != value.get() }
}
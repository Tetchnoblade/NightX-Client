package net.aspw.client.visual.client.clickgui.element.module.value.impl

import net.aspw.client.utils.MouseUtils
import net.aspw.client.value.BoolValue
import net.aspw.client.visual.client.clickgui.element.components.Checkbox
import net.aspw.client.visual.client.clickgui.element.module.value.ValueElement
import net.aspw.client.visual.font.Fonts
import java.awt.Color

class BooleanElement(value: BoolValue) : ValueElement<Boolean>(value) {
    private val checkbox = Checkbox()

    override fun drawElement(
        mouseX: Int,
        mouseY: Int,
        x: Float,
        y: Float,
        width: Float,
        bgColor: Color,
        accentColor: Color
    ): Float {
        checkbox.state = value.get()
        checkbox.onDraw(x + 10F, y + 5F, 10F, 10F, bgColor, accentColor)
        Fonts.fontSFUI40.drawString(value.name, x + 25F, y + 10F - Fonts.fontSFUI40.FONT_HEIGHT / 2F + 2F, -1)
        return valueHeight
    }

    override fun onClick(mouseX: Int, mouseY: Int, x: Float, y: Float, width: Float) {
        if (isDisplayable() && MouseUtils.mouseWithinBounds(mouseX, mouseY, x, y, x + width, y + 20F))
            value.set(!value.get())
    }
}
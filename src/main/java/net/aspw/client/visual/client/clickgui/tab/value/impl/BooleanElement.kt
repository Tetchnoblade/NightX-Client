package net.aspw.client.visual.client.clickgui.tab.value.impl

import net.aspw.client.utils.MouseUtils
import net.aspw.client.value.BoolValue
import net.aspw.client.visual.client.clickgui.tab.components.Checkbox
import net.aspw.client.visual.client.clickgui.tab.value.ValueElement
import net.aspw.client.visual.font.smooth.FontLoaders
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
        FontLoaders.SF20.drawStringWithShadow(
            value.name,
            x + 25f.toDouble(),
            y + 10F - FontLoaders.SF20.height / 2F + 2F.toDouble(),
            -1
        )
        return valueHeight
    }

    override fun onClick(mouseX: Int, mouseY: Int, x: Float, y: Float, width: Float) {
        if (isDisplayable() && MouseUtils.mouseWithinBounds(mouseX, mouseY, x, y, x + width, y + 20F))
            value.set(!value.get())
    }
}
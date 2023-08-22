package net.aspw.client.visual.client.clickgui.tab.value.impl

import net.aspw.client.util.MouseUtils
import net.aspw.client.util.newfont.FontLoaders
import net.aspw.client.util.render.RenderUtils
import net.aspw.client.value.IntegerValue
import net.aspw.client.visual.client.clickgui.tab.ColorManager
import net.aspw.client.visual.client.clickgui.tab.components.Slider
import net.aspw.client.visual.client.clickgui.tab.value.ValueElement
import net.aspw.client.visual.font.Fonts
import java.awt.Color

class IntElement(val savedValue: IntegerValue) : ValueElement<Int>(savedValue) {
    private val slider = Slider()
    private var dragged = false

    override fun drawElement(
        mouseX: Int,
        mouseY: Int,
        x: Float,
        y: Float,
        width: Float,
        bgColor: Color,
        accentColor: Color
    ): Float {
        val valueDisplay = 30F + FontLoaders.SF20.getStringWidth("${savedValue.maximum}${savedValue.suffix}")
        val maxLength = FontLoaders.SF20.getStringWidth("${savedValue.maximum}${savedValue.suffix}")
        val minLength = FontLoaders.SF20.getStringWidth("${savedValue.minimum}${savedValue.suffix}")
        val nameLength = FontLoaders.SF20.getStringWidth(value.name)
        val sliderWidth = width - 50F - nameLength - maxLength - minLength - valueDisplay
        val startPoint = x + width - 20F - sliderWidth - maxLength - valueDisplay
        if (dragged)
            savedValue.set(
                (savedValue.minimum + (savedValue.maximum - savedValue.minimum) / sliderWidth * (mouseX - startPoint)).toInt()
                    .coerceIn(savedValue.minimum, savedValue.maximum)
            )
        val currLength = FontLoaders.SF20.getStringWidth("${savedValue.get()}${savedValue.suffix}")
        FontLoaders.SF20.drawString(value.name, x + 10F, y + 10F - FontLoaders.SF20.height / 2F + 2F, -1)
        FontLoaders.SF20.drawString(
            "${savedValue.maximum}${savedValue.suffix}",
            x + width - 10F - maxLength - valueDisplay,
            y + 10F - FontLoaders.SF20.height / 2F + 2F, -1
        )
        FontLoaders.SF20.drawString(
            "${savedValue.minimum}${savedValue.suffix}",
            x + width - 30F - sliderWidth - maxLength - minLength - valueDisplay,
            y + 10F - FontLoaders.SF20.height / 2F + 2F, -1
        )
        slider.setValue(
            savedValue.get().coerceIn(savedValue.minimum, savedValue.maximum).toFloat(),
            savedValue.minimum.toFloat(),
            savedValue.maximum.toFloat()
        )
        slider.onDraw(x + width - 20F - sliderWidth - maxLength - valueDisplay, y + 10F, sliderWidth, accentColor)
        RenderUtils.originalRoundedRect(
            x + width - 5F - valueDisplay,
            y + 2F,
            x + width - 10F,
            y + 18F,
            4F,
            ColorManager.button.rgb
        )
        RenderUtils.customRounded(
            x + width - 18F,
            y + 2F,
            x + width - 10F,
            y + 18F,
            0F,
            4F,
            4F,
            0F,
            ColorManager.buttonOutline.rgb
        )
        RenderUtils.customRounded(
            x + width - 5F - valueDisplay,
            y + 2F,
            x + width + 3F - valueDisplay,
            y + 18,
            4F,
            0F,
            0F,
            4F,
            ColorManager.buttonOutline.rgb
        )
        FontLoaders.SF20.drawString(
            "${savedValue.get()}${savedValue.suffix}",
            x + width + 6F - valueDisplay,
            y + 10F - FontLoaders.SF20.height / 2F + 2F,
            -1
        )
        FontLoaders.SF20.drawString(
            "-",
            x + width - 3F - valueDisplay,
            y + 10F - FontLoaders.SF20.height / 2F + 2F,
            -1
        )
        FontLoaders.SF20.drawString("+", x + width - 17F, y + 10F - FontLoaders.SF20.height / 2F + 2F, -1)

        return valueHeight
    }

    override fun onClick(mouseX: Int, mouseY: Int, x: Float, y: Float, width: Float) {
        val valueDisplay = 30F + FontLoaders.SF20.getStringWidth("${savedValue.maximum}${savedValue.suffix}")
        val maxLength = FontLoaders.SF20.getStringWidth("${savedValue.maximum}${savedValue.suffix}")
        val minLength = FontLoaders.SF20.getStringWidth("${savedValue.minimum}${savedValue.suffix}")
        val nameLength = FontLoaders.SF20.getStringWidth(value.name)
        val sliderWidth = width - 50F - nameLength - maxLength - minLength - valueDisplay
        val startPoint = x + width - 30F - sliderWidth - valueDisplay - maxLength
        val endPoint = x + width - 10F - valueDisplay - maxLength

        if (MouseUtils.mouseWithinBounds(mouseX, mouseY, startPoint, y + 5F, endPoint, y + 15F))
            dragged = true
        if (MouseUtils.mouseWithinBounds(
                mouseX,
                mouseY,
                x + width - 5F - valueDisplay,
                y + 2F,
                x + width + 3F - valueDisplay,
                y + 18F
            )
        )
            savedValue.set((savedValue.get() - 1).coerceIn(savedValue.minimum, savedValue.maximum))
        if (MouseUtils.mouseWithinBounds(mouseX, mouseY, x + width - 18F, y + 2F, x + width - 10F, y + 18F))
            savedValue.set((savedValue.get() + 1).coerceIn(savedValue.minimum, savedValue.maximum))
    }

    override fun onRelease(mouseX: Int, mouseY: Int, x: Float, y: Float, width: Float) {
        if (dragged) dragged = false
    }
}
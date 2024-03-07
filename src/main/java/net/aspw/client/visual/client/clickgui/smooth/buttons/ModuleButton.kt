package net.aspw.client.visual.client.clickgui.smooth.buttons

import net.aspw.client.features.module.Module
import net.aspw.client.utils.MouseButtons
import net.aspw.client.utils.geom.Rectangle
import net.aspw.client.utils.render.RenderUtils.drawRect
import net.aspw.client.value.*
import net.aspw.client.visual.client.clickgui.smooth.SmoothConstants.BACKGROUND_MODULE
import net.aspw.client.visual.client.clickgui.smooth.SmoothConstants.FONT
import net.aspw.client.visual.client.clickgui.smooth.SmoothConstants.MODULE_HEIGHT
import net.aspw.client.visual.client.clickgui.smooth.SmoothConstants.VALUE_HEIGHT
import net.aspw.client.visual.client.clickgui.smooth.buttons.value.*
import net.aspw.client.visual.client.clickgui.smooth.drawHeightCenteredString
import net.aspw.client.visual.client.clickgui.smooth.getHeight
import net.aspw.client.visual.font.semi.Fonts
import java.awt.Color

class ModuleButton(x: Float, y: Float, width: Float, height: Float, var module: Module, var color: Color) :
    Button(x, y, width, height) {
    var open = false
    var valueButtons = ArrayList<BaseValueButton>()

    init {
        val startY = y + height
        for ((count, v) in module.values.withIndex()) {
            when (v) {
                is BoolValue -> valueButtons.add(
                    BoolValueButton(
                        x,
                        startY + MODULE_HEIGHT * count,
                        width,
                        VALUE_HEIGHT,
                        v,
                        color
                    )
                )

                is ListValue -> valueButtons.add(
                    ListValueButton(
                        x,
                        startY + MODULE_HEIGHT * count,
                        width,
                        VALUE_HEIGHT,
                        v,
                        color
                    )
                )

                is IntegerValue -> valueButtons.add(
                    IntegerValueButton(
                        x,
                        startY + MODULE_HEIGHT * count,
                        width,
                        VALUE_HEIGHT,
                        v,
                        color
                    )
                )

                is FloatValue -> valueButtons.add(
                    FloatValueButton(
                        x,
                        startY + MODULE_HEIGHT * count,
                        width,
                        VALUE_HEIGHT,
                        v,
                        color
                    )
                )

                is TextValue -> valueButtons.add(
                    TextValueButton(
                        x,
                        startY + MODULE_HEIGHT * count,
                        width,
                        VALUE_HEIGHT,
                        v,
                        color
                    )
                )
            }
        }
    }

    override fun drawPanel(mouseX: Int, mouseY: Int): Rectangle {
        val background = Rectangle(x, y, width, height)
        drawRect(background, BACKGROUND_MODULE)

        val foreground = Rectangle(x + 2, y, width - 2 * 2, height - 2)
        if (module.state) drawRect(foreground, Color(137, 189, 222).darker().rgb)
        else drawRect(foreground, BACKGROUND_MODULE)

        if (module.state)
            FONT.drawHeightCenteredString(
                module.name,
                x - 4 + (height - getHeight(FONT)) / 2 + 3,
                y - 9 + height + 1 / 2,
                -1
            )
        else FONT.drawHeightCenteredString(
            "§7" + module.name,
            x - 4 + (height - getHeight(FONT)) / 2 + 3,
            y - 9 + height + 1 / 2,
            -1
        )

        if (valueButtons.size > 0) {
            val char = if (open) "§e-" else "§7+"
            Fonts.font72.drawString(char, x + width - 12, y - 11 + height / 2, Int.MAX_VALUE)
        }

        var used = 0f
        var count = 0

        if (open) {
            val startY = y + height
            for (valueButton in valueButtons) {
                if (!valueButton.canDisplay()) {
                    valueButton.show = false
                    continue
                }

                valueButton.show = true

                valueButton.x = x
                valueButton.y = startY + used
                val box = valueButton.drawPanel(mouseX, mouseY)
                used += box.height
                count++
            }
        }

        return Rectangle(x, y, width, used + height)
    }

    override fun mouseAction(mouseX: Int, mouseY: Int, click: Boolean, button: Int) {
        if (isHovered(mouseX, mouseY) && click) {
            when (button) {
                MouseButtons.LEFT.ordinal -> module.toggle()
                MouseButtons.RIGHT.ordinal -> if (module.values.isNotEmpty()) open = !open
            }
        }
    }
}
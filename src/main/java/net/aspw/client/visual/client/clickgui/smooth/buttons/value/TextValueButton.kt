package net.aspw.client.visual.client.clickgui.smooth.buttons.value

import net.aspw.client.utils.geom.Rectangle
import net.aspw.client.utils.render.RenderUtils.drawRect
import net.aspw.client.value.TextValue
import net.aspw.client.visual.client.clickgui.smooth.SmoothConstants.BACKGROUND_VALUE
import net.aspw.client.visual.client.clickgui.smooth.SmoothConstants.FONT
import net.aspw.client.visual.client.clickgui.smooth.drawHeightCenteredString
import java.awt.Color

class TextValueButton(x: Float, y: Float, width: Float, height: Float, var setting: TextValue, var color: Color) :
    BaseValueButton(x, y, width, height, setting) {
    override fun drawPanel(mouseX: Int, mouseY: Int): Rectangle {
        val background = Rectangle(x, y, width, height)
        drawRect(background, BACKGROUND_VALUE)
        FONT.drawHeightCenteredString(setting.name + ": " + setting.get(), x + hOffset, y + 1 + height / 2, -0x1)
        return background
    }

    override fun mouseAction(mouseX: Int, mouseY: Int, click: Boolean, button: Int) {
    }
}

package net.aspw.client.visual.client.clickgui.smooth.buttons.value

import net.aspw.client.utils.geom.Rectangle
import net.aspw.client.value.Value
import net.aspw.client.visual.client.clickgui.smooth.SmoothConstants.FONT
import net.aspw.client.visual.client.clickgui.smooth.buttons.Button
import net.aspw.client.visual.client.clickgui.smooth.getHeight

abstract class BaseValueButton(x: Float, y: Float, width: Float, height: Float, val value: Value<*>) :
    Button(x, y, width, height) {
    val baseRect: Rectangle
        get() = Rectangle(x, y, width, height)
    val hOffset: Float
        get() = (height - getHeight(FONT)) / 2 + 4
    var show = true

    fun canDisplay() = value.canDisplay.invoke()
}
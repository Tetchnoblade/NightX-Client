package net.aspw.client.visual.client.clickgui.smooth.buttons

import net.aspw.client.utils.geom.Rectangle

abstract class Button(var x: Float, var y: Float, var width: Float, var height: Float) {
    abstract fun drawPanel(mouseX: Int, mouseY: Int): Rectangle
    abstract fun mouseAction(mouseX: Int, mouseY: Int, click: Boolean, button: Int)
    fun isHovered(mouseX: Int, mouseY: Int): Boolean {
        return mouseX >= x && mouseX <= x + width && mouseY > y && mouseY < y + height
    }
}

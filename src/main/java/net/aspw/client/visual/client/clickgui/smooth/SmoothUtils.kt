package net.aspw.client.visual.client.clickgui.smooth

import net.aspw.client.visual.font.semi.GameFontRenderer
import net.minecraft.client.gui.FontRenderer
import java.awt.Color

fun FontRenderer.drawHeightCenteredString(string: String, x: Float, y: Float, color: Int) {
    this.drawStringWithShadow(string, x, y - FONT_HEIGHT / 2, color)
}

fun getHeight(font: GameFontRenderer) = font.height
fun getHeight(font: FontRenderer) = font.FONT_HEIGHT

fun dim(color: Color): Color {
    return color.darker()
}
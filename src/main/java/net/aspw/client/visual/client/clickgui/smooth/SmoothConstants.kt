package net.aspw.client.visual.client.clickgui.smooth

import net.aspw.client.visual.font.semi.Fonts
import net.minecraft.client.gui.FontRenderer
import java.awt.Color

object SmoothConstants {
    const val PANEL_WIDTH = 110f
    const val PANEL_HEIGHT = 18f

    const val MODULE_HEIGHT = 18f

    const val VALUE_HEIGHT = 10f
    val FONT: FontRenderer
        get() = Fonts.fontSFUI35

    const val SLIDER_OFFSET = 0.02f

    val BACKGROUND_CATEGORY = Color(2, 2, 2).rgb
    val BACKGROUND_MODULE = Color(14, 14, 14).rgb
    val BACKGROUND_VALUE = Color(5, 5, 5).rgb
}
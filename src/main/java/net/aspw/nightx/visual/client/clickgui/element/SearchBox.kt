package net.aspw.nightx.visual.client.clickgui.element

import net.aspw.nightx.visual.font.Fonts
import net.minecraft.client.gui.GuiTextField

class SearchBox(componentId: Int, x: Int, y: Int, width: Int, height: Int) :
    GuiTextField(componentId, Fonts.fontSFUI40, x, y, width, height) {
    override fun getEnableBackgroundDrawing() = false
}
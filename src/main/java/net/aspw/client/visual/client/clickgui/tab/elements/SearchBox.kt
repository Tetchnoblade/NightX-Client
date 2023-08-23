package net.aspw.client.visual.client.clickgui.tab.elements

import net.aspw.client.visual.font.Fonts
import net.minecraft.client.gui.GuiTextField

class SearchBox(componentId: Int, x: Int, y: Int, width: Int, height: Int) :
    GuiTextField(componentId, Fonts.fontSFUI37, x, y, width, height) {
    override fun getEnableBackgroundDrawing() = false
}
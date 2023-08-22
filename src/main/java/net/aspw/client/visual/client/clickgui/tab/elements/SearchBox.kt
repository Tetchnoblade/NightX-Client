package net.aspw.client.visual.client.clickgui.tab.elements

import net.aspw.client.util.newfont.FontLoaders
import net.aspw.client.visual.font.Fonts
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiTextField

class SearchBox(componentId: Int, x: Int, y: Int, width: Int, height: Int) :
    GuiTextField(componentId, Minecraft.getMinecraft().fontRendererObj, x, y, width, height) {
    override fun getEnableBackgroundDrawing() = false
}
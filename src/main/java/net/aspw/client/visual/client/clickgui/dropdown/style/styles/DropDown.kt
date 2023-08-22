package net.aspw.client.visual.client.clickgui.dropdown.style.styles

import net.aspw.client.features.module.impl.visual.Gui.Companion.generateColor
import net.aspw.client.util.block.BlockUtils.getBlockName
import net.aspw.client.util.newfont.FontLoaders
import net.aspw.client.util.render.RenderUtils
import net.aspw.client.value.*
import net.aspw.client.visual.client.clickgui.dropdown.ClickGui
import net.aspw.client.visual.client.clickgui.dropdown.Panel
import net.aspw.client.visual.client.clickgui.dropdown.elements.ButtonElement
import net.aspw.client.visual.client.clickgui.dropdown.elements.ModuleElement
import net.aspw.client.visual.client.clickgui.dropdown.style.Style
import net.aspw.client.visual.font.Fonts
import net.aspw.client.visual.font.GameFontRenderer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.MathHelper
import org.lwjgl.input.Mouse
import java.awt.Color
import java.math.BigDecimal
import java.util.*

class DropDown : Style() {
    private var mouseDown = false
    private var rightMouseDown = false
    private fun getCategoryColor(): Color {
        return Color(142, 69, 174, 175)
    }

    override fun drawPanel(mouseX: Int, mouseY: Int, panel: Panel) {
        RenderUtils.drawRect(
            (panel.getX() - 2).toFloat(),
            panel.getY().toFloat(),
            (panel.getX() + panel.width + 2).toFloat(),
            (panel.getY() + 21 + panel.fade).toFloat(),
            Color(17, 17, 17).rgb
        )
        RenderUtils.drawRect(
            panel.getX().toFloat() + 1, panel.getY().toFloat() + 19, panel.getX().toFloat() + panel.width - 1,
            (
                    panel.getY() + 18 + panel.fade).toFloat(), Color(26, 26, 26).rgb
        )
        GlStateManager.resetColor()
        FontLoaders.SF19.drawStringWithShadow(
            panel.name.lowercase(Locale.getDefault()),
            (panel.getX() + 2).toFloat().toDouble(),
            (panel.getY() + 6).toFloat().toDouble(),
            Color(255, 255, 255, 200).rgb
        )
        if (panel.open) {
            FontLoaders.icon35.drawStringWithShadow(
                "d",
                (panel.getX() + 85).toFloat().toDouble(),
                (panel.getY() + 4).toFloat().toDouble(),
                Color(255, 255, 255, 200).rgb
            )
        } else {
            FontLoaders.icon35.drawStringWithShadow(
                "d",
                (panel.getX() + 85).toFloat().toDouble(),
                (panel.getY() + 4).toFloat().toDouble(),
                Color(255, 255, 255, 80).rgb
            )
        }
        if (panel.name.equals("combat", true)) {
            FontLoaders.icon35.drawStringWithShadow(
                "J",
                (panel.getX() + 38).toFloat().toDouble(),
                (panel.getY() + 4).toFloat().toDouble(),
                Color(255, 255, 255, 200).rgb
            )
        }
        if (panel.name.equals("movement", true)) {
            FontLoaders.icon35.drawStringWithShadow(
                "G",
                (panel.getX() + 52).toFloat().toDouble(),
                (panel.getY() + 4).toFloat().toDouble(),
                Color(255, 255, 255, 200).rgb
            )
        }
        if (panel.name.equals("player", true)) {
            FontLoaders.icon35.drawStringWithShadow(
                "F",
                (panel.getX() + 34).toFloat().toDouble(),
                (panel.getY() + 4).toFloat().toDouble(),
                Color(255, 255, 255, 200).rgb
            )
        }
        if (panel.name.equals("exploit", true)) {
            FontLoaders.icon35.drawStringWithShadow(
                "A",
                (panel.getX() + 38).toFloat().toDouble(),
                (panel.getY() + 4).toFloat().toDouble(),
                Color(255, 255, 255, 200).rgb
            )
        }
        if (panel.name.equals("other", true)) {
            FontLoaders.icon35.drawStringWithShadow(
                "B",
                (panel.getX() + 31).toFloat().toDouble(),
                (panel.getY() + 4).toFloat().toDouble(),
                Color(255, 255, 255, 200).rgb
            )
        }
        if (panel.name.equals("visual", true)) {
            FontLoaders.icon35.drawStringWithShadow(
                "H",
                (panel.getX() + 33).toFloat().toDouble(),
                (panel.getY() + 4).toFloat().toDouble(),
                Color(255, 255, 255, 200).rgb
            )
        }
        if (panel.name.equals("targets", true)) {
            FontLoaders.icon35.drawStringWithShadow(
                "I",
                (panel.getX() + 37).toFloat().toDouble(),
                (panel.getY() + 4).toFloat().toDouble(),
                Color(255, 255, 255, 200).rgb
            )
        }
        if (panel.name.equals("beta", true)) {
            FontLoaders.icon35.drawStringWithShadow(
                "C",
                (panel.getX() + 30).toFloat().toDouble(),
                (panel.getY() + 4).toFloat().toDouble(),
                Color(255, 255, 255, 200).rgb
            )
        }
    }

    override fun drawButtonElement(mouseX: Int, mouseY: Int, buttonElement: ButtonElement) {
        ClickGui.drawRect(
            buttonElement.x - 1,
            buttonElement.y + 1,
            buttonElement.x + buttonElement.width + 1,
            buttonElement.y + buttonElement.height + 2,
            hoverColor(
                if (buttonElement.color != Int.MAX_VALUE) generateColor() else Color(26, 26, 26),
                buttonElement.hoverTime
            ).rgb
        )
        GlStateManager.resetColor()
        FontLoaders.SF19.drawString(
            buttonElement.displayName.lowercase(Locale.getDefault()), buttonElement.x + 3f,
            buttonElement.y + 6f, Color.WHITE.rgb
        )
    }

    override fun drawModuleElement(mouseX: Int, mouseY: Int, moduleElement: ModuleElement) {
        ClickGui.drawRect(
            moduleElement.x + 1, moduleElement.y + 1, moduleElement.x + moduleElement.width - 1,
            moduleElement.y + moduleElement.height + 2, hoverColor(Color(26, 26, 26), moduleElement.hoverTime).rgb
        )
        ClickGui.drawRect(
            moduleElement.x + 1, moduleElement.y + 1, moduleElement.x
                    + moduleElement.width - 1, moduleElement.y + moduleElement.height + 2, hoverColor(
                Color(
                    getCategoryColor().red,
                    getCategoryColor().green,
                    getCategoryColor().blue,
                    moduleElement.slowlyFade
                ), moduleElement.hoverTime
            ).rgb
        )
        val guiColor = generateColor().rgb
        GlStateManager.resetColor()
        FontLoaders.SF19.drawString(
            moduleElement.displayName.lowercase(Locale.getDefault()), moduleElement.x + 3f,
            moduleElement.y + 7f, Color(200, 200, 200, 255).rgb
        )
        val moduleValues = moduleElement.module.values
        if (!moduleValues.isEmpty()) {
            if (moduleElement.isShowSettings) {
                FontLoaders.logog38.drawString(
                    "-", moduleElement.x + moduleElement.width - 9f,
                    moduleElement.y + moduleElement.height / 10f, Color(124, 252, 0, 255).rgb
                )
                var yPos = 2
                for (value in moduleValues) {
                    if (!value.canDisplay.invoke()) continue
                    if (value is BoolValue) {
                        val text = value.name
                        val textWidth = FontLoaders.SF19.getStringWidth(text).toFloat()
                        if (moduleElement.settingsWidth < textWidth + 8) moduleElement.settingsWidth = textWidth + 8
                        RenderUtils.drawRect(
                            (moduleElement.width + 4).toFloat(),
                            (yPos + 2).toFloat(),
                            moduleElement.width + moduleElement.settingsWidth,
                            (yPos + 14).toFloat(),
                            Color(26, 26, 26).rgb
                        )
                        if (mouseX >= moduleElement.width + 4 && mouseX <= moduleElement.width +
                            moduleElement.settingsWidth && mouseY >= yPos + 2 && mouseY <= yPos + 14
                        ) {
                            if (Mouse.isButtonDown(0) && moduleElement.isntPressed()) {
                                val boolValue = value
                                boolValue.set(!boolValue.get())
                            }
                        }
                        GlStateManager.resetColor()
                        FontLoaders.SF19.drawString(
                            text, moduleElement.width + 6f,
                            yPos + 4f, if (value.get()) guiColor else Int.MAX_VALUE
                        )
                        yPos += 12
                    } else if (value is ListValue) {
                        val listValue = value
                        val text = value.name
                        val textWidth = FontLoaders.SF19.getStringWidth(text).toFloat()
                        if (moduleElement.settingsWidth < textWidth + 16) moduleElement.settingsWidth = textWidth + 16
                        RenderUtils.drawRect(
                            (moduleElement.width + 4).toFloat(),
                            (yPos + 2).toFloat(),
                            moduleElement.width + moduleElement.settingsWidth,
                            (yPos + 14).toFloat(),
                            Color(26, 26, 26).rgb
                        )
                        GlStateManager.resetColor()
                        FontLoaders.SF19.drawString(
                            "§c$text",
                            moduleElement.width + 6f,
                            yPos + 4f,
                            0xffffff
                        )
                        FontLoaders.SF19.drawString(
                            if (listValue.openList) "-" else "+",
                            (moduleElement.width +
                                    moduleElement.settingsWidth - if (listValue.openList) 5 else 6).toFloat(),
                            yPos + 4f,
                            0xffffff
                        )
                        if (mouseX >= moduleElement.width + 4 && mouseX <= moduleElement.width +
                            moduleElement.settingsWidth && mouseY >= yPos + 2 && mouseY <= yPos + 14
                        ) {
                            if (Mouse.isButtonDown(0) && moduleElement.isntPressed()) {
                                listValue.openList = !listValue.openList
                            }
                        }
                        yPos += 12
                        for (valueOfList in listValue.values) {
                            val textWidth2 = FontLoaders.SF19.getStringWidth(">$valueOfList").toFloat()
                            if (moduleElement.settingsWidth < textWidth2 + 12) moduleElement.settingsWidth =
                                textWidth2 + 12
                            if (listValue.openList) {
                                RenderUtils.drawRect(
                                    (moduleElement.width + 4).toFloat(),
                                    (yPos + 2).toFloat(),
                                    moduleElement.width + moduleElement.settingsWidth,
                                    (yPos + 14).toFloat(),
                                    Color(26, 26, 26).rgb
                                )
                                if (mouseX >= moduleElement.width + 4 && mouseX <= moduleElement.width +
                                    moduleElement.settingsWidth && mouseY >= yPos + 2 && mouseY <= yPos + 14
                                ) {
                                    if (Mouse.isButtonDown(0) && moduleElement.isntPressed()) {
                                        listValue.set(valueOfList)
                                    }
                                }
                                GlStateManager.resetColor()
                                FontLoaders.SF19.drawString(">", moduleElement.width + 6f, yPos + 4f, Int.MAX_VALUE)
                                FontLoaders.SF19.drawString(
                                    valueOfList.uppercase(Locale.getDefault()),
                                    moduleElement.width + 14f,
                                    yPos + 4f,
                                    if (listValue.get()
                                            .equals(valueOfList, ignoreCase = true)
                                    ) guiColor else Int.MAX_VALUE
                                )
                                yPos += 12
                            }
                        }
                    } else if (value is FloatValue) {
                        val floatValue = value
                        val text = value.name + "§f: §c" + round(floatValue.get())
                        val textWidth = FontLoaders.SF19.getStringWidth(text).toFloat()
                        if (moduleElement.settingsWidth < textWidth + 8) moduleElement.settingsWidth = textWidth + 8
                        RenderUtils.drawRect(
                            (moduleElement.width + 4).toFloat(),
                            (yPos + 2).toFloat(),
                            moduleElement.width + moduleElement.settingsWidth,
                            (yPos + 24).toFloat(),
                            Color(26, 26, 26).rgb
                        )
                        RenderUtils.drawRect(
                            (moduleElement.width + 8).toFloat(), (yPos + 18).toFloat(),
                            moduleElement.width + moduleElement.settingsWidth - 4, (yPos + 19).toFloat(), Int.MAX_VALUE
                        )
                        val sliderValue = moduleElement.width + (moduleElement.settingsWidth - 12) *
                                (floatValue.get() - floatValue.minimum) / (floatValue.maximum - floatValue.minimum)
                        RenderUtils.drawRect(
                            8 + sliderValue, (yPos + 15).toFloat(), sliderValue + 11, (yPos + 21).toFloat(),
                            guiColor
                        )
                        if (mouseX >= moduleElement.width + 4 && mouseX <= moduleElement.width + moduleElement.settingsWidth - 4 && mouseY >= yPos + 15 && mouseY <= yPos + 21) {
                            if (Mouse.isButtonDown(0)) {
                                val i = MathHelper.clamp_double(
                                    ((mouseX - moduleElement.width - 8) / (moduleElement.settingsWidth - 12)).toDouble(),
                                    0.0,
                                    1.0
                                )
                                floatValue.set(round((floatValue.minimum + (floatValue.maximum - floatValue.minimum) * i).toFloat()).toFloat())
                            }
                        }
                        GlStateManager.resetColor()
                        FontLoaders.SF19.drawString(
                            text,
                            moduleElement.width + 6f,
                            yPos + 4f,
                            0xffffff
                        )
                        yPos += 22
                    } else if (value is IntegerValue) {
                        val integerValue = value
                        val text =
                            value.name + "§f: §c" + if (value is BlockValue) getBlockName(integerValue.get()) + " (" + integerValue.get() + ")" else integerValue.get()
                        val textWidth = FontLoaders.SF19.getStringWidth(text).toFloat()
                        if (moduleElement.settingsWidth < textWidth + 8) moduleElement.settingsWidth = textWidth + 8
                        RenderUtils.drawRect(
                            (moduleElement.width + 4).toFloat(),
                            (yPos + 2).toFloat(),
                            moduleElement.width + moduleElement.settingsWidth,
                            (yPos + 24).toFloat(),
                            Color(26, 26, 26).rgb
                        )
                        RenderUtils.drawRect(
                            (moduleElement.width + 8).toFloat(),
                            (yPos + 18).toFloat(),
                            moduleElement.width + moduleElement.settingsWidth - 4,
                            (yPos + 19).toFloat(),
                            Int.MAX_VALUE
                        )
                        val sliderValue = moduleElement.width + (moduleElement.settingsWidth - 12) *
                                (integerValue.get() - integerValue.minimum) / (integerValue.maximum - integerValue.minimum)
                        RenderUtils.drawRect(
                            8 + sliderValue,
                            (yPos + 15).toFloat(),
                            sliderValue + 11,
                            (yPos + 21).toFloat(),
                            guiColor
                        )
                        if (mouseX >= moduleElement.width + 4 && mouseX <= moduleElement.width +
                            moduleElement.settingsWidth && mouseY >= yPos + 15 && mouseY <= yPos + 21
                        ) {
                            if (Mouse.isButtonDown(0)) {
                                val i = MathHelper.clamp_double(
                                    ((mouseX - moduleElement.width - 8) /
                                            (moduleElement.settingsWidth - 12)).toDouble(), 0.0, 1.0
                                )
                                integerValue.set((integerValue.minimum + (integerValue.maximum - integerValue.minimum) * i).toInt())
                            }
                        }
                        GlStateManager.resetColor()
                        FontLoaders.SF19.drawString(
                            text,
                            moduleElement.width + 6f,
                            yPos + 4f,
                            0xffffff
                        )
                        yPos += 22
                    } else if (value is FontValue) {
                        val fontValue = value
                        val fontRenderer = fontValue.get()
                        RenderUtils.drawRect(
                            (moduleElement.width + 4).toFloat(),
                            (yPos + 2).toFloat(),
                            moduleElement.width + moduleElement.settingsWidth,
                            (yPos + 14).toFloat(),
                            Color(26, 26, 26).rgb
                        )
                        var displayString = "Font: Unknown"
                        if (fontRenderer is GameFontRenderer) {
                            val liquidFontRenderer = fontRenderer
                            displayString =
                                "Font: " + liquidFontRenderer.defaultFont.font.name + " - " + liquidFontRenderer.defaultFont.font.size
                        } else if (fontRenderer === Fonts.minecraftFont) displayString = "Font: Minecraft" else {
                            val objects = Fonts.getFontDetails(fontRenderer)
                            if (objects != null) {
                                displayString =
                                    objects[0].toString() + if (objects[1] as Int != -1) " - " + objects[1] else ""
                            }
                        }
                        FontLoaders.SF19.drawString(
                            displayString,
                            moduleElement.width + 6f,
                            yPos + 4f,
                            Color.WHITE.rgb
                        )
                        val stringWidth = FontLoaders.SF19.getStringWidth(displayString)
                        if (moduleElement.settingsWidth < stringWidth + 8) moduleElement.settingsWidth =
                            (stringWidth + 8).toFloat()
                        if ((Mouse.isButtonDown(0) && !mouseDown || Mouse.isButtonDown(1) && !rightMouseDown) && mouseX >= moduleElement.width +
                            4 && mouseX <= moduleElement.width + moduleElement.settingsWidth && mouseY >= yPos + 4 && mouseY <= yPos + 12
                        ) {
                            val fonts = Fonts.getFonts()
                            if (Mouse.isButtonDown(0)) {
                                var i = 0
                                while (i < fonts.size) {
                                    val font = fonts[i]
                                    if (font === fontRenderer) {
                                        i++
                                        if (i >= fonts.size) i = 0
                                        fontValue.set(fonts[i])
                                        break
                                    }
                                    i++
                                }
                            } else {
                                var i = fonts.size - 1
                                while (i >= 0) {
                                    val font = fonts[i]
                                    if (font === fontRenderer) {
                                        i--
                                        if (i >= fonts.size) i = 0
                                        if (i < 0) i = fonts.size - 1
                                        fontValue.set(fonts[i])
                                        break
                                    }
                                    i--
                                }
                            }
                        }
                        yPos += 11
                    } else {
                        val text = value.name + "§f: §c" + value.get()
                        val textWidth = FontLoaders.SF19.getStringWidth(text).toFloat()
                        if (moduleElement.settingsWidth < textWidth + 8) moduleElement.settingsWidth = textWidth + 8
                        RenderUtils.drawRect(
                            (moduleElement.width + 4).toFloat(),
                            (yPos + 2).toFloat(),
                            moduleElement.width + moduleElement.settingsWidth,
                            (yPos + 14).toFloat(),
                            Color(26, 26, 26).rgb
                        )
                        GlStateManager.resetColor()
                        FontLoaders.SF19.drawString(text, moduleElement.width + 6f, yPos + 4f, 0xffffff)
                        yPos += 12
                    }
                }
                moduleElement.updatePressed()
                mouseDown = Mouse.isButtonDown(0)
                rightMouseDown = Mouse.isButtonDown(1)
                if (moduleElement.settingsWidth > 0f && yPos > moduleElement.y + 4) RenderUtils.drawBorderedRect(
                    (moduleElement.width + 4).toFloat(),
                    (moduleElement.y + 6).toFloat(),
                    moduleElement.width + moduleElement.settingsWidth,
                    (yPos + 2).toFloat(),
                    1f,
                    Color(26, 26, 26).rgb,
                    0
                )
            } else {
                FontLoaders.logog38.drawString(
                    "+", moduleElement.x + moduleElement.width - 10f,
                    moduleElement.y + moduleElement.height / 10f, Color(160, 160, 160, 120).rgb
                )
            }
        }
    }

    private fun round(f: Float): BigDecimal {
        var bd = BigDecimal(java.lang.Float.toString(f))
        bd = bd.setScale(2, 4)
        return bd
    }

    private fun hoverColor(color: Color, hover: Int): Color {
        val r = color.red - hover * 2
        val g = color.green - hover * 2
        val b = color.blue - hover * 2
        return Color(Math.max(r, 0), Math.max(g, 0), Math.max(b, 0), color.alpha)
    }
}
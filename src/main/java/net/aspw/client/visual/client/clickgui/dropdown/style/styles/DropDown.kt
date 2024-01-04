package net.aspw.client.visual.client.clickgui.dropdown.style.styles

import net.aspw.client.features.module.impl.visual.Gui.Companion.generateColor
import net.aspw.client.util.block.BlockUtils.getBlockName
import net.aspw.client.util.render.RenderUtils
import net.aspw.client.value.*
import net.aspw.client.visual.client.clickgui.dropdown.ClickGui
import net.aspw.client.visual.client.clickgui.dropdown.Panel
import net.aspw.client.visual.client.clickgui.dropdown.elements.ButtonElement
import net.aspw.client.visual.client.clickgui.dropdown.elements.ModuleElement
import net.aspw.client.visual.client.clickgui.dropdown.style.Style
import net.aspw.client.visual.font.semi.Fonts
import net.aspw.client.visual.font.semi.GameFontRenderer
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
            (panel.getX()).toFloat(),
            panel.getY().toFloat() + 0.5f,
            (panel.getX() + panel.width).toFloat(),
            panel.getY().toFloat() + 17,
            Color(0, 0, 0).rgb
        )
        RenderUtils.drawRect(
            panel.getX().toFloat() + 1, panel.getY().toFloat() + 19, panel.getX().toFloat() + panel.width - 1,
            (
                    panel.getY() + 18 + panel.fade).toFloat(), Color(26, 26, 26).rgb
        )
        GlStateManager.resetColor()
        Fonts.fontSFUI37.drawStringWithShadow(
            "§l" + panel.name.lowercase(Locale.getDefault()),
            (panel.getX() + 2).toFloat(),
            (panel.getY() + 6).toFloat(),
            Color(255, 255, 255, 200).rgb
        )
        if (panel.open) {
            Fonts.marks.drawStringWithShadow(
                "d",
                (panel.getX() + 85).toFloat(),
                (panel.getY() + 4).toFloat(),
                Color(255, 255, 255, 200).rgb
            )
        } else {
            Fonts.marks.drawStringWithShadow(
                "d",
                (panel.getX() + 85).toFloat(),
                (panel.getY() + 4).toFloat(),
                Color(255, 255, 255, 80).rgb
            )
        }
        if (panel.name.equals("combat", true)) {
            Fonts.icons.drawStringWithShadow(
                "J",
                (panel.getX() + 38).toFloat(),
                (panel.getY() + 4).toFloat(),
                Color(255, 255, 255, 200).rgb
            )
        }
        if (panel.name.equals("movement", true)) {
            Fonts.icons.drawStringWithShadow(
                "G",
                (panel.getX() + 52).toFloat(),
                (panel.getY() + 4).toFloat(),
                Color(255, 255, 255, 200).rgb
            )
        }
        if (panel.name.equals("player", true)) {
            Fonts.icons.drawStringWithShadow(
                "F",
                (panel.getX() + 34).toFloat(),
                (panel.getY() + 4).toFloat(),
                Color(255, 255, 255, 200).rgb
            )
        }
        if (panel.name.equals("exploit", true)) {
            Fonts.icons.drawStringWithShadow(
                "A",
                (panel.getX() + 38).toFloat(),
                (panel.getY() + 4).toFloat(),
                Color(255, 255, 255, 200).rgb
            )
        }
        if (panel.name.equals("other", true)) {
            Fonts.icons.drawStringWithShadow(
                "B",
                (panel.getX() + 31).toFloat(),
                (panel.getY() + 4).toFloat(),
                Color(255, 255, 255, 200).rgb
            )
        }
        if (panel.name.equals("visual", true)) {
            Fonts.icons.drawStringWithShadow(
                "H",
                (panel.getX() + 33).toFloat(),
                (panel.getY() + 4).toFloat(),
                Color(255, 255, 255, 200).rgb
            )
        }
        if (panel.name.equals("minigames", true)) {
            Fonts.icons.drawStringWithShadow(
                "C",
                (panel.getX() + 54).toFloat(),
                (panel.getY() + 4).toFloat(),
                Color(255, 255, 255, 200).rgb
            )
        }
        if (panel.name.equals("targets", true)) {
            Fonts.icons.drawStringWithShadow(
                "I",
                (panel.getX() + 37).toFloat(),
                (panel.getY() + 4).toFloat(),
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
        Fonts.fontSFUI37.drawString(
            buttonElement.displayName.lowercase(Locale.getDefault()), buttonElement.x + 3,
            buttonElement.y + 6, Color.WHITE.rgb
        )
    }

    override fun drawModuleElement(mouseX: Int, mouseY: Int, moduleElement: ModuleElement) {
        ClickGui.drawRect(
            moduleElement.x + 1, moduleElement.y + 1, moduleElement.x + moduleElement.width - 1,
            moduleElement.y + moduleElement.height + 2, hoverColor(Color(18, 18, 18), moduleElement.hoverTime).rgb
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
        Fonts.fontSFUI37.drawString(
            moduleElement.displayName.lowercase(Locale.getDefault()), moduleElement.x + 3,
            moduleElement.y + 7, Color(200, 200, 200, 255).rgb
        )
        val moduleValues = moduleElement.module.values
        if (moduleValues.isNotEmpty()) {
            if (moduleElement.isShowSettings) {
                Fonts.font72.drawString(
                    "-", moduleElement.x + moduleElement.width - 9,
                    moduleElement.y + moduleElement.height / 10, Color(124, 252, 0, 255).rgb
                )
                var yPos = 2
                for (value in moduleValues) {
                    if (!value.canDisplay.invoke()) continue
                    if (value is BoolValue) {
                        val text = value.name
                        val textWidth = Fonts.fontSFUI37.getStringWidth(text).toFloat()
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
                                value.set(!value.get())
                            }
                        }
                        GlStateManager.resetColor()
                        Fonts.fontSFUI37.drawString(
                            text, moduleElement.width + 6,
                            yPos + 4, if (value.get()) guiColor else Int.MAX_VALUE
                        )
                        yPos += 12
                    } else if (value is ListValue) {
                        val text = value.name
                        val textWidth = Fonts.fontSFUI37.getStringWidth(text).toFloat()
                        if (moduleElement.settingsWidth < textWidth + 16) moduleElement.settingsWidth = textWidth + 16
                        RenderUtils.drawRect(
                            (moduleElement.width + 4).toFloat(),
                            (yPos + 2).toFloat(),
                            moduleElement.width + moduleElement.settingsWidth,
                            (yPos + 14).toFloat(),
                            Color(26, 26, 26).rgb
                        )
                        GlStateManager.resetColor()
                        Fonts.fontSFUI37.drawString(
                            "§c$text",
                            moduleElement.width + 6,
                            yPos + 4,
                            0xffffff
                        )
                        Fonts.fontSFUI37.drawString(
                            if (value.openList) "-" else "+",
                            (moduleElement.width +
                                    moduleElement.settingsWidth - if (value.openList) 5 else 6).toInt(),
                            yPos + 4,
                            0xffffff
                        )
                        if (mouseX >= moduleElement.width + 4 && mouseX <= moduleElement.width +
                            moduleElement.settingsWidth && mouseY >= yPos + 2 && mouseY <= yPos + 14
                        ) {
                            if (Mouse.isButtonDown(0) && moduleElement.isntPressed()) {
                                value.openList = !value.openList
                            }
                        }
                        yPos += 12
                        for (valueOfList in value.values) {
                            val textWidth2 = Fonts.fontSFUI37.getStringWidth(">$valueOfList").toFloat()
                            if (moduleElement.settingsWidth < textWidth2 + 12) moduleElement.settingsWidth =
                                textWidth2 + 12
                            if (value.openList) {
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
                                        value.set(valueOfList)
                                    }
                                }
                                GlStateManager.resetColor()
                                Fonts.fontSFUI37.drawString(">", moduleElement.width + 6, yPos + 4, Int.MAX_VALUE)
                                Fonts.fontSFUI37.drawString(
                                    valueOfList.uppercase(Locale.getDefault()),
                                    moduleElement.width + 14,
                                    yPos + 4,
                                    if (value.get()
                                            .equals(valueOfList, ignoreCase = true)
                                    ) guiColor else Int.MAX_VALUE
                                )
                                yPos += 12
                            }
                        }
                    } else if (value is FloatValue) {
                        val text = value.name + "§f: §c" + round(value.get())
                        val textWidth = Fonts.fontSFUI37.getStringWidth(text).toFloat()
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
                                (value.get() - value.minimum) / (value.maximum - value.minimum)
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
                                value.set(round((value.minimum + (value.maximum - value.minimum) * i).toFloat()).toFloat())
                            }
                        }
                        GlStateManager.resetColor()
                        Fonts.fontSFUI37.drawString(
                            text,
                            moduleElement.width + 6,
                            yPos + 4,
                            0xffffff
                        )
                        yPos += 22
                    } else if (value is IntegerValue) {
                        val text =
                            value.name + "§f: §c" + if (value is BlockValue) getBlockName(value.get()) + " (" + value.get() + ")" else value.get()
                        val textWidth = Fonts.fontSFUI37.getStringWidth(text).toFloat()
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
                                (value.get() - value.minimum) / (value.maximum - value.minimum)
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
                                value.set((value.minimum + (value.maximum - value.minimum) * i).toInt())
                            }
                        }
                        GlStateManager.resetColor()
                        Fonts.fontSFUI37.drawString(
                            text,
                            moduleElement.width + 6,
                            yPos + 4,
                            0xffffff
                        )
                        yPos += 22
                    } else if (value is FontValue) {
                        val fontRenderer = value.get()
                        RenderUtils.drawRect(
                            (moduleElement.width + 4).toFloat(),
                            (yPos + 2).toFloat(),
                            moduleElement.width + moduleElement.settingsWidth,
                            (yPos + 14).toFloat(),
                            Color(26, 26, 26).rgb
                        )
                        var displayString = "Font: Unknown"
                        if (fontRenderer is GameFontRenderer) {
                            displayString =
                                "Font: " + fontRenderer.defaultFont.font.name + " - " + fontRenderer.defaultFont.font.size
                        } else if (fontRenderer === Fonts.minecraftFont) displayString = "Font: Minecraft" else {
                            val objects = Fonts.getFontDetails(fontRenderer)
                            if (objects != null) {
                                displayString =
                                    objects[0].toString() + if (objects[1] as Int != -1) " - " + objects[1] else ""
                            }
                        }
                        Fonts.fontSFUI37.drawString(
                            displayString,
                            moduleElement.width + 6,
                            yPos + 4,
                            Color.WHITE.rgb
                        )
                        val stringWidth = Fonts.fontSFUI37.getStringWidth(displayString)
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
                                        value.set(fonts[i])
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
                                        value.set(fonts[i])
                                        break
                                    }
                                    i--
                                }
                            }
                        }
                        yPos += 11
                    } else {
                        val text = value.name + "§f: §c" + value.get()
                        val textWidth = Fonts.fontSFUI37.getStringWidth(text).toFloat()
                        if (moduleElement.settingsWidth < textWidth + 8) moduleElement.settingsWidth = textWidth + 8
                        RenderUtils.drawRect(
                            (moduleElement.width + 4).toFloat(),
                            (yPos + 2).toFloat(),
                            moduleElement.width + moduleElement.settingsWidth,
                            (yPos + 14).toFloat(),
                            Color(26, 26, 26).rgb
                        )
                        GlStateManager.resetColor()
                        Fonts.fontSFUI37.drawString(text, moduleElement.width + 6, yPos + 4, 0xffffff)
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
                Fonts.font72.drawString(
                    "+", moduleElement.x + moduleElement.width - 10,
                    moduleElement.y + moduleElement.height / 10, Color(160, 160, 160, 120).rgb
                )
            }
        }
    }

    private fun round(f: Float): BigDecimal {
        var bd = BigDecimal(f.toString())
        bd = bd.setScale(2, 4)
        return bd
    }

    private fun hoverColor(color: Color, hover: Int): Color {
        val r = color.red - hover * 2
        val g = color.green - hover * 2
        val b = color.blue - hover * 2
        return Color(r.coerceAtLeast(0), g.coerceAtLeast(0), b.coerceAtLeast(0), color.alpha)
    }
}
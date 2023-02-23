package net.aspw.client.visual.hud.element.elements

import net.aspw.client.Client
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.utils.render.BlurUtils
import net.aspw.client.utils.render.ColorUtils
import net.aspw.client.utils.render.RenderUtils
import net.aspw.client.utils.render.shader.shaders.RainbowShader
import net.aspw.client.value.*
import net.aspw.client.visual.font.AWTFontRenderer
import net.aspw.client.visual.font.Fonts
import net.aspw.client.visual.hud.element.Border
import net.aspw.client.visual.hud.element.Element
import net.aspw.client.visual.hud.element.ElementInfo
import net.aspw.client.visual.hud.element.Side
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.renderer.GlStateManager
import org.lwjgl.input.Keyboard
import org.lwjgl.opengl.GL11
import java.awt.Color
import java.util.*

@ElementInfo(name = "TabGui")
class TabGui(x: Double = -1.0, y: Double = 17.0) : Element(x = x, y = y) {

    private val blurValue = BoolValue("Blur", false)
    private val blurStrength = FloatValue("BlurStrength", 5F, 0F, 30F)
    private val rainbowX = FloatValue("Rainbow-X", -1000F, -2000F, 2000F)
    private val rainbowY = FloatValue("Rainbow-Y", -1000F, -2000F, 2000F)
    private val redValue = IntegerValue("Rectangle Red", 0, 0, 255)
    private val greenValue = IntegerValue("Rectangle Green", 210, 0, 255)
    private val blueValue = IntegerValue("Rectangle Blue", 255, 0, 255)
    private val alphaValue = IntegerValue("Rectangle Alpha", 210, 0, 255)
    private val rectangleRainbow =
        ListValue("Rectangle Rainbow", arrayOf("Off", "Normal", "CRainbow", "OldRainbow", "Sky", "Fade"), "Off")
    private val backgroundRedValue = IntegerValue("Background Red", 0, 0, 255)
    private val backgroundGreenValue = IntegerValue("Background Green", 0, 0, 255)
    private val backgroundBlueValue = IntegerValue("Background Blue", 0, 0, 255)
    private val backgroundAlphaValue = IntegerValue("Background Alpha", 150, 0, 255)
    private val borderValue = BoolValue("Border", false)
    private val borderStrength = FloatValue("Border Strength", 2F, 1F, 5F)
    private val borderRedValue = IntegerValue("Border Red", 0, 0, 255)
    private val borderGreenValue = IntegerValue("Border Green", 0, 0, 255)
    private val borderBlueValue = IntegerValue("Border Blue", 0, 0, 255)
    private val borderAlphaValue = IntegerValue("Border Alpha", 150, 0, 255)
    private val borderRainbow =
        ListValue("Border Rainbow", arrayOf("Off", "Normal", "CRainbow", "OldRainbow", "Sky", "Fade"), "Off")
    private val skySaturationValue = FloatValue("Sky-Saturation", 0.9f, 0f, 1f)
    private val skyBrightnessValue = FloatValue("Sky-Brightness", 1f, 0f, 1f)
    private val cRainbowSecValue = IntegerValue("CRainbow-Seconds", 2, 1, 10)
    private val cRainbowSatValue = FloatValue("CRainbow-Saturation", 0.9f, 0f, 1f)
    private val cRainbowBrgValue = FloatValue("CRainbow-Brightness", 1f, 0f, 1f)
    private val oldRainbowSaturationValue = FloatValue("OldRainbow-Saturation", 0.9f, 0f, 1f)
    private val oldRainbowBrightnessValue = FloatValue("OldRainbow-Brightness", 1f, 0f, 1f)
    private val arrowsValue = BoolValue("Arrows", false)
    private val fontValue = FontValue("Font", Fonts.fontSFUI37)
    private val textShadow = BoolValue("TextShadow", true)
    private val textFade = BoolValue("TextFade", true)
    private val textPositionY = FloatValue("TextPosition-Y", 2F, 0F, 5F)
    private val width = FloatValue("Width", 60F, 55F, 100F)
    private val tabHeight = FloatValue("TabHeight", 12F, 10F, 15F)
    private val lowerCaseValue = BoolValue("LowerCase", false)

    private val tabs = mutableListOf<Tab>()

    private var categoryMenu = true
    private var selectedCategory = 0
    private var selectedModule = 0

    private var tabY = 0F
    private var itemY = 0F

    init {
        for (category in ModuleCategory.values()) {
            val tab = Tab(category.displayName)

            Client.moduleManager.modules
                .filter { module: Module -> category == module.category }
                .forEach { e: Module -> tab.modules.add(e) }

            tabs.add(tab)
        }
    }

    override fun drawElement(): Border {
        updateAnimation()

        AWTFontRenderer.assumeNonVolatile = true

        val fontRenderer = fontValue.get()

        val rectangleRainbowEnabled = rectangleRainbow.get().equals("normal", ignoreCase = true)

        val backgroundColor = Color(
            backgroundRedValue.get(), backgroundGreenValue.get(), backgroundBlueValue.get(),
            backgroundAlphaValue.get()
        )

        val borderColor = if (!borderRainbow.get().equals("Normal", ignoreCase = true))
            Color(borderRedValue.get(), borderGreenValue.get(), borderBlueValue.get(), borderAlphaValue.get())
        else
            Color.black

        // Draw
        val guiHeight = tabs.size * tabHeight.get()

        if (blurValue.get()) {
            GL11.glTranslated(-renderX, -renderY, 0.0)
            GL11.glScalef(1F, 1F, 1F)
            GL11.glPushMatrix()
            BlurUtils.blurArea(
                renderX.toFloat() * scale + 1F * scale,
                renderY.toFloat() * scale,
                renderX.toFloat() * scale + width.get() * scale,
                renderY.toFloat() * scale + guiHeight * scale,
                blurStrength.get()
            )
            GL11.glPopMatrix()
            GL11.glScalef(scale, scale, scale)
            GL11.glTranslated(renderX, renderY, 0.0)
        }

        RenderUtils.drawRect(1F, 0F, width.get(), guiHeight, backgroundColor.rgb)

        val rainbow = borderRainbow.get().equals("normal", ignoreCase = true)

        if (borderValue.get()) {
            RainbowShader.begin(
                rainbow,
                if (rainbowX.get() == 0.0F) 0.0F else 1.0F / rainbowX.get(),
                if (rainbowY.get() == 0.0F) 0.0F else 1.0F / rainbowY.get(),
                System.currentTimeMillis() % 10000 / 10000F
            ).use {
                RenderUtils.drawBorder(
                    1F, 0F, width.get(), guiHeight, borderStrength.get(), when (borderRainbow.get()) {
                        "Normal" -> 0
                        "CRainbow" -> RenderUtils.getRainbowOpaque(
                            cRainbowSecValue.get(),
                            cRainbowSatValue.get(),
                            cRainbowBrgValue.get(),
                            0
                        )

                        "OldRainbow" -> RenderUtils.getNormalRainbow(
                            0,
                            oldRainbowSaturationValue.get(),
                            oldRainbowBrightnessValue.get()
                        )

                        "Sky" -> RenderUtils.SkyRainbow(0, skySaturationValue.get(), skyBrightnessValue.get())
                        "Fade" -> ColorUtils.fade(borderColor, 0, 100).rgb
                        else -> borderColor.rgb
                    }
                )
            }
        }

        // Color
        val rectColor = when (rectangleRainbow.get()) {
            "Normal" -> 0
            "CRainbow" -> RenderUtils.getRainbowOpaque(
                cRainbowSecValue.get(),
                cRainbowSatValue.get(),
                cRainbowBrgValue.get(),
                0
            )

            "OldRainbow" -> RenderUtils.getNormalRainbow(
                0,
                oldRainbowSaturationValue.get(),
                oldRainbowBrightnessValue.get()
            )

            "Sky" -> RenderUtils.SkyRainbow(0, skySaturationValue.get(), skyBrightnessValue.get())
            "Fade" -> ColorUtils.fade(
                Color(redValue.get(), greenValue.get(), blueValue.get(), alphaValue.get()),
                0,
                100
            ).rgb

            else -> Color(redValue.get(), greenValue.get(), blueValue.get(), alphaValue.get()).rgb
        }

        RainbowShader.begin(
            rectangleRainbowEnabled,
            if (rainbowX.get() == 0.0F) 0.0F else 1.0F / rainbowX.get(),
            if (rainbowY.get() == 0.0F) 0.0F else 1.0F / rainbowY.get(),
            System.currentTimeMillis() % 10000 / 10000F
        ).use {
            RenderUtils.drawRect(1F, 1 + tabY - 1, width.get(), tabY + tabHeight.get(), rectColor)
        }

        GlStateManager.resetColor()

        var y = 1F
        tabs.forEachIndexed { index, tab ->
            val tabName = if (lowerCaseValue.get())
                tab.tabName.lowercase(Locale.getDefault())
            else
                tab.tabName

            val textX = if (side.horizontal == Side.Horizontal.RIGHT)
                width.get() - fontRenderer.getStringWidth(tabName) - tab.textFade - 3
            else
                tab.textFade + 5
            val textY = y + textPositionY.get()

            val textColor = if (selectedCategory == index) 0xffffff else Color(210, 210, 210).rgb

            fontRenderer.drawString(tabName, textX, textY, textColor, textShadow.get())

            if (arrowsValue.get()) {
                if (side.horizontal == Side.Horizontal.RIGHT)
                    fontRenderer.drawString(
                        if (!categoryMenu && selectedCategory == index) "+" else "-", 3F, y + 2F,
                        0xffffff, textShadow.get()
                    )
                else
                    fontRenderer.drawString(
                        if (!categoryMenu && selectedCategory == index) "-" else "+",
                        width.get() - 8F, y + 2F, 0xffffff, textShadow.get()
                    )
            }

            if (index == selectedCategory && !categoryMenu) {
                val tabX = if (side.horizontal == Side.Horizontal.RIGHT)
                    1F - tab.menuWidth
                else
                    width.get() + 5

                tab.drawTab(
                    tabX,
                    y,
                    rectColor,
                    backgroundColor.rgb,
                    borderColor.rgb,
                    borderStrength.get(),
                    lowerCaseValue.get(),
                    fontRenderer,
                    borderRainbow.get().equals("Normal", ignoreCase = true),
                    rectangleRainbowEnabled,
                    blurValue.get(),
                    blurStrength.get(),
                    scale, renderX, renderY
                )
            }
            y += tabHeight.get()
        }

        AWTFontRenderer.assumeNonVolatile = false

        return Border(1F, 0F, width.get(), guiHeight)
    }

    override fun handleKey(c: Char, keyCode: Int) {
        when (keyCode) {
            Keyboard.KEY_UP -> parseAction(Action.UP)
            Keyboard.KEY_DOWN -> parseAction(Action.DOWN)
            Keyboard.KEY_RIGHT -> parseAction(if (side.horizontal == Side.Horizontal.RIGHT) Action.LEFT else Action.RIGHT)
            Keyboard.KEY_LEFT -> parseAction(if (side.horizontal == Side.Horizontal.RIGHT) Action.RIGHT else Action.LEFT)
            Keyboard.KEY_RETURN -> parseAction(Action.TOGGLE)
        }
    }

    private fun updateAnimation() {
        val delta = RenderUtils.deltaTime

        val xPos = tabHeight.get() * selectedCategory
        if (tabY.toInt() != xPos.toInt()) {
            if (xPos > tabY)
                tabY += 0.5F * delta
            else
                tabY -= 0.5F * delta
        } else
            tabY = xPos
        val xPos2 = tabHeight.get() * selectedModule

        if (itemY.toInt() != xPos2.toInt()) {
            if (xPos2 > itemY)
                itemY += 0.5F * delta
            else
                itemY -= 0.5F * delta
        } else
            itemY = xPos2

        if (categoryMenu)
            itemY = 0F

        if (textFade.get()) {
            tabs.forEachIndexed { index, tab ->
                if (index == selectedCategory) {
                    if (tab.textFade < 4)
                        tab.textFade += 0.1F * delta

                    if (tab.textFade > 4)
                        tab.textFade = 4F
                } else {
                    if (tab.textFade > 0)
                        tab.textFade -= 0.1F * delta

                    if (tab.textFade < 0)
                        tab.textFade = 0F
                }
            }
        } else {
            for (tab in tabs) {
                if (tab.textFade > 0)
                    tab.textFade -= 0.1F * delta

                if (tab.textFade < 0)
                    tab.textFade = 0F
            }
        }
    }

    private fun parseAction(action: Action) {
        var toggle = false

        when (action) {
            Action.UP -> if (categoryMenu) {
                --selectedCategory
                if (selectedCategory < 0) {
                    selectedCategory = tabs.size - 1
                    tabY = tabHeight.get() * selectedCategory.toFloat()
                }
            } else {
                --selectedModule
                if (selectedModule < 0) {
                    selectedModule = tabs[selectedCategory].modules.size - 1
                    itemY = tabHeight.get() * selectedModule.toFloat()
                }
            }

            Action.DOWN -> if (categoryMenu) {
                ++selectedCategory
                if (selectedCategory > tabs.size - 1) {
                    selectedCategory = 0
                    tabY = tabHeight.get() * selectedCategory.toFloat()
                }
            } else {
                ++selectedModule
                if (selectedModule > tabs[selectedCategory].modules.size - 1) {
                    selectedModule = 0
                    itemY = tabHeight.get() * selectedModule.toFloat()
                }
            }

            Action.LEFT -> {
                if (!categoryMenu)
                    categoryMenu = true
            }

            Action.RIGHT ->
                if (!categoryMenu) {
                    toggle = true
                } else {
                    categoryMenu = false
                    selectedModule = 0
                }


            Action.TOGGLE -> if (!categoryMenu) toggle = true
        }

        if (toggle) {
            val sel = selectedModule
            tabs[selectedCategory].modules[sel].toggle()
        }
    }

    /**
     * TabGUI Tab
     */
    private inner class Tab(val tabName: String) {

        val modules = mutableListOf<Module>()
        var menuWidth = 0
        var textFade = 0F

        fun drawTab(
            x: Float, y: Float, color: Int, backgroundColor: Int, borderColor: Int, borderStrength: Float,
            lowerCase: Boolean, fontRenderer: FontRenderer, borderRainbow: Boolean, rectRainbow: Boolean,
            blur: Boolean, blurStrength: Float, scale: Float, renderX: Double, renderY: Double
        ) {
            var maxWidth = 0

            for (module in modules)
                if (fontRenderer.getStringWidth(if (lowerCase) module.name.lowercase(Locale.getDefault()) else module.name) + 4 > maxWidth)
                    maxWidth =
                        (fontRenderer.getStringWidth(if (lowerCase) module.name.lowercase(Locale.getDefault()) else module.name) + 7F).toInt()

            menuWidth = maxWidth

            val menuHeight = modules.size * tabHeight.get()

            if (borderValue.get()) {
                RainbowShader.begin(
                    borderRainbow,
                    if (rainbowX.get() == 0.0F) 0.0F else 1.0F / rainbowX.get(),
                    if (rainbowY.get() == 0.0F) 0.0F else 1.0F / rainbowY.get(),
                    System.currentTimeMillis() % 10000 / 10000F
                ).use {
                    RenderUtils.drawBorder(
                        x - 1F,
                        y - 1F,
                        x + menuWidth - 2F,
                        y + menuHeight - 1F,
                        borderStrength,
                        borderColor
                    )
                }
            }

            if (blur) {
                GL11.glTranslated(-renderX, -renderY, 0.0)
                GL11.glScalef(1F, 1F, 1F)
                GL11.glPushMatrix()
                BlurUtils.blurArea(
                    (renderX.toFloat() + x - 1F) * scale,
                    (renderY.toFloat() + y - 1F) * scale,
                    (renderX.toFloat() + x + menuWidth - 2F) * scale,
                    (renderY.toFloat() + y + menuHeight - 1F) * scale,
                    blurStrength
                )
                GL11.glPopMatrix()
                GL11.glScalef(scale, scale, scale)
                GL11.glTranslated(renderX, renderY, 0.0)
            }

            RenderUtils.drawRect(x - 1F, y - 1F, x + menuWidth - 2F, y + menuHeight - 1F, backgroundColor)


            RainbowShader.begin(
                rectRainbow,
                if (rainbowX.get() == 0.0F) 0.0F else 1.0F / rainbowX.get(),
                if (rainbowY.get() == 0.0F) 0.0F else 1.0F / rainbowY.get(),
                System.currentTimeMillis() % 10000 / 10000F
            ).use {
                RenderUtils.drawRect(
                    x - 1.toFloat(),
                    y + itemY - 1,
                    x + menuWidth - 2F,
                    y + itemY + tabHeight.get() - 1,
                    color
                )
            }

            GlStateManager.resetColor()

            modules.forEachIndexed { index, module ->
                val moduleColor = if (module.state) 0xffffff else Color(205, 205, 205).rgb

                fontRenderer.drawString(
                    if (lowerCase) module.name.lowercase(Locale.getDefault()) else module.name, x + 2F,
                    y + tabHeight.get() * index + textPositionY.get(), moduleColor, textShadow.get()
                )
            }
        }

    }

    /**
     * TabGUI Action
     */
    enum class Action { UP, DOWN, LEFT, RIGHT, TOGGLE }
}
package net.aspw.client.visual.hud.designer

import net.aspw.client.Client
import net.aspw.client.util.MinecraftInstance
import net.aspw.client.util.newfont.FontLoaders
import net.aspw.client.util.render.RenderUtils
import net.aspw.client.value.*
import net.aspw.client.visual.font.Fonts
import net.aspw.client.visual.font.GameFontRenderer
import net.aspw.client.visual.hud.HUD.Companion.createDefault
import net.aspw.client.visual.hud.HUD.Companion.elements
import net.aspw.client.visual.hud.element.Element
import net.aspw.client.visual.hud.element.ElementInfo
import net.aspw.client.visual.hud.element.Side
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.util.MathHelper
import org.lwjgl.input.Mouse
import org.lwjgl.opengl.GL11
import java.awt.Color

class EditorPanel(private val hudDesigner: GuiHudDesigner, var x: Int, var y: Int) : MinecraftInstance() {

    var width = 80
        private set
    var height = 20
        private set
    var realHeight = 20
        private set

    private var drag = false
    private var dragX = 0
    private var dragY = 0

    private var mouseDown = false

    private var scroll = 0

    var create = false
    private var currentElement: Element? = null

    /**
     * Draw editor panel to screen
     *
     * @param mouseX
     * @param mouseY
     * @param wheel
     */
    fun drawPanel(mouseX: Int, mouseY: Int, wheel: Int) {
        // Drag panel
        drag(mouseX, mouseY)

        // Set current element
        if (currentElement != hudDesigner.selectedElement)
            scroll = 0
        currentElement = hudDesigner.selectedElement

        // Scrolling start
        var currMouseY = mouseY
        val shouldScroll = realHeight > 200

        if (shouldScroll) {
            GL11.glPushMatrix()
            RenderUtils.makeScissorBox(x.toFloat(), y + 1F, x + width.toFloat(), y + 200F)
            GL11.glEnable(GL11.GL_SCISSOR_TEST)

            if (y + 200 < currMouseY)
                currMouseY = -1

            if (mouseX >= x && mouseX <= x + width && currMouseY >= y && currMouseY <= y + 200 && Mouse.hasWheel()) {
                if (wheel < 0 && -scroll + 205 <= realHeight) {
                    scroll -= 12
                } else if (wheel > 0) {
                    scroll += 12
                    if (scroll > 0) scroll = 0
                }
            }
        }

        // Draw panel
        Gui.drawRect(x, y + 12, x + width, y + realHeight, Color(0, 0, 0, 150).rgb)
        when {
            create -> drawCreate(mouseX, currMouseY)
            currentElement != null -> drawEditor(mouseX, currMouseY)
            else -> drawSelection(mouseX, currMouseY)
        }

        // Scrolling end
        if (shouldScroll) {
            Gui.drawRect(
                x + width - 5, y + 15, x + width - 2, y + 197,
                Color(41, 41, 41).rgb
            )

            val v = 197 * (-scroll / (realHeight - 170F))
            RenderUtils.drawRect(
                x + width - 5F, y + 15 + v, x + width - 2F, y + 20 + v,
                Color(37, 126, 255).rgb
            )

            GL11.glDisable(GL11.GL_SCISSOR_TEST)
            GL11.glPopMatrix()
        }

        // Save mouse states
        mouseDown = Mouse.isButtonDown(0)
    }

    /**
     * Draw create panel
     */
    private fun drawCreate(mouseX: Int, mouseY: Int) {
        height = 15 + scroll
        realHeight = 15
        width = 90

        for (element in elements) {
            val info = element.getAnnotation(ElementInfo::class.java) ?: continue

            if (info.single && Client.hud.elements.any { it.javaClass == element })
                continue

            val name = info.name
            FontLoaders.SF18.drawString(name, x + 2f, y + height.toFloat(), Color.WHITE.rgb)

            val stringWidth = FontLoaders.SF18.getStringWidth(name)
            if (width < stringWidth + 8)
                width = stringWidth + 8

            if (Mouse.isButtonDown(0) && !mouseDown && mouseX >= x && mouseX <= x + width && mouseY >= y + height
                && mouseY <= y + height + 10
            ) {
                try {
                    val newElement = element.newInstance()

                    if (newElement.createElement())
                        Client.hud.addElement(newElement)
                } catch (e: InstantiationException) {
                    e.printStackTrace()
                } catch (e: IllegalAccessException) {
                    e.printStackTrace()
                }
                create = false
            }

            height += 10
            realHeight += 10
        }

        Gui.drawRect(x, y, x + width, y + 12, Color(0, 0, 0, 150).rgb)
        FontLoaders.SF18.drawString("Create element", x + 2F, y + 3.5F, Color.WHITE.rgb)
    }

    /**
     * Draw selection panel
     */
    private fun drawSelection(mouseX: Int, mouseY: Int) {
        height = 15 + scroll
        realHeight = 15
        width = 120

        FontLoaders.SF18.drawString("Create element", x + 2f, y + height.toFloat(), Color.WHITE.rgb)
        if (Mouse.isButtonDown(0) && !mouseDown && mouseX >= x && mouseX <= x + width && mouseY >= y + height
            && mouseY <= y + height + 10
        )
            create = true

        height += 10
        realHeight += 10

        FontLoaders.SF18.drawString("Reset", x + 2f, y + height.toFloat(), Color.WHITE.rgb)
        if (Mouse.isButtonDown(0) && !mouseDown && mouseX >= x && mouseX <= x + width && mouseY >= y + height
            && mouseY <= y + height + 10
        )
            Client.hud = createDefault()

        height += 15
        realHeight += 15

        FontLoaders.SF18.drawString("Available Elements", x + 2f, y + height.toFloat(), Color.WHITE.rgb)
        height += 10
        realHeight += 10

        for (element in Client.hud.elements) {
            FontLoaders.SF18.drawString(element.name, x + 2f, y + height.toFloat(), Color.WHITE.rgb)

            val stringWidth = FontLoaders.SF18.getStringWidth(element.name)
            if (width < stringWidth + 8)
                width = stringWidth + 8

            if (Mouse.isButtonDown(0) && !mouseDown && mouseX >= x && mouseX <= x + width && mouseY >= y + height
                && mouseY <= y + height + 10
            )
                hudDesigner.selectedElement = element

            height += 10
            realHeight += 10
        }

        Gui.drawRect(x, y, x + width, y + 12, Color(0, 0, 0, 150).rgb)
        FontLoaders.SF18.drawString("Editor", x + 2F, y + 3.5f, Color.WHITE.rgb)
    }

    /**
     * Draw editor panel
     */
    private fun drawEditor(mouseX: Int, mouseY: Int) {
        height = scroll + 15
        realHeight = 15

        val prevWidth = width
        width = 100

        val element = currentElement ?: return

        // X
        FontLoaders.SF18.drawString(
            "X: ${"%.2f".format(element.renderX)} (${"%.2f".format(element.x)})",
            x + 2f,
            y + height.toFloat(),
            Color.WHITE.rgb
        )
        height += 10
        realHeight += 10

        // Y
        FontLoaders.SF18.drawString(
            "Y: ${"%.2f".format(element.renderY)} (${"%.2f".format(element.y)})",
            x + 2f,
            y + height.toFloat(),
            Color.WHITE.rgb
        )
        height += 10
        realHeight += 10

        // Scale
        FontLoaders.SF18.drawString("Scale: ${"%.2f".format(element.scale)}", x + 2f, y + height.toFloat(), Color.WHITE.rgb)
        height += 10
        realHeight += 10

        // Horizontal
        FontLoaders.SF18.drawString("H:", x + 2f, y + height.toFloat(), Color.WHITE.rgb)
        FontLoaders.SF18.drawString(
            element.side.horizontal.sideName,
            x + 12f, y + height.toFloat(), Color.GRAY.rgb
        )

        if (Mouse.isButtonDown(0) && !mouseDown && mouseX >= x && mouseX <= x + width && mouseY >= y + height
            && mouseY <= y + height + 10
        ) {
            val values = Side.Horizontal.values()
            val currIndex = values.indexOf(element.side.horizontal)

            val x = element.renderX

            element.side.horizontal = values[if (currIndex + 1 >= values.size) 0 else currIndex + 1]
            element.x = when (element.side.horizontal) {
                Side.Horizontal.LEFT -> x
                Side.Horizontal.MIDDLE -> (ScaledResolution(mc).scaledWidth / 2) - x
                Side.Horizontal.RIGHT -> ScaledResolution(mc).scaledWidth - x
            }
        }

        height += 10
        realHeight += 10

        // Vertical
        FontLoaders.SF18.drawString("V:", x + 2f, y + height.toFloat(), Color.WHITE.rgb)
        FontLoaders.SF18.drawString(
            element.side.vertical.sideName,
            x + 12f, y + height.toFloat(), Color.GRAY.rgb
        )

        if (Mouse.isButtonDown(0) && !mouseDown && mouseX >= x && mouseX <= x + width && mouseY >= y + height && mouseY <= y + height + 10) {
            val values = Side.Vertical.values()
            val currIndex = values.indexOf(element.side.vertical)

            val y = element.renderY

            element.side.vertical = values[if (currIndex + 1 >= values.size) 0 else currIndex + 1]
            element.y = when (element.side.vertical) {
                Side.Vertical.UP -> y
                Side.Vertical.MIDDLE -> (ScaledResolution(mc).scaledHeight / 2) - y
                Side.Vertical.DOWN -> ScaledResolution(mc).scaledHeight - y
            }

        }

        height += 10
        realHeight += 10

        // Values
        for (value in element.values) {
            if (!value.canDisplay.invoke()) continue
            when (value) {
                is BoolValue -> {
                    // Title
                    FontLoaders.SF18.drawString(
                        value.name,
                        x + 2f,
                        y + height.toFloat(),
                        if (value.get()) Color.WHITE.rgb else Color.GRAY.rgb
                    )

                    val stringWidth = FontLoaders.SF18.getStringWidth(value.name)
                    if (width < stringWidth + 8)
                        width = stringWidth + 8

                    // Toggle value
                    if (Mouse.isButtonDown(0) && !mouseDown && mouseX >= x && mouseX <= x + width &&
                        mouseY >= y + height && mouseY <= y + height + 10
                    )
                        value.set(!value.get())

                    // Change pos
                    height += 10
                    realHeight += 10
                }

                is FloatValue -> {
                    val current = value.get()
                    val min = value.minimum
                    val max = value.maximum

                    // Title
                    val text = "${value.name}: §c${"%.2f".format(current)}"

                    FontLoaders.SF18.drawString(text, x + 2f, y + height.toFloat(), Color.WHITE.rgb)

                    val stringWidth = FontLoaders.SF18.getStringWidth(text)
                    if (width < stringWidth + 8)
                        width = stringWidth + 8

                    // Slider
                    RenderUtils.drawRect(x + 8F, y + height + 12F, x + prevWidth - 8F, y + height + 13F, Color.WHITE)

                    // Slider mark
                    val sliderValue = x + ((prevWidth - 18F) * (current - min) / (max - min))
                    RenderUtils.drawRect(
                        8F + sliderValue, y + height + 9F, sliderValue + 11F, y + height
                                + 15F, Color(37, 126, 255).rgb
                    )

                    // Slider changer
                    if (mouseX >= x + 8 && mouseX <= x + prevWidth && mouseY >= y + height + 9 && mouseY <= y + height + 15 &&
                        Mouse.isButtonDown(0)
                    ) {
                        val curr = MathHelper.clamp_float((mouseX - x - 8F) / (prevWidth - 18F), 0F, 1F)

                        value.set(min + (max - min) * curr)
                    }

                    // Change pos
                    height += 20
                    realHeight += 20
                }

                is IntegerValue -> {
                    val current = value.get()
                    val min = value.minimum
                    val max = value.maximum

                    // Title
                    val text = "${value.name}: §c$current"

                    FontLoaders.SF18.drawString(text, x + 2f, y + height.toFloat(), Color.WHITE.rgb)

                    val stringWidth = FontLoaders.SF18.getStringWidth(text)
                    if (width < stringWidth + 8)
                        width = stringWidth + 8

                    // Slider
                    RenderUtils.drawRect(x + 8F, y + height + 12F, x + prevWidth - 8F, y + height + 13F, Color.WHITE)

                    // Slider mark
                    val sliderValue = x + ((prevWidth - 18F) * (current - min) / (max - min))
                    RenderUtils.drawRect(
                        8F + sliderValue, y + height + 9F, sliderValue + 11F, y + height
                                + 15F, Color(37, 126, 255).rgb
                    )

                    // Slider changer
                    if (mouseX >= x + 8 && mouseX <= x + prevWidth && mouseY >= y + height + 9 && mouseY <= y + height + 15 &&
                        Mouse.isButtonDown(0)
                    ) {
                        val curr = MathHelper.clamp_float((mouseX - x - 8F) / (prevWidth - 18F), 0F, 1F)

                        value.set((min + (max - min) * curr).toInt())
                    }

                    // Change pos
                    height += 20
                    realHeight += 20
                }

                is ListValue -> {
                    // Title
                    FontLoaders.SF18.drawString(value.name, x + 2f, y + height.toFloat(), Color.WHITE.rgb)
                    FontLoaders.SF18.drawString(
                        if (value.openList) "-" else "+",
                        x + width - 10f,
                        y + height + 1f,
                        Color.GRAY.rgb
                    )

                    if (Mouse.isButtonDown(0) && !mouseDown && mouseX >= x && mouseX <= x + width && mouseY >= y + height && mouseY <= y + height + 10)
                        value.openList = !value.openList

                    height += 10
                    realHeight += 10

                    // Selectable values
                    if (value.openList) for (s in value.values) {
                        // Value title
                        val text = "§c> §r$s"
                        FontLoaders.SF18.drawString(
                            text,
                            x + 2f,
                            y + height.toFloat(),
                            if (s == value.get()) Color.WHITE.rgb else Color.GRAY.rgb
                        )

                        val stringWidth = FontLoaders.SF18.getStringWidth(text)
                        if (width < stringWidth + 8)
                            width = stringWidth + 8

                        // Select value
                        if (Mouse.isButtonDown(0) && !mouseDown && mouseX >= x && mouseX <= x + width
                            && mouseY >= y + height && mouseY <= y + height + 10
                        )
                            value.set(s)

                        // Change pos
                        height += 10
                        realHeight += 10
                    }
                }

                is FontValue -> {
                    val fontRenderer = value.get()

                    // Title
                    val text = when (fontRenderer) {
                        is GameFontRenderer -> "${value.name}: ${fontRenderer.defaultFont.font.name} - ${fontRenderer.defaultFont.font.size}"
                        Fonts.minecraftFont -> "${value.name}: Minecraft"
                        else -> "${value.name}: Unknown"
                    }

                    FontLoaders.SF18.drawString(text, x + 2f, y + height.toFloat(), Color.WHITE.rgb)

                    val stringWidth = FontLoaders.SF18.getStringWidth(text)
                    if (width < stringWidth + 8)
                        width = stringWidth + 8

                    if (Mouse.isButtonDown(0) && !mouseDown && mouseX >= x && mouseX <= x + width &&
                        mouseY >= y + height && mouseY <= y + height + 10
                    ) {
                        val fonts = Fonts.getFonts()

                        fonts.forEachIndexed { index, font ->
                            if (font == fontRenderer) {
                                value.set(fonts[if (index + 1 >= fonts.size) 0 else index + 1])
                                return@forEachIndexed
                            }
                        }
                    }

                    height += 10
                    realHeight += 10
                }
            }
        }

        // Header
        Gui.drawRect(x, y, x + width, y + 12, Color(0, 0, 0, 150).rgb)
        FontLoaders.SF18.drawString("${element.name}", x + 2F, y + 3.5F, Color.WHITE.rgb)

        // Delete button
        if (!element.info.force) {
            val deleteWidth = x + width - FontLoaders.SF18.getStringWidth("Delete") - 2F
            FontLoaders.SF18.drawString("Delete", deleteWidth, y + 3.5F, Color.WHITE.rgb)
            if (Mouse.isButtonDown(0) && !mouseDown && mouseX >= deleteWidth && mouseX <= x + width && mouseY >= y
                && mouseY <= y + 10
            )
                Client.hud.removeElement(element)
        }
    }

    /**
     * Drag panel
     */
    private fun drag(mouseX: Int, mouseY: Int) {
        if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + 12 && Mouse.isButtonDown(0)
            && !mouseDown
        ) {
            drag = true
            dragX = mouseX - x
            dragY = mouseY - y
        }

        if (Mouse.isButtonDown(0) && drag) {
            x = mouseX - dragX
            y = mouseY - dragY
        } else drag = false
    }

}
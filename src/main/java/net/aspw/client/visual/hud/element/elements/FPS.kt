package net.aspw.client.visual.hud.element.elements

import net.aspw.client.features.module.impl.visual.ColorMixer
import net.aspw.client.util.render.ColorUtils
import net.aspw.client.util.render.RenderUtils
import net.aspw.client.value.*
import net.aspw.client.visual.font.Fonts
import net.aspw.client.visual.hud.designer.GuiHudDesigner
import net.aspw.client.visual.hud.element.Border
import net.aspw.client.visual.hud.element.Element
import net.aspw.client.visual.hud.element.ElementInfo
import net.aspw.client.visual.hud.element.Side
import net.minecraft.client.Minecraft
import org.lwjgl.opengl.GL11
import java.awt.Color
import java.lang.Math.pow
import java.text.DecimalFormat
import kotlin.math.sqrt

@ElementInfo(name = "FPS")
class FPS(
    x: Double = 3.0, y: Double = 22.0, scale: Float = 1F,
    side: Side = Side(Side.Horizontal.LEFT, Side.Vertical.DOWN)
) : Element(x, y, scale, side) {

    companion object {

        val DECIMAL_FORMAT_INT = DecimalFormat("0")

    }

    private val redValue = IntegerValue("Red", 255, 0, 255)
    private val greenValue = IntegerValue("Green", 255, 0, 255)
    private val blueValue = IntegerValue("Blue", 255, 0, 255)
    private val alphaValue = IntegerValue("Alpha", 255, 0, 255)
    private val rainbowList =
        ListValue("Rainbow", arrayOf("Off", "CRainbow", "Sky", "LiquidSlowly", "Fade", "Mixer"), "Off")
    private val saturationValue = FloatValue("Saturation", 0.5f, 0f, 1f)
    private val brightnessValue = FloatValue("Brightness", 1f, 0f, 1f)
    private val cRainbowSecValue = IntegerValue("Seconds", 2, 1, 10)
    private val shadow = BoolValue("Shadow", true)
    private var fontValue = FontValue("Font", Fonts.fontSFUI35)

    private var editMode = false
    private var editTicks = 0

    private var lastX: Double = 0.0
    private var lastZ: Double = 0.0

    private var suggestion = mutableListOf<String>()
    private var pointer = 0
    private var autoComplete = ""

    /**
     * Draw element
     */
    override fun drawElement(): Border {
        val color = Color(redValue.get(), greenValue.get(), blueValue.get(), alphaValue.get()).rgb

        val fontRenderer = fontValue.get()

        val rainbowType = rainbowList.get()

        when (side.horizontal) {
            Side.Horizontal.LEFT -> GL11.glTranslatef(0F, 0F, 0F)
            Side.Horizontal.MIDDLE -> GL11.glTranslatef(
                -fontRenderer.getStringWidth(
                    "X: " + DECIMAL_FORMAT_INT.format(mc.thePlayer.posX) + ", Y: " + DECIMAL_FORMAT_INT.format(
                        mc.thePlayer.posY
                    ) + ", Z: " + DECIMAL_FORMAT_INT.format(mc.thePlayer.posZ)
                ).toFloat() / 2F,
                0F,
                -fontRenderer.getStringWidth(
                    "X: " + DECIMAL_FORMAT_INT.format(mc.thePlayer.posX) + ", Y: " + DECIMAL_FORMAT_INT.format(
                        mc.thePlayer.posY
                    ) + ", Z: " + DECIMAL_FORMAT_INT.format(mc.thePlayer.posZ)
                ).toFloat() / 2F
            )

            Side.Horizontal.RIGHT -> GL11.glTranslatef(
                -fontRenderer.getStringWidth(
                    "X: " + DECIMAL_FORMAT_INT.format(mc.thePlayer.posX) + ", Y: " + DECIMAL_FORMAT_INT.format(
                        mc.thePlayer.posY
                    ) + ", Z: " + DECIMAL_FORMAT_INT.format(mc.thePlayer.posZ)
                ).toFloat(),
                0F,
                -fontRenderer.getStringWidth(
                    "X: " + DECIMAL_FORMAT_INT.format(mc.thePlayer.posX) + ", Y: " + DECIMAL_FORMAT_INT.format(
                        mc.thePlayer.posY
                    ) + ", Z: " + DECIMAL_FORMAT_INT.format(mc.thePlayer.posZ)
                ).toFloat()
            )
        }

        var FadeColor: Int =
            ColorUtils.fade(Color(redValue.get(), greenValue.get(), blueValue.get(), alphaValue.get()), 0, 100).rgb
        val LiquidSlowly =
            ColorUtils.LiquidSlowly(System.nanoTime(), 0, saturationValue.get(), brightnessValue.get()).rgb
        var liquidSlowli: Int = LiquidSlowly

        val mixerColor = ColorMixer.getMixedColor(0, cRainbowSecValue.get()).rgb

        fontRenderer.drawString(
            "FPS: " + Minecraft.getDebugFPS().toString(), 0F, 0F, when (rainbowType) {
                "CRainbow" -> RenderUtils.getRainbowOpaque(
                    cRainbowSecValue.get(),
                    saturationValue.get(),
                    brightnessValue.get(),
                    0
                )

                "Sky" -> RenderUtils.SkyRainbow(0, saturationValue.get(), brightnessValue.get())
                "LiquidSlowly" -> liquidSlowli
                "Fade" -> FadeColor
                "Mixer" -> mixerColor
                else -> color
            }, shadow.get()
        )

        if (editMode && mc.currentScreen is GuiHudDesigner) {
            if (editTicks <= 40)
                fontRenderer.drawString(
                    "_",
                    fontRenderer.getStringWidth(
                        "FPS: " + Minecraft.getDebugFPS().toString()
                    ) + 2F,
                    0F,
                    when (rainbowType) {
                        "CRainbow" -> RenderUtils.getRainbowOpaque(
                            cRainbowSecValue.get(),
                            saturationValue.get(),
                            brightnessValue.get(),
                            0
                        )

                        "Sky" -> RenderUtils.SkyRainbow(0, saturationValue.get(), brightnessValue.get())
                        "LiquidSlowly" -> liquidSlowli
                        "Fade" -> FadeColor
                        "Mixer" -> mixerColor
                        else -> color
                    },
                    shadow.get()
                )
            if (suggestion.size > 0) {
                GL11.glColor4f(1f, 1f, 1f, 1f)
                val totalLength = fontRenderer.getStringWidth(suggestion[0])
                suggestion.forEachIndexed { index, suggest ->
                    RenderUtils.drawRect(
                        fontRenderer.getStringWidth(
                            "X: " + DECIMAL_FORMAT_INT.format(mc.thePlayer.posX) + ", Y: " + DECIMAL_FORMAT_INT.format(
                                mc.thePlayer.posY
                            ) + ", Z: " + DECIMAL_FORMAT_INT.format(mc.thePlayer.posZ)
                        ) + 2F,
                        fontRenderer.FONT_HEIGHT * index.toFloat() + 5F,
                        fontRenderer.getStringWidth(
                            "X: " + DECIMAL_FORMAT_INT.format(mc.thePlayer.posX) + ", Y: " + DECIMAL_FORMAT_INT.format(
                                mc.thePlayer.posY
                            ) + ", Z: " + DECIMAL_FORMAT_INT.format(mc.thePlayer.posZ)
                        ) + 6F + totalLength,
                        fontRenderer.FONT_HEIGHT * index.toFloat() + 5F + fontRenderer.FONT_HEIGHT,
                        if (index == pointer) Color(90, 90, 90, 120).rgb else Color(0, 0, 0, 120).rgb
                    )
                    fontRenderer.drawStringWithShadow(
                        suggest,
                        fontRenderer.getStringWidth(
                            "X: " + DECIMAL_FORMAT_INT.format(mc.thePlayer.posX) + ", Y: " + DECIMAL_FORMAT_INT.format(
                                mc.thePlayer.posY
                            ) + ", Z: " + DECIMAL_FORMAT_INT.format(mc.thePlayer.posZ)
                        ) + 4F,
                        fontRenderer.FONT_HEIGHT * index.toFloat() + 5F,
                        -1
                    )
                }
            }
        }

        if (editMode && mc.currentScreen !is GuiHudDesigner) {
            editMode = false
            updateElement()
        }

        when (side.horizontal) {
            Side.Horizontal.LEFT -> GL11.glTranslatef(0F, 0F, 0F)
            Side.Horizontal.MIDDLE -> GL11.glTranslatef(
                fontRenderer.getStringWidth(
                    "X: " + DECIMAL_FORMAT_INT.format(mc.thePlayer.posX) + ", Y: " + DECIMAL_FORMAT_INT.format(
                        mc.thePlayer.posY
                    ) + ", Z: " + DECIMAL_FORMAT_INT.format(mc.thePlayer.posZ)
                ).toFloat() / 2F,
                0F,
                fontRenderer.getStringWidth(
                    "X: " + DECIMAL_FORMAT_INT.format(mc.thePlayer.posX) + ", Y: " + DECIMAL_FORMAT_INT.format(
                        mc.thePlayer.posY
                    ) + ", Z: " + DECIMAL_FORMAT_INT.format(mc.thePlayer.posZ)
                ).toFloat() / 2F
            )

            Side.Horizontal.RIGHT -> GL11.glTranslatef(
                fontRenderer.getStringWidth(
                    "X: " + DECIMAL_FORMAT_INT.format(mc.thePlayer.posX) + ", Y: " + DECIMAL_FORMAT_INT.format(
                        mc.thePlayer.posY
                    ) + ", Z: " + DECIMAL_FORMAT_INT.format(mc.thePlayer.posZ)
                ).toFloat(),
                0F,
                fontRenderer.getStringWidth(
                    "X: " + DECIMAL_FORMAT_INT.format(mc.thePlayer.posX) + ", Y: " + DECIMAL_FORMAT_INT.format(
                        mc.thePlayer.posY
                    ) + ", Z: " + DECIMAL_FORMAT_INT.format(mc.thePlayer.posZ)
                ).toFloat()
            )
        }

        return when (side.horizontal) {
            Side.Horizontal.LEFT -> Border(
                -2F,
                -2F,
                fontRenderer.getStringWidth(
                    "X: " + DECIMAL_FORMAT_INT.format(mc.thePlayer.posX) + ", Y: " + DECIMAL_FORMAT_INT.format(
                        mc.thePlayer.posY
                    ) + ", Z: " + DECIMAL_FORMAT_INT.format(mc.thePlayer.posZ)
                ) + 2F,
                fontRenderer.FONT_HEIGHT.toFloat()
            )

            Side.Horizontal.MIDDLE -> Border(
                -fontRenderer.getStringWidth(
                    "X: " + DECIMAL_FORMAT_INT.format(mc.thePlayer.posX) + ", Y: " + DECIMAL_FORMAT_INT.format(
                        mc.thePlayer.posY
                    ) + ", Z: " + DECIMAL_FORMAT_INT.format(mc.thePlayer.posZ)
                ).toFloat() / 2F,
                -2F,
                fontRenderer.getStringWidth(
                    "X: " + DECIMAL_FORMAT_INT.format(mc.thePlayer.posX) + ", Y: " + DECIMAL_FORMAT_INT.format(
                        mc.thePlayer.posY
                    ) + ", Z: " + DECIMAL_FORMAT_INT.format(mc.thePlayer.posZ)
                ).toFloat() / 2F + 2F,
                fontRenderer.FONT_HEIGHT.toFloat()
            )

            Side.Horizontal.RIGHT -> Border(
                2F,
                -2F,
                -fontRenderer.getStringWidth(
                    "X: " + DECIMAL_FORMAT_INT.format(mc.thePlayer.posX) + ", Y: " + DECIMAL_FORMAT_INT.format(
                        mc.thePlayer.posY
                    ) + ", Z: " + DECIMAL_FORMAT_INT.format(mc.thePlayer.posZ)
                ) - 2F,
                fontRenderer.FONT_HEIGHT.toFloat()
            )
        }
    }

    override fun updateElement() {
        editTicks += 5
        if (editTicks > 80) editTicks = 0

        autoComplete = ""

        pointer = pointer.coerceIn(0, (suggestion.size - 1).coerceAtLeast(0))

        //blocks per sec counter
        if (mc.thePlayer == null) return
        sqrt(
            pow(lastX - mc.thePlayer.posX, 2.0) + pow(
                lastZ - mc.thePlayer.posZ,
                2.0
            )
        ) * 20 * mc.timer.timerSpeed
        lastX = mc.thePlayer.posX
        lastZ = mc.thePlayer.posZ
    }

}
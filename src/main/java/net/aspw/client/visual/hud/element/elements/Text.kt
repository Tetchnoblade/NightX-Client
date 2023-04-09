package net.aspw.client.visual.hud.element.elements

import de.enzaxd.viaforge.ViaForge
import de.enzaxd.viaforge.protocol.ProtocolCollection
import net.aspw.client.Client
import net.aspw.client.features.module.impl.other.BanNotifier
import net.aspw.client.features.module.impl.visual.ColorMixer
import net.aspw.client.utils.*
import net.aspw.client.utils.render.BlurUtils
import net.aspw.client.utils.render.ColorUtils
import net.aspw.client.utils.render.RenderUtils
import net.aspw.client.value.*
import net.aspw.client.visual.font.Fonts
import net.aspw.client.visual.hud.designer.GuiHudDesigner
import net.aspw.client.visual.hud.element.Border
import net.aspw.client.visual.hud.element.Element
import net.aspw.client.visual.hud.element.ElementInfo
import net.aspw.client.visual.hud.element.Side
import net.minecraft.client.Minecraft
import net.minecraft.util.ChatAllowedCharacters
import org.lwjgl.input.Keyboard
import org.lwjgl.opengl.GL11
import java.awt.Color
import java.lang.Math.pow
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import kotlin.math.sqrt

/**
 * CustomHUD text element
 *
 * Allows to draw custom text
 */
@ElementInfo(name = "Text")
class Text(
    x: Double = 2.0, y: Double = 5.0, scale: Float = 1F,
    side: Side = Side.default()
) : Element(x, y, scale, side) {

    companion object {

        val DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd")
        val HOUR_FORMAT = SimpleDateFormat("HH:mm")

        val DECIMAL_FORMAT = DecimalFormat("0.00")
        val DECIMAL_FORMAT_INT = DecimalFormat("0")

        /**
         * Create default element
         */
        fun defaultClient(): Text {
            val text = Text(x = 2.0, y = 5.0, scale = 1F)

            text.displayString.set("%clientName%")
            text.shadow.set(true)
            text.fontValue.set(Fonts.fontSFUI40)
            text.setColor(Color(255, 255, 255))

            return text
        }

    }

    private val displayString =
        TextValue("DisplayText", "%clientName%")
    private val backgroundValue = BoolValue("Background", false)
    private val skeetRectValue = BoolValue("SkeetRect", false)
    private val lineValue = BoolValue("Line", false)
    private val blurValue = BoolValue("Blur", false)
    private val blurStrength = FloatValue("BlurStrength", 6F, 0F, 30F)
    private val redValue = IntegerValue("Red", 255, 0, 255)
    private val greenValue = IntegerValue("Green", 255, 0, 255)
    private val blueValue = IntegerValue("Blue", 255, 0, 255)
    private val alphaValue = IntegerValue("Alpha", 255, 0, 255)
    private val bgredValue = IntegerValue("Background-Red", 0, 0, 255)
    private val bggreenValue = IntegerValue("Background-Green", 0, 0, 255)
    private val bgblueValue = IntegerValue("Background-Blue", 0, 0, 255)
    private val bgalphaValue = IntegerValue("Background-Alpha", 80, 0, 255)
    private val rainbowList =
        ListValue("Rainbow", arrayOf("Off", "CRainbow", "Sky", "LiquidSlowly", "Fade", "Mixer"), "Sky")
    private val saturationValue = FloatValue("Saturation", 0.4f, 0f, 1f)
    private val brightnessValue = FloatValue("Brightness", 1f, 0f, 1f)
    private val cRainbowSecValue = IntegerValue("Seconds", 2, 1, 10)
    private val distanceValue = IntegerValue("Line-Distance", 0, 0, 400)
    private val gradientAmountValue = IntegerValue("Gradient-Amount", 25, 1, 50)
    private val shadow = BoolValue("Shadow", true)
    private var fontValue = FontValue("Font", Fonts.fontSFUI40)

    private var editMode = false
    private var editTicks = 0
    private var prevClick = 0L

    private var lastX: Double = 0.0
    private var lastZ: Double = 0.0

    private var speedStr = ""

    private var suggestion = mutableListOf<String>()
    private var displayText = display
    private var pointer = 0
    private var autoComplete = ""

    private val display: String
        get() {
            val textContent = if (displayString.get().isEmpty() && !editMode)
                "Text Element"
            else
                displayString.get()


            return ColorUtils.translateAlternateColorCodes(multiReplace(textContent))
        }

    private fun getReplacement(str: String): String? {
        if (mc.thePlayer != null) {
            when (str) {
                "x" -> return DECIMAL_FORMAT.format(mc.thePlayer.posX)
                "y" -> return DECIMAL_FORMAT.format(mc.thePlayer.posY)
                "z" -> return DECIMAL_FORMAT.format(mc.thePlayer.posZ)
                "xInt" -> return DECIMAL_FORMAT_INT.format(mc.thePlayer.posX)
                "yInt" -> return DECIMAL_FORMAT_INT.format(mc.thePlayer.posY)
                "zInt" -> return DECIMAL_FORMAT_INT.format(mc.thePlayer.posZ)
                "xdp" -> return mc.thePlayer.posX.toString()
                "ydp" -> return mc.thePlayer.posY.toString()
                "zdp" -> return mc.thePlayer.posZ.toString()
                "velocity" -> return DECIMAL_FORMAT.format(sqrt(mc.thePlayer.motionX * mc.thePlayer.motionX + mc.thePlayer.motionZ * mc.thePlayer.motionZ))
                "ping" -> return EntityUtils.getPing(mc.thePlayer).toString()
                "health" -> return DECIMAL_FORMAT.format(mc.thePlayer.health)
                "maxHealth" -> return DECIMAL_FORMAT.format(mc.thePlayer.maxHealth)
                "healthInt" -> return DECIMAL_FORMAT_INT.format(mc.thePlayer.health)
                "maxHealthInt" -> return DECIMAL_FORMAT_INT.format(mc.thePlayer.maxHealth)
                "yaw" -> return DECIMAL_FORMAT.format(mc.thePlayer.rotationYaw)
                "pitch" -> return DECIMAL_FORMAT.format(mc.thePlayer.rotationPitch)
                "yawInt" -> return DECIMAL_FORMAT_INT.format(mc.thePlayer.rotationYaw)
                "pitchInt" -> return DECIMAL_FORMAT_INT.format(mc.thePlayer.rotationPitch)
                "bps" -> return speedStr
                "inBound" -> return PacketUtils.avgInBound.toString()
                "outBound" -> return PacketUtils.avgOutBound.toString()
                "hurtTime" -> return mc.thePlayer.hurtTime.toString()
                "onGround" -> return mc.thePlayer.onGround.toString()
            }
        }

        return when (str) {
            "userName" -> mc.session.username
            "clientName" -> Client.CLIENT_COLORED
            "clientVersion" -> Client.CLIENT_VERSION
            "clientCreator" -> Client.CLIENT_CREATOR
            "fps" -> Minecraft.getDebugFPS().toString()
            "date" -> DATE_FORMAT.format(System.currentTimeMillis())
            "time" -> HOUR_FORMAT.format(System.currentTimeMillis())
            "serverIp" -> ServerUtils.getRemoteIp()
            "cps" -> return CPSCounter.getCPS(CPSCounter.MouseButton.LEFT).toString()
            "mcps" -> return CPSCounter.getCPS(CPSCounter.MouseButton.MIDDLE).toString()
            "rcps" -> return CPSCounter.getCPS(CPSCounter.MouseButton.RIGHT).toString()
            "portalVersion" -> ProtocolCollection.getProtocolById(ViaForge.getInstance().version).name
            "watchdogLastMin" -> BanNotifier.WATCHDOG_BAN_LAST_MIN.toString()
            "staffLastMin" -> BanNotifier.STAFF_BAN_LAST_MIN.toString()
            "wdStatus" -> return if (PacketUtils.isWatchdogActive()) "Active" else "Inactive"
            "sessionTime" -> return SessionUtils.getFormatSessionTime()
            "worldTime" -> return SessionUtils.getFormatWorldTime()
            else -> null
        }
    }

    private fun multiReplace(str: String): String {
        var lastPercent = -1
        val result = StringBuilder()
        for (i in str.indices) {
            if (str[i] == '%') {
                if (lastPercent != -1) {
                    if (lastPercent + 1 != i) {
                        val replacement = getReplacement(str.substring(lastPercent + 1, i))

                        if (replacement != null) {
                            result.append(replacement)
                            lastPercent = -1
                            continue
                        }
                    }
                    result.append(str, lastPercent, i)
                }
                lastPercent = i
            } else if (lastPercent == -1) {
                result.append(str[i])
            }
        }

        if (lastPercent != -1) {
            result.append(str, lastPercent, str.length)
        }

        return result.toString()
    }

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
                -fontRenderer.getStringWidth(displayText).toFloat() / 2F,
                0F,
                -fontRenderer.getStringWidth(displayText).toFloat() / 2F
            )

            Side.Horizontal.RIGHT -> GL11.glTranslatef(
                -fontRenderer.getStringWidth(displayText).toFloat(),
                0F,
                -fontRenderer.getStringWidth(displayText).toFloat()
            )
        }

        val floatX = renderX.toFloat()
        val floatY = renderY.toFloat()

        if (blurValue.get()) {
            GL11.glTranslated(-renderX, -renderY, 0.0)
            GL11.glScalef(1F, 1F, 1F)
            GL11.glPushMatrix()
            BlurUtils.blurArea(
                floatX * scale - 2F * scale,
                floatY * scale - 2F * scale,
                (floatX + fontRenderer.getStringWidth(displayText) + 2F) * scale,
                (floatY + fontRenderer.FONT_HEIGHT) * scale,
                blurStrength.get()
            )
            GL11.glPopMatrix()
            GL11.glScalef(scale, scale, scale)
            GL11.glTranslated(renderX, renderY, 0.0)
        }

        if (backgroundValue.get()) {
            RenderUtils.drawRect(
                -2F,
                -2F,
                fontRenderer.getStringWidth(displayText) + 2F,
                fontRenderer.FONT_HEIGHT + 0F,
                Color(bgredValue.get(), bggreenValue.get(), bgblueValue.get(), bgalphaValue.get())
            )
        }

        if (skeetRectValue.get()) {
            drawExhiRect(
                -4F,
                if (lineValue.get()) -5F else -4F,
                fontRenderer.getStringWidth(displayText) + 4F,
                fontRenderer.FONT_HEIGHT + 2F
            )
        }

        var FadeColor: Int =
            ColorUtils.fade(Color(redValue.get(), greenValue.get(), blueValue.get(), alphaValue.get()), 0, 100).rgb
        val LiquidSlowly =
            ColorUtils.LiquidSlowly(System.nanoTime(), 0, saturationValue.get(), brightnessValue.get()).rgb
        var liquidSlowli: Int = LiquidSlowly

        val mixerColor = ColorMixer.getMixedColor(0, cRainbowSecValue.get()).rgb

        if (lineValue.get()) {
            val barLength = (fontRenderer.getStringWidth(displayText) + 4F).toDouble()

            for (i in 0..(gradientAmountValue.get() - 1)) {
                val barStart = i.toDouble() / gradientAmountValue.get().toDouble() * barLength
                val barEnd = (i + 1).toDouble() / gradientAmountValue.get().toDouble() * barLength
                RenderUtils.drawGradientSideways(
                    -2.0 + barStart, -3.0, -2.0 + barEnd, -2.0,
                    when (rainbowType) {
                        "CRainbow" -> RenderUtils.getRainbowOpaque(
                            cRainbowSecValue.get(),
                            saturationValue.get(),
                            brightnessValue.get(),
                            i * distanceValue.get()
                        )

                        "Sky" -> RenderUtils.SkyRainbow(
                            i * distanceValue.get(),
                            saturationValue.get(),
                            brightnessValue.get()
                        )

                        "LiquidSlowly" -> ColorUtils.LiquidSlowly(
                            System.nanoTime(),
                            i * distanceValue.get(),
                            saturationValue.get(),
                            brightnessValue.get()
                        ).rgb

                        "Mixer" -> ColorMixer.getMixedColor(i * distanceValue.get(), cRainbowSecValue.get()).rgb
                        "Fade" -> ColorUtils.fade(
                            Color(redValue.get(), greenValue.get(), blueValue.get()),
                            i * distanceValue.get(),
                            100
                        ).rgb

                        else -> color
                    },
                    when (rainbowType) {
                        "CRainbow" -> RenderUtils.getRainbowOpaque(
                            cRainbowSecValue.get(),
                            saturationValue.get(),
                            brightnessValue.get(),
                            (i + 1) * distanceValue.get()
                        )

                        "Sky" -> RenderUtils.SkyRainbow(
                            (i + 1) * distanceValue.get(),
                            saturationValue.get(),
                            brightnessValue.get()
                        )

                        "LiquidSlowly" -> ColorUtils.LiquidSlowly(
                            System.nanoTime(),
                            (i + 1) * distanceValue.get(),
                            saturationValue.get(),
                            brightnessValue.get()
                        ).rgb

                        "Mixer" -> ColorMixer.getMixedColor((i + 1) * distanceValue.get(), cRainbowSecValue.get()).rgb
                        "Fade" -> ColorUtils.fade(
                            Color(redValue.get(), greenValue.get(), blueValue.get()),
                            (i + 1) * distanceValue.get(),
                            100
                        ).rgb

                        else -> color
                    }
                )
            }
        }

        fontRenderer.drawString(
            displayText, 0F, 0F, when (rainbowType) {
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
                    "_", fontRenderer.getStringWidth(displayText) + 2F,
                    0F, when (rainbowType) {
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
            if (suggestion.size > 0) {
                GL11.glColor4f(1f, 1f, 1f, 1f)
                val totalLength = fontRenderer.getStringWidth(suggestion[0])
                suggestion.forEachIndexed { index, suggest ->
                    RenderUtils.drawRect(
                        fontRenderer.getStringWidth(displayText) + 2F,
                        fontRenderer.FONT_HEIGHT * index.toFloat() + 5F,
                        fontRenderer.getStringWidth(displayText) + 6F + totalLength,
                        fontRenderer.FONT_HEIGHT * index.toFloat() + 5F + fontRenderer.FONT_HEIGHT,
                        if (index == pointer) Color(90, 90, 90, 120).rgb else Color(0, 0, 0, 120).rgb
                    )
                    fontRenderer.drawStringWithShadow(
                        suggest,
                        fontRenderer.getStringWidth(displayText) + 4F,
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
                fontRenderer.getStringWidth(displayText).toFloat() / 2F,
                0F,
                fontRenderer.getStringWidth(displayText).toFloat() / 2F
            )

            Side.Horizontal.RIGHT -> GL11.glTranslatef(
                fontRenderer.getStringWidth(displayText).toFloat(),
                0F,
                fontRenderer.getStringWidth(displayText).toFloat()
            )
        }

        return when (side.horizontal) {
            Side.Horizontal.LEFT -> Border(
                -2F,
                -2F,
                fontRenderer.getStringWidth(displayText) + 2F,
                fontRenderer.FONT_HEIGHT.toFloat()
            )

            Side.Horizontal.MIDDLE -> Border(
                -fontRenderer.getStringWidth(displayText).toFloat() / 2F,
                -2F,
                fontRenderer.getStringWidth(displayText).toFloat() / 2F + 2F,
                fontRenderer.FONT_HEIGHT.toFloat()
            )

            Side.Horizontal.RIGHT -> Border(
                2F,
                -2F,
                -fontRenderer.getStringWidth(displayText) - 2F,
                fontRenderer.FONT_HEIGHT.toFloat()
            )
        }
    }

    private fun drawExhiRect(x: Float, y: Float, x2: Float, y2: Float) {
        RenderUtils.drawRect(x - 1.5F, y - 1.5F, x2 + 1.5F, y2 + 1.5F, Color(8, 8, 8).rgb)
        RenderUtils.drawRect(x - 1, y - 1, x2 + 1, y2 + 1, Color(49, 49, 49).rgb)
        RenderUtils.drawBorderedRect(
            x + 2F,
            y + 2F,
            x2 - 2F,
            y2 - 2F,
            0.5F,
            Color(18, 18, 18).rgb,
            Color(28, 28, 28).rgb
        )
    }

    override fun updateElement() {
        editTicks += 5
        if (editTicks > 80) editTicks = 0

        displayText = if (editMode) displayString.get() else display

        var suggestStr = ""
        var foundPlaceHolder = false
        for (i in displayText.length - 1 downTo 0 step 1) {
            if (displayText.get(i).toString() == "%") {
                var placeHolderCounter = 1
                var z = i

                for (j in z downTo 0 step 1) {
                    if (displayText.get(j).toString() == "%") placeHolderCounter++
                }

                if (placeHolderCounter % 2 == 0) {
                    try {
                        suggestStr = displayText.substring(i, displayText.length).replace("%", "")
                        foundPlaceHolder = true
                    } catch (e: Exception) {
                        e.printStackTrace() // and then ignore
                    }
                }

                break
            }
        }

        autoComplete = ""

        if (!foundPlaceHolder)
            suggestion.clear()
        else suggestion = listOf(
            "x",
            "y",
            "z",
            "xInt",
            "yInt",
            "zInt",
            "xdp",
            "ydp",
            "zdp",
            "velocity",
            "ping",
            "health",
            "maxHealth",
            "healthInt",
            "maxHealthInt",
            "yaw",
            "pitch",
            "yawInt",
            "pitchInt",
            "bps",
            "inBound",
            "outBound",
            "hurtTime",
            "onGround",
            "userName",
            "clientName",
            "clientVersion",
            "clientCreator",
            "fps",
            "date",
            "time",
            "serverIp",
            "cps", "lcps",
            "mcps",
            "rcps",
            "portalVersion",
            "watchdogLastMin",
            "staffLastMin",
            "wdStatus",
            "sessionTime",
            "worldTime"
        ).filter { it.startsWith(suggestStr, true) && it.length > suggestStr.length }.sortedBy { it.length }.reversed()
            .toMutableList()

        pointer = pointer.coerceIn(0, (suggestion.size - 1).coerceAtLeast(0))

        // may require sth
        if (suggestion.size > 0) {
            autoComplete = suggestion[pointer].substring(
                (suggestStr.length).coerceIn(0, suggestion[pointer].length),
                suggestion[pointer].length
            )
            suggestion.replaceAll { s ->
                "§7$suggestStr§r${
                    s.substring(
                        (suggestStr.length).coerceIn(0, s.length),
                        s.length
                    )
                }"
            }
        }

        //blocks per sec counter
        if (mc.thePlayer == null) return
        speedStr = DECIMAL_FORMAT.format(
            sqrt(
                pow(lastX - mc.thePlayer.posX, 2.0) + pow(
                    lastZ - mc.thePlayer.posZ,
                    2.0
                )
            ) * 20 * mc.timer.timerSpeed
        )
        lastX = mc.thePlayer.posX
        lastZ = mc.thePlayer.posZ
    }

    override fun handleMouseClick(x: Double, y: Double, mouseButton: Int) {
        if (isInBorder(x, y) && mouseButton == 0) {
            if (System.currentTimeMillis() - prevClick <= 250L)
                editMode = true

            prevClick = System.currentTimeMillis()
        } else {
            editMode = false
        }
    }

    override fun handleKey(c: Char, keyCode: Int) {
        if (editMode && mc.currentScreen is GuiHudDesigner) {
            if (keyCode == Keyboard.KEY_BACK) {
                if (displayString.get().isNotEmpty())
                    displayString.set(displayString.get().substring(0, displayString.get().length - 1))

                updateElement()
                return
            }

            if (keyCode == Keyboard.KEY_UP) {
                if (suggestion.size > 1) {
                    if (pointer <= 0)
                        pointer = suggestion.size - 1
                    else
                        pointer--
                }

                updateElement()
                return
            }

            if (keyCode == Keyboard.KEY_DOWN) {
                if (suggestion.size > 1) {
                    if (pointer >= suggestion.size - 1)
                        pointer = 0
                    else
                        pointer++
                }

                updateElement()
                return
            }

            if (keyCode == Keyboard.KEY_TAB || keyCode == Keyboard.KEY_RETURN) {
                displayString.set(displayString.get() + autoComplete)

                updateElement()
                return
            }

            if (ChatAllowedCharacters.isAllowedCharacter(c) || c == '§')
                displayString.set(displayString.get() + c)

            updateElement()
        }
    }

    fun setColor(c: Color): Text {
        redValue.set(c.red)
        greenValue.set(c.green)
        blueValue.set(c.blue)
        return this
    }

}
package net.aspw.client.visual.hud.element.elements

import com.google.common.collect.Iterables
import com.google.common.collect.Lists
import net.aspw.client.Client
import net.aspw.client.features.module.impl.visual.ColorMixer
import net.aspw.client.features.module.impl.visual.Hud
import net.aspw.client.features.module.impl.visual.NoEffect
import net.aspw.client.utils.misc.StringUtils
import net.aspw.client.utils.render.*
import net.aspw.client.utils.timer.MSTimer
import net.aspw.client.value.*
import net.aspw.client.visual.font.Fonts
import net.aspw.client.visual.hud.element.Border
import net.aspw.client.visual.hud.element.Element
import net.aspw.client.visual.hud.element.ElementInfo
import net.aspw.client.visual.hud.element.Side
import net.minecraft.client.gui.Gui
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.scoreboard.ScoreObjective
import net.minecraft.scoreboard.ScorePlayerTeam
import net.minecraft.scoreboard.Scoreboard
import net.minecraft.util.EnumChatFormatting
import org.lwjgl.opengl.GL11
import java.awt.Color
import java.util.*

/**
 * CustomHUD scoreboard
 *
 * Allows to move and customize minecraft scoreboard
 */
@ElementInfo(name = "Scoreboard")
class ScoreboardElement(
    x: Double = 6.0, y: Double = -12.0, scale: Float = 1F,
    side: Side = Side(Side.Horizontal.RIGHT, Side.Vertical.MIDDLE)
) : Element(x, y, scale, side) {

    private val useVanillaBackground = BoolValue("UseVanillaBackground", true)
    private val backgroundColorRedValue = IntegerValue("Background-R", 0, 0, 255, { !useVanillaBackground.get() })
    private val backgroundColorGreenValue = IntegerValue("Background-G", 0, 0, 255, { !useVanillaBackground.get() })
    private val backgroundColorBlueValue = IntegerValue("Background-B", 0, 0, 255, { !useVanillaBackground.get() })
    private val backgroundColorAlphaValue =
        IntegerValue("Background-Alpha", 95, 0, 255, { !useVanillaBackground.get() })

    private val rectValue = BoolValue("Rect", false)
    private val rectHeight = IntegerValue("Rect-Height", 1, 1, 10, { rectValue.get() })

    private val blurValue = BoolValue("Blur", false)
    private val blurStrength = FloatValue("Blur-Strength", 5F, 0F, 30F, { blurValue.get() })

    private val shadowShaderValue = BoolValue("Shadow", false)
    private val shadowStrength = FloatValue("Shadow-Strength", 10F, 0F, 30F, { shadowShaderValue.get() })
    private val shadowColorMode =
        ListValue("Shadow-Color", arrayOf("Background", "Custom"), "Background", { shadowShaderValue.get() })

    private val shadowColorRedValue = IntegerValue(
        "Shadow-Red",
        0,
        0,
        255,
        { shadowShaderValue.get() && shadowColorMode.get().equals("custom", true) })
    private val shadowColorGreenValue = IntegerValue(
        "Shadow-Green",
        111,
        0,
        255,
        { shadowShaderValue.get() && shadowColorMode.get().equals("custom", true) })
    private val shadowColorBlueValue = IntegerValue(
        "Shadow-Blue",
        255,
        0,
        255,
        { shadowShaderValue.get() && shadowColorMode.get().equals("custom", true) })

    private val bgRoundedValue = BoolValue("Rounded", false)
    private val roundStrength = FloatValue("Rounded-Strength", 10F, 0F, 50F, { bgRoundedValue.get() })

    private val rectColorModeValue =
        ListValue("Color", arrayOf("Custom", "Rainbow", "LiquidSlowly", "Fade", "Sky", "Mixer"), "Custom")

    private val rectColorRedValue = IntegerValue("Red", 0, 0, 255)
    private val rectColorGreenValue = IntegerValue("Green", 0, 0, 255)
    private val rectColorBlueValue = IntegerValue("Blue", 0, 0, 255)
    private val rectColorBlueAlpha = IntegerValue("Alpha", 255, 0, 255)

    private val saturationValue = FloatValue("Saturation", 0.9f, 0f, 1f)
    private val brightnessValue = FloatValue("Brightness", 1f, 0f, 1f)
    private val cRainbowSecValue = IntegerValue("Seconds", 2, 1, 10)
    private val delayValue = IntegerValue("Delay", 50, 0, 200)

    private val shadowValue = BoolValue("TextShadow", false)
    private val antiSnipeMatch = BoolValue("AntiSnipeMatch", false)
    private val showRedNumbersValue = BoolValue("ShowRedNumbers", true)
    private val fontValue = FontValue("Font", Fonts.minecraftFont)
    private val domainFontValue = FontValue("DomainFont", Fonts.minecraftFont)
    private val domainFontYValue = FloatValue("Domain-TextY", 0F, 0F, 3F)
    private val domainShadowValue = ListValue("Domain-Shadow", arrayOf("None", "Outline", "Default"), "None")
    private val outlineWidthValue = FloatValue("OutlineWidth", 0.5F, 0.5F, 2F)

    private val cachedDomains = arrayListOf<String>()

    private val garbageTimer = MSTimer()

    private val hypickleRegex = Regex("[0-9][0-9]/[0-9][0-9]/[0-9][0-9]")

    override fun updateElement() {
        if (garbageTimer.hasTimePassed(30000L) || cachedDomains.size > 50) {
            cachedDomains.clear()
            garbageTimer.reset()
        }
    }

    /**
     * Draw element
     */
    override fun drawElement(): Border? {
        val antiBlind = Client.moduleManager.getModule(NoEffect::class.java) as NoEffect
        if (antiBlind.state && antiBlind.scoreBoard.get())
            return null

        val fontRenderer = fontValue.get()
        val backColor = backgroundColor().rgb

        val rectColorMode = rectColorModeValue.get()
        val rectCustomColor = Color(
            rectColorRedValue.get(), rectColorGreenValue.get(), rectColorBlueValue.get(),
            rectColorBlueAlpha.get()
        ).rgb

        val worldScoreboard: Scoreboard = mc.theWorld.scoreboard
        var currObjective: ScoreObjective? = null
        val playerTeam = worldScoreboard.getPlayersTeam(mc.thePlayer.name)

        if (playerTeam != null) {
            val colorIndex = playerTeam.chatFormat.colorIndex

            if (colorIndex >= 0)
                currObjective = worldScoreboard.getObjectiveInDisplaySlot(3 + colorIndex)
        }

        val objective = currObjective ?: worldScoreboard.getObjectiveInDisplaySlot(1) ?: return null

        val scoreboard: Scoreboard = objective.scoreboard
        var scoreCollection = scoreboard.getSortedScores(objective)
        val scores = Lists.newArrayList(Iterables.filter(scoreCollection) { input ->
            input?.playerName != null && !input.playerName.startsWith("#")
        })

        scoreCollection = if (scores.size > 15)
            Lists.newArrayList(Iterables.skip(scores, scoreCollection.size - 15))
        else
            scores

        var maxWidth = fontRenderer.getStringWidth(objective.displayName)

        val hud = Client.moduleManager.getModule(Hud::class.java) as Hud

        for (score in scoreCollection) {
            val scorePlayerTeam = scoreboard.getPlayersTeam(score.playerName)
            var name = ScorePlayerTeam.formatPlayerName(scorePlayerTeam, score.playerName)
            var stripped = StringUtils.fixString(ColorUtils.stripColor(name)!!)
            val width = "$name: ${EnumChatFormatting.RED}${score.scorePoints}"
            maxWidth = maxWidth.coerceAtLeast(fontRenderer.getStringWidth(width))
        }

        val maxHeight = scoreCollection.size * fontRenderer.FONT_HEIGHT
        val l1 = if (side.horizontal == Side.Horizontal.LEFT) {
            maxWidth + 3
        } else {
            -maxWidth - 3
        }

        var FadeColor: Int = ColorUtils.fade(
            Color(
                rectColorRedValue.get(),
                rectColorGreenValue.get(),
                rectColorBlueValue.get(),
                rectColorBlueAlpha.get()
            ), 0, 100
        ).rgb
        val LiquidSlowly =
            ColorUtils.LiquidSlowly(System.nanoTime(), 0, saturationValue.get(), brightnessValue.get()).rgb
        var liquidSlowli: Int = LiquidSlowly

        val mixerColor = ColorMixer.getMixedColor(0, cRainbowSecValue.get()).rgb

        if (scoreCollection.size > 0) { // only draw background and rect whenever there's something on scoreboard
            // shadow
            if (shadowShaderValue.get()) {
                GL11.glTranslated(-renderX, -renderY, 0.0)
                GL11.glScalef(1F, 1F, 1F)
                GL11.glPushMatrix()
                ShadowUtils.shadow(shadowStrength.get(), {
                    GL11.glPushMatrix()
                    GL11.glTranslated(renderX, renderY, 0.0)
                    GL11.glScalef(scale, scale, scale)
                    if (bgRoundedValue.get())
                        RenderUtils.originalRoundedRect(
                            l1.toFloat() + if (side.horizontal == Side.Horizontal.LEFT) 2F else -2F,
                            if (rectValue.get()) -2F - rectHeight.get().toFloat() else -2F,
                            if (side.horizontal == Side.Horizontal.LEFT) -5F else 5F,
                            (maxHeight + fontRenderer.FONT_HEIGHT).toFloat(), roundStrength.get(),
                            if (shadowColorMode.get().equals("background", true))
                                Color(
                                    backgroundColorRedValue.get(),
                                    backgroundColorGreenValue.get(),
                                    backgroundColorBlueValue.get()
                                ).rgb
                            else
                                Color(
                                    shadowColorRedValue.get(),
                                    shadowColorGreenValue.get(),
                                    shadowColorBlueValue.get()
                                ).rgb
                        )
                    else
                        RenderUtils.newDrawRect(
                            l1.toFloat() + if (side.horizontal == Side.Horizontal.LEFT) 2F else -2F,
                            if (rectValue.get()) -2F - rectHeight.get().toFloat() else -2F,
                            if (side.horizontal == Side.Horizontal.LEFT) -5F else 5F,
                            (maxHeight + fontRenderer.FONT_HEIGHT).toFloat(),
                            if (shadowColorMode.get().equals("background", true))
                                Color(
                                    backgroundColorRedValue.get(),
                                    backgroundColorGreenValue.get(),
                                    backgroundColorBlueValue.get()
                                ).rgb
                            else
                                Color(
                                    shadowColorRedValue.get(),
                                    shadowColorGreenValue.get(),
                                    shadowColorBlueValue.get()
                                ).rgb
                        )
                    GL11.glPopMatrix()
                }, {
                    GL11.glPushMatrix()
                    GL11.glTranslated(renderX, renderY, 0.0)
                    GL11.glScalef(scale, scale, scale)
                    GlStateManager.enableBlend()
                    GlStateManager.disableTexture2D()
                    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)
                    if (bgRoundedValue.get())
                        RenderUtils.fastRoundedRect(
                            l1.toFloat() + if (side.horizontal == Side.Horizontal.LEFT) 2F else -2F,
                            if (rectValue.get()) -2F - rectHeight.get().toFloat() else -2F,
                            if (side.horizontal == Side.Horizontal.LEFT) -5F else 5F,
                            (maxHeight + fontRenderer.FONT_HEIGHT).toFloat(), roundStrength.get()
                        )
                    else
                        RenderUtils.quickDrawRect(
                            l1.toFloat() + if (side.horizontal == Side.Horizontal.LEFT) 2F else -2F,
                            if (rectValue.get()) -2F - rectHeight.get().toFloat() else -2F,
                            if (side.horizontal == Side.Horizontal.LEFT) -5F else 5F,
                            (maxHeight + fontRenderer.FONT_HEIGHT).toFloat()
                        )
                    GlStateManager.enableTexture2D()
                    GlStateManager.disableBlend()
                    GL11.glPopMatrix()
                })
                GL11.glPopMatrix()
                GL11.glScalef(scale, scale, scale)
                GL11.glTranslated(renderX, renderY, 0.0)
            }
            // blur
            if (blurValue.get()) {
                GL11.glTranslated(-renderX, -renderY, 0.0)
                GL11.glScalef(1F, 1F, 1F)
                GL11.glPushMatrix()

                if (bgRoundedValue.get()) {
                    if (side.horizontal == Side.Horizontal.LEFT)
                        BlurUtils.blurAreaRounded(
                            renderX.toFloat() + (l1 + 2F) * scale,
                            renderY.toFloat() + -2F * scale,
                            renderX.toFloat() + -5F * scale,
                            renderY.toFloat() + (maxHeight + fontRenderer.FONT_HEIGHT) * scale,
                            roundStrength.get(),
                            blurStrength.get()
                        )
                    else
                        BlurUtils.blurAreaRounded(
                            renderX.toFloat() + (l1 - 2F) * scale,
                            renderY.toFloat() + -2F * scale,
                            renderX.toFloat() + 5F * scale,
                            renderY.toFloat() + (maxHeight + fontRenderer.FONT_HEIGHT) * scale,
                            roundStrength.get(),
                            blurStrength.get()
                        )
                } else {
                    if (side.horizontal == Side.Horizontal.LEFT)
                        BlurUtils.blurArea(
                            renderX.toFloat() + (l1 + 2F) * scale,
                            renderY.toFloat() + -2F * scale,
                            renderX.toFloat() + -5F * scale,
                            renderY.toFloat() + (maxHeight + fontRenderer.FONT_HEIGHT) * scale,
                            blurStrength.get()
                        )
                    else
                        BlurUtils.blurArea(
                            renderX.toFloat() + (l1 - 2F) * scale,
                            renderY.toFloat() + -2F * scale,
                            renderX.toFloat() + 5F * scale,
                            renderY.toFloat() + (maxHeight + fontRenderer.FONT_HEIGHT) * scale,
                            blurStrength.get()
                        )
                }

                GL11.glPopMatrix()
                GL11.glScalef(scale, scale, scale)
                GL11.glTranslated(renderX, renderY, 0.0)
            }

            // backyard
            if (bgRoundedValue.get()) {
                Stencil.write(false)
                GlStateManager.enableBlend()
                GlStateManager.disableTexture2D()
                GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)
                RenderUtils.fastRoundedRect(
                    l1.toFloat() + if (side.horizontal == Side.Horizontal.LEFT) 2F else -2F,
                    if (rectValue.get()) -2F - rectHeight.get().toFloat() else -2F,
                    if (side.horizontal == Side.Horizontal.LEFT) -5F else 5F,
                    (maxHeight + fontRenderer.FONT_HEIGHT).toFloat(), roundStrength.get()
                )
                GlStateManager.enableTexture2D()
                GlStateManager.disableBlend()
                Stencil.erase(true)
            }

            if (useVanillaBackground.get()) {
                if (side.horizontal == Side.Horizontal.LEFT) {
                    Gui.drawRect(l1 + 2, -2, -5, -2 + fontRenderer.FONT_HEIGHT + 1, 1610612736)
                    Gui.drawRect(
                        l1 + 2,
                        -2 + fontRenderer.FONT_HEIGHT + 1,
                        -5,
                        maxHeight + fontRenderer.FONT_HEIGHT,
                        1342177280
                    )
                } else {
                    Gui.drawRect(l1 - 2, -2, 5, -2 + fontRenderer.FONT_HEIGHT + 1, 1610612736)
                    Gui.drawRect(
                        l1 - 2,
                        -2 + fontRenderer.FONT_HEIGHT + 1,
                        5,
                        maxHeight + fontRenderer.FONT_HEIGHT,
                        1342177280
                    )
                }
            } else if (side.horizontal == Side.Horizontal.LEFT)
                Gui.drawRect(l1 + 2, -2, -5, maxHeight + fontRenderer.FONT_HEIGHT, backColor)
            else
                Gui.drawRect(l1 - 2, -2, 5, maxHeight + fontRenderer.FONT_HEIGHT, backColor)

            // rect
            if (rectValue.get()) {
                val rectColor = when (rectColorMode.lowercase(Locale.getDefault())) {
                    "sky" -> RenderUtils.SkyRainbow(0, saturationValue.get(), brightnessValue.get())
                    "rainbow" -> RenderUtils.getRainbowOpaque(
                        cRainbowSecValue.get(),
                        saturationValue.get(),
                        brightnessValue.get(),
                        0
                    )

                    "liquidslowly" -> liquidSlowli
                    "fade" -> FadeColor
                    "mixer" -> mixerColor
                    else -> rectCustomColor
                }

                if (side.horizontal == Side.Horizontal.LEFT)
                    Gui.drawRect(l1 + 2, -2, -5, -2 - rectHeight.get(), rectColor)
                else
                    Gui.drawRect(l1 - 2, -2, 5, -2 - rectHeight.get(), rectColor)
            }

            if (bgRoundedValue.get())
                Stencil.dispose()
        }

        scoreCollection.forEachIndexed { index, score ->
            val team = scoreboard.getPlayersTeam(score.playerName)

            var name = ScorePlayerTeam.formatPlayerName(team, score.playerName)
            val scorePoints = "${EnumChatFormatting.RED}${score.scorePoints}"

            val width = 5
            val height = maxHeight - index * fontRenderer.FONT_HEIGHT

            var changed = false

            var stripped = StringUtils.fixString(ColorUtils.stripColor(name)!!)

            GlStateManager.resetColor()
            if (antiSnipeMatch.get() && hypickleRegex.containsMatchIn(stripped))
                name = ""

            if (changed) {
                var stringZ = ""
                for (z in 0..(name.length - 1)) {
                    val typeColor = when {
                        rectColorMode.equals("Sky", ignoreCase = true) -> RenderUtils.SkyRainbow(
                            z * delayValue.get(),
                            saturationValue.get(),
                            brightnessValue.get()
                        )

                        rectColorMode.equals("Rainbow", ignoreCase = true) -> RenderUtils.getRainbowOpaque(
                            cRainbowSecValue.get(),
                            saturationValue.get(),
                            brightnessValue.get(),
                            z * delayValue.get()
                        )

                        rectColorMode.equals(
                            "LiquidSlowly",
                            ignoreCase = true
                        ) -> ColorUtils.LiquidSlowly(
                            System.nanoTime(),
                            z * delayValue.get(),
                            saturationValue.get(),
                            brightnessValue.get()
                        ).rgb

                        rectColorMode.equals("Fade", ignoreCase = true) -> ColorUtils.fade(
                            Color(
                                rectColorRedValue.get(),
                                rectColorGreenValue.get(),
                                rectColorBlueValue.get(),
                                rectColorBlueAlpha.get()
                            ),
                            z * delayValue.get(),
                            100
                        ).rgb

                        rectColorMode.equals(
                            "Mixer",
                            ignoreCase = true
                        ) -> ColorMixer.getMixedColor(z * delayValue.get(), cRainbowSecValue.get()).rgb

                        else -> rectCustomColor
                    }
                    if (side.horizontal == Side.Horizontal.LEFT) {
                        when (domainShadowValue.get().lowercase(Locale.getDefault())) {
                            "none" -> domainFontValue.get().drawString(
                                name.get(z).toString(),
                                -3F + domainFontValue.get().getStringWidth(stringZ).toFloat(),
                                height.toFloat() + domainFontYValue.get(),
                                typeColor,
                                false
                            )

                            "default" -> domainFontValue.get().drawStringWithShadow(
                                name.get(z).toString(),
                                -3F + domainFontValue.get().getStringWidth(stringZ).toFloat(),
                                height.toFloat() + domainFontYValue.get(),
                                typeColor
                            )

                            "outline" -> {
                                domainFontValue.get().drawString(
                                    name.get(z).toString(),
                                    -3F + domainFontValue.get().getStringWidth(stringZ)
                                        .toFloat() - outlineWidthValue.get(),
                                    height.toFloat() + domainFontYValue.get(),
                                    Color.black.rgb,
                                    shadowValue.get()
                                )
                                domainFontValue.get().drawString(
                                    name.get(z).toString(),
                                    -3F + domainFontValue.get().getStringWidth(stringZ)
                                        .toFloat() + outlineWidthValue.get(),
                                    height.toFloat() + domainFontYValue.get(),
                                    Color.black.rgb,
                                    shadowValue.get()
                                )
                                domainFontValue.get().drawString(
                                    name.get(z).toString(),
                                    -3F + domainFontValue.get().getStringWidth(stringZ).toFloat(),
                                    height.toFloat() + domainFontYValue.get() - outlineWidthValue.get(),
                                    Color.black.rgb,
                                    shadowValue.get()
                                )
                                domainFontValue.get().drawString(
                                    name.get(z).toString(),
                                    -3F + domainFontValue.get().getStringWidth(stringZ).toFloat(),
                                    height.toFloat() + domainFontYValue.get() + outlineWidthValue.get(),
                                    Color.black.rgb,
                                    shadowValue.get()
                                )
                                domainFontValue.get().drawString(
                                    name.get(z).toString(),
                                    -3F + domainFontValue.get().getStringWidth(stringZ).toFloat(),
                                    height.toFloat() + domainFontYValue.get(),
                                    typeColor,
                                    shadowValue.get()
                                )
                            }
                        }
                    } else {
                        when (domainShadowValue.get().lowercase(Locale.getDefault())) {
                            "none" -> domainFontValue.get().drawString(
                                name.get(z).toString(),
                                l1.toFloat() + domainFontValue.get().getStringWidth(stringZ),
                                height.toFloat() + domainFontYValue.get(),
                                typeColor,
                                false
                            )

                            "default" -> domainFontValue.get().drawStringWithShadow(
                                name.get(z).toString(),
                                l1.toFloat() + domainFontValue.get().getStringWidth(stringZ),
                                height.toFloat() + domainFontYValue.get(),
                                typeColor
                            )

                            "outline" -> {
                                domainFontValue.get().drawString(
                                    name.get(z).toString(),
                                    l1.toFloat() + domainFontValue.get()
                                        .getStringWidth(stringZ) - outlineWidthValue.get(),
                                    height.toFloat() + domainFontYValue.get(),
                                    Color.black.rgb,
                                    shadowValue.get()
                                )
                                domainFontValue.get().drawString(
                                    name.get(z).toString(),
                                    l1.toFloat() + domainFontValue.get()
                                        .getStringWidth(stringZ) + outlineWidthValue.get(),
                                    height.toFloat() + domainFontYValue.get(),
                                    Color.black.rgb,
                                    shadowValue.get()
                                )
                                domainFontValue.get().drawString(
                                    name.get(z).toString(),
                                    l1.toFloat() + domainFontValue.get().getStringWidth(stringZ),
                                    height.toFloat() + domainFontYValue.get() - outlineWidthValue.get(),
                                    Color.black.rgb,
                                    shadowValue.get()
                                )
                                domainFontValue.get().drawString(
                                    name.get(z).toString(),
                                    l1.toFloat() + domainFontValue.get().getStringWidth(stringZ),
                                    height.toFloat() + domainFontYValue.get() + outlineWidthValue.get(),
                                    Color.black.rgb,
                                    shadowValue.get()
                                )
                                domainFontValue.get().drawString(
                                    name.get(z).toString(),
                                    l1.toFloat() + domainFontValue.get().getStringWidth(stringZ),
                                    height.toFloat() + domainFontYValue.get(),
                                    typeColor,
                                    shadowValue.get()
                                )
                            }
                        }
                    }

                    stringZ += name.get(z).toString()
                }
            } else if (side.horizontal == Side.Horizontal.LEFT)
                fontRenderer.drawString(name, -3F, height.toFloat(), -1, shadowValue.get())
            else
                fontRenderer.drawString(name, l1.toFloat(), height.toFloat(), -1, shadowValue.get())

            if (showRedNumbersValue.get())
                if (side.horizontal == Side.Horizontal.LEFT)
                    fontRenderer.drawString(
                        scorePoints,
                        (l1 + 1 - fontRenderer.getStringWidth(scorePoints)).toFloat(),
                        height.toFloat(),
                        -1,
                        shadowValue.get()
                    )
                else
                    fontRenderer.drawString(
                        scorePoints,
                        (width - fontRenderer.getStringWidth(scorePoints)).toFloat(),
                        height.toFloat(),
                        -1,
                        shadowValue.get()
                    )

            if (index == scoreCollection.size - 1) {
                val displayName = objective.displayName

                GlStateManager.resetColor()

                fontRenderer.drawString(
                    displayName,
                    if (side.horizontal == Side.Horizontal.LEFT) (maxWidth / 2 - fontRenderer.getStringWidth(displayName) / 2).toFloat() else (l1 + maxWidth / 2 - fontRenderer.getStringWidth(
                        displayName
                    ) / 2).toFloat(),
                    (height - fontRenderer.FONT_HEIGHT).toFloat(),
                    -1, shadowValue.get()
                )
            }

        }

        return if (side.horizontal == Side.Horizontal.LEFT) Border(
            maxWidth.toFloat() + 5,
            -2F,
            -5F,
            maxHeight.toFloat() + fontRenderer.FONT_HEIGHT
        ) else Border(-maxWidth.toFloat() - 5, -2F, 5F, maxHeight.toFloat() + fontRenderer.FONT_HEIGHT)
    }

    private fun backgroundColor() = Color(
        backgroundColorRedValue.get(), backgroundColorGreenValue.get(),
        backgroundColorBlueValue.get(), backgroundColorAlphaValue.get()
    )

}
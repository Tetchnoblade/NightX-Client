package net.aspw.client.visual.hud.element.elements

import com.google.common.collect.Iterables
import com.google.common.collect.Lists
import net.aspw.client.Client
import net.aspw.client.features.module.impl.visual.AntiBlind
import net.aspw.client.features.module.impl.visual.ColorMixer
import net.aspw.client.util.render.BlurUtils
import net.aspw.client.util.render.ColorUtils
import net.aspw.client.util.render.RenderUtils
import net.aspw.client.util.render.ShadowUtils
import net.aspw.client.util.render.Stencil
import net.aspw.client.util.timer.MSTimer
import net.aspw.client.value.*
import net.aspw.client.visual.font.semi.Fonts
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
import java.awt.Color
import org.lwjgl.opengl.GL11
import java.util.*

@ElementInfo(name = "ScoreboardElement")
class ScoreboardElement(
    x: Double = 5.0, y: Double = 0.0, scale: Float = 1F,
    side: Side = Side(Side.Horizontal.RIGHT, Side.Vertical.MIDDLE)
) : Element(x, y, scale, side) {

    private val useVanillaBackground = BoolValue("UseVanillaBackground", false)
    private val backgroundColorRedValue = IntegerValue("Background-R", 0, 0, 255) { !useVanillaBackground.get() }
    private val backgroundColorGreenValue = IntegerValue("Background-G", 0, 0, 255) { !useVanillaBackground.get() }
    private val backgroundColorBlueValue = IntegerValue("Background-B", 0, 0, 255) { !useVanillaBackground.get() }
    private val backgroundColorAlphaValue = IntegerValue("Background-Alpha", 95, 0, 255) { !useVanillaBackground.get() }

    private val rectValue = BoolValue("Rect", false)
    private val rectHeight = IntegerValue("Rect-Height", 1, 1, 10) { rectValue.get() }

    private val blurValue = BoolValue("Blur", false)
    private val blurStrength = FloatValue("Blur-Strength", 0F, 0F, 30F) { blurValue.get() }

    private val shadowShaderValue = BoolValue("Shadow", false)
    private val shadowStrength = FloatValue("Shadow-Strength", 0F, 0F, 30F) { shadowShaderValue.get() }
    private val shadowColorMode = ListValue("Shadow-Color", arrayOf("Background", "Custom"), "Background") { shadowShaderValue.get() }

    private val shadowColorRedValue = IntegerValue("Shadow-Red", 0, 0, 255) {
        shadowShaderValue.get() && shadowColorMode.get().equals("custom", true)
    }
    private val shadowColorGreenValue = IntegerValue("Shadow-Green", 111, 0, 255) {
        shadowShaderValue.get() && shadowColorMode.get().equals("custom", true)
    }
    private val shadowColorBlueValue = IntegerValue("Shadow-Blue", 255, 0, 255) {
        shadowShaderValue.get() && shadowColorMode.get().equals("custom", true)
    }

    private val bgRoundedValue = BoolValue("Rounded", false)
    private val roundStrength = FloatValue("Rounded-Strength", 5F, 0F, 30F) { bgRoundedValue.get() }

    private val rectColorModeValue = ListValue("Color", arrayOf("Custom", "Rainbow", "LiquidSlowly", "Fade", "Sky", "Mixer"), "Custom")

    private val rectColorRedValue = IntegerValue("Red", 0, 0, 255)
    private val rectColorGreenValue = IntegerValue("Green", 111, 0, 255)
    private val rectColorBlueValue = IntegerValue("Blue", 255, 0, 255)
    private val rectColorBlueAlpha = IntegerValue("Alpha", 255, 0, 255)

    private val saturationValue = FloatValue("Saturation", 0.9f, 0f, 1f)
    private val brightnessValue = FloatValue("Brightness", 1f, 0f, 1f)
    private val cRainbowSecValue = IntegerValue("Seconds", 2, 1, 10)

    private val shadowValue = BoolValue("FontShadow", false)
    private val showRedNumbersValue = BoolValue("ShowRedNumbers", false)
    private val fontValue = FontValue("Font", Fonts.minecraftFont)

    private val cachedDomains = arrayListOf<String>()

    private val garbageTimer = MSTimer()

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
        val antiBlind = Client.moduleManager.getModule(AntiBlind::class.java) as AntiBlind
        if (antiBlind.state && antiBlind.scoreBoard.get())
            return null

        val fontRenderer = fontValue.get()
        val backColor = backgroundColor().rgb

        val rectColorMode = rectColorModeValue.get()
        val rectCustomColor = Color(rectColorRedValue.get(), rectColorGreenValue.get(), rectColorBlueValue.get(),
            rectColorBlueAlpha.get()).rgb

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

        for (score in scoreCollection) {
            val scorePlayerTeam = scoreboard.getPlayersTeam(score.playerName)
            val name = ScorePlayerTeam.formatPlayerName(scorePlayerTeam, score.playerName)
            val width = "$name: ${EnumChatFormatting.RED}${score.scorePoints}"
            maxWidth = maxWidth.coerceAtLeast(fontRenderer.getStringWidth(width))
        }

        val maxHeight = scoreCollection.size * fontRenderer.FONT_HEIGHT
        val l1 = if (side.horizontal == Side.Horizontal.LEFT) {maxWidth + 3} else {-maxWidth - 3}

        val FadeColor : Int = ColorUtils.fade(Color(rectColorRedValue.get(), rectColorGreenValue.get(), rectColorBlueValue.get(), rectColorBlueAlpha.get()), 0, 100).rgb
        val LiquidSlowly = ColorUtils.LiquidSlowly(System.nanoTime(), 0, saturationValue.get(), brightnessValue.get()).rgb
        val liquidSlowli : Int = LiquidSlowly

        val mixerColor = ColorMixer.getMixedColor(0, cRainbowSecValue.get()).rgb

        if (scoreCollection.isNotEmpty()) { // only draw background and rect whenever there's something on scoreboard
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
                                Color(backgroundColorRedValue.get(), backgroundColorGreenValue.get(), backgroundColorBlueValue.get()).rgb
                            else
                                Color(shadowColorRedValue.get(), shadowColorGreenValue.get(), shadowColorBlueValue.get()).rgb)
                    else
                        RenderUtils.newDrawRect(
                            l1.toFloat() + if (side.horizontal == Side.Horizontal.LEFT) 2F else -2F,
                            if (rectValue.get()) -2F - rectHeight.get().toFloat() else -2F,
                            if (side.horizontal == Side.Horizontal.LEFT) -5F else 5F,
                            (maxHeight + fontRenderer.FONT_HEIGHT).toFloat(),
                            if (shadowColorMode.get().equals("background", true))
                                Color(backgroundColorRedValue.get(), backgroundColorGreenValue.get(), backgroundColorBlueValue.get()).rgb
                            else
                                Color(shadowColorRedValue.get(), shadowColorGreenValue.get(), shadowColorBlueValue.get()).rgb)
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
                            (maxHeight + fontRenderer.FONT_HEIGHT).toFloat(), roundStrength.get())
                    else
                        RenderUtils.quickDrawRect(
                            l1.toFloat() + if (side.horizontal == Side.Horizontal.LEFT) 2F else -2F,
                            if (rectValue.get()) -2F - rectHeight.get().toFloat() else -2F,
                            if (side.horizontal == Side.Horizontal.LEFT) -5F else 5F,
                            (maxHeight + fontRenderer.FONT_HEIGHT).toFloat())
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
                        BlurUtils.blurAreaRounded(renderX.toFloat() + (l1 + 2F) * scale, renderY.toFloat() + -2F * scale,
                            renderX.toFloat() + -5F * scale, renderY.toFloat() + (maxHeight + fontRenderer.FONT_HEIGHT) * scale, roundStrength.get(), blurStrength.get())
                    else
                        BlurUtils.blurAreaRounded(renderX.toFloat() + (l1 - 2F) * scale, renderY.toFloat() + -2F * scale,
                            renderX.toFloat() + 5F * scale, renderY.toFloat() + (maxHeight + fontRenderer.FONT_HEIGHT) * scale, roundStrength.get(), blurStrength.get())
                } else {
                    if (side.horizontal == Side.Horizontal.LEFT)
                        BlurUtils.blurArea(renderX.toFloat() + (l1 + 2F) * scale, renderY.toFloat() + -2F * scale,
                            renderX.toFloat() + -5F * scale, renderY.toFloat() + (maxHeight + fontRenderer.FONT_HEIGHT) * scale, blurStrength.get())
                    else
                        BlurUtils.blurArea(renderX.toFloat() + (l1 - 2F) * scale, renderY.toFloat() + -2F * scale,
                            renderX.toFloat() + 5F * scale, renderY.toFloat() + (maxHeight + fontRenderer.FONT_HEIGHT) * scale, blurStrength.get())
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
                    (maxHeight + fontRenderer.FONT_HEIGHT).toFloat(), roundStrength.get())
                GlStateManager.enableTexture2D()
                GlStateManager.disableBlend()
                Stencil.erase(true)
            }

            if (useVanillaBackground.get()) {
                if (side.horizontal == Side.Horizontal.LEFT) {
                    Gui.drawRect(l1 + 2, -2, -5, -2 + fontRenderer.FONT_HEIGHT + 1, 1610612736)
                    Gui.drawRect(l1 + 2, -2 + fontRenderer.FONT_HEIGHT + 1, -5, maxHeight + fontRenderer.FONT_HEIGHT, 1342177280)
                } else {
                    Gui.drawRect(l1 - 2, -2, 5, -2 + fontRenderer.FONT_HEIGHT + 1, 1610612736)
                    Gui.drawRect(l1 - 2, -2 + fontRenderer.FONT_HEIGHT + 1, 5, maxHeight + fontRenderer.FONT_HEIGHT, 1342177280)
                }
            } else if (side.horizontal == Side.Horizontal.LEFT)
                Gui.drawRect(l1 + 2, -2, -5, maxHeight + fontRenderer.FONT_HEIGHT, backColor)
            else
                Gui.drawRect(l1 - 2, -2, 5, maxHeight + fontRenderer.FONT_HEIGHT, backColor)

            // rect
            if (rectValue.get()) {
                val rectColor = when (rectColorMode.lowercase(Locale.getDefault())) {
                    "sky" -> RenderUtils.SkyRainbow(0, saturationValue.get(), brightnessValue.get())
                    "rainbow" -> RenderUtils.getRainbowOpaque(cRainbowSecValue.get(), saturationValue.get(), brightnessValue.get(), 0)
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

            val name = ScorePlayerTeam.formatPlayerName(team, score.playerName)
            val scorePoints = "${EnumChatFormatting.RED}${score.scorePoints}"

            val width = 5
            val height = maxHeight - index * fontRenderer.FONT_HEIGHT

            GlStateManager.resetColor()

            if (side.horizontal == Side.Horizontal.LEFT)
                fontRenderer.drawString(name, -3F, height.toFloat(), -1, shadowValue.get())
            else
                fontRenderer.drawString(name, l1.toFloat(), height.toFloat(), -1, shadowValue.get())

            if (showRedNumbersValue.get())
                if (side.horizontal == Side.Horizontal.LEFT)
                    fontRenderer.drawString(scorePoints, (l1 + 1 - fontRenderer.getStringWidth(scorePoints)).toFloat(), height.toFloat(), -1, shadowValue.get())
                else
                    fontRenderer.drawString(scorePoints, (width - fontRenderer.getStringWidth(scorePoints)).toFloat(), height.toFloat(), -1, shadowValue.get())

            if (index == scoreCollection.size - 1) {
                val displayName = objective.displayName

                GlStateManager.resetColor()

                fontRenderer.drawString(displayName,
                    if (side.horizontal == Side.Horizontal.LEFT) (maxWidth / 2 - fontRenderer.getStringWidth(displayName) / 2).toFloat() else (l1 + maxWidth / 2 - fontRenderer.getStringWidth(displayName) / 2).toFloat(),
                    (height - fontRenderer.FONT_HEIGHT).toFloat(),
                    -1, shadowValue.get())
            }

        }

        return if (side.horizontal == Side.Horizontal.LEFT) Border(maxWidth.toFloat() + 5, -2F, -5F, maxHeight.toFloat() + fontRenderer.FONT_HEIGHT) else Border(-maxWidth.toFloat() - 5, -2F, 5F, maxHeight.toFloat() + fontRenderer.FONT_HEIGHT)
    }

    private fun backgroundColor() = Color(backgroundColorRedValue.get(), backgroundColorGreenValue.get(),
        backgroundColorBlueValue.get(), backgroundColorAlphaValue.get())

}
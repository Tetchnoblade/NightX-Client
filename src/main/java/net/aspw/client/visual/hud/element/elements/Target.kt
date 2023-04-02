package net.aspw.client.visual.hud.element.elements

import net.aspw.client.Client
import net.aspw.client.features.module.impl.combat.KillAura
import net.aspw.client.features.module.impl.combat.TPAura
import net.aspw.client.features.module.impl.visual.ColorMixer
import net.aspw.client.utils.render.*
import net.aspw.client.value.*
import net.aspw.client.visual.hud.designer.GuiHudDesigner
import net.aspw.client.visual.hud.element.Border
import net.aspw.client.visual.hud.element.Element
import net.aspw.client.visual.hud.element.ElementInfo
import net.aspw.client.visual.hud.element.Side
import net.aspw.client.visual.hud.element.elements.targets.TargetStyle
import net.aspw.client.visual.hud.element.elements.targets.impl.*
import net.minecraft.client.gui.GuiChat
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.entity.player.EntityPlayer
import org.lwjgl.opengl.GL11
import java.awt.Color

/**
 * A target hud
 */
@ElementInfo(name = "Target", disableScale = true, retrieveDamage = true)
class Target(
    x: Double = 213.0, y: Double = 160.0, scale: Float = 1F,
    side: Side = Side(Side.Horizontal.LEFT, Side.Vertical.UP)
) : Element(x, y, scale, side) {

    val styleList = mutableListOf<TargetStyle>()

    val styleValue: ListValue

    // Global variables
    val blurValue = BoolValue("Blur", false)
    val blurStrength = FloatValue("Blur-Strength", 12F, 0.01F, 40F, { blurValue.get() })

    val shadowValue = BoolValue("Shadow", false)
    val shadowStrength = FloatValue("Shadow-Strength", 12F, 0.01F, 40F, { shadowValue.get() })
    val shadowColorMode =
        ListValue("Shadow-Color", arrayOf("Background", "Custom", "Bar"), "Custom", { shadowValue.get() })

    val shadowColorRedValue =
        IntegerValue("Shadow-Red", 255, 0, 255, { shadowValue.get() && shadowColorMode.get().equals("custom", true) })
    val shadowColorGreenValue =
        IntegerValue("Shadow-Green", 0, 0, 255, { shadowValue.get() && shadowColorMode.get().equals("custom", true) })
    val shadowColorBlueValue =
        IntegerValue("Shadow-Blue", 255, 0, 255, { shadowValue.get() && shadowColorMode.get().equals("custom", true) })

    val fadeValue = BoolValue("FadeAnim", false)
    val fadeSpeed = FloatValue("Fade-Speed", 1F, 0F, 5F, { fadeValue.get() })

    val noAnimValue = BoolValue("No-Animation", true)
    val globalAnimSpeed = FloatValue("Global-AnimSpeed", 5F, 1F, 9F, { !noAnimValue.get() })

    val showWithChatOpen = BoolValue("Show-ChatOpen", true)
    val resetBar = BoolValue("ResetBarWhenHiding", true)

    val colorModeValue =
        ListValue("Color", arrayOf("Custom", "Rainbow", "Sky", "Slowly", "Fade", "Mixer", "Health"), "Sky")
    val redValue = IntegerValue("Red", 255, 0, 255)
    val greenValue = IntegerValue("Green", 255, 0, 255)
    val blueValue = IntegerValue("Blue", 255, 0, 255)
    val saturationValue = FloatValue("Saturation", 0.5F, 0F, 1F)
    val brightnessValue = FloatValue("Brightness", 1F, 0F, 1F)
    val waveSecondValue = IntegerValue("Seconds", 2, 1, 10)
    val bgRedValue = IntegerValue("Background-Red", 0, 0, 255)
    val bgGreenValue = IntegerValue("Background-Green", 0, 0, 255)
    val bgBlueValue = IntegerValue("Background-Blue", 0, 0, 255)
    val bgAlphaValue = IntegerValue("Background-Alpha", 120, 0, 255)

    override val values: List<Value<*>>
        get() {
            val valueList = mutableListOf<Value<*>>()
            styleList.forEach { valueList.addAll(it.values) }
            return super.values.toMutableList() + valueList
        }

    init {
        styleValue = ListValue(
            "Style", addStyles(
                LiquidBounce(this),
                Chill(this),
                Rice(this),
                Exhibition(this),
                Remix(this),
                Slowly(this),
                Blocky(this),
                Simple(this)
            ).toTypedArray(), "Simple"
        )
    }

    var mainTarget: EntityPlayer? = null
    var animProgress = 0F

    var barColor = Color(-1)
    var bgColor = Color(-1)

    override fun drawElement(): Border? {
        val mainStyle = getCurrentStyle(styleValue.get()) ?: return null

        val kaTarget = (Client.moduleManager[KillAura::class.java] as KillAura).target
        val taTarget = (Client.moduleManager[TPAura::class.java] as TPAura).lastTarget

        val actualTarget = if (kaTarget != null && kaTarget is EntityPlayer) kaTarget
        else if (taTarget != null && taTarget is EntityPlayer) taTarget
        else if ((mc.currentScreen is GuiChat && showWithChatOpen.get()) || mc.currentScreen is GuiHudDesigner) mc.thePlayer
        else null

        val preBarColor = when (colorModeValue.get()) {
            "Rainbow" -> Color(
                RenderUtils.getRainbowOpaque(
                    waveSecondValue.get(),
                    saturationValue.get(),
                    brightnessValue.get(),
                    0
                )
            )

            "Custom" -> Color(redValue.get(), greenValue.get(), blueValue.get())
            "Sky" -> RenderUtils.skyRainbow(0, saturationValue.get(), brightnessValue.get())
            "Fade" -> ColorUtils.fade(Color(redValue.get(), greenValue.get(), blueValue.get()), 0, 100)
            "Health" -> if (actualTarget != null) BlendUtils.getHealthColor(
                actualTarget.health,
                actualTarget.maxHealth
            ) else Color.green

            "Mixer" -> ColorMixer.getMixedColor(0, waveSecondValue.get())
            else -> ColorUtils.LiquidSlowly(System.nanoTime(), 0, saturationValue.get(), brightnessValue.get())
        }

        val preBgColor = Color(bgRedValue.get(), bgGreenValue.get(), bgBlueValue.get(), bgAlphaValue.get())

        if (fadeValue.get())
            animProgress += (0.0075F * fadeSpeed.get() * RenderUtils.deltaTime * if (actualTarget != null) -1F else 1F)
        else animProgress = 0F

        animProgress = animProgress.coerceIn(0F, 1F)

        barColor = ColorUtils.reAlpha(preBarColor, preBarColor.alpha / 255F * (1F - animProgress))
        bgColor = ColorUtils.reAlpha(preBgColor, preBgColor.alpha / 255F * (1F - animProgress))

        if (actualTarget != null || !fadeValue.get())
            mainTarget = actualTarget
        else if (animProgress >= 1F)
            mainTarget = null

        val returnBorder = mainStyle.getBorder(mainTarget) ?: return null
        val borderWidth = returnBorder.x2 - returnBorder.x
        val borderHeight = returnBorder.y2 - returnBorder.y

        if (mainTarget == null) {
            if (resetBar.get())
                mainStyle.easingHealth = 0F
            if (mainStyle is Rice)
                mainStyle.particleList.clear()
            return returnBorder
        }
        val convertTarget = mainTarget!!

        val calcScaleX = animProgress * (4F / (borderWidth / 2F))
        val calcScaleY = animProgress * (4F / (borderHeight / 2F))
        val calcTranslateX = borderWidth / 2F * calcScaleX
        val calcTranslateY = borderHeight / 2F * calcScaleY

        if (shadowValue.get() && mainStyle.shaderSupport) {
            val floatX = renderX.toFloat()
            val floatY = renderY.toFloat()

            GL11.glTranslated(-renderX, -renderY, 0.0)
            GL11.glPushMatrix()

            ShadowUtils.shadow(shadowStrength.get(), {
                GL11.glPushMatrix()
                GL11.glTranslated(renderX, renderY, 0.0)
                if (fadeValue.get()) {
                    GL11.glTranslatef(calcTranslateX, calcTranslateY, 0F)
                    GL11.glScalef(1F - calcScaleX, 1F - calcScaleY, 1F - calcScaleX)
                }
                mainStyle.handleShadow(convertTarget)
                GL11.glPopMatrix()
            }, {
                GL11.glPushMatrix()
                GL11.glTranslated(renderX, renderY, 0.0)
                if (fadeValue.get()) {
                    GL11.glTranslatef(calcTranslateX, calcTranslateY, 0F)
                    GL11.glScalef(1F - calcScaleX, 1F - calcScaleY, 1F - calcScaleX)
                }
                mainStyle.handleShadowCut(convertTarget)
                GL11.glPopMatrix()
            })

            GL11.glPopMatrix()
            GL11.glTranslated(renderX, renderY, 0.0)
        }

        if (blurValue.get() && mainStyle.shaderSupport) {
            val floatX = renderX.toFloat()
            val floatY = renderY.toFloat()

            GL11.glTranslated(-renderX, -renderY, 0.0)
            GL11.glPushMatrix()
            BlurUtils.blur(
                floatX + returnBorder.x,
                floatY + returnBorder.y,
                floatX + returnBorder.x2,
                floatY + returnBorder.y2,
                blurStrength.get() * (1F - animProgress),
                false
            ) {
                GL11.glPushMatrix()
                GL11.glTranslated(renderX, renderY, 0.0)
                if (fadeValue.get()) {
                    GL11.glTranslatef(calcTranslateX, calcTranslateY, 0F)
                    GL11.glScalef(1F - calcScaleX, 1F - calcScaleY, 1F - calcScaleX)
                }
                mainStyle.handleBlur(convertTarget)
                GL11.glPopMatrix()
            }
            GL11.glPopMatrix()
            GL11.glTranslated(renderX, renderY, 0.0)
        }

        if (fadeValue.get()) {
            GL11.glPushMatrix()
            GL11.glTranslatef(calcTranslateX, calcTranslateY, 0F)
            GL11.glScalef(1F - calcScaleX, 1F - calcScaleY, 1F - calcScaleX)
        }

        if (mainStyle is Chill)
            mainStyle.updateData(
                renderX.toFloat() + calcTranslateX,
                renderY.toFloat() + calcTranslateY,
                calcScaleX,
                calcScaleY
            )
        mainStyle.drawTarget(convertTarget)

        if (fadeValue.get())
            GL11.glPopMatrix()

        GlStateManager.resetColor()
        return returnBorder
    }

    override fun handleDamage(ent: EntityPlayer) {
        if (mainTarget != null && ent == mainTarget)
            getCurrentStyle(styleValue.get())?.handleDamage(ent)
    }

    fun getFadeProgress() = animProgress

    @SafeVarargs
    fun addStyles(vararg styles: TargetStyle): List<String> {
        val nameList = mutableListOf<String>()
        styles.forEach {
            styleList.add(it)
            nameList.add(it.name)
        }
        return nameList
    }

    fun getCurrentStyle(styleName: String): TargetStyle? = styleList.find { it.name.equals(styleName, true) }

}
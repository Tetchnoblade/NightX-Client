package net.aspw.client.visual.hud.element.elements.targets.impl

import net.aspw.client.features.module.impl.visual.ColorMixer
import net.aspw.client.utils.extensions.getDistanceToEntityBox
import net.aspw.client.utils.misc.RandomUtils
import net.aspw.client.utils.render.BlendUtils
import net.aspw.client.utils.render.ColorUtils
import net.aspw.client.utils.render.RenderUtils
import net.aspw.client.utils.render.Stencil
import net.aspw.client.value.BoolValue
import net.aspw.client.value.FloatValue
import net.aspw.client.value.IntegerValue
import net.aspw.client.value.ListValue
import net.aspw.client.visual.font.Fonts
import net.aspw.client.visual.hud.element.Border
import net.aspw.client.visual.hud.element.elements.Target
import net.aspw.client.visual.hud.element.elements.targets.TargetStyle
import net.aspw.client.visual.hud.element.elements.targets.utils.Particle
import net.aspw.client.visual.hud.element.elements.targets.utils.ShapeType
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.entity.player.EntityPlayer
import org.lwjgl.opengl.GL11
import java.awt.Color
import java.util.*

class Rice(inst: Target) : TargetStyle("Rice", inst, true) {

    // Bar gradient
    val gradientLoopValue =
        IntegerValue("GradientLoop", 4, 1, 40, { targetInstance.styleValue.get().equals("Rice", true) })
    val gradientDistanceValue =
        IntegerValue("GradientDistance", 50, 1, 200, { targetInstance.styleValue.get().equals("Rice", true) })
    val gradientRoundedBarValue =
        BoolValue("GradientRoundedBar", true, { targetInstance.styleValue.get().equals("Rice", true) })

    val riceParticle = BoolValue("Rice-Particle", true, { targetInstance.styleValue.get().equals("Rice", true) })
    val riceParticleSpin = BoolValue(
        "Rice-ParticleSpin",
        true,
        { targetInstance.styleValue.get().equals("Rice", true) && riceParticle.get() })
    val generateAmountValue = IntegerValue(
        "GenerateAmount",
        10,
        1,
        40,
        { targetInstance.styleValue.get().equals("Rice", true) && riceParticle.get() })
    val riceParticleCircle = ListValue(
        "Circle-Particles",
        arrayOf("Outline", "Solid", "None"),
        "Solid",
        { targetInstance.styleValue.get().equals("Rice", true) && riceParticle.get() })
    val riceParticleRect = ListValue(
        "Rect-Particles",
        arrayOf("Outline", "Solid", "None"),
        "Outline",
        { targetInstance.styleValue.get().equals("Rice", true) && riceParticle.get() })
    val riceParticleTriangle = ListValue(
        "Triangle-Particles",
        arrayOf("Outline", "Solid", "None"),
        "Outline",
        { targetInstance.styleValue.get().equals("Rice", true) && riceParticle.get() })

    val riceParticleSpeed = FloatValue(
        "Rice-ParticleSpeed",
        0.05F,
        0.01F,
        0.2F,
        { targetInstance.styleValue.get().equals("Rice", true) && riceParticle.get() })
    val riceParticleFade = BoolValue(
        "Rice-ParticleFade",
        true,
        { targetInstance.styleValue.get().equals("Rice", true) && riceParticle.get() })
    val riceParticleFadingSpeed = FloatValue(
        "ParticleFadingSpeed",
        0.05F,
        0.01F,
        0.2F,
        { targetInstance.styleValue.get().equals("Rice", true) && riceParticle.get() })

    val particleRange = FloatValue(
        "Rice-ParticleRange",
        50f,
        0f,
        50f,
        { targetInstance.styleValue.get().equals("Rice", true) && riceParticle.get() })
    val minParticleSize: FloatValue = object : FloatValue(
        "MinParticleSize",
        0.5f,
        0f,
        5f,
        { targetInstance.styleValue.get().equals("Rice", true) && riceParticle.get() }) {
        override fun onChanged(oldValue: Float, newValue: Float) {
            val v = maxParticleSize.get()
            if (v < newValue) set(v)
        }
    }
    val maxParticleSize: FloatValue = object : FloatValue(
        "MaxParticleSize",
        2.5f,
        0f,
        5f,
        { targetInstance.styleValue.get().equals("Rice", true) && riceParticle.get() }) {
        override fun onChanged(oldValue: Float, newValue: Float) {
            val v = minParticleSize.get()
            if (v > newValue) set(v)
        }
    }

    val particleList = mutableListOf<Particle>()
    private var gotDamaged = false

    override fun drawTarget(entity: EntityPlayer) {
        updateAnim(entity.health)

        val font = Fonts.fontSFUI40
        val name = "Name: ${entity.name}"
        val info = "Distance: ${decimalFormat2.format(mc.thePlayer.getDistanceToEntityBox(entity))}"
        val healthName = decimalFormat2.format(easingHealth)

        val length =
            (font.getStringWidth(name).coerceAtLeast(font.getStringWidth(info)).toFloat() + 40F).coerceAtLeast(125F)
        val maxHealthLength = font.getStringWidth(decimalFormat2.format(entity.maxHealth)).toFloat()

        // background
        RenderUtils.drawRoundedRect(0F, 0F, 10F + length, 55F, 8F, targetInstance.bgColor.rgb)

        // particle engine
        if (riceParticle.get()) {
            // adding system
            if (gotDamaged) {
                for (j in 0..(generateAmountValue.get())) {
                    var parSize = RandomUtils.nextFloat(minParticleSize.get(), maxParticleSize.get())
                    var parDistX = RandomUtils.nextFloat(-particleRange.get(), particleRange.get())
                    var parDistY = RandomUtils.nextFloat(-particleRange.get(), particleRange.get())
                    var firstChar = RandomUtils.random(
                        1,
                        "${if (riceParticleCircle.get().equals("none", true)) "" else "c"}${
                            if (riceParticleRect.get().equals("none", true)) "" else "r"
                        }${if (riceParticleTriangle.get().equals("none", true)) "" else "t"}"
                    )
                    var drawType = ShapeType.getTypeFromName(
                        when (firstChar) {
                            "c" -> "c_${riceParticleCircle.get().lowercase(Locale.getDefault())}"
                            "r" -> "r_${riceParticleRect.get().lowercase(Locale.getDefault())}"
                            else -> "t_${riceParticleTriangle.get().lowercase(Locale.getDefault())}"
                        }
                    ) ?: break

                    particleList.add(
                        Particle(
                            BlendUtils.blendColors(
                                floatArrayOf(0F, 1F),
                                arrayOf<Color>(Color.white, targetInstance.barColor),
                                if (RandomUtils.nextBoolean()) RandomUtils.nextFloat(0.5F, 1.0F) else 0F
                            ),
                            parDistX, parDistY, parSize, drawType
                        )
                    )
                }
                gotDamaged = false
            }

            // render and removing system
            val deleteQueue = mutableListOf<Particle>()

            particleList.forEach { particle ->
                if (particle.alpha > 0F)
                    particle.render(
                        20F,
                        20F,
                        riceParticleFade.get(),
                        riceParticleSpeed.get(),
                        riceParticleFadingSpeed.get(),
                        riceParticleSpin.get()
                    )
                else
                    deleteQueue.add(particle)
            }

            particleList.removeAll(deleteQueue)
        }

        // custom head 
        val scaleHT = (entity.hurtTime.toFloat() / entity.maxHurtTime.coerceAtLeast(1).toFloat()).coerceIn(0F, 1F)
        if (mc.netHandler.getPlayerInfo(entity.uniqueID) != null)
            drawHead(
                mc.netHandler.getPlayerInfo(entity.uniqueID).locationSkin,
                5F + 15F * (scaleHT * 0.2F),
                5F + 15F * (scaleHT * 0.2F),
                1F - scaleHT * 0.2F,
                30, 30,
                1F, 0.4F + (1F - scaleHT) * 0.6F, 0.4F + (1F - scaleHT) * 0.6F,
                1F - targetInstance.getFadeProgress()
            )

        // player's info
        GlStateManager.resetColor()
        font.drawString(name, 39F, 11F, getColor(-1).rgb)
        font.drawString(info, 39F, 23F, getColor(-1).rgb)

        // gradient health bar
        val barWidth = (length - 5F - maxHealthLength) * (easingHealth / entity.maxHealth.toFloat()).coerceIn(0F, 1F)
        Stencil.write(false)
        GL11.glDisable(GL11.GL_TEXTURE_2D)
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)

        if (gradientRoundedBarValue.get()) {
            if (barWidth > 0F)
                RenderUtils.fastRoundedRect(5F, 42F, 5F + barWidth, 48F, 3F)
        } else
            RenderUtils.quickDrawRect(5F, 42F, 5F + barWidth, 48F)

        GL11.glDisable(GL11.GL_BLEND)
        Stencil.erase(true)
        when (targetInstance.colorModeValue.get().lowercase(Locale.getDefault())) {
            "custom", "health" -> RenderUtils.drawRect(
                5F,
                42F,
                length - maxHealthLength,
                48F,
                targetInstance.barColor.rgb
            )

            else -> for (i in 0..(gradientLoopValue.get() - 1)) {
                val barStart =
                    i.toDouble() / gradientLoopValue.get().toDouble() * (length - 5F - maxHealthLength).toDouble()
                val barEnd =
                    (i + 1).toDouble() / gradientLoopValue.get().toDouble() * (length - 5F - maxHealthLength).toDouble()
                RenderUtils.drawGradientSideways(
                    5.0 + barStart,
                    42.0,
                    5.0 + barEnd,
                    48.0,
                    getColorAtIndex(i),
                    getColorAtIndex(i + 1)
                )
            }
        }
        Stencil.dispose()

        GlStateManager.resetColor()
        font.drawString(healthName, 10F + barWidth, 41F, getColor(-1).rgb)
    }

    private fun getColorAtIndex(i: Int): Int {
        return getColor(
            when (targetInstance.colorModeValue.get()) {
                "Rainbow" -> RenderUtils.getRainbowOpaque(
                    targetInstance.waveSecondValue.get(),
                    targetInstance.saturationValue.get(),
                    targetInstance.brightnessValue.get(),
                    i * gradientDistanceValue.get()
                )

                "Sky" -> RenderUtils.SkyRainbow(
                    i * gradientDistanceValue.get(),
                    targetInstance.saturationValue.get(),
                    targetInstance.brightnessValue.get()
                )

                "Slowly" -> ColorUtils.LiquidSlowly(
                    System.nanoTime(),
                    i * gradientDistanceValue.get(),
                    targetInstance.saturationValue.get(),
                    targetInstance.brightnessValue.get()
                ).rgb

                "Mixer" -> ColorMixer.getMixedColor(
                    i * gradientDistanceValue.get(),
                    targetInstance.waveSecondValue.get()
                ).rgb

                "Fade" -> ColorUtils.fade(
                    Color(
                        targetInstance.redValue.get(),
                        targetInstance.greenValue.get(),
                        targetInstance.blueValue.get()
                    ), i * gradientDistanceValue.get(), 100
                ).rgb

                else -> -1
            }
        ).rgb
    }

    override fun handleDamage(entity: EntityPlayer) {
        gotDamaged = true
    }

    override fun handleBlur(entity: EntityPlayer) {
        val font = Fonts.fontSFUI40
        val name = "Name: ${entity.name}"
        val info = "Distance: ${decimalFormat2.format(mc.thePlayer.getDistanceToEntityBox(entity))}"
        val length =
            (font.getStringWidth(name).coerceAtLeast(font.getStringWidth(info)).toFloat() + 40F).coerceAtLeast(125F)

        GlStateManager.enableBlend()
        GlStateManager.disableTexture2D()
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)
        RenderUtils.fastRoundedRect(0F, 0F, 10F + length, 55F, 8F)
        GlStateManager.enableTexture2D()
        GlStateManager.disableBlend()
    }

    override fun handleShadowCut(entity: EntityPlayer) = handleBlur(entity)

    override fun handleShadow(entity: EntityPlayer) {
        val font = Fonts.fontSFUI40
        val name = "Name: ${entity.name}"
        val info = "Distance: ${decimalFormat2.format(mc.thePlayer.getDistanceToEntityBox(entity))}"
        val length =
            (font.getStringWidth(name).coerceAtLeast(font.getStringWidth(info)).toFloat() + 40F).coerceAtLeast(125F)

        RenderUtils.originalRoundedRect(0F, 0F, 10F + length, 55F, 8F, shadowOpaque.rgb)
    }

    override fun getBorder(entity: EntityPlayer?): Border {
        entity ?: return Border(0F, 0F, 135F, 55F)

        val font = Fonts.fontSFUI40
        val name = "Name: ${entity.name}"
        val info = "Distance: ${decimalFormat2.format(mc.thePlayer.getDistanceToEntityBox(entity))}"
        val length =
            (font.getStringWidth(name).coerceAtLeast(font.getStringWidth(info)).toFloat() + 40F).coerceAtLeast(125F)

        return Border(0F, 0F, 10F + length, 55F)
    }

}
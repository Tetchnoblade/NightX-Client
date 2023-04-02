package net.aspw.client.features.module.impl.visual

import net.aspw.client.Client
import net.aspw.client.event.EventTarget
import net.aspw.client.event.Render3DEvent
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.features.module.impl.combat.KillAura
import net.aspw.client.utils.render.ColorUtils
import net.aspw.client.utils.render.RenderUtils
import net.aspw.client.value.BoolValue
import net.aspw.client.value.FloatValue
import net.aspw.client.value.IntegerValue
import net.aspw.client.value.ListValue
import net.minecraft.client.renderer.GlStateManager
import org.lwjgl.opengl.GL11
import java.awt.Color

@ModuleInfo(name = "TargetESP", spacedName = "Target ESP", category = ModuleCategory.VISUAL, array = false)
class TargetESP : Module() {
    val radius = FloatValue("Radius", 0.9f, 0.1f, 4.0f, "m")
    private val colorType =
        ListValue("Color", arrayOf("Custom", "Rainbow", "Rainbow2", "Sky", "Fade", "Mixer"), "Custom")
    private val redValue = IntegerValue("Red", 0, 0, 255)
    private val greenValue = IntegerValue("Green", 255, 0, 255)
    private val blueValue = IntegerValue("Blue", 255, 0, 255)
    private val saturationValue = FloatValue("Saturation", 0.5F, 0F, 1F)
    private val brightnessValue = FloatValue("Brightness", 1F, 0F, 1F)
    private val mixerSecondsValue = IntegerValue("Mixer-Seconds", 2, 1, 10)
    private val accuracyValue = IntegerValue("Accuracy", 15, 0, 59)
    private val thicknessValue = FloatValue("Thickness", 3F, 0.1F, 5F)
    private val outLine = BoolValue("Outline", true)
    val killAura = Client.moduleManager.getModule(KillAura::class.java)

    @EventTarget
    fun onRender3D(event: Render3DEvent) {
        val target = killAura?.target
        target ?: return
        GL11.glPushMatrix()
        GL11.glTranslated(
            target.lastTickPosX + (target.posX - target.lastTickPosX) * mc.timer.renderPartialTicks - mc.renderManager.renderPosX,
            target.lastTickPosY + (target.posY - target.lastTickPosY) * mc.timer.renderPartialTicks - mc.renderManager.renderPosY,
            target.lastTickPosZ + (target.posZ - target.lastTickPosZ) * mc.timer.renderPartialTicks - mc.renderManager.renderPosZ
        )
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glEnable(GL11.GL_LINE_SMOOTH)
        GL11.glDisable(GL11.GL_TEXTURE_2D)
        GL11.glDisable(GL11.GL_DEPTH_TEST)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GL11.glRotatef(90F, 1F, 0F, 0F)

        if (outLine.get()) {
            GL11.glLineWidth(thicknessValue.get() + 1.25F)
            GL11.glColor3f(0F, 0F, 0F)
            GL11.glBegin(GL11.GL_LINE_LOOP)

            for (i in 0..360 step 60 - accuracyValue.get()) { // You can change circle accuracy  (60 - accuracy)
                GL11.glVertex2f(
                    Math.cos(i * Math.PI / 180.0).toFloat() * radius.get(),
                    (Math.sin(i * Math.PI / 180.0).toFloat() * radius.get())
                )
            }

            GL11.glEnd()
        }

        val rainbow2 = ColorUtils.LiquidSlowly(System.nanoTime(), 0, saturationValue.get(), brightnessValue.get())
        val sky = RenderUtils.skyRainbow(0, saturationValue.get(), brightnessValue.get())
        val mixer = ColorMixer.getMixedColor(0, mixerSecondsValue.get())
        val fade = ColorUtils.fade(Color(redValue.get(), greenValue.get(), blueValue.get()), 0, 100)

        GL11.glLineWidth(thicknessValue.get())
        GL11.glBegin(GL11.GL_LINE_LOOP)

        for (i in 0..360 step 60 - accuracyValue.get()) { // You can change circle accuracy  (60 - accuracy)
            when (colorType.get()) {
                "Custom" -> GL11.glColor3f(
                    redValue.get() / 255.0f,
                    greenValue.get() / 255.0f,
                    blueValue.get() / 255.0f
                )

                "Rainbow" -> {
                    val rainbow =
                        Color(RenderUtils.getNormalRainbow(i, saturationValue.get(), brightnessValue.get()))
                    GL11.glColor3f(rainbow.red / 255.0f, rainbow.green / 255.0f, rainbow.blue / 255.0f)
                }

                "Rainbow2" -> GL11.glColor3f(
                    rainbow2.red / 255.0f,
                    rainbow2.green / 255.0f,
                    rainbow2.blue / 255.0f
                )

                "Sky" -> GL11.glColor3f(sky.red / 255.0f, sky.green / 255.0f, sky.blue / 255.0f)
                "Mixer" -> GL11.glColor3f(mixer.red / 255.0f, mixer.green / 255.0f, mixer.blue / 255.0f)
                else -> GL11.glColor3f(fade.red / 255.0f, fade.green / 255.0f, fade.blue / 255.0f)
            }
            GL11.glVertex2f(
                Math.cos(i * Math.PI / 180.0).toFloat() * radius.get(),
                (Math.sin(i * Math.PI / 180.0).toFloat() * radius.get())
            )
        }

        GL11.glEnd()

        GL11.glDisable(GL11.GL_BLEND)
        GL11.glEnable(GL11.GL_TEXTURE_2D)
        GL11.glEnable(GL11.GL_DEPTH_TEST)
        GL11.glDisable(GL11.GL_LINE_SMOOTH)

        GL11.glPopMatrix()

        GlStateManager.resetColor()
        GL11.glColor4f(1F, 1F, 1F, 1F)
    }

    init {
        state = true
    }
}
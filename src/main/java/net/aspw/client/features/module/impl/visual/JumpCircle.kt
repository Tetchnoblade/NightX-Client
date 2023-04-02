package net.aspw.client.features.module.impl.visual

import net.aspw.client.Client
import net.aspw.client.event.EventTarget
import net.aspw.client.event.Render3DEvent
import net.aspw.client.event.UpdateEvent
import net.aspw.client.event.WorldEvent
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.utils.render.ColorUtils.LiquidSlowly
import net.aspw.client.utils.render.ColorUtils.fade
import net.aspw.client.utils.render.Render
import net.aspw.client.utils.render.RenderUtils
import net.aspw.client.value.FloatValue
import net.aspw.client.value.IntegerValue
import net.aspw.client.value.ListValue
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import org.lwjgl.opengl.GL11
import java.awt.Color
import kotlin.math.cos
import kotlin.math.sin

@ModuleInfo(name = "JumpCircle", spacedName = "Jump Circle", category = ModuleCategory.VISUAL)
class JumpCircle : Module() {
    val radius = FloatValue("Radius", 1f, 1f, 5f, "m")
    private val colorModeValue =
        ListValue("Color", arrayOf("Custom", "Rainbow", "Sky", "LiquidSlowly", "Fade", "Mixer"), "Fade")
    private val colorRedValue = IntegerValue("Red", 200, 0, 255)
    private val colorGreenValue = IntegerValue("Green", 150, 0, 255)
    private val colorBlueValue = IntegerValue("Blue", 200, 0, 255)
    private val saturationValue = FloatValue("Saturation", 1f, 0f, 1f)
    private val brightnessValue = FloatValue("Brightness", 1f, 0f, 1f)
    private val mixerSecondsValue = IntegerValue("Seconds", 2, 1, 10)

    private val points = mutableMapOf<Int, MutableList<Render>>()
    var jump = false
    val circles = mutableListOf<Circle>()
    var red = colorRedValue.get()
    var green = colorGreenValue.get()
    var blue = colorBlueValue.get()

    @EventTarget
    fun onRender3D(event: Render3DEvent?) {
        circles.removeIf { System.currentTimeMillis() > it.time + 1000 }

        GL11.glPushMatrix()

        GL11.glEnable(GL11.GL_BLEND)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GL11.glDisable(GL11.GL_CULL_FACE)
        GL11.glDisable(GL11.GL_TEXTURE_2D)
        GL11.glDisable(GL11.GL_DEPTH_TEST)
        GL11.glDepthMask(false)
        GL11.glDisable(GL11.GL_ALPHA_TEST)
        GL11.glShadeModel(GL11.GL_SMOOTH)

        circles.forEach { it.draw() }

        GL11.glDisable(GL11.GL_BLEND)
        GL11.glEnable(GL11.GL_CULL_FACE)
        GL11.glEnable(GL11.GL_TEXTURE_2D)
        GL11.glEnable(GL11.GL_DEPTH_TEST)
        GL11.glDepthMask(true)
        GL11.glEnable(GL11.GL_ALPHA_TEST)
        GL11.glShadeModel(GL11.GL_FLAT)

        GL11.glPopMatrix()
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (!mc.thePlayer.onGround && !jump) {
            jump = true
        }
        if (mc.thePlayer.onGround && jump) {
            updatePoints(mc.thePlayer)
            jump = false
        }
    }

    fun updatePoints(entity: EntityLivingBase) {
        circles.add(Circle(System.currentTimeMillis(), mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ))
    }

    @EventTarget
    fun onWorld(event: WorldEvent) {
        points.clear()
    }

    override fun onDisable() {
        points.clear()
    }

    class Circle(val time: Long, val x: Double, val y: Double, val z: Double) {
        var entity: EntityLivingBase = mc.thePlayer
        val jumpModule = Client.moduleManager.getModule(JumpCircle::class.java) as JumpCircle
        var colorModeValue = jumpModule.colorModeValue.get()
        var colorRedValue = jumpModule.colorRedValue.get()
        var colorGreenValue = jumpModule.colorGreenValue.get()
        var colorBlueValue = jumpModule.colorBlueValue.get()
        var mixerSecondsValue = jumpModule.mixerSecondsValue.get()
        var saturationValue = jumpModule.saturationValue.get()
        var brightnessValue = jumpModule.brightnessValue.get()

        fun draw() {
            if (jumpModule == null) {
                return
            }

            val dif = (System.currentTimeMillis() - time)
            val c = 125 - (dif / 1000.toFloat()) * 125

            GL11.glPushMatrix()

            GL11.glTranslated(
                x - mc.renderManager.viewerPosX,
                y - mc.renderManager.viewerPosY,
                z - mc.renderManager.viewerPosZ
            )

            GL11.glBegin(GL11.GL_TRIANGLE_STRIP)
            for (i in 0..360) {
                val color = getColor(entity, 0)

                val x = (dif * jumpModule.radius.get() * 0.001 * sin(i.toDouble()))
                val z = (dif * jumpModule.radius.get() * 0.001 * cos(i.toDouble()))

                RenderUtils.glColor(color.red, color.green, color.blue, 0)
                GL11.glVertex3d(x / 2, 0.0, z / 2)

                RenderUtils.glColor(color.red, color.green, color.blue, c.toInt())
                GL11.glVertex3d(x, 0.0, z)
            }
            GL11.glEnd()

            GL11.glPopMatrix()
        }

        fun getColor(ent: Entity?, index: Int): Color {
            return when (colorModeValue) {
                "Custom" -> Color(colorRedValue, colorGreenValue, colorBlueValue)
                "Rainbow" -> Color(
                    RenderUtils.getRainbowOpaque(
                        mixerSecondsValue,
                        saturationValue,
                        brightnessValue,
                        index
                    )
                )

                "Sky" -> RenderUtils.skyRainbow(index, saturationValue, brightnessValue)
                "LiquidSlowly" -> LiquidSlowly(System.nanoTime(), index, saturationValue, brightnessValue)
                "Mixer" -> ColorMixer.getMixedColor(index, mixerSecondsValue)
                else -> fade(Color(colorRedValue, colorGreenValue, colorBlueValue), index, 100)
            }
        }
    }
}
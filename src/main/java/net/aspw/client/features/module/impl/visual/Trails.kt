package net.aspw.client.features.module.impl.visual

import net.aspw.client.event.EventTarget
import net.aspw.client.event.Render3DEvent
import net.aspw.client.event.UpdateEvent
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.utils.render.ColorUtils.rainbow
import net.aspw.client.utils.render.ColorUtils.reAlpha
import net.aspw.client.utils.render.RenderUtils
import net.aspw.client.value.BoolValue
import net.aspw.client.value.FloatValue
import net.aspw.client.value.IntegerValue
import org.lwjgl.opengl.GL11
import java.awt.Color
import java.util.*

@ModuleInfo(name = "Trails", category = ModuleCategory.VISUAL)
class Trails : Module() {
    val unlimitedValue = BoolValue("Unlimited", false)
    val lineWidth = FloatValue("LineWidth", 4f, 1f, 10f)
    val colorRedValue = IntegerValue("R", 255, 0, 255)
    val colorGreenValue = IntegerValue("G", 255, 0, 255)
    val colorBlueValue = IntegerValue("B", 255, 0, 255)
    val alphaValue = IntegerValue("Alpha", 150, 0, 255)
    val fadeSpeedValue = IntegerValue("Fade-Speed", 1, 0, 255)
    val colorRainbow = BoolValue("Rainbow", true)
    private val positions = LinkedList<Dot>()
    private var lastX = 0.0
    private var lastY = 0.0
    private var lastZ = 0.0

    @EventTarget
    fun onRender3D(event: Render3DEvent?) {
        val color = if (colorRainbow.get()) rainbow() else Color(
            colorRedValue.get(),
            colorGreenValue.get(),
            colorBlueValue.get()
        )
        synchronized(positions) {
            GL11.glPushMatrix()
            GL11.glDisable(GL11.GL_TEXTURE_2D)
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
            GL11.glEnable(GL11.GL_LINE_SMOOTH)
            GL11.glEnable(GL11.GL_BLEND)
            GL11.glDisable(GL11.GL_DEPTH_TEST)
            mc.entityRenderer.disableLightmap()
            GL11.glLineWidth(lineWidth.get())
            GL11.glBegin(GL11.GL_LINE_STRIP)
            val renderPosX = mc.renderManager.viewerPosX
            val renderPosY = mc.renderManager.viewerPosY - 0.5
            val renderPosZ = mc.renderManager.viewerPosZ
            val removeQueue: MutableList<Dot> = ArrayList()
            for (dot in positions) {
                if (dot.alpha > 0) dot.render(
                    color,
                    renderPosX,
                    renderPosY,
                    renderPosZ,
                    if (unlimitedValue.get()) 0 else fadeSpeedValue.get()
                ) else removeQueue.add(dot)
            }
            for (removeDot in removeQueue) positions.remove(removeDot)
            GL11.glColor4d(1.0, 1.0, 1.0, 1.0)
            GL11.glEnd()
            GL11.glEnable(GL11.GL_DEPTH_TEST)
            GL11.glDisable(GL11.GL_LINE_SMOOTH)
            GL11.glDisable(GL11.GL_BLEND)
            GL11.glEnable(GL11.GL_TEXTURE_2D)
            GL11.glPopMatrix()
        }
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent?) {
        synchronized(positions) {
            if (mc.thePlayer.posX != lastX || mc.thePlayer.entityBoundingBox.minY != lastY || mc.thePlayer.posZ != lastZ) {
                positions.add(
                    Dot(
                        doubleArrayOf(
                            mc.thePlayer.posX,
                            mc.thePlayer.entityBoundingBox.minY,
                            mc.thePlayer.posZ
                        )
                    )
                )
                lastX = mc.thePlayer.posX
                lastY = mc.thePlayer.entityBoundingBox.minY
                lastZ = mc.thePlayer.posZ
            }
        }
    }

    override fun onEnable() {
        if (mc.thePlayer == null) return
        synchronized(positions) {
            positions.add(
                Dot(
                    doubleArrayOf(
                        mc.thePlayer.posX,
                        mc.thePlayer.entityBoundingBox.minY + mc.thePlayer.getEyeHeight() * 0.5f,
                        mc.thePlayer.posZ
                    )
                )
            )
            positions.add(Dot(doubleArrayOf(mc.thePlayer.posX, mc.thePlayer.entityBoundingBox.minY, mc.thePlayer.posZ)))
        }
        super.onEnable()
    }

    override fun onDisable() {
        synchronized(positions) { positions.clear() }
        super.onDisable()
    }

    internal inner class Dot(private val pos: DoubleArray) {
        var alpha = alphaValue.get()
        fun render(color: Color?, renderPosX: Double, renderPosY: Double, renderPosZ: Double, decreaseBy: Int) {
            val reColor = reAlpha(color!!, alpha)
            RenderUtils.glColor(reColor)
            GL11.glVertex3d(pos[0] - renderPosX, pos[1] - renderPosY, pos[2] - renderPosZ)
            alpha -= decreaseBy
            if (alpha < 0) alpha = 0
        }
    }
}
package net.aspw.client.features.module.impl.visual

import net.aspw.client.event.EventTarget
import net.aspw.client.event.Render3DEvent
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.utils.EntityUtils
import net.aspw.client.utils.RotationUtils
import net.aspw.client.utils.render.ColorUtils
import net.aspw.client.utils.render.RenderUtils
import net.aspw.client.value.BoolValue
import net.aspw.client.value.FloatValue
import net.aspw.client.value.IntegerValue
import net.aspw.client.value.ListValue
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.entity.Entity
import net.minecraft.util.Vec3
import org.lwjgl.opengl.GL11
import java.awt.Color
import java.util.*

@ModuleInfo(name = "Tracers", category = ModuleCategory.VISUAL, array = false)
class Tracers : Module() {

    private val colorMode = ListValue("Color", arrayOf("Custom", "DistanceColor", "Rainbow"), "Custom")

    private val thicknessValue = FloatValue("Thickness", 1F, 1F, 5F)

    private val colorRedValue = IntegerValue("R", 200, 0, 255)
    private val colorGreenValue = IntegerValue("G", 0, 0, 255)
    private val colorBlueValue = IntegerValue("B", 255, 0, 255)

    private val directLineValue = BoolValue("Directline", false)
    private val fovModeValue = ListValue("FOV-Mode", arrayOf("All", "Back", "Front"), "All")
    private val fovValue = FloatValue("FOV", 180F, 0F, 180F, { !fovModeValue.get().equals("all", true) })

    @EventTarget
    fun onRender3D(event: Render3DEvent) {
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glEnable(GL11.GL_LINE_SMOOTH)
        GL11.glLineWidth(thicknessValue.get())
        GL11.glDisable(GL11.GL_TEXTURE_2D)
        GL11.glDisable(GL11.GL_DEPTH_TEST)
        GL11.glDepthMask(false)

        GL11.glBegin(GL11.GL_LINES)

        for (entity in if (fovModeValue.get()
                .equals("all", true)
        ) mc.theWorld.loadedEntityList else mc.theWorld.loadedEntityList.filter {
            if (fovModeValue.get()
                    .equals("back", true)
            ) RotationUtils.getRotationBackDifference(it) <= fovValue.get() else RotationUtils.getRotationDifference(it) <= fovValue.get()
        }) {
            if (entity != null && entity != mc.thePlayer && EntityUtils.isSelected(entity, false)) {
                var dist = (mc.thePlayer.getDistanceToEntity(entity) * 2).toInt()

                if (dist > 255) dist = 255

                val colorMode = colorMode.get().lowercase(Locale.getDefault())
                val color = when {
                    EntityUtils.isFriend(entity) -> Color(0, 0, 255, 150)
                    colorMode.equals("custom") -> Color(
                        colorRedValue.get(),
                        colorGreenValue.get(),
                        colorBlueValue.get(),
                        150
                    )

                    colorMode.equals("distancecolor") -> Color(255 - dist, dist, 0, 150)
                    colorMode.equals("rainbow") -> ColorUtils.rainbow()
                    else -> Color(255, 255, 255, 150)
                }

                drawTraces(entity, color, !directLineValue.get())
            }
        }

        GL11.glEnd()

        GL11.glEnable(GL11.GL_TEXTURE_2D)
        GL11.glDisable(GL11.GL_LINE_SMOOTH)
        GL11.glEnable(GL11.GL_DEPTH_TEST)
        GL11.glDepthMask(true)
        GL11.glDisable(GL11.GL_BLEND)
        GlStateManager.resetColor()
    }

    fun drawTraces(entity: Entity, color: Color, drawHeight: Boolean) {
        val x = (entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * mc.timer.renderPartialTicks
                - mc.renderManager.renderPosX)
        val y = (entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * mc.timer.renderPartialTicks
                - mc.renderManager.renderPosY)
        val z = (entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * mc.timer.renderPartialTicks
                - mc.renderManager.renderPosZ)

        val eyeVector = Vec3(0.0, 0.0, 1.0)
            .rotatePitch((-Math.toRadians(mc.thePlayer.rotationPitch.toDouble())).toFloat())
            .rotateYaw((-Math.toRadians(mc.thePlayer.rotationYaw.toDouble())).toFloat())

        RenderUtils.glColor(color)

        GL11.glVertex3d(eyeVector.xCoord, mc.thePlayer.getEyeHeight().toDouble() + eyeVector.yCoord, eyeVector.zCoord)
        if (drawHeight) {
            GL11.glVertex3d(x, y, z)
            GL11.glVertex3d(x, y, z)
            GL11.glVertex3d(x, y + entity.height, z)
        } else {
            GL11.glVertex3d(x, y + entity.height / 2.0, z)
        }

    }
}

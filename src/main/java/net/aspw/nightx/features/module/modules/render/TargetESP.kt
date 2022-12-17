package net.aspw.nightx.features.module.modules.render

import net.aspw.nightx.NightX
import net.aspw.nightx.event.*
import net.aspw.nightx.features.module.Module
import net.aspw.nightx.features.module.ModuleCategory
import net.aspw.nightx.features.module.ModuleInfo
import net.aspw.nightx.features.module.modules.client.ColorMixer
import net.aspw.nightx.features.module.modules.combat.KillAura
import net.aspw.nightx.utils.MovementUtils
import net.aspw.nightx.utils.RotationUtils
import net.aspw.nightx.utils.render.ColorUtils
import net.aspw.nightx.utils.render.RenderUtils
import net.aspw.nightx.value.BoolValue
import net.aspw.nightx.value.FloatValue
import net.aspw.nightx.value.IntegerValue
import net.aspw.nightx.value.ListValue
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.util.AxisAlignedBB
import org.lwjgl.opengl.GL11
import java.awt.Color

@ModuleInfo(name = "TargetESP", spacedName = "Target ESP", category = ModuleCategory.RENDER, array = false)
class TargetESP : Module() {
    val radius = FloatValue("Radius", 1.2f, 0.1f, 4.0f, "m")
    private val render = BoolValue("Render", true)
    private val colorType =
        ListValue("Color", arrayOf("Custom", "Dynamic", "Rainbow", "Rainbow2", "Sky", "Fade", "Mixer"), "Sky")
    private val redValue = IntegerValue("Red", 255, 0, 255)
    private val greenValue = IntegerValue("Green", 255, 0, 255)
    private val blueValue = IntegerValue("Blue", 255, 0, 255)
    private val saturationValue = FloatValue("Saturation", 0.45F, 0F, 1F)
    private val brightnessValue = FloatValue("Brightness", 1F, 0F, 1F)
    private val mixerSecondsValue = IntegerValue("Mixer-Seconds", 2, 1, 10)
    private val accuracyValue = IntegerValue("Accuracy", 10, 0, 59)
    private val thicknessValue = FloatValue("Thickness", 3F, 0.1F, 5F)
    private val outLine = BoolValue("Outline", true)
    private lateinit var killAura: KillAura

    var direction = 1

    var lastView = 0
    var hasChangedThirdPerson = true

    var hasModifiedMovement = false

    override fun onInitialize() {
        killAura = NightX.moduleManager.getModule(KillAura::class.java) as KillAura
    }

    override fun onEnable() {
        hasChangedThirdPerson = true
        lastView = mc.gameSettings.thirdPersonView
    }

    @EventTarget
    fun onMotion(event: MotionEvent) {
        if (event.eventState == EventState.PRE) {
            if (mc.thePlayer.isCollidedHorizontally)
                this.direction = -this.direction

            if (mc.gameSettings.keyBindLeft.pressed)
                this.direction = 1

            if (mc.gameSettings.keyBindRight.pressed)
                this.direction = -1
        }
    }

    @EventTarget(priority = 2)
    fun onMove(event: MoveEvent) {
        if (canStrafe) {
            if (!hasModifiedMovement) strafe(event, MovementUtils.getSpeed(event.x, event.z))
        }
        hasModifiedMovement = false
    }

    fun strafe(event: MoveEvent, moveSpeed: Double) {
        if (killAura.target == null) return

        val target = killAura.target!!
        val rotYaw = RotationUtils.getRotationsEntity(target).yaw

        val forward = if (mc.thePlayer.getDistanceToEntity(target) <= radius.get()) 0.0 else 1.0
        val strafe = direction.toDouble()
        val modifySpeed = maximizeSpeed(target, moveSpeed, killAura.rangeValue.get())

        MovementUtils.setSpeed(event, modifySpeed, rotYaw, strafe, forward)
        hasModifiedMovement = true
    }

    fun getData(): Array<Float> {
        if (killAura.target == null) return arrayOf(0F, 0F, 0F)

        val target = killAura.target!!
        val rotYaw = RotationUtils.getRotationsEntity(target).yaw

        val forward = if (mc.thePlayer.getDistanceToEntity(target) <= radius.get()) 0F else 1F
        val strafe = direction.toFloat()

        return arrayOf(rotYaw, strafe, forward)
    }

    fun getMovingYaw(): Float {
        val dt = getData()
        return MovementUtils.getRawDirectionRotation(dt[0], dt[1], dt[2])
    }

    fun getMovingDir(): Double {
        val dt = getData()
        return MovementUtils.getDirectionRotation(dt[0], dt[1], dt[2])
    }

    private fun maximizeSpeed(ent: EntityLivingBase, speed: Double, range: Float): Double {
        mc.thePlayer ?: return 0.0
        return if (mc.thePlayer.getDistanceToEntity(ent) <= radius.get()) speed.coerceIn(
            0.0,
            range.toDouble() / 20.0
        ) else speed
    }

    val canStrafe: Boolean
        get() = (mc.thePlayer.isRidingHorse)

    private fun checkVoid(): Boolean {
        for (x in -1..0) {
            for (z in -1..0) {
                if (isVoid(x, z)) {
                    return true
                }
            }
        }
        return false
    }

    private fun isVoid(X: Int, Z: Int): Boolean {
        if (mc.thePlayer.posY < 0.0) {
            return true
        }
        var off = 0
        while (off < mc.thePlayer.posY.toInt() + 2) {
            val bb: AxisAlignedBB = mc.thePlayer.entityBoundingBox.offset(X.toDouble(), (-off).toDouble(), Z.toDouble())
            if (mc.theWorld!!.getCollidingBoundingBoxes(mc.thePlayer as Entity, bb).isEmpty()) {
                off += 2
                continue
            }
            return false
            off += 2
        }
        return true
    }

    @EventTarget
    fun onRender3D(event: Render3DEvent) {
        val target = killAura.target
        if (render.get()) {
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

                    "Dynamic" -> if (canStrafe) GL11.glColor4f(0.25f, 1f, 0.25f, 1f) else GL11.glColor4f(1f, 1f, 1f, 1f)
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
    }

    init {
        state = true
    }
}
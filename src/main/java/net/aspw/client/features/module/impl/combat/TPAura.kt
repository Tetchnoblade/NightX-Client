package net.aspw.client.features.module.impl.combat

import net.aspw.client.event.EventTarget
import net.aspw.client.event.PacketEvent
import net.aspw.client.event.Render3DEvent
import net.aspw.client.event.UpdateEvent
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.utils.EntityUtils
import net.aspw.client.utils.PathUtils
import net.aspw.client.utils.RotationUtils
import net.aspw.client.utils.render.RenderUtils
import net.aspw.client.utils.timer.MSTimer
import net.aspw.client.value.BoolValue
import net.aspw.client.value.FloatValue
import net.aspw.client.value.IntegerValue
import net.aspw.client.value.ListValue
import net.minecraft.entity.EntityLivingBase
import net.minecraft.network.play.client.C02PacketUseEntity
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition
import net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook
import net.minecraft.network.play.client.C0APacketAnimation
import net.minecraft.network.play.server.S08PacketPlayerPosLook
import net.minecraft.util.Vec3
import org.lwjgl.opengl.GL11
import java.awt.Color
import java.util.*

@ModuleInfo(
    name = "TPAura", spacedName = "TP Aura",
    category = ModuleCategory.COMBAT
)
class TPAura : Module() {

    /*
     * Values
     */
    private val apsValue = IntegerValue("APS", 2, 1, 10)
    private val maxTargetsValue = IntegerValue("MaxTargets", 3, 1, 8)
    private val rangeValue = IntegerValue("Range", 50, 10, 200, "m")
    private val fovValue = FloatValue("FOV", 180F, 0F, 180F, "°")
    private val swingValue = ListValue("Swing", arrayOf("Normal", "Packet", "None"), "Normal")
    private val noPureC03Value = BoolValue("NoStandingPackets", false)
    private val renderValue = ListValue("Render", arrayOf("Box", "Lines", "None"), "Lines")
    private val priorityValue = ListValue("Priority", arrayOf("Health", "Distance", "LivingTime"), "Distance")

    /*
     * Variables
     */
    private val clickTimer = MSTimer()
    private var tpVectors = arrayListOf<Vec3>()
    private var thread: Thread? = null

    var lastTarget: EntityLivingBase? = null

    private val attackDelay: Long
        get() = 1000L / apsValue.get().toLong()

    override fun onEnable() {
        clickTimer.reset()
        tpVectors.clear()
        lastTarget = null
    }

    override fun onDisable() {
        clickTimer.reset()
        tpVectors.clear()
        lastTarget = null
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (!clickTimer.hasTimePassed(attackDelay)) return

        if (thread == null || !thread!!.isAlive) {
            tpVectors.clear()
            clickTimer.reset()
            thread = Thread { runAttack() }
            thread!!.start()
        } else
            clickTimer.reset()
    }

    private fun runAttack() {
        if (mc.thePlayer == null || mc.theWorld == null) return

        val targets = arrayListOf<EntityLivingBase>()
        var entityCount = 0

        for (entity in mc.theWorld.loadedEntityList)
            if (entity is EntityLivingBase && EntityUtils.isSelected(entity, true) && mc.thePlayer.getDistanceToEntity(
                    entity
                ) <= rangeValue.get()
            ) {
                if (fovValue.get() < 180F && RotationUtils.getRotationDifference(entity) > fovValue.get())
                    continue

                if (entityCount >= maxTargetsValue.get())
                    break

                targets.add(entity)
                entityCount++
            }

        if (targets.isEmpty()) {
            lastTarget = null
            return
        }

        // Sort targets by priority
        when (priorityValue.get().lowercase(Locale.getDefault())) {
            "distance" -> targets.sortBy { mc.thePlayer.getDistanceToEntity(it) } // Sort by distance
            "health" -> targets.sortBy { it.health } // Sort by health
            "livingtime" -> targets.sortBy { -it.ticksExisted } // Sort by existence
        }

        targets.forEach {
            if (mc.thePlayer == null || mc.theWorld == null) return

            val path = PathUtils.findTeleportPath(mc.thePlayer, it, 3.0)

            path.forEach { point ->
                tpVectors.add(point)
                mc.netHandler.addToSendQueue(C04PacketPlayerPosition(point.xCoord, point.yCoord, point.zCoord, true))
            }

            lastTarget = it

            when (swingValue.get().lowercase(Locale.getDefault())) {
                "normal" -> mc.thePlayer.swingItem()
                "packet" -> mc.netHandler.addToSendQueue(C0APacketAnimation())
            }

            mc.netHandler.addToSendQueue(C02PacketUseEntity(it, C02PacketUseEntity.Action.ATTACK))

            path.reversed().forEach { point ->
                if (renderValue.get().equals("lines", true)) tpVectors.add(point)
                mc.netHandler.addToSendQueue(C04PacketPlayerPosition(point.xCoord, point.yCoord, point.zCoord, true))
            }
        }
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet
        if (packet is S08PacketPlayerPosLook)
            clickTimer.reset()

        if (noPureC03Value.get() && packet is C03PacketPlayer
            && packet !is C04PacketPlayerPosition && packet !is C06PacketPlayerPosLook
        )
            event.cancelEvent()
    }

    @EventTarget
    fun onRender3D(event: Render3DEvent) {
        synchronized(tpVectors) {
            if (renderValue.get().equals("none", true) || tpVectors.isEmpty()) return
            val renderPosX = mc.renderManager.viewerPosX
            val renderPosY = mc.renderManager.viewerPosY
            val renderPosZ = mc.renderManager.viewerPosZ

            GL11.glPushMatrix()
            GL11.glEnable(GL11.GL_BLEND)
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
            GL11.glShadeModel(GL11.GL_SMOOTH)
            GL11.glDisable(GL11.GL_TEXTURE_2D)
            GL11.glEnable(GL11.GL_LINE_SMOOTH)
            GL11.glDisable(GL11.GL_DEPTH_TEST)
            GL11.glDisable(GL11.GL_LIGHTING)
            GL11.glDepthMask(false)
            GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST)

            GL11.glLoadIdentity()
            mc.entityRenderer.setupCameraTransform(mc.timer.renderPartialTicks, 2)
            RenderUtils.glColor(Color.WHITE)
            GL11.glLineWidth(1F)

            if (renderValue.get().equals("lines", true))
                GL11.glBegin(GL11.GL_LINE_STRIP)

            try {
                for (vec in tpVectors) {
                    val x = vec.xCoord - renderPosX
                    val y = vec.yCoord - renderPosY
                    val z = vec.zCoord - renderPosZ
                    val width = 0.3
                    val height = mc.thePlayer.getEyeHeight().toDouble()

                    when (renderValue.get().lowercase(Locale.getDefault())) {
                        "box" -> {
                            GL11.glBegin(GL11.GL_LINE_STRIP)
                            GL11.glVertex3d(x - width, y, z - width)
                            GL11.glVertex3d(x - width, y, z - width)
                            GL11.glVertex3d(x - width, y + height, z - width)
                            GL11.glVertex3d(x + width, y + height, z - width)
                            GL11.glVertex3d(x + width, y, z - width)
                            GL11.glVertex3d(x - width, y, z - width)
                            GL11.glVertex3d(x - width, y, z + width)
                            GL11.glEnd()

                            GL11.glBegin(GL11.GL_LINE_STRIP)
                            GL11.glVertex3d(x + width, y, z + width)
                            GL11.glVertex3d(x + width, y + height, z + width)
                            GL11.glVertex3d(x - width, y + height, z + width)
                            GL11.glVertex3d(x - width, y, z + width)
                            GL11.glVertex3d(x + width, y, z + width)
                            GL11.glVertex3d(x + width, y, z - width)
                            GL11.glEnd()

                            GL11.glBegin(GL11.GL_LINE_STRIP)
                            GL11.glVertex3d(x + width, y + height, z + width)
                            GL11.glVertex3d(x + width, y + height, z - width)
                            GL11.glEnd()

                            GL11.glBegin(GL11.GL_LINE_STRIP)
                            GL11.glVertex3d(x - width, y + height, z + width)
                            GL11.glVertex3d(x - width, y + height, z - width)
                            GL11.glEnd()
                        }

                        "lines" -> GL11.glVertex3d(x, y, z)
                    }
                }
            } catch (e: Exception) {
                // ignore, concurrent modification error
            }

            if (renderValue.get().equals("lines", true))
                GL11.glEnd()

            GL11.glDepthMask(true)
            GL11.glEnable(GL11.GL_DEPTH_TEST)
            GL11.glDisable(GL11.GL_LINE_SMOOTH)
            GL11.glEnable(GL11.GL_TEXTURE_2D)
            GL11.glDisable(GL11.GL_BLEND)
            GL11.glPopMatrix()
            GL11.glColor4f(1F, 1F, 1F, 1F)
        }
    }

}
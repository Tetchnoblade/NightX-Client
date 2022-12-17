package net.aspw.nightx.features.module.modules.player

import net.aspw.nightx.NightX
import net.aspw.nightx.event.*
import net.aspw.nightx.features.module.Module
import net.aspw.nightx.features.module.ModuleCategory
import net.aspw.nightx.features.module.ModuleInfo
import net.aspw.nightx.features.module.modules.movement.Flight
import net.aspw.nightx.features.module.modules.world.Scaffold
import net.aspw.nightx.utils.MovementUtils
import net.aspw.nightx.utils.PacketUtils
import net.aspw.nightx.utils.block.BlockUtils.getBlock
import net.aspw.nightx.utils.misc.NewFallingPlayer
import net.aspw.nightx.utils.misc.RandomUtils
import net.aspw.nightx.value.BoolValue
import net.aspw.nightx.value.FloatValue
import net.aspw.nightx.value.IntegerValue
import net.aspw.nightx.value.ListValue
import net.minecraft.block.BlockAir
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition
import net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook
import net.minecraft.network.play.server.S08PacketPlayerPosLook
import net.minecraft.util.BlockPos
import org.lwjgl.opengl.GL11
import java.util.*

@ModuleInfo(name = "AntiFall", spacedName = "Anti Fall", category = ModuleCategory.PLAYER)
class AntiFall : Module() {
    val voidDetectionAlgorithm = ListValue("Detect-Method", arrayOf("Collision", "Predict"), "Collision")
    val setBackModeValue = ListValue(
        "SetBack-Mode",
        arrayOf(
            "Teleport",
            "FlyFlag",
            "IllegalPacket",
            "IllegalTeleport",
            "StopMotion",
            "Position",
            "Edit",
            "SpoofBack"
        ),
        "FlyFlag"
    )
    val maxFallDistSimulateValue = IntegerValue("Predict-CheckFallDistance", 255, 0, 255, "m") {
        voidDetectionAlgorithm.get().equals("predict", ignoreCase = true)
    }
    val maxFindRangeValue = IntegerValue("Predict-MaxFindRange", 60, 0, 255, "m") {
        voidDetectionAlgorithm.get().equals("predict", ignoreCase = true)
    }
    val illegalDupeValue = IntegerValue("Illegal-Dupe", 1, 1, 5, "x") {
        setBackModeValue.get().lowercase(Locale.getDefault()).contains("illegal")
    }
    val setBackFallDistValue = FloatValue("Max-FallDistance", 5f, 0f, 255f, "m")
    val resetFallDistanceValue = BoolValue("Reset-FallDistance", false)
    val renderTraceValue = BoolValue("Render-Trace", false)
    val scaffoldValue = BoolValue("AutoScaffold", false)
    val noFlyValue = BoolValue("NoFlight", true)
    private val positions = LinkedList<DoubleArray>()
    private var detectedLocation = BlockPos.ORIGIN
    private var lastX = 0.0
    private var lastY = 0.0
    private var lastZ = 0.0
    private var lastFound = 0.0
    private var shouldRender = false
    private var shouldStopMotion = false
    private var shouldEdit = false

    @EventTarget
    fun onUpdate(event: UpdateEvent?) {
        if (noFlyValue.get() && NightX.moduleManager.getModule(Flight::class.java)!!.state) return
        detectedLocation = null
        if (voidDetectionAlgorithm.get().equals("collision", ignoreCase = true)) {
            if (mc.thePlayer.onGround && getBlock(
                    BlockPos(
                        mc.thePlayer.posX,
                        mc.thePlayer.posY - 1.0,
                        mc.thePlayer.posZ
                    )
                ) !is BlockAir
            ) {
                lastX = mc.thePlayer.prevPosX
                lastY = mc.thePlayer.prevPosY
                lastZ = mc.thePlayer.prevPosZ
            }
            shouldRender = renderTraceValue.get() && !MovementUtils.isBlockUnder()
            shouldStopMotion = false
            shouldEdit = false
            if (!MovementUtils.isBlockUnder()) {
                if (mc.thePlayer.fallDistance >= setBackFallDistValue.get()) {
                    shouldStopMotion = true
                    when (setBackModeValue.get()) {
                        "IllegalTeleport" -> {
                            mc.thePlayer.setPositionAndUpdate(lastX, lastY, lastZ)
                            var i = 0
                            while (i < illegalDupeValue.get()) {
                                PacketUtils.sendPacketNoEvent(
                                    C04PacketPlayerPosition(
                                        mc.thePlayer.posX,
                                        mc.thePlayer.posY - 1E+159,
                                        mc.thePlayer.posZ,
                                        false
                                    )
                                )
                                i++
                            }
                        }

                        "IllegalPacket" -> {
                            var i = 0
                            while (i < illegalDupeValue.get()) {
                                PacketUtils.sendPacketNoEvent(
                                    C04PacketPlayerPosition(
                                        mc.thePlayer.posX,
                                        mc.thePlayer.posY - 1E+159,
                                        mc.thePlayer.posZ,
                                        false
                                    )
                                )
                                i++
                            }
                        }

                        "Teleport" -> mc.thePlayer.setPositionAndUpdate(lastX, lastY, lastZ)
                        "FlyFlag" -> mc.thePlayer.motionY = 0.0
                        "StopMotion" -> {
                            val oldFallDist = mc.thePlayer.fallDistance
                            mc.thePlayer.motionY = 0.0
                            mc.thePlayer.fallDistance = oldFallDist
                        }

                        "Position" -> PacketUtils.sendPacketNoEvent(
                            C06PacketPlayerPosLook(
                                mc.thePlayer.posX,
                                mc.thePlayer.posY + RandomUtils.nextDouble(6.0, 10.0),
                                mc.thePlayer.posZ,
                                mc.thePlayer.rotationYaw,
                                mc.thePlayer.rotationPitch,
                                false
                            )
                        )

                        "Edit", "SpoofBack" -> shouldEdit = true
                    }
                    if (resetFallDistanceValue.get() && !setBackModeValue.get()
                            .equals("StopMotion", ignoreCase = true)
                    ) mc.thePlayer.fallDistance = 0f
                    if (scaffoldValue.get() && !NightX.moduleManager.getModule(Scaffold::class.java)!!.state) NightX.moduleManager.getModule(
                        Scaffold::class.java
                    )!!.state = true
                }
            }
        } else {
            if (mc.thePlayer.onGround && getBlock(
                    BlockPos(
                        mc.thePlayer.posX,
                        mc.thePlayer.posY - 1.0,
                        mc.thePlayer.posZ
                    )
                ) !is BlockAir
            ) {
                lastX = mc.thePlayer.prevPosX
                lastY = mc.thePlayer.prevPosY
                lastZ = mc.thePlayer.prevPosZ
            }
            shouldStopMotion = false
            shouldEdit = false
            shouldRender = false
            if (!mc.thePlayer.onGround && !mc.thePlayer.isOnLadder && !mc.thePlayer.isInWater) {
                val NewFallingPlayer = NewFallingPlayer(mc.thePlayer)
                try {
                    detectedLocation = NewFallingPlayer.findCollision(maxFindRangeValue.get())
                } catch (e: Exception) {
                    // do nothing. i hate errors
                }
                if (detectedLocation != null && Math.abs(mc.thePlayer.posY - detectedLocation.y) +
                    mc.thePlayer.fallDistance <= maxFallDistSimulateValue.get()
                ) {
                    lastFound = mc.thePlayer.fallDistance.toDouble()
                }
                shouldRender = renderTraceValue.get() && detectedLocation == null
                if (mc.thePlayer.fallDistance - lastFound > setBackFallDistValue.get()) {
                    shouldStopMotion = true
                    when (setBackModeValue.get()) {
                        "IllegalTeleport" -> {
                            mc.thePlayer.setPositionAndUpdate(lastX, lastY, lastZ)
                            var i = 0
                            while (i < illegalDupeValue.get()) {
                                PacketUtils.sendPacketNoEvent(
                                    C04PacketPlayerPosition(
                                        mc.thePlayer.posX,
                                        mc.thePlayer.posY - 1E+159,
                                        mc.thePlayer.posZ,
                                        false
                                    )
                                )
                                i++
                            }
                        }

                        "IllegalPacket" -> {
                            var i = 0
                            while (i < illegalDupeValue.get()) {
                                PacketUtils.sendPacketNoEvent(
                                    C04PacketPlayerPosition(
                                        mc.thePlayer.posX,
                                        mc.thePlayer.posY - 1E+159,
                                        mc.thePlayer.posZ,
                                        false
                                    )
                                )
                                i++
                            }
                        }

                        "Teleport" -> mc.thePlayer.setPositionAndUpdate(lastX, lastY, lastZ)
                        "FlyFlag" -> mc.thePlayer.motionY = 0.0
                        "StopMotion" -> {
                            val oldFallDist = mc.thePlayer.fallDistance
                            mc.thePlayer.motionY = 0.0
                            mc.thePlayer.fallDistance = oldFallDist
                        }

                        "Position" -> PacketUtils.sendPacketNoEvent(
                            C06PacketPlayerPosLook(
                                mc.thePlayer.posX,
                                mc.thePlayer.posY + RandomUtils.nextDouble(6.0, 10.0),
                                mc.thePlayer.posZ,
                                mc.thePlayer.rotationYaw,
                                mc.thePlayer.rotationPitch,
                                false
                            )
                        )

                        "Edit", "SpoofBack" -> shouldEdit = true
                    }
                    if (resetFallDistanceValue.get() && !setBackModeValue.get()
                            .equals("StopMotion", ignoreCase = true)
                    ) mc.thePlayer.fallDistance = 0f
                    if (scaffoldValue.get() && !NightX.moduleManager.getModule(Scaffold::class.java)!!.state) NightX.moduleManager.getModule(
                        Scaffold::class.java
                    )!!.state = true

                    /*if (towerValue.get() && !LiquidBounce.moduleManager.getModule(Tower.class).getState())
                        LiquidBounce.moduleManager.getModule(Tower.class).setState(true);*/
                }
            }
        }
        if (shouldRender) synchronized(positions) {
            positions.add(
                doubleArrayOf(
                    mc.thePlayer.posX,
                    mc.thePlayer.entityBoundingBox.minY,
                    mc.thePlayer.posZ
                )
            )
        } else synchronized(positions) { positions.clear() }
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        if (noFlyValue.get() && NightX.moduleManager.getModule(Flight::class.java)!!.state) return
        if (setBackModeValue.get()
                .equals("StopMotion", ignoreCase = true) && event.packet is S08PacketPlayerPosLook
        ) mc.thePlayer.fallDistance = 0f
        if (setBackModeValue.get().equals("Edit", ignoreCase = true) && shouldEdit && event.packet is C03PacketPlayer) {
            event.packet.y += 100.0
            shouldEdit = false
        }
        if (setBackModeValue.get()
                .equals("SpoofBack", ignoreCase = true) && shouldEdit && event.packet is C03PacketPlayer
        ) {
            val packetPlayer = event.packet
            packetPlayer.x = lastX
            packetPlayer.y = lastY
            packetPlayer.z = lastZ
            packetPlayer.isMoving = false
            shouldEdit = false
        }
    }

    @EventTarget
    fun onMove(event: MoveEvent) {
        if (noFlyValue.get() && NightX.moduleManager.getModule(Flight::class.java)!!.state) return
        if (setBackModeValue.get().equals("StopMotion", ignoreCase = true) && shouldStopMotion) {
            event.zero()
        }
    }

    @EventTarget
    fun onRender3D(event: Render3DEvent?) {
        if (noFlyValue.get() && NightX.moduleManager.getModule(Flight::class.java)!!.state) return
        if (shouldRender) synchronized(positions) {
            GL11.glPushMatrix()
            GL11.glDisable(GL11.GL_TEXTURE_2D)
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
            GL11.glEnable(GL11.GL_LINE_SMOOTH)
            GL11.glEnable(GL11.GL_BLEND)
            GL11.glDisable(GL11.GL_DEPTH_TEST)
            mc.entityRenderer.disableLightmap()
            GL11.glLineWidth(1f)
            GL11.glBegin(GL11.GL_LINE_STRIP)
            GL11.glColor4f(1f, 1f, 0.1f, 1f)
            val renderPosX = mc.renderManager.viewerPosX
            val renderPosY = mc.renderManager.viewerPosY
            val renderPosZ = mc.renderManager.viewerPosZ
            for (pos in positions) GL11.glVertex3d(pos[0] - renderPosX, pos[1] - renderPosY, pos[2] - renderPosZ)
            GL11.glColor4d(1.0, 1.0, 1.0, 1.0)
            GL11.glEnd()
            GL11.glEnable(GL11.GL_DEPTH_TEST)
            GL11.glDisable(GL11.GL_LINE_SMOOTH)
            GL11.glDisable(GL11.GL_BLEND)
            GL11.glEnable(GL11.GL_TEXTURE_2D)
            GL11.glPopMatrix()
        }
    }

    override fun onDisable() {
        reset()
        super.onDisable()
    }

    override fun onEnable() {
        reset()
        super.onEnable()
    }

    private fun reset() {
        detectedLocation = null
        lastFound = 0.0
        lastZ = lastFound
        lastY = lastZ
        lastX = lastY
        shouldRender = false
        shouldStopMotion = shouldRender
        synchronized(positions) { positions.clear() }
    }
}
package net.aspw.client.features.module.impl.movement

import net.aspw.client.Launch
import net.aspw.client.event.*
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.utils.MovementUtils
import net.aspw.client.utils.PacketUtils
import net.aspw.client.utils.PredictUtils
import net.aspw.client.utils.block.BlockUtils.getBlock
import net.aspw.client.utils.misc.NewFallingPlayer
import net.aspw.client.utils.misc.RandomUtils
import net.aspw.client.utils.pathfinder.MainPathFinder
import net.aspw.client.utils.pathfinder.Vec3
import net.aspw.client.utils.timer.TickTimer
import net.aspw.client.value.FloatValue
import net.aspw.client.value.IntegerValue
import net.aspw.client.value.ListValue
import net.minecraft.block.BlockAir
import net.minecraft.network.Packet
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition
import net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook
import net.minecraft.network.play.server.S08PacketPlayerPosLook
import net.minecraft.util.BlockPos
import java.util.*
import java.util.concurrent.LinkedBlockingQueue
import kotlin.math.abs

@ModuleInfo(name = "AntiVoid", spacedName = "Anti Void", category = ModuleCategory.MOVEMENT)
class AntiVoid : Module() {
    private val setBackModeValue = ListValue(
        "SetBack-Mode",
        arrayOf(
            "Blink",
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
    private val voidDetectionAlgorithm = ListValue("Detect-Method", arrayOf("Collision", "Predict"), "Collision") {
        !setBackModeValue.get().equals("blink", true)
    }
    private val maxFallDistSimulateValue = IntegerValue("Predict-CheckFallDistance", 255, 0, 255, "m") {
        voidDetectionAlgorithm.get().equals("predict", ignoreCase = true) && !setBackModeValue.get()
            .equals("blink", true)
    }
    private val maxFindRangeValue = IntegerValue("Predict-MaxFindRange", 60, 0, 255, "m") {
        voidDetectionAlgorithm.get().equals("predict", ignoreCase = true) && !setBackModeValue.get()
            .equals("blink", true)
    }
    private val illegalDupeValue = IntegerValue("Illegal-Dupe", 1, 1, 5, "x") {
        setBackModeValue.get().lowercase(Locale.getDefault()).contains("illegal")
    }
    private val setBackFallDistValue = FloatValue("Max-FallDistance", 8f, 0f, 255f, "m")
    private val positions = LinkedList<DoubleArray>()
    private var detectedLocation = BlockPos.ORIGIN
    private var lastX = 0.0
    private var lastY = 0.0
    private var lastZ = 0.0
    private var lastFound = 0.0
    private var shouldRender = false
    private var shouldStopMotion = false
    private var shouldEdit = false
    private var packets = LinkedBlockingQueue<Packet<*>>()
    private var safeTimer = TickTimer()
    private var togglePrevent = false
    private var disableLogger = false
    private var preX: Double? = null
    private var preY: Double? = null
    private var preZ: Double? = null
    private var preYaw: Float? = null
    private var prePitch: Float? = null

    @EventTarget
    fun onUpdate(event: UpdateEvent?) {
        if (Launch.moduleManager.getModule(Flight::class.java)!!.state || setBackModeValue.get()
                .equals("blink", true)
        ) return
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

                        "Teleport" -> Thread {
                            val path: ArrayList<Vec3> = MainPathFinder.computePath(
                                Vec3(
                                    mc.thePlayer.posX,
                                    mc.thePlayer.posY,
                                    mc.thePlayer.posZ
                                ),
                                Vec3(lastX, lastY, lastZ)
                            )
                            for (point in path) PacketUtils.sendPacketNoEvent(
                                C04PacketPlayerPosition(
                                    point.x,
                                    point.y,
                                    point.z,
                                    true
                                )
                            )
                            mc.thePlayer.setPosition(lastX, lastY, lastZ)
                        }.start()

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
                    if (!setBackModeValue.get()
                            .equals("StopMotion", ignoreCase = true)
                    ) mc.thePlayer.fallDistance = 0f
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
                } catch (_: Exception) {
                }
                if (detectedLocation != null && abs(mc.thePlayer.posY - detectedLocation.y) +
                    mc.thePlayer.fallDistance <= maxFallDistSimulateValue.get()
                ) {
                    lastFound = mc.thePlayer.fallDistance.toDouble()
                }
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

                        "Teleport" -> Thread {
                            val path: ArrayList<Vec3> = MainPathFinder.computePath(
                                Vec3(
                                    mc.thePlayer.posX,
                                    mc.thePlayer.posY,
                                    mc.thePlayer.posZ
                                ),
                                Vec3(lastX, lastY, lastZ)
                            )
                            for (point in path) PacketUtils.sendPacketNoEvent(
                                C04PacketPlayerPosition(
                                    point.x,
                                    point.y,
                                    point.z,
                                    true
                                )
                            )
                            mc.thePlayer.setPosition(lastX, lastY, lastZ)
                        }.start()

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
                    if (!setBackModeValue.get()
                            .equals("StopMotion", ignoreCase = true)
                    ) mc.thePlayer.fallDistance = 0f
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
        if (Launch.moduleManager.getModule(Flight::class.java)!!.state) return
        val packet = event.packet
        if (setBackModeValue.get().equals("blink", true)) {
            if (mc.thePlayer == null || disableLogger || !togglePrevent) return
            if (packet is C03PacketPlayer) {
                if (preX == null)
                    preX = packet.x
                if (preY == null)
                    preY = packet.y
                if (preZ == null)
                    preZ = packet.z
                if (preYaw == null)
                    preYaw = packet.yaw
                if (prePitch == null)
                    prePitch = packet.pitch
                packets.add(packet)
                safeTimer.update()
                event.cancelEvent()
            }
            return
        }
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
        if (Launch.moduleManager.getModule(Flight::class.java)!!.state || setBackModeValue.get()
                .equals("blink", true)
        ) return
        if (setBackModeValue.get().equals("StopMotion", ignoreCase = true) && shouldStopMotion)
            event.zero()
    }

    @EventTarget
    fun onRender3D(event: Render3DEvent) {
        if (setBackModeValue.get().equals("blink", true)) {
            togglePrevent = PredictUtils.checkVoid(7)
            if (togglePrevent) {
                if (abs(mc.thePlayer.posY - preY!!) > setBackFallDistValue.get()) {
                    mc.thePlayer.setPositionAndRotation(preX!!, preY!!, preZ!!, preYaw!!, prePitch!!)
                    mc.thePlayer.motionX = 0.0
                    mc.thePlayer.motionY = 0.0
                    mc.thePlayer.motionZ = 0.0
                    resetBlink()
                } else if (safeTimer.hasTimePassed(20) || shouldSync(0.8f) || shouldSync(1.8f))
                    sync()
            } else sync()
        }
    }

    @EventTarget
    fun onWorld(event: WorldEvent) {
        if (setBackModeValue.get().equals("blink", true))
            resetBlink()
    }

    @EventTarget
    fun onTeleport(event: TeleportEvent) {
        if (setBackModeValue.get().equals("blink", true)) {
            if (togglePrevent) {
                if (preX == null)
                    preX = event.posX
                if (preY == null)
                    preY = event.posY
                if (preZ == null)
                    preZ = event.posZ
                if (preYaw == null)
                    preYaw = event.yaw
                if (prePitch == null)
                    prePitch = event.pitch
            }
        }
    }

    override fun onDisable() {
        reset()
        resetBlink()
    }

    private fun shouldSync(y: Float): Boolean {
        return mc.theWorld.getBlockState(
            BlockPos(
                mc.thePlayer.posX,
                mc.thePlayer.posY - y,
                mc.thePlayer.posZ
            )
        ).block !is BlockAir || mc.theWorld.getBlockState(
            BlockPos(
                mc.thePlayer.posX + 0.3,
                mc.thePlayer.posY - y,
                mc.thePlayer.posZ + 0.3
            )
        ).block !is BlockAir || mc.theWorld.getBlockState(
            BlockPos(
                mc.thePlayer.posX + 0.3,
                mc.thePlayer.posY - y,
                mc.thePlayer.posZ - 0.3
            )
        ).block !is BlockAir || mc.theWorld.getBlockState(
            BlockPos(
                mc.thePlayer.posX - 0.3,
                mc.thePlayer.posY - y,
                mc.thePlayer.posZ + 0.3
            )
        ).block !is BlockAir || mc.theWorld.getBlockState(
            BlockPos(
                mc.thePlayer.posX - 0.3,
                mc.thePlayer.posY - y,
                mc.thePlayer.posZ - 0.3
            )
        ).block !is BlockAir
    }

    private fun sync() {
        try {
            disableLogger = true
            while (packets.isNotEmpty()) {
                mc.netHandler.networkManager.sendPacket(packets.take())
            }
            resetBlink()
        } catch (_: Exception) {
        }
    }

    private fun resetBlink() {
        if (packets.isNotEmpty())
            packets.clear()
        safeTimer.reset()
        togglePrevent = false
        disableLogger = false
        preX = null
        preY = null
        preZ = null
        preYaw = null
        prePitch = null
    }

    private fun reset() {
        detectedLocation = null
        lastFound = 0.0
        lastZ = lastFound
        lastY = lastZ
        lastX = lastY
        shouldRender = false
        shouldStopMotion = false
        synchronized(positions) { positions.clear() }
    }
}
package net.aspw.client.features.module.impl.movement

import net.aspw.client.Launch
import net.aspw.client.event.EventTarget
import net.aspw.client.event.MoveEvent
import net.aspw.client.event.PacketEvent
import net.aspw.client.event.UpdateEvent
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.features.module.impl.player.Scaffold
import net.aspw.client.utils.MovementUtils
import net.aspw.client.utils.PacketUtils
import net.aspw.client.utils.block.BlockUtils.getBlock
import net.aspw.client.utils.misc.NewFallingPlayer
import net.aspw.client.utils.misc.RandomUtils
import net.aspw.client.utils.pathfinder.MainPathFinder
import net.aspw.client.utils.pathfinder.Vec3
import net.aspw.client.value.BoolValue
import net.aspw.client.value.FloatValue
import net.aspw.client.value.IntegerValue
import net.aspw.client.value.ListValue
import net.minecraft.block.BlockAir
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition
import net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook
import net.minecraft.network.play.server.S08PacketPlayerPosLook
import net.minecraft.util.BlockPos
import java.util.*
import kotlin.math.abs

@ModuleInfo(name = "AntiVoid", spacedName = "Anti Void", category = ModuleCategory.MOVEMENT)
class AntiVoid : Module() {
    private val voidDetectionAlgorithm = ListValue("Detect-Method", arrayOf("Collision", "Predict"), "Collision")
    private val setBackModeValue = ListValue(
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
    private val maxFallDistSimulateValue = IntegerValue("Predict-CheckFallDistance", 255, 0, 255, "m") {
        voidDetectionAlgorithm.get().equals("predict", ignoreCase = true)
    }
    private val maxFindRangeValue = IntegerValue("Predict-MaxFindRange", 60, 0, 255, "m") {
        voidDetectionAlgorithm.get().equals("predict", ignoreCase = true)
    }
    private val illegalDupeValue = IntegerValue("Illegal-Dupe", 1, 1, 5, "x") {
        setBackModeValue.get().lowercase(Locale.getDefault()).contains("illegal")
    }
    private val setBackFallDistValue = FloatValue("Max-FallDistance", 5f, 0f, 255f, "m")
    private val scaffoldValue = BoolValue("AutoScaffold", false)
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
        if (Launch.moduleManager.getModule(Flight::class.java)!!.state) return
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
                    if (scaffoldValue.get() && !Launch.moduleManager.getModule(Scaffold::class.java)!!.state) Launch.moduleManager.getModule(
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
                    if (scaffoldValue.get() && !Launch.moduleManager.getModule(Scaffold::class.java)!!.state) Launch.moduleManager.getModule(
                        Scaffold::class.java
                    )!!.state = true
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
        if (Launch.moduleManager.getModule(Flight::class.java)!!.state) return
        if (setBackModeValue.get().equals("StopMotion", ignoreCase = true) && shouldStopMotion) {
            event.zero()
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
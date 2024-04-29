package net.aspw.client.features.module.impl.player

import net.aspw.client.Launch
import net.aspw.client.event.*
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.features.module.impl.movement.Speed
import net.aspw.client.features.module.impl.visual.SilentRotations
import net.aspw.client.utils.*
import net.aspw.client.utils.block.BlockUtils
import net.aspw.client.utils.block.BlockUtils.canBeClicked
import net.aspw.client.utils.block.BlockUtils.isReplaceable
import net.aspw.client.utils.block.PlaceInfo
import net.aspw.client.utils.misc.RandomUtils
import net.aspw.client.utils.timer.MSTimer
import net.aspw.client.utils.timer.TickTimer
import net.aspw.client.utils.timer.TimeUtils
import net.aspw.client.value.BoolValue
import net.aspw.client.value.FloatValue
import net.aspw.client.value.IntegerValue
import net.aspw.client.value.ListValue
import net.aspw.client.visual.font.smooth.FontLoaders
import net.minecraft.block.BlockAir
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.settings.GameSettings
import net.minecraft.client.settings.KeyBinding
import net.minecraft.init.Blocks
import net.minecraft.item.ItemBlock
import net.minecraft.network.Packet
import net.minecraft.network.play.client.*
import net.minecraft.potion.Potion
import net.minecraft.stats.StatList
import net.minecraft.util.*
import java.util.*
import java.util.concurrent.LinkedBlockingQueue
import kotlin.math.*


@ModuleInfo(name = "Scaffold", category = ModuleCategory.PLAYER)
class Scaffold : Module() {
    /**
     * OPTIONS (Tower)
     */
    // Global settings
    private val allowTower = BoolValue("EnableTower", true)
    private val towerMove =
        ListValue("TowerWhen", arrayOf("Always", "Moving", "Standing"), "Always") { allowTower.get() }
    private val towerModeValue = ListValue(
        "TowerMode", arrayOf(
            "Jump",
            "Motion",
            "StableMotion",
            "ConstantMotion",
            "MotionTP",
            "Teleport",
            "AAC3.3.9",
            "AAC3.6.4",
            "BlocksMC",
            "Watchdog",
            "Float"
        ), "ConstantMotion"
    ) { allowTower.get() }
    private val towerTimerValue = FloatValue("TowerTimer", 1f, 0.1f, 1.4f) { allowTower.get() }

    // Watchdog
    private val watchdogTowerBoostValue =
        BoolValue("WatchdogTowerBoost", true) { allowTower.get() && towerModeValue.get().equals("Watchdog", true) }
    private val watchdogTowerSpeed = FloatValue("Watchdog-TowerSpeed", 1.7f, 1.5f, 2.5f, "x") {
        allowTower.get() && towerModeValue.get().equals("Watchdog", true) && watchdogTowerBoostValue.get()
    }

    // Jump mode
    private val jumpMotionValue = FloatValue("JumpMotion", 0.42f, 0.3681289f, 0.79f) {
        allowTower.get() && towerModeValue.get().equals("Jump", ignoreCase = true)
    }
    private val jumpDelayValue = IntegerValue("JumpDelay", 0, 0, 20) {
        allowTower.get() && towerModeValue.get().equals("Jump", ignoreCase = true)
    }

    // StableMotion
    private val stableMotionValue = FloatValue("StableMotion", 0.41982f, 0.1f, 1f) {
        allowTower.get() && towerModeValue.get().equals("StableMotion", ignoreCase = true)
    }
    private val stableFakeJumpValue = BoolValue("StableFakeJump", false) {
        allowTower.get() && towerModeValue.get().equals("StableMotion", ignoreCase = true)
    }
    private val stableStopValue = BoolValue("StableStop", false) {
        allowTower.get() && towerModeValue.get().equals("StableMotion", ignoreCase = true)
    }
    private val stableStopDelayValue = IntegerValue("StableStopDelay", 1500, 0, 5000) {
        allowTower.get() && towerModeValue.get().equals("StableMotion", ignoreCase = true) && stableStopValue.get()
    }

    // ConstantMotion
    private val constantMotionValue = FloatValue("ConstantMotion", 0.42f, 0.1f, 1f) {
        allowTower.get() && towerModeValue.get().equals("ConstantMotion", ignoreCase = true)
    }
    private val constantMotionJumpGroundValue = FloatValue("ConstantMotionJumpGround", 0.79f, 0.76f, 1f) {
        allowTower.get() && towerModeValue.get().equals("ConstantMotion", ignoreCase = true)
    }

    // Teleport
    private val teleportHeightValue = FloatValue("TeleportHeight", 1.15f, 0.1f, 5f) {
        allowTower.get() && towerModeValue.get().equals("Teleport", ignoreCase = true)
    }
    private val teleportDelayValue = IntegerValue("TeleportDelay", 0, 0, 20) {
        allowTower.get() && towerModeValue.get().equals("Teleport", ignoreCase = true)
    }
    private val teleportGroundValue = BoolValue("TeleportGround", true) {
        allowTower.get() && towerModeValue.get().equals("Teleport", ignoreCase = true)
    }
    private val teleportNoMotionValue = BoolValue("TeleportNoMotion", false) {
        allowTower.get() && towerModeValue.get().equals("Teleport", ignoreCase = true)
    }

    // Timing
    @JvmField
    val sprintModeValue = ListValue("SprintMode", arrayOf("Same", "Silent", "Ground", "Air", "Off"), "Same")
    private val placeConditionValue =
        ListValue("Place-Condition", arrayOf("Air", "FallDown", "NegativeMotion", "Always"), "Always")

    private val rotationModeValue = ListValue(
        "RotationMode",
        arrayOf("Normal", "Smooth"),
        "Normal"
    )
    private val preRotationValue = ListValue("WaitRotationMode", arrayOf("Normal", "Lock", "None"), "Normal")
    private val autoJumpValue =
        ListValue("AutoJumpMode", arrayOf("Off", "Normal", "HypixelKeepY", "KeepY", "Breezily"), "Off")
    private val breezilyDelayValue =
        IntegerValue("Breezily-Delay", 7, 2, 12) { autoJumpValue.get().equals("breezily", true) }
    private val timerValue = FloatValue("Timer", 1f, 0.1f, 1.4f)

    // Delay
    private val placeableDelay = BoolValue("PlaceableDelay", false)
    private val maxDelayValue: IntegerValue =
        object : IntegerValue("MaxDelay", 50, 0, 1000, "ms", { placeableDelay.get() }) {
            override fun onChanged(oldValue: Int, newValue: Int) {
                val i = minDelayValue.get()
                if (i > newValue) set(i)
            }
        }
    private val minDelayValue: IntegerValue =
        object : IntegerValue("MinDelay", 50, 0, 1000, "ms", { placeableDelay.get() }) {
            override fun onChanged(oldValue: Int, newValue: Int) {
                val i = maxDelayValue.get()
                if (i < newValue) set(i)
            }
        }

    private val startPlaceDelayValue = BoolValue("StartPlaceChecks", false)
    private val startPlaceDelay = IntegerValue("StartPlace-Delay", 5, 5, 30) { startPlaceDelayValue.get() }
    private val placeSlowDownValue = BoolValue("Place-SlowDown", false)
    private val speedModifierValue = FloatValue("Speed-Multiplier", 0.8f, 0f, 1.4f, "x") { placeSlowDownValue.get() }
    private val slowDownValue = BoolValue("SlowDown", false)
    private val xzMultiplier = FloatValue("XZ-Multiplier", 0.7f, 0f, 1.2f, "x") { slowDownValue.get() }
    private val noSpeedPotValue = BoolValue("NoSpeedPot", false)
    private val speedSlowDown = FloatValue("SpeedPot-SlowDown", 0.8f, 0.0f, 1.1f, "x") { noSpeedPotValue.get() }
    private val customSpeedValue = BoolValue("CustomSpeed", false)
    private val customMoveSpeedValue = FloatValue("CustomMoveSpeed", 0.2f, 0f, 5f) { customSpeedValue.get() }
    private val autoSneakValue = BoolValue("AutoSneak", false)
    private val smartSpeedValue = BoolValue("SpeedKeepY", true)
    private val safeWalkValue = BoolValue("SafeWalk", false)
    private val airSafeValue = BoolValue("AirSafe", false) { safeWalkValue.get() }
    private val autoDisableSpeedValue = BoolValue("AutoDisable-Speed", false)
    private val desyncValue = BoolValue("Desync", false)
    private val desyncDelayValue = IntegerValue("DesyncDelay", 400, 10, 810, "ms") { desyncValue.get() }
    private val maxTurnSpeed: FloatValue =
        object : FloatValue("MaxTurnSpeed", 120f, 0f, 180f, "°") {
            override fun onChanged(oldValue: Float, newValue: Float) {
                val i = minTurnSpeed.get()
                if (i > newValue) set(i)
            }
        }
    private val minTurnSpeed: FloatValue =
        object : FloatValue("MinTurnSpeed", 80f, 0f, 180f, "°") {
            override fun onChanged(oldValue: Float, newValue: Float) {
                val i = maxTurnSpeed.get()
                if (i < newValue) set(i)
            }
        }

    // Delay
    private val delayTimer = MSTimer()
    private val towerDelayTimer = MSTimer()

    // Mode stuff
    private val timer = TickTimer()
    private val startPlaceTimer = TickTimer()

    /**
     * MODULE
     */
    // Target block
    private var targetPlace: PlaceInfo? = null

    // Desync
    private val packets = LinkedBlockingQueue<Packet<*>>()
    private val positions = LinkedList<DoubleArray>()
    private val pulseTimer = MSTimer()
    private var disableLogger = false

    // Launch position
    private var launchY = 0
    private var placeCount = 0
    private var hypixelCount = 0
    private var faceBlock = false

    // Rotation lock
    private var lockRotation: Rotation? = null
    private var lookupRotation: Rotation? = null

    // Auto block slot
    private var slot = 0
    private var lastSlot = 0

    private var delay: Long = 0

    // Tower
    private var offGroundTicks: Int = 0
    private var verusJumped = false
    private var wdTick = 0
    private var wdSpoof = false
    private var towerTick = 0

    // Render thingy
    var canTower = false
    private var firstPitch = 0f
    private var firstRotate = 0f
    private var progress = 0f
    private var spinYaw = 0f
    private var lastMS = 0L
    private var jumpGround = 0.0
    private var verusState = 0

    /**
     * Enable module
     */
    override fun onEnable() {
        if (mc.thePlayer == null) return
        progress = 0f
        spinYaw = 0f
        wdTick = 5
        placeCount = 0
        hypixelCount = 0
        firstPitch = mc.thePlayer.rotationPitch
        firstRotate = mc.thePlayer.rotationYaw
        launchY = mc.thePlayer.posY.toInt()
        lastSlot = mc.thePlayer.inventory.currentItem
        slot = mc.thePlayer.inventory.currentItem
        canTower = false
        lastMS = System.currentTimeMillis()
        if (desyncValue.get()) {
            synchronized(positions) {
                positions.add(
                    doubleArrayOf(
                        mc.thePlayer.posX,
                        mc.thePlayer.entityBoundingBox.minY + mc.thePlayer.getEyeHeight() / 2,
                        mc.thePlayer.posZ
                    )
                )
                positions.add(doubleArrayOf(mc.thePlayer.posX, mc.thePlayer.entityBoundingBox.minY, mc.thePlayer.posZ))
            }
            pulseTimer.reset()
        }
    }

    //Send jump packets, bypasses Hypixel.
    private fun fakeJump() {
        mc.thePlayer.isAirBorne = true
        mc.thePlayer.triggerAchievement(StatList.jumpStat)
    }

    /**
     * Update event
     *
     * @param event
     */
    @EventTarget
    fun onUpdate(event: UpdateEvent?) {
        if (!faceBlock && startPlaceTimer.hasTimePassed(1))
            startPlaceTimer.reset()
        if (blocksAmount <= 0) {
            faceBlock = false
            return
        }
        if (lockRotation != null) {
            RotationUtils.setTargetRotation(
                RotationUtils.limitAngleChange(
                    RotationUtils.serverRotation!!,
                    lockRotation!!,
                    RandomUtils.nextFloat(minTurnSpeed.get(), maxTurnSpeed.get())
                )
            )
        } else {
            faceBlock = false
            when (preRotationValue.get().lowercase()) {
                "normal" -> {
                    val yaw = MovementUtils.getRawDirection() - 180f
                    val pitch = 83f
                    RotationUtils.setTargetRotation(
                        RotationUtils.limitAngleChange(
                            RotationUtils.serverRotation!!,
                            Rotation(yaw, pitch),
                            RandomUtils.nextFloat(minTurnSpeed.get(), maxTurnSpeed.get())
                        )
                    )
                }

                "lock" -> {
                    RotationUtils.setTargetRotation(
                        Rotation(firstRotate, firstPitch)
                    )
                }
            }
        }
        val shouldEagle = mc.theWorld.getBlockState(
            BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.0, mc.thePlayer.posZ)
        ).block === Blocks.air
        if (!canTower && (autoSneakValue.get() && mc.thePlayer.onGround && shouldEagle || GameSettings.isKeyDown(mc.gameSettings.keyBindSneak)))
            mc.gameSettings.keyBindSneak.pressed = true
        else if (!GameSettings.isKeyDown(mc.gameSettings.keyBindSneak))
            mc.gameSettings.keyBindSneak.pressed = false
        if (!canTower && towerModeValue.get().equals("watchdog", true) && mc.thePlayer.ticksExisted % 2 == 0) {
            wdTick = 5
            towerTick = 0
            wdSpoof = false
        }
        if (allowTower.get() && GameSettings.isKeyDown(mc.gameSettings.keyBindJump) && !GameSettings.isKeyDown(mc.gameSettings.keyBindSneak) && blocksAmount > 0 && MovementUtils.isRidingBlock() && (towerMove.get()
                .equals("always", true) || !MovementUtils.isMoving() && towerMove.get()
                .equals("standing", true) || MovementUtils.isMoving() && towerMove.get().equals("moving", true))
        ) {
            canTower = true
            when (towerModeValue.get().lowercase(Locale.getDefault())) {
                "jump" -> if (mc.thePlayer.onGround && timer.hasTimePassed(jumpDelayValue.get())) {
                    fakeJump()
                    mc.thePlayer.motionY = jumpMotionValue.get().toDouble()
                    timer.reset()
                }

                "motion" -> if (mc.thePlayer.onGround) {
                    fakeJump()
                    mc.thePlayer.motionY = 0.41999998688698
                } else if (mc.thePlayer.motionY < 0.1) mc.thePlayer.motionY = -0.3

                "motiontp" -> if (mc.thePlayer.onGround) {
                    fakeJump()
                    mc.thePlayer.motionY = 0.41999998688698
                } else if (mc.thePlayer.motionY < 0.23) mc.thePlayer.setPosition(
                    mc.thePlayer.posX, mc.thePlayer.posY.toInt()
                        .toDouble(), mc.thePlayer.posZ
                )

                "teleport" -> {
                    if (teleportNoMotionValue.get()) mc.thePlayer.motionY = 0.0
                    if ((mc.thePlayer.onGround || !teleportGroundValue.get()) && timer.hasTimePassed(
                            teleportDelayValue.get()
                        )
                    ) {
                        fakeJump()
                        mc.thePlayer.setPositionAndUpdate(
                            mc.thePlayer.posX,
                            mc.thePlayer.posY + teleportHeightValue.get(),
                            mc.thePlayer.posZ
                        )
                        timer.reset()
                    }
                }

                "stablemotion" -> {
                    if (stableFakeJumpValue.get()) fakeJump()
                    mc.thePlayer.motionY = stableMotionValue.get().toDouble()
                    if (stableStopValue.get() && towerDelayTimer.hasTimePassed(
                            stableStopDelayValue.get().toLong()
                        )
                    ) {
                        mc.thePlayer.motionY = -0.28
                        towerDelayTimer.reset()
                    }
                }

                "constantmotion" -> {
                    if (mc.thePlayer.onGround) {
                        fakeJump()
                        jumpGround = mc.thePlayer.posY
                        mc.thePlayer.motionY = constantMotionValue.get().toDouble()
                    }
                    if (mc.thePlayer.posY > jumpGround + constantMotionJumpGroundValue.get()) {
                        fakeJump()
                        mc.thePlayer.setPosition(
                            mc.thePlayer.posX, mc.thePlayer.posY.toInt()
                                .toDouble(), mc.thePlayer.posZ
                        )
                        mc.thePlayer.motionY = constantMotionValue.get().toDouble()
                        jumpGround = mc.thePlayer.posY
                    }
                }

                "watchdog" -> {
                    if (wdTick != 0) {
                        towerTick = 0
                        return
                    }
                    if (towerTick > 0) {
                        ++towerTick
                        if (towerTick > 6) {
                            mc.thePlayer.motionX *= 0.9f
                            mc.thePlayer.motionZ *= 0.9f
                        }
                        if (towerTick > 16) {
                            towerTick = 0
                        }
                    }
                    if (mc.thePlayer.onGround) {
                        if (towerTick == 0 || towerTick == 5) {
                            if (watchdogTowerBoostValue.get()) {
                                mc.thePlayer.motionX *= watchdogTowerSpeed.get()
                                mc.thePlayer.motionZ *= watchdogTowerSpeed.get()
                            }
                            mc.thePlayer.motionY = 0.42
                            towerTick = 1
                        }
                    }
                    if (mc.thePlayer.motionY > -0.0784000015258789) {
                        if (!mc.thePlayer.onGround) {
                            when ((mc.thePlayer.posY % 1.0 * 100.0).roundToInt()) {
                                42 -> {
                                    mc.thePlayer.motionY = 0.33
                                }

                                75 -> {
                                    mc.thePlayer.motionY = 1.0 - mc.thePlayer.posY % 1.0
                                    wdSpoof = true
                                }

                                0 -> {
                                    if (MovementUtils.isRidingBlock())
                                        mc.thePlayer.motionY = -0.0784000015258789
                                }
                            }
                        }
                    } else mc.thePlayer.jump()
                }

                "blocksmc" -> {
                    if (mc.thePlayer.posY % 1 <= 0.00153598) {
                        mc.thePlayer.setPosition(
                            mc.thePlayer.posX,
                            floor(mc.thePlayer.posY),
                            mc.thePlayer.posZ
                        )
                        mc.thePlayer.motionY = 0.42
                    } else if (mc.thePlayer.posY % 1 < 0.1 && offGroundTicks != 0) {
                        mc.thePlayer.setPosition(
                            mc.thePlayer.posX,
                            floor(mc.thePlayer.posY),
                            mc.thePlayer.posZ
                        )
                    }
                }

                "aac3.3.9" -> {
                    if (mc.thePlayer.onGround) {
                        fakeJump()
                        mc.thePlayer.motionY = 0.4001
                    }
                    mc.timer.timerSpeed = 1f
                    if (mc.thePlayer.motionY < 0) {
                        mc.thePlayer.motionY -= 0.00000945
                        mc.timer.timerSpeed = 1.6f
                    }
                }

                "aac3.6.4" -> if (mc.thePlayer.ticksExisted % 4 == 1) {
                    mc.thePlayer.motionY = 0.4195464
                    mc.thePlayer.setPosition(mc.thePlayer.posX - 0.035, mc.thePlayer.posY, mc.thePlayer.posZ)
                } else if (mc.thePlayer.ticksExisted % 4 == 0) {
                    mc.thePlayer.motionY = -0.5
                    mc.thePlayer.setPosition(mc.thePlayer.posX + 0.035, mc.thePlayer.posY, mc.thePlayer.posZ)
                }
            }
        } else {
            canTower = false
        }
        if (autoDisableSpeedValue.get() && Launch.moduleManager.getModule(
                Speed::class.java
            )!!.state
        ) {
            Launch.moduleManager.getModule(Speed::class.java)!!.state = false
            chat("Speed was disabled")
        }
        mc.timer.timerSpeed = timerValue.get()

        if (mc.thePlayer.onGround) {
            offGroundTicks = 0
        } else offGroundTicks++
        if ((hypixelCount == 0 || !mc.thePlayer.isPotionActive(Potion.moveSpeed) && hypixelCount >= 5 || mc.thePlayer.isPotionActive(
                Potion.moveSpeed
            ) && hypixelCount >= 6) && autoJumpValue.get().equals("hypixelkeepy", true)
        ) {
            mc.thePlayer.motionX = 0.0
            mc.thePlayer.motionZ = 0.0
            if (mc.thePlayer.posY >= launchY + 1 && mc.thePlayer.posY < launchY + 2) {
                KeyBinding.onTick(mc.gameSettings.keyBindUseItem.keyCode)
                hypixelCount++
            }
            if (!mc.thePlayer.isPotionActive(Potion.moveSpeed) && hypixelCount >= 6 || mc.thePlayer.isPotionActive(
                    Potion.moveSpeed
                ) && hypixelCount >= 7
            )
                hypixelCount = 0
        }
        if (desyncValue.get()) {
            synchronized(positions) {
                positions.add(
                    doubleArrayOf(
                        mc.thePlayer.posX,
                        mc.thePlayer.entityBoundingBox.minY,
                        mc.thePlayer.posZ
                    )
                )
            }
            if (pulseTimer.hasTimePassed(desyncDelayValue.get().toLong())) {
                blink()
                pulseTimer.reset()
            }
        }

        // scaffold custom speed if enabled
        if (customSpeedValue.get()) MovementUtils.strafe(customMoveSpeedValue.get())
        if (sprintModeValue.get().equals("off", ignoreCase = true) || sprintModeValue.get()
                .equals("ground", ignoreCase = true) && !mc.thePlayer.onGround || sprintModeValue.get()
                .equals("air", ignoreCase = true) && mc.thePlayer.onGround
        ) {
            mc.thePlayer.isSprinting = false
        }
        if (!autoJumpValue.get().equals("keepy", true) && !autoJumpValue.get()
                .equals("hypixelkeepy", true) && !(smartSpeedValue.get() && Launch.moduleManager.getModule(
                Speed::class.java
            )!!.state) || GameSettings.isKeyDown(mc.gameSettings.keyBindJump) || mc.thePlayer.posY < launchY
        ) launchY = mc.thePlayer.posY.toInt()
        if (GameSettings.isKeyDown(mc.gameSettings.keyBindJump) && mc.thePlayer.onGround) {
            placeCount = 0
            hypixelCount = 0
        }
        if (((autoJumpValue.get().equals(
                "keepy",
                true
            ) || autoJumpValue.get().equals(
                "hypixelkeepy",
                true
            )) && !Launch.moduleManager.getModule(Speed::class.java)!!.state || autoJumpValue.get()
                .equals("normal", true) && faceBlock || autoJumpValue.get().equals(
                "breezily",
                true
            ) && placeCount >= breezilyDelayValue.get()) && MovementUtils.isMoving() && mc.thePlayer.onGround
        ) {
            mc.thePlayer.jump()
            placeCount = 0
        }
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        if (mc.thePlayer == null) return

        val packet = event.packet

        if (towerModeValue.get().equals("watchdog", true)) {
            if (packet is C03PacketPlayer) {
                if (wdSpoof) {
                    packet.onGround = true
                    wdSpoof = false
                }
            }
        }

        if (desyncValue.get()) {
            if (disableLogger) return
            if (packet is C03PacketPlayer)
                event.cancelEvent()
            if (packet is C03PacketPlayer.C04PacketPlayerPosition || packet is C03PacketPlayer.C06PacketPlayerPosLook ||
                packet is C08PacketPlayerBlockPlacement ||
                packet is C0APacketAnimation ||
                packet is C0BPacketEntityAction || packet is C02PacketUseEntity
            ) {
                event.cancelEvent()
                packets.add(packet)
            }
        }

        // Sprint
        if (sprintModeValue.get().equals("silent", ignoreCase = true)) {
            if (packet is C0BPacketEntityAction &&
                (packet.action == C0BPacketEntityAction.Action.STOP_SPRINTING || packet.action == C0BPacketEntityAction.Action.START_SPRINTING)
            ) event.cancelEvent()
        }

        // AutoBlock
        if (packet is C09PacketHeldItemChange)
            slot = packet.slotId
    }

    @EventTarget
    fun onStrafe(event: StrafeEvent) {
        if (blocksAmount <= 0 || RotationUtils.targetRotation == null) return
        if (Launch.moduleManager.getModule(SilentRotations::class.java)?.state!! && !Launch.moduleManager.getModule(
                SilentRotations::class.java
            )?.customStrafe?.get()!!
        ) {
            if (Launch.moduleManager.getModule(SilentRotations::class.java)?.state!! && !Launch.moduleManager.getModule(
                    SilentRotations::class.java
                )?.customStrafe?.get()!!
            ) {
                event.yaw = RotationUtils.targetRotation?.yaw!! - 180f
            }
        }
    }

    private fun shouldPlace(): Boolean {
        val placeWhenAir = placeConditionValue.get().equals("air", ignoreCase = true)
        val placeWhenFall = placeConditionValue.get().equals("falldown", ignoreCase = true)
        val placeWhenNegativeMotion = placeConditionValue.get().equals("negativemotion", ignoreCase = true)
        val alwaysPlace = placeConditionValue.get().equals("always", ignoreCase = true)
        return alwaysPlace || canTower || placeWhenAir && !mc.thePlayer.onGround || placeWhenFall && mc.thePlayer.fallDistance > 0 || placeWhenNegativeMotion && mc.thePlayer.motionY < 0
    }

    private fun blink() {
        try {
            disableLogger = true
            while (!packets.isEmpty()) {
                mc.netHandler.networkManager.sendPacket(packets.take())
            }
            disableLogger = false
        } catch (e: Exception) {
            e.printStackTrace()
            disableLogger = false
        }
        synchronized(positions) { positions.clear() }
    }

    @EventTarget
    fun onMotion(event: MotionEvent) {
        if (blocksAmount <= 0) return
        if (canTower && !MovementUtils.isMoving() && event.eventState == EventState.POST) {
            mc.thePlayer.motionX = 0.0
            mc.thePlayer.motionZ = 0.0
        }

        if (towerModeValue.get().equals("watchdog", true)) {
            if (wdTick > 0) {
                wdTick--
            }
        }

        val shouldEagle = mc.theWorld.getBlockState(
            BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.0, mc.thePlayer.posZ)
        ).block === Blocks.air

        if ((!mc.thePlayer.onGround || shouldEagle) && mc.thePlayer.ticksExisted % 2 == 0 && !faceBlock)
            faceBlock = true

        try {
            if (faceBlock) {
                place()
            } else if (slot != mc.thePlayer.inventoryContainer.getSlot(InventoryUtils.findAutoBlockBlock()).slotIndex) {
                mc.thePlayer.inventory.currentItem = InventoryUtils.findAutoBlockBlock() - 36
                mc.playerController.updateController()
            }
        } catch (ignored: Exception) {
        }

        // No SpeedPot
        if (noSpeedPotValue.get() && mc.thePlayer.isPotionActive(Potion.moveSpeed) && !canTower && mc.thePlayer.onGround) {
            mc.thePlayer.motionX = mc.thePlayer.motionX * speedSlowDown.get()
            mc.thePlayer.motionZ = mc.thePlayer.motionZ * speedSlowDown.get()
        }

        // XZReducer
        if (mc.thePlayer.onGround && slowDownValue.get()) {
            mc.thePlayer.motionX *= xzMultiplier.get().toDouble()
            mc.thePlayer.motionZ *= xzMultiplier.get().toDouble()
        }

        val eventState = event.eventState

        // I think patches should be here instead
        for (i in 0..7) {
            if (mc.thePlayer.inventory.mainInventory[i] != null
                && mc.thePlayer.inventory.mainInventory[i].stackSize <= 0
            ) mc.thePlayer.inventory.mainInventory[i] = null
        }
        if (eventState === EventState.PRE) {
            if (!shouldPlace() || InventoryUtils.findAutoBlockBlock() == -1) return
            findBlock()
        }
        if (targetPlace == null) {
            if (placeableDelay.get()) delayTimer.reset()
        }
        if (canTower) {
            mc.timer.timerSpeed = towerTimerValue.get()
            if (eventState === EventState.POST && towerModeValue.get().equals("float", true) && BlockUtils.getBlock(
                    BlockPos(mc.thePlayer.posX, mc.thePlayer.posY + 2, mc.thePlayer.posZ)
                ) is BlockAir
            )
                floatUP(event)
        } else {
            verusState = 0
        }
    }

    private fun floatUP(event: MotionEvent) {
        if (mc.theWorld.getCollidingBoundingBoxes(
                mc.thePlayer,
                mc.thePlayer.entityBoundingBox.offset(0.0, -0.01, 0.0)
            ).isNotEmpty() && mc.thePlayer.onGround && mc.thePlayer.isCollidedVertically
        ) {
            verusState = 0
            verusJumped = true
        }
        if (verusJumped) {
            MovementUtils.strafe()
            when (verusState) {
                0 -> {
                    fakeJump()
                    mc.thePlayer.motionY = 0.41999998688697815
                    ++verusState
                }

                1 -> ++verusState
                2 -> ++verusState
                3 -> {
                    event.onGround = true
                    mc.thePlayer.motionY = 0.0
                    ++verusState
                }

                4 -> ++verusState
            }
            verusJumped = false
        }
        verusJumped = true
    }

    /**
     * Search for new target block
     */
    private fun findBlock() {
        val blockPosition =
            if (!canTower && ((autoJumpValue.get()
                    .equals("keepy", true) || autoJumpValue.get()
                    .equals("hypixelkeepy", true) || smartSpeedValue.get() && Launch.moduleManager.getModule(
                    Speed::class.java
                )!!.state) && !GameSettings.isKeyDown(mc.gameSettings.keyBindJump)) && launchY <= mc.thePlayer.posY
            ) BlockPos(
                mc.thePlayer.posX,
                (launchY - 1).toDouble(),
                mc.thePlayer.posZ
            ) else if (mc.thePlayer.posY == mc.thePlayer.posY.toInt() + 0.5) BlockPos(
                mc.thePlayer
            ) else BlockPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ).down()
        if (!isReplaceable(blockPosition) || search(blockPosition)) return
        for (x in -1..1) {
            for (z in -1..1) {
                if (search(blockPosition.add(x, 0, z))) {
                    return
                }
            }
        }
    }

    /**
     * Place target block
     */
    private fun place() {
        if (slot != mc.thePlayer.inventoryContainer.getSlot(InventoryUtils.findAutoBlockBlock()).slotIndex) {
            mc.thePlayer.inventory.currentItem = InventoryUtils.findAutoBlockBlock() - 36
            mc.playerController.updateController()
        }
        if (startPlaceDelayValue.get() && faceBlock && !startPlaceTimer.hasTimePassed(startPlaceDelay.get())) {
            if (!mc.thePlayer.onGround)
                startPlaceTimer.tick = startPlaceDelay.get()
            else startPlaceTimer.update()
            return
        }
        if ((targetPlace) == null) {
            if (placeableDelay.get()) delayTimer.reset()
            return
        }
        if (!canTower && (!delayTimer.hasTimePassed(delay) || ((autoJumpValue.get()
                .equals("keepy", true) || autoJumpValue.get()
                .equals("hypixelkeepy", true) || smartSpeedValue.get() && Launch.moduleManager.getModule(
                Speed::class.java
            )!!.state) && !GameSettings.isKeyDown(mc.gameSettings.keyBindJump)) && launchY - 1 != (targetPlace)!!.vec3.yCoord.toInt())
        ) return
        if (mc.thePlayer.heldItem != null && mc.thePlayer.heldItem.item != null && mc.thePlayer.heldItem.item is ItemBlock) {
            val block = (mc.thePlayer.heldItem.item as ItemBlock).getBlock()
            if (InventoryUtils.BLOCK_BLACKLIST.contains(block) || !block.isFullCube || mc.thePlayer.heldItem.stackSize <= 0) return
        }
        if (mc.thePlayer.heldItem != null && mc.thePlayer.heldItem.item is ItemBlock)
            KeyBinding.onTick(mc.gameSettings.keyBindUseItem.keyCode)
        delayTimer.reset()
        delay = if (!placeableDelay.get()) 0L else TimeUtils.randomDelay(minDelayValue.get(), maxDelayValue.get())
        if (mc.thePlayer.onGround && placeSlowDownValue.get()) {
            val modifier = speedModifierValue.get()
            mc.thePlayer.motionX *= modifier.toDouble()
            mc.thePlayer.motionZ *= modifier.toDouble()
        }
        placeCount += 1
        hypixelCount += 1
        targetPlace = null
    }

    /**
     * Disable scaffold module
     */
    override fun onDisable() {
        if (mc.thePlayer == null) return
        blink()
        startPlaceTimer.reset()
        faceBlock = false
        placeCount = 0
        hypixelCount = 0
        firstPitch = 0f
        firstRotate = 0f
        wdTick = 5
        canTower = false
        mc.gameSettings.keyBindSneak.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindSneak)
        if (sprintModeValue.get().equals("silent", ignoreCase = true)) {
            if (mc.thePlayer.isSprinting) {
                PacketUtils.sendPacketNoEvent(
                    C0BPacketEntityAction(
                        mc.thePlayer,
                        C0BPacketEntityAction.Action.STOP_SPRINTING
                    )
                )
                PacketUtils.sendPacketNoEvent(
                    C0BPacketEntityAction(
                        mc.thePlayer,
                        C0BPacketEntityAction.Action.START_SPRINTING
                    )
                )
            }
        }
        if (!GameSettings.isKeyDown(mc.gameSettings.keyBindRight)) mc.gameSettings.keyBindRight.pressed = false
        if (!GameSettings.isKeyDown(mc.gameSettings.keyBindLeft)) mc.gameSettings.keyBindLeft.pressed = false
        lockRotation = null
        lookupRotation = null
        mc.timer.timerSpeed = 1f
        faceBlock = false
        if (slot != mc.thePlayer.inventory.currentItem)
            mc.netHandler.addToSendQueue(C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem))
        if (lastSlot != mc.thePlayer.inventory.currentItem) {
            mc.thePlayer.inventory.currentItem = lastSlot
            mc.playerController.updateController()
        }
    }

    /**
     * Entity movement event
     *
     * @param event
     */
    @EventTarget
    fun onMove(event: MoveEvent) {
        if (!safeWalkValue.get()) return
        if (airSafeValue.get() || mc.thePlayer.onGround) event.isSafeWalk = true
    }

    @EventTarget
    fun onJump(event: JumpEvent) {
        if (blocksAmount <= 0 || RotationUtils.targetRotation == null) return
        if (Launch.moduleManager.getModule(SilentRotations::class.java)?.state!! && !Launch.moduleManager.getModule(
                SilentRotations::class.java
            )?.customStrafe?.get()!!
        ) {
            if (Launch.moduleManager.getModule(SilentRotations::class.java)?.state!! && !Launch.moduleManager.getModule(
                    SilentRotations::class.java
                )?.customStrafe?.get()!!
            ) {
                event.yaw = RotationUtils.targetRotation?.yaw!! - 180f
            }
        }
        if (canTower) event.cancelEvent()
    }

    @EventTarget
    fun onWorld(event: WorldEvent) {
        faceBlock = false
        startPlaceTimer.reset()
    }

    /**
     * Scaffold visuals
     *
     * @param event
     */
    @EventTarget
    fun onRender2D(event: Render2DEvent) {
        val scaledResolution = ScaledResolution(mc)
        val counter = "$blocksAmount Blocks"
        val infoWidth = FontLoaders.SF20.getStringWidth(counter)
        FontLoaders.SF20.drawStringWithShadow(
            counter,
            (scaledResolution.scaledWidth / 2 - infoWidth + 21).toDouble(),
            (scaledResolution.scaledHeight / 2 - 30).toDouble(),
            -0x1111111
        )
    }

    /**
     * Search for placeable block
     *
     * @param blockPosition pos
     * @param checks        visible
     * @return
     */
    private fun search(blockPosition: BlockPos): Boolean {
        if (!isReplaceable(blockPosition)) return false
        val eyesPos = Vec3(
            mc.thePlayer.posX,
            mc.thePlayer.entityBoundingBox.minY + mc.thePlayer.getEyeHeight(),
            mc.thePlayer.posZ
        )
        var placeRotation: PlaceRotation? = null
        for (side in EnumFacing.VALUES) {
            val neighbor = blockPosition.offset(side)
            if (!canBeClicked(neighbor)) continue
            val dirVec = Vec3(side.directionVec)
            var xSearch = 0.1
            while (xSearch < 0.9) {
                var ySearch = 0.1
                while (ySearch < 0.9) {
                    var zSearch = 0.1
                    while (zSearch < 0.9) {
                        val posVec = Vec3(blockPosition).addVector(xSearch, ySearch, zSearch)
                        val distanceSqPosVec = eyesPos.squareDistanceTo(posVec)
                        val hitVec = posVec.add(Vec3(dirVec.xCoord * 0.5, dirVec.yCoord * 0.5, dirVec.zCoord * 0.5))
                        if ((eyesPos.squareDistanceTo(hitVec) > 18.0 || distanceSqPosVec > eyesPos.squareDistanceTo(
                                posVec.add(dirVec)
                            ) || mc.theWorld.rayTraceBlocks(eyesPos, hitVec, false, true, false) != null)
                        ) {
                            zSearch += 0.1
                            continue
                        }

                        // face block
                        for (i in 0 until if (rotationModeValue.get().equals("Smooth", ignoreCase = true)) 2 else 1) {
                            val diffX: Double = if (rotationModeValue.get()
                                    .equals("Smooth", ignoreCase = true) && i == 0
                            ) 0.0 else hitVec.xCoord - eyesPos.xCoord
                            val diffY = hitVec.yCoord - eyesPos.yCoord
                            val diffZ: Double = if (rotationModeValue.get()
                                    .equals("Smooth", ignoreCase = true) && i == 1
                            ) 0.0 else hitVec.zCoord - eyesPos.zCoord
                            val diffXZ = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ).toDouble()
                            val rotation = Rotation(
                                MathHelper.wrapAngleTo180_float(
                                    Math.toDegrees(atan2(diffZ, diffX)).toFloat() - 90f
                                ),
                                MathHelper.wrapAngleTo180_float(-Math.toDegrees(atan2(diffY, diffXZ)).toFloat())
                            )
                            lookupRotation = rotation
                            val rotationVector = rotation.let { RotationUtils.getVectorForRotation(it) }
                            val vector = eyesPos.addVector(
                                rotationVector.xCoord * 4,
                                rotationVector.yCoord * 4,
                                rotationVector.zCoord * 4
                            )
                            val obj = mc.theWorld.rayTraceBlocks(eyesPos, vector, false, false, true)
                            if (!(obj.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && obj.blockPos == neighbor)) continue
                            if (placeRotation == null || RotationUtils.getRotationDifference(rotation) < RotationUtils.getRotationDifference(
                                    placeRotation.rotation
                                )
                            ) placeRotation = PlaceRotation(PlaceInfo(neighbor, side.opposite, hitVec), rotation)
                        }
                        zSearch += 0.1
                    }
                    ySearch += 0.1
                }
                xSearch += 0.1
            }
        }
        if (placeRotation == null) {
            faceBlock = false
            return false
        }
        val limitedRotation = RotationUtils.limitAngleChange(
            RotationUtils.serverRotation!!,
            placeRotation.rotation,
            RandomUtils.nextFloat(minTurnSpeed.get(), maxTurnSpeed.get())
        )
        if ((10 * MathHelper.wrapAngleTo180_float(limitedRotation.yaw)).toInt() == (10 * MathHelper.wrapAngleTo180_float(
                placeRotation.rotation.yaw
            )).toInt()
            && (10 * MathHelper.wrapAngleTo180_float(limitedRotation.pitch)).toInt() == (10 * MathHelper.wrapAngleTo180_float(
                placeRotation.rotation.pitch
            )).toInt()
        ) {
            RotationUtils.setTargetRotation(placeRotation.rotation, 0)
            lockRotation = placeRotation.rotation
            faceBlock = true
        } else {
            RotationUtils.setTargetRotation(limitedRotation, 0)
            lockRotation = limitedRotation
        }
        lookupRotation = lockRotation
        targetPlace = placeRotation.placeInfo
        return true
    }

    private val blocksAmount: Int
        /**
         * @return hotbar blocks amount
         */
        get() {
            var amount = 0
            for (i in 36..44) {
                val itemStack = mc.thePlayer.inventoryContainer.getSlot(i).stack
                if (itemStack != null && itemStack.item is ItemBlock) {
                    val block = (itemStack.item as ItemBlock).getBlock()
                    if (!InventoryUtils.BLOCK_BLACKLIST.contains(block) && block.isFullCube) amount += itemStack.stackSize
                }
            }
            return amount
        }
}
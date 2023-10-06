package net.aspw.client.features.module.impl.movement

import net.aspw.client.Client
import net.aspw.client.event.*
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.util.*
import net.aspw.client.util.render.RenderUtils
import net.aspw.client.util.timer.MSTimer
import net.aspw.client.util.timer.TickTimer
import net.aspw.client.value.BoolValue
import net.aspw.client.value.FloatValue
import net.aspw.client.value.IntegerValue
import net.aspw.client.value.ListValue
import net.aspw.client.visual.hud.element.elements.Notification
import net.minecraft.block.BlockAir
import net.minecraft.block.BlockSlime
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.settings.GameSettings
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemEnderPearl
import net.minecraft.network.Packet
import net.minecraft.network.play.INetHandlerPlayServer
import net.minecraft.network.play.client.*
import net.minecraft.network.play.client.C03PacketPlayer.*
import net.minecraft.network.play.server.S08PacketPlayerPosLook
import net.minecraft.potion.Potion
import net.minecraft.util.AxisAlignedBB
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing
import net.minecraft.util.Vec3
import java.util.*
import java.util.concurrent.LinkedBlockingQueue
import javax.vecmath.Vector2f
import kotlin.math.*

@ModuleInfo(name = "Flight", description = "", category = ModuleCategory.MOVEMENT)
class Flight : Module() {
    @JvmField
    val modeValue = ListValue(
        "Mode", arrayOf(
            "Motion",
            "NoClip",
            "Creative",
            "Vanilla",
            "Water",
            "Pearl",
            "Packet",
            "Desync",
            "LatestNCP",
            "Cubecraft",
            "NCP",
            "AAC1.9.10",
            "AAC3.0.5",
            "AAC3.1.6",
            "AAC3.3.12",
            "AAC3.3.13",
            "AAC5-Vanilla",
            "Exploit",
            "Zoom",
            "BlockDrop",
            "Jump",
            "FakeGround",
            "Minemora",
            "Sentinel",
            "Funcraft",
            "NeruxVace",
            "Verus",
            "VerusLowHop",
            "Matrix",
            "VulcanZoom",
            "VulcanFast",
            "VulcanClip",
            "VulcanGlide",
            "NewSpartan",
            "OldSpartan",
            "BugSpartan",
            "Slime",
            "Float",
            "Jetpack",
            "KeepAlive",
            "Flag",
            "Clip"
        ), "Motion"
    )
    private val vanillaSpeedValue = FloatValue("Speed", 1f, 0f, 5f) {
        modeValue.get().equals("motion", ignoreCase = true) || modeValue.get()
            .equals("noclip", ignoreCase = true) || modeValue.get()
            .equals("blockdrop", ignoreCase = true) || modeValue.get()
            .equals("desync", ignoreCase = true) || modeValue.get()
            .equals("pearl", ignoreCase = true) || modeValue.get()
            .equals("aac5-vanilla", ignoreCase = true) || modeValue.get()
            .equals("bugspartan", ignoreCase = true) || modeValue.get()
            .equals("keepalive", ignoreCase = true)
    }
    private val vanillaVSpeedValue = FloatValue("V-Speed", 0.6f, 0f, 5f) {
        modeValue.get().equals("motion", ignoreCase = true) || modeValue.get()
            .equals("noclip", ignoreCase = true) || modeValue.get()
            .equals("blockdrop", ignoreCase = true) || modeValue.get()
            .equals("desync", ignoreCase = true) || modeValue.get()
            .equals("bugspartan", ignoreCase = true) || modeValue.get().equals("keepalive", ignoreCase = true)
    }
    private val vanillaMotionYValue = FloatValue("Y-Motion", 0f, -1f, 1f) {
        modeValue.get().equals("motion", ignoreCase = true) || modeValue.get()
            .equals("noclip", ignoreCase = true)
    }
    private val groundSpoofValue = BoolValue("SpoofGround", false) {
        modeValue.get().equals("motion", ignoreCase = true) || modeValue.get()
            .equals("noclip", ignoreCase = true) || modeValue.get().equals("creative", ignoreCase = true)
    }
    private val ncpMotionValue =
        FloatValue("NCPMotion", 0.04f, 0f, 1f) { modeValue.get().equals("ncp", ignoreCase = true) }

    // Verus
    private val verusDmgModeValue =
        ListValue("Verus-DamageMode", arrayOf("None", "Instant", "InstantC06", "Jump"), "Instant") {
            modeValue.get().equals("verus", ignoreCase = true)
        }
    private val verusBoostModeValue = ListValue("Verus-BoostMode", arrayOf("Static", "Gradual"), "Gradual") {
        modeValue.get().equals("verus", ignoreCase = true) && !verusDmgModeValue.get().equals("none", ignoreCase = true)
    }
    private val verusReDamageValue = BoolValue("Verus-ReDamage", false) {
        modeValue.get().equals("verus", ignoreCase = true) && !verusDmgModeValue.get()
            .equals("none", ignoreCase = true) && !verusDmgModeValue.get().equals("jump", ignoreCase = true)
    }
    private val verusReDmgTickValue = IntegerValue("Verus-ReDamage-Ticks", 100, 0, 300) {
        modeValue.get().equals("verus", ignoreCase = true) && !verusDmgModeValue.get()
            .equals("none", ignoreCase = true) && !verusDmgModeValue.get()
            .equals("jump", ignoreCase = true) && verusReDamageValue.get()
    }
    private val verusSpeedValue = FloatValue("Verus-Speed", 1f, 0f, 10f) {
        modeValue.get().equals("verus", ignoreCase = true) && !verusDmgModeValue.get().equals("none", ignoreCase = true)
    }
    private val verusTimerValue = FloatValue("Verus-Timer", 1f, 0.1f, 10f) {
        modeValue.get().equals("verus", ignoreCase = true) && !verusDmgModeValue.get().equals("none", ignoreCase = true)
    }
    private val verusDmgTickValue = IntegerValue("Verus-Ticks", 200, 0, 300) {
        modeValue.get().equals("verus", ignoreCase = true) && !verusDmgModeValue.get().equals("none", ignoreCase = true)
    }
    private val verusVisualValue =
        BoolValue("Verus-VisualPos", true) { modeValue.get().equals("verus", ignoreCase = true) }
    private val verusVisualHeightValue = FloatValue("Verus-VisualHeight", 0.3f, 0f, 1f) {
        modeValue.get().equals("verus", ignoreCase = true) && verusVisualValue.get()
    }
    private val verusSpoofGround =
        BoolValue("Verus-SpoofGround", true) { modeValue.get().equals("verus", ignoreCase = true) }

    // Vulcan
    private val timerValue =
        FloatValue("VulcanFast-Timer", 3f, 1f, 3f) { modeValue.get().equals("vulcanfast", ignoreCase = true) }

    // Matrix
    private val bypassMode = ListValue("BypassMode", arrayOf("New", "Stable", "High", "Custom"), "New") {
        modeValue.get().equals("matrix", ignoreCase = true)
    }
    private val speed =
        FloatValue("BoostSpeed", 2.0f, 1.0f, 3.0f) { modeValue.get().equals("matrix", ignoreCase = true) }
    private val customYMotion = FloatValue("CustomJumpMotion", 0.6f, 0.2f, 5f) {
        modeValue.get().equals("matrix", ignoreCase = true) && bypassMode.equals("Custom")
    }
    private val jumpTimer =
        FloatValue("JumpTimer", 0.1f, 0.1f, 2f) { modeValue.get().equals("matrix", ignoreCase = true) }
    private val speedTimer =
        FloatValue("BoostTimer", 1f, 0.5f, 3f) { modeValue.get().equals("matrix", ignoreCase = true) }

    // AAC
    private val aac5NofallValue =
        BoolValue("AAC5-NoFall", true) { modeValue.get().equals("aac5-vanilla", ignoreCase = true) }
    private val aac5UseC04Packet =
        BoolValue("AAC5-UseC04", true) { modeValue.get().equals("aac5-vanilla", ignoreCase = true) }
    private val aac5Packet = ListValue("AAC5-Packet", arrayOf("Original", "Rise", "Other"), "Original") {
        modeValue.get().equals("aac5-vanilla", ignoreCase = true)
    }
    private val aac5PursePacketsValue =
        IntegerValue("AAC5-Purse", 7, 3, 20) { modeValue.get().equals("aac5-vanilla", ignoreCase = true) }
    private val clipDelay =
        IntegerValue("Clip-DelayTick", 25, 1, 50) { modeValue.get().equals("clip", ignoreCase = true) }
    private val clipH = FloatValue("Clip-Horizontal", 8f, 0f, 10f) { modeValue.get().equals("clip", ignoreCase = true) }
    private val clipV =
        FloatValue("Clip-Vertical", -1.75f, -10f, 10f) { modeValue.get().equals("clip", ignoreCase = true) }
    private val clipMotionY =
        FloatValue("Clip-MotionY", 0f, -2f, 2f) { modeValue.get().equals("clip", ignoreCase = true) }
    private val clipTimer =
        FloatValue("Clip-Timer", 1f, 0.08f, 10f) { modeValue.get().equals("clip", ignoreCase = true) }
    private val clipGroundSpoof =
        BoolValue("Clip-GroundSpoof", true) { modeValue.get().equals("clip", ignoreCase = true) }
    private val clipCollisionCheck =
        BoolValue("Clip-CollisionCheck", false) { modeValue.get().equals("clip", ignoreCase = true) }
    private val clipNoMove = BoolValue("Clip-NoMove", true) { modeValue.get().equals("clip", ignoreCase = true) }

    // Pearl
    private val pearlActivateCheck = ListValue("PearlActiveCheck", arrayOf("Teleport", "Damage"), "Teleport") {
        modeValue.get().equals("pearl", ignoreCase = true)
    }

    // AAC
    private val aacSpeedValue =
        FloatValue("AAC1.9.10-Speed", 0.3f, 0f, 1f) { modeValue.get().equals("aac1.9.10", ignoreCase = true) }
    private val aacFast = BoolValue("AAC3.0.5-Fast", false) { modeValue.get().equals("aac3.0.5", ignoreCase = true) }
    private val aacMotion =
        FloatValue("AAC3.3.12-Motion", 8f, 0.1f, 10f) { modeValue.get().equals("aac3.3.12", ignoreCase = true) }
    private val aacMotion2 =
        FloatValue("AAC3.3.13-Motion", 8f, 0.1f, 10f) { modeValue.get().equals("aac3.3.13", ignoreCase = true) }

    private val neruxVaceTicks =
        IntegerValue("NeruxVace-Ticks", 6, 0, 20) { modeValue.get().equals("neruxvace", ignoreCase = true) }
    private val fakeSprintingValue =
        BoolValue("FakeSprinting", false) { modeValue.get().lowercase(Locale.getDefault()).contains("exploit") }
    private val fakeNoMoveValue =
        BoolValue("FakeNoMove", false) { modeValue.get().lowercase(Locale.getDefault()).contains("exploit") }

    // Visuals
    private val fakeDmgValue = BoolValue("FakeDamage", false)
    val fakeYValue = BoolValue("FakeY", false)
    private val viewBobbingValue = BoolValue("ViewBobbing", false)
    private val bobbingAmountValue = FloatValue("BobbingAmount", 0.1f, 0f, 0.1f) { viewBobbingValue.get() }
    private val flyTimer = MSTimer()
    private val boostTimer = MSTimer()
    private val spartanTimer = TickTimer()
    private val verusTimer = TickTimer()
    private val cubecraftTeleportTickTimer = TickTimer()
    private val cubecraftTeleportYTickTimer = TickTimer()
    private val cubecraftTeleportYDownTickTimer = TickTimer()
    private val aac5C03List = ArrayList<C03PacketPlayer>()
    private var tick = 0
    private var boost = false
    private var boostGround = false
    private var disableLogger = false
    private val packetBuffer = LinkedBlockingQueue<Packet<INetHandlerPlayServer>>()
    private var wdState = 0
    private var wdTick = 0
    private var fly = false
    var y = 0.0
    private val timer = MSTimer()
    private val packetLol = LinkedList<C0FPacketConfirmTransaction>()
    private var flag = false
    private var startY = 0.0
    private var shouldFakeJump = false
    private var tickso = 0
    private var modifyTicks = 0
    private var stage = FlyStage.WAITING
    private var flags = 0
    private var groundX = 0.0
    private var groundY = 0.0
    private var groundZ = 0.0
    private var shouldActive = false
    private var noPacketModify = false
    private var isBoostActive = false
    private var noFlag = false
    private var boostMotion = 0
    private var pog = false
    private var started = false
    private var lastSentX = 0.0
    private var lastSentY = 0.0
    private var lastSentZ = 0.0
    private var c = false
    private var startVec: Vec3? = null
    private var rotationVec: Vector2f? = null
    private var pearlState = 0
    private var bypassValue = 0.0
    private var wasDead = false
    private var boostTicks = 0
    private var dmgCooldown = 0
    private var verusJumpTimes = 0
    private var verusDmged = false
    private var shouldActiveDmg = false
    private var lastYaw = 0f
    private var lastPitch = 0f
    private var moveSpeed = 0.0
    private var waitFlag = false
    private var canGlide = false
    private var ticks = 0
    private var expectItemStack = -1
    private var aacJump = 0.0
    private var aac3delay = 0
    private var aac3glideDelay = 0

    private fun isInventory(action: Short): Boolean = action in 1..99

    private fun hClip(x: Double, y: Double, z: Double) {
        if (mc.thePlayer == null) return
        val expectedX = mc.thePlayer.posX + x
        val expectedY = mc.thePlayer.posY + y
        val expectedZ = mc.thePlayer.posZ + z
        mc.netHandler.addToSendQueue(C04PacketPlayerPosition(expectedX, expectedY, expectedZ, mc.thePlayer.onGround))
        mc.thePlayer.setPosition(expectedX, expectedY, expectedZ)
    }

    private fun getMoves(h: Double, v: Double): DoubleArray {
        if (mc.thePlayer == null) return doubleArrayOf(0.0, 0.0, 0.0)
        val yaw = Math.toRadians(mc.thePlayer.rotationYaw.toDouble())
        val expectedX = -sin(yaw) * h
        val expectedZ = cos(yaw) * h
        return doubleArrayOf(expectedX, v, expectedZ)
    }

    override fun onEnable() {
        if (mc.thePlayer == null) return
        noPacketModify = true
        verusTimer.reset()
        flyTimer.reset()
        bypassValue = 0.0
        packetLol.clear()
        y = mc.thePlayer.posY
        shouldFakeJump = false
        boostMotion = 0
        shouldActive = true
        fly = false
        isBoostActive = false
        expectItemStack = -1
        val x = mc.thePlayer.posX
        val y = mc.thePlayer.posY
        val z = mc.thePlayer.posZ
        lastYaw = mc.thePlayer.rotationYaw
        lastPitch = mc.thePlayer.rotationPitch
        val mode = modeValue.get()
        boostTicks = 0
        dmgCooldown = 0
        pearlState = 0
        flag = false
        timer.reset()
        verusJumpTimes = 0
        verusDmged = false
        moveSpeed = 0.0
        wdState = 0
        wdTick = 0
        when (mode.lowercase(Locale.getDefault())) {
            "ncp" -> {
                mc.thePlayer.motionY = -ncpMotionValue.get().toDouble()
                if (mc.gameSettings.keyBindSneak.isKeyDown) mc.thePlayer.motionY = -0.5
                MovementUtils.strafe()
            }

            "vulcanzoom" -> {
                pog = false
                lastSentX = mc.thePlayer.posX
                lastSentY = mc.thePlayer.posY
                lastSentZ = mc.thePlayer.posZ
                started = false
                mc.thePlayer.motionX = 0.0
                mc.thePlayer.motionZ = 0.0
                mc.thePlayer.jumpMovementFactor = 0.00f
                if (mc.thePlayer.onGround) {
                    mc.thePlayer.onGround = false
                    mc.timer.timerSpeed = 0.1f
                    started = true
                    PacketUtils.sendPacketNoEvent(
                        C04PacketPlayerPosition(
                            mc.thePlayer.posX,
                            mc.thePlayer.posY,
                            mc.thePlayer.posZ,
                            true
                        )
                    )
                    PacketUtils.sendPacketNoEvent(
                        C04PacketPlayerPosition(
                            mc.thePlayer.posX,
                            mc.thePlayer.posY - 2.8 + Math.random() / 50,
                            mc.thePlayer.posZ,
                            false
                        )
                    )
                    PacketUtils.sendPacketNoEvent(
                        C04PacketPlayerPosition(
                            mc.thePlayer.posX,
                            mc.thePlayer.posY,
                            mc.thePlayer.posZ,
                            true
                        )
                    )
                }
            }

            "vulcanfast" -> {
                tickso = 0
                modifyTicks = 0
                flags = 0
                mc.thePlayer.setPosition(
                    mc.thePlayer.posX,
                    (mc.thePlayer.posY * 2).roundToInt().toDouble() / 2,
                    mc.thePlayer.posZ
                )
                stage = FlyStage.WAITING
                ClientUtils.displayChatMessage(Client.CLIENT_CHAT + "§aPlease press sneak before you land on ground!")
                ClientUtils.displayChatMessage(Client.CLIENT_CHAT + "§aYou can go Up/Down by pressing Jump/Sneak")
            }

            "minemora" -> {
                boostGround = !mc.thePlayer.onGround
                boost = false
                tick = 0
                mc.gameSettings.keyBindJump.pressed = false
                mc.gameSettings.keyBindSneak.pressed = false
            }

            "vulcanclip" -> {
                if (mc.thePlayer.onGround) {
                    clip(0f, -0.1f)
                    waitFlag = true
                    canGlide = false
                    ticks = 0
                    mc.timer.timerSpeed = 0.1f
                } else {
                    waitFlag = false
                    canGlide = true
                }
            }

            "blockdrop" -> {
                startVec = Vec3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ)
                rotationVec = Vector2f(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch)
            }

            "verus" -> {
                if (verusDmgModeValue.get().equals("Instant", ignoreCase = true)) {
                    if (mc.thePlayer.onGround && mc.theWorld.getCollidingBoundingBoxes(
                            mc.thePlayer,
                            mc.thePlayer.entityBoundingBox.offset(0.0, 4.0, 0.0).expand(0.0, 0.0, 0.0)
                        ).isEmpty()
                    ) {
                        PacketUtils.sendPacketNoEvent(
                            C04PacketPlayerPosition(
                                mc.thePlayer.posX,
                                y + 4,
                                mc.thePlayer.posZ,
                                false
                            )
                        )
                        PacketUtils.sendPacketNoEvent(
                            C04PacketPlayerPosition(
                                mc.thePlayer.posX,
                                y,
                                mc.thePlayer.posZ,
                                false
                            )
                        )
                        PacketUtils.sendPacketNoEvent(
                            C04PacketPlayerPosition(
                                mc.thePlayer.posX,
                                y,
                                mc.thePlayer.posZ,
                                true
                            )
                        )
                        mc.thePlayer.motionZ = 0.0
                        mc.thePlayer.motionX = mc.thePlayer.motionZ
                        if (verusReDamageValue.get()) dmgCooldown = verusReDmgTickValue.get()
                    }
                } else if (verusDmgModeValue.get().equals("InstantC06", ignoreCase = true)) {
                    if (mc.thePlayer.onGround && mc.theWorld.getCollidingBoundingBoxes(
                            mc.thePlayer,
                            mc.thePlayer.entityBoundingBox.offset(0.0, 4.0, 0.0).expand(0.0, 0.0, 0.0)
                        ).isEmpty()
                    ) {
                        PacketUtils.sendPacketNoEvent(
                            C06PacketPlayerPosLook(
                                mc.thePlayer.posX,
                                y + 4,
                                mc.thePlayer.posZ,
                                mc.thePlayer.rotationYaw,
                                mc.thePlayer.rotationPitch,
                                false
                            )
                        )
                        PacketUtils.sendPacketNoEvent(
                            C06PacketPlayerPosLook(
                                mc.thePlayer.posX,
                                y,
                                mc.thePlayer.posZ,
                                mc.thePlayer.rotationYaw,
                                mc.thePlayer.rotationPitch,
                                false
                            )
                        )
                        PacketUtils.sendPacketNoEvent(
                            C06PacketPlayerPosLook(
                                mc.thePlayer.posX,
                                y,
                                mc.thePlayer.posZ,
                                mc.thePlayer.rotationYaw,
                                mc.thePlayer.rotationPitch,
                                true
                            )
                        )
                        mc.thePlayer.motionZ = 0.0
                        mc.thePlayer.motionX = mc.thePlayer.motionZ
                        if (verusReDamageValue.get()) dmgCooldown = verusReDmgTickValue.get()
                    }
                } else if (verusDmgModeValue.get().equals("Jump", ignoreCase = true)) {
                    if (mc.thePlayer.onGround) {
                        mc.thePlayer.jump()
                        verusJumpTimes = 1
                    }
                } else {
                    // set dmged = true since there's no damage method
                    verusDmged = true
                }
                if (verusVisualValue.get()) mc.thePlayer.setPosition(
                    mc.thePlayer.posX,
                    y + verusVisualHeightValue.get(),
                    mc.thePlayer.posZ
                )
                shouldActiveDmg = dmgCooldown > 0
            }

            "bugspartan" -> {
                var i = 0
                while (i < 65) {
                    mc.netHandler.addToSendQueue(C04PacketPlayerPosition(x, y + 0.049, z, false))
                    mc.netHandler.addToSendQueue(C04PacketPlayerPosition(x, y, z, false))
                    ++i
                }
                mc.netHandler.addToSendQueue(C04PacketPlayerPosition(x, y + 0.1, z, true))
                mc.thePlayer.motionX *= 0.1
                mc.thePlayer.motionZ *= 0.1
                mc.netHandler.addToSendQueue(C0APacketAnimation())
            }

            "funcraft" -> {
                moveSpeed = if (mc.thePlayer.onGround) {
                    mc.thePlayer.jump()
                    1.61
                } else {
                    0.25
                }
            }

            "zoom" -> {
                val awax = mc.thePlayer.posX
                val away = mc.thePlayer.posY
                val awaz = mc.thePlayer.posZ

                repeat(65 * 1) {
                    mc.netHandler.addToSendQueue(C04PacketPlayerPosition(awax, away + 0.049, awaz, false))
                    mc.netHandler.addToSendQueue(C04PacketPlayerPosition(awax, away, awaz, false))
                }
                mc.netHandler.addToSendQueue(C04PacketPlayerPosition(awax, away, awaz, true))
                if (mc.thePlayer.onGround) mc.thePlayer.jump()
                moveSpeed = 2.0
            }

            "slime" -> {
                expectItemStack = slimeSlot
                if (expectItemStack == -1) {
                    Client.hud.addNotification(Notification("The fly requires slime blocks to be activated properly."))
                }
                if (mc.thePlayer.onGround) {
                    mc.thePlayer.jump()
                    wdState = 1
                }
            }
        }
        startY = mc.thePlayer.posY
        noPacketModify = false
        aacJump = -3.8
        if (fakeDmgValue.get() && (!mode.equals("slime", ignoreCase = true) && !mode.equals(
                "exploit",
                ignoreCase = true
            )
                    && !mode.equals("bugspartan", ignoreCase = true) && !mode.equals(
                "verus",
                ignoreCase = true
            ))
        ) {
            mc.thePlayer.handleStatusUpdate(2.toByte())
        }
        super.onEnable()
    }

    override fun onDisable() {
        val speed = Client.moduleManager.getModule(
            Speed::class.java
        )
        mc.thePlayer.eyeHeight = mc.thePlayer.defaultEyeHeight
        wasDead = false
        fly = false
        c = false
        packetLol.clear()
        mc.thePlayer?.noClip = false
        if (mc.thePlayer == null) return
        noFlag = false
        if (GameSettings.isKeyDown(mc.gameSettings.keyBindSneak))
            mc.gameSettings.keyBindSneak.pressed = true
        val mode = modeValue.get()
        if (!mode.uppercase(Locale.getDefault()).startsWith("NCP") && !mode.equals(
                "float",
                ignoreCase = true
            ) && !mode.equals("vanilla", ignoreCase = true) && !mode.equals(
                "creative",
                ignoreCase = true
            ) && !mode.equals(
                "water",
                ignoreCase = true
            ) && !mode.equals("vanilla", ignoreCase = true) && !mode.equals(
                "newspartan",
                ignoreCase = true
            ) && !mode.equals(
                "aac1.9.10",
                ignoreCase = true
            ) && !mode.equals(
                "aac3.3.12",
                ignoreCase = true
            ) && !mode.equals(
                "neruxvace",
                ignoreCase = true
            ) && !mode.equals(
                "jump",
                ignoreCase = true
            ) && !mode.equals(
                "fakeground",
                ignoreCase = true
            ) && !mode.equals(
                "oldspartan",
                ignoreCase = true
            ) && !mode.equals("jetpack", ignoreCase = true) && !mode.equals(
                "clip",
                ignoreCase = true
            ) && !mode.equals("vulcanglide", ignoreCase = true) && !mode.equals(
                "matrix",
                ignoreCase = true
            )
        ) {
            if (speed?.state!!) {
                mc.thePlayer.motionY = 0.0
            } else {
                mc.thePlayer.motionX = 0.0
                mc.thePlayer.motionY = 0.0
                mc.thePlayer.motionZ = 0.0
            }
        }
        when (mode.lowercase()) {
            "minemora" -> {
                tick = 0
                try {
                    disableLogger = true
                    while (!packetBuffer.isEmpty()) {
                        mc.netHandler.addToSendQueue(packetBuffer.take())
                    }
                    disableLogger = false
                } finally {
                    disableLogger = false
                }
            }

            "aac5-vanilla" -> {
                if (!mc.isIntegratedServerRunning)
                    sendAAC5Packets()
            }

            "vulcanfast" -> {
                mc.netHandler.addToSendQueue(
                    C0BPacketEntityAction(
                        mc.thePlayer,
                        C0BPacketEntityAction.Action.STOP_SNEAKING
                    )
                )
            }
        }
        mc.thePlayer.capabilities.isFlying = false
        mc.thePlayer.capabilities.allowFlying = false
        if (mc.thePlayer.capabilities.isCreativeMode)
            mc.thePlayer.capabilities.allowFlying = true
        mc.timer.timerSpeed = 1f
    }

    @EventTarget
    fun onWorld(event: WorldEvent) {
        packetLol.clear()
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent?) {
        val vanillaSpeed = vanillaSpeedValue.get()
        val vanillaVSpeed = vanillaVSpeedValue.get()
        mc.thePlayer.noClip = false
        when (modeValue.get().lowercase(Locale.getDefault())) {
            "vulcanzoom" -> {
                if (started && pog) {
                    mc.gameSettings.keyBindJump.pressed = false
                    mc.gameSettings.keyBindSneak.pressed = false
                    MovementUtils.strafe((1.96 + Math.random() / 50).toFloat())
                    if (mc.thePlayer.onGround) {
                        mc.thePlayer.motionX = 0.0
                        mc.thePlayer.motionZ = 0.0
                        mc.thePlayer.motionY = 0.2
                    }
                    if (!MovementUtils.isMoving()) {
                        mc.thePlayer.motionX = 0.0
                        mc.thePlayer.motionZ = 0.0
                    }
                    mc.thePlayer.jumpMovementFactor = 0.00f
                }
            }

            "vulcanfast" -> {
                tickso++
                modifyTicks++
                mc.gameSettings.keyBindJump.pressed = false
                mc.gameSettings.keyBindSneak.pressed = false
                when (stage) {
                    FlyStage.FLYING, FlyStage.WAITING -> {
                        if (stage == FlyStage.FLYING) {
                            mc.timer.timerSpeed = timerValue.get()
                        } else {
                            mc.timer.timerSpeed = 1.0f
                        }
                        if (tickso == 2 && GameSettings.isKeyDown(mc.gameSettings.keyBindJump) && modifyTicks >= 6 && mc.theWorld.getCollisionBoxes(
                                mc.thePlayer.entityBoundingBox.offset(0.0, 0.5, 0.0)
                            ).isEmpty()
                        ) {
                            mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.5, mc.thePlayer.posZ)
                            modifyTicks = 0
                        }
                        if (!MovementUtils.isMoving() && tickso == 1 && (GameSettings.isKeyDown(mc.gameSettings.keyBindSneak) || GameSettings.isKeyDown(
                                mc.gameSettings.keyBindJump
                            )) && modifyTicks >= 5
                        ) {
                            val playerYaw = mc.thePlayer.rotationYaw * Math.PI / 180
                            mc.thePlayer.setPosition(
                                mc.thePlayer.posX + 0.05 * -sin(playerYaw),
                                mc.thePlayer.posY,
                                mc.thePlayer.posZ + 0.05 * cos(playerYaw)
                            )
                        }
                        if (tickso == 2 && GameSettings.isKeyDown(mc.gameSettings.keyBindSneak) && modifyTicks >= 6 && mc.theWorld.getCollisionBoxes(
                                mc.thePlayer.entityBoundingBox.offset(0.0, -0.5, 0.0)
                            ).isEmpty()
                        ) {
                            mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - 0.5, mc.thePlayer.posZ)
                            modifyTicks = 0
                        } else if (tickso == 2 && GameSettings.isKeyDown(mc.gameSettings.keyBindSneak) && mc.theWorld.getCollisionBoxes(
                                mc.thePlayer.entityBoundingBox.offset(0.0, -0.5, 0.0)
                            )
                                .isNotEmpty()
                        ) {
                            PacketUtils.sendPacketNoEvent(
                                C04PacketPlayerPosition(
                                    mc.thePlayer.posX + 0.05,
                                    mc.thePlayer.posY,
                                    mc.thePlayer.posZ,
                                    true
                                )
                            )
                            PacketUtils.sendPacketNoEvent(
                                C04PacketPlayerPosition(
                                    mc.thePlayer.posX,
                                    mc.thePlayer.posY,
                                    mc.thePlayer.posZ,
                                    true
                                )
                            )
                            PacketUtils.sendPacketNoEvent(
                                C04PacketPlayerPosition(
                                    mc.thePlayer.posX,
                                    mc.thePlayer.posY + 0.42,
                                    mc.thePlayer.posZ,
                                    true
                                )
                            )
                            PacketUtils.sendPacketNoEvent(
                                C04PacketPlayerPosition(
                                    mc.thePlayer.posX,
                                    mc.thePlayer.posY + 0.7532,
                                    mc.thePlayer.posZ,
                                    true
                                )
                            )
                            PacketUtils.sendPacketNoEvent(
                                C04PacketPlayerPosition(
                                    mc.thePlayer.posX,
                                    mc.thePlayer.posY + 1.0,
                                    mc.thePlayer.posZ,
                                    true
                                )
                            )
                            mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 1.0, mc.thePlayer.posZ)
                            stage = FlyStage.WAIT_APPLY
                            modifyTicks = 0
                            groundY = mc.thePlayer.posY - 1.0
                            groundX = mc.thePlayer.posX
                            groundZ = mc.thePlayer.posZ
                            ClientUtils.displayChatMessage(Client.CLIENT_CHAT + "§aWaiting to land...")
                        }
                        mc.thePlayer.onGround = true
                        mc.thePlayer.motionY = 0.0
                    }

                    FlyStage.WAIT_APPLY -> {
                        mc.timer.timerSpeed = 1.0f
                        mc.thePlayer.motionX = 0.0
                        mc.thePlayer.motionZ = 0.0
                        mc.thePlayer.jumpMovementFactor = 0.0f
                        if (modifyTicks >= 10) {
                            val playerYaw = mc.thePlayer.rotationYaw * Math.PI / 180
                            if (modifyTicks % 2 != 0) {
                                mc.thePlayer.setPosition(
                                    mc.thePlayer.posX + 0.1 * -sin(playerYaw),
                                    mc.thePlayer.posY,
                                    mc.thePlayer.posZ + 0.1 * cos(playerYaw)
                                )
                            } else {
                                mc.thePlayer.setPosition(
                                    mc.thePlayer.posX - 0.1 * -sin(playerYaw),
                                    mc.thePlayer.posY,
                                    mc.thePlayer.posZ - 0.1 * cos(playerYaw)
                                )
                                if (modifyTicks >= 16 && tickso == 2) {
                                    modifyTicks = 16
                                    mc.thePlayer.setPosition(
                                        mc.thePlayer.posX, mc.thePlayer.posY + 0.5, mc.thePlayer.posZ
                                    )
                                }
                            }
                        }
                    }
                }
            }

            "matrix" -> {
                if (boostMotion == 0) {
                    val yaw = Math.toRadians(mc.thePlayer.rotationYaw.toDouble())
                    mc.netHandler.addToSendQueue(
                        C04PacketPlayerPosition(
                            mc.thePlayer.posX,
                            mc.thePlayer.posY,
                            mc.thePlayer.posZ,
                            true
                        )
                    )
                    if (bypassMode.equals("High")) {
                        MovementUtils.strafe(5f)
                        mc.thePlayer.motionY = 2.0
                    } else {
                        mc.netHandler.addToSendQueue(
                            C04PacketPlayerPosition(
                                mc.thePlayer.posX + -sin(yaw) * 1.5,
                                mc.thePlayer.posY + 1,
                                mc.thePlayer.posZ + cos(yaw) * 1.5,
                                false
                            )
                        )
                    }
                    boostMotion = 1
                    mc.timer.timerSpeed = jumpTimer.get()
                } else if (boostMotion == 1 && bypassMode.equals("High")) {
                    MovementUtils.strafe(1.89f)
                    mc.thePlayer.motionY = 2.0
                } else if (boostMotion == 2) {
                    MovementUtils.strafe(speed.get())
                    when (bypassMode.get().lowercase()) {
                        "stable" -> mc.thePlayer.motionY = 0.8
                        "new" -> mc.thePlayer.motionY = 0.48
                        "high" -> {
                            val yaw = Math.toRadians(mc.thePlayer.rotationYaw.toDouble())
                            mc.netHandler.addToSendQueue(
                                C04PacketPlayerPosition(
                                    mc.thePlayer.posX + -sin(yaw) * 2,
                                    mc.thePlayer.posY + 2.0,
                                    mc.thePlayer.posZ + cos(yaw) * 2,
                                    true
                                )
                            )
                            mc.thePlayer.motionY = 2.0
                            MovementUtils.strafe(1.89f)
                        }

                        "custom" -> mc.thePlayer.motionY = customYMotion.get().toDouble()
                    }
                    boostMotion = 3
                } else if (boostMotion < 5) {
                    boostMotion++
                } else if (boostMotion >= 5) {
                    mc.timer.timerSpeed = speedTimer.get()
                }
            }

            "motion" -> {
                mc.thePlayer.capabilities.isFlying = false
                mc.thePlayer.motionY = vanillaMotionYValue.get().toDouble()
                mc.thePlayer.motionX = 0.0
                mc.thePlayer.motionZ = 0.0
                if (GameSettings.isKeyDown(mc.gameSettings.keyBindJump)) {
                    mc.thePlayer.motionY += vanillaVSpeed.toDouble()
                }
                if (GameSettings.isKeyDown(mc.gameSettings.keyBindSneak)) {
                    mc.thePlayer.motionY -= vanillaVSpeed.toDouble()
                    mc.gameSettings.keyBindSneak.pressed = false
                }
                MovementUtils.strafe(vanillaSpeed)
            }

            "cubecraft" -> {
                mc.thePlayer.motionY = -1E-10
                mc.thePlayer.motionX = 0.0
                mc.thePlayer.motionZ = 0.0
                if (GameSettings.isKeyDown(mc.gameSettings.keyBindJump)) {
                    mc.thePlayer.motionY += 0.4f
                }
                if (GameSettings.isKeyDown(mc.gameSettings.keyBindSneak)) {
                    mc.thePlayer.motionY -= 0.4f
                    mc.gameSettings.keyBindSneak.pressed = false
                }
                MovementUtils.strafe(1f)
            }

            "veruslowhop" -> {
                if (GameSettings.isKeyDown(mc.gameSettings.keyBindSneak)) {
                    mc.gameSettings.keyBindSneak.pressed = false
                }
            }

            "fakeground" -> {
                if (GameSettings.isKeyDown(mc.gameSettings.keyBindSneak)) {
                    mc.gameSettings.keyBindSneak.pressed = false
                }
            }

            "minemora" -> {
                if (GameSettings.isKeyDown(mc.gameSettings.keyBindSneak))
                    mc.gameSettings.keyBindSneak.pressed = false
            }

            "jump" -> {
                if (mc.thePlayer.onGround)
                    mc.thePlayer.jump()
            }

            "noclip" -> {
                mc.thePlayer.capabilities.isFlying = false
                mc.thePlayer.motionY = vanillaMotionYValue.get().toDouble()
                mc.thePlayer.motionX = 0.0
                mc.thePlayer.motionZ = 0.0
                mc.thePlayer.noClip = true
                if (GameSettings.isKeyDown(mc.gameSettings.keyBindJump)) {
                    mc.thePlayer.motionY += vanillaVSpeed.toDouble()
                }
                if (GameSettings.isKeyDown(mc.gameSettings.keyBindSneak)) {
                    mc.thePlayer.motionY -= vanillaVSpeed.toDouble()
                    mc.gameSettings.keyBindSneak.pressed = false
                }
                MovementUtils.strafe(vanillaSpeed)
            }

            "newspartan" -> {
                if (modeValue.get() == "NewSpartan" && !mc.thePlayer.onGround && !mc.thePlayer.isSneaking) {
                    if (mc.thePlayer.ticksExisted % 3 != 0) return
                    mc.thePlayer.motionY = 0.0
                    if (mc.thePlayer.fallDistance > 0) {
                        mc.thePlayer.motionY = 0.2
                        mc.thePlayer.fallDistance = 0f
                    }
                    if (mc.thePlayer.moveForward == 0f) {
                        mc.thePlayer.motionX = 0.0
                        mc.thePlayer.motionZ = 0.0
                    }
                }
            }

            "vanilla" -> {
                mc.thePlayer.capabilities.allowFlying = true
            }

            "minemora" -> {
                if (boost) {
                    repeat(10) {
                        mc.timer.timerSpeed = (it / 10).toFloat()
                        mc.netHandler.addToSendQueue(
                            C04PacketPlayerPosition(
                                mc.thePlayer.posX,
                                mc.thePlayer.posY,
                                mc.thePlayer.posZ,
                                false
                            )
                        )
                        mc.netHandler.addToSendQueue(
                            C04PacketPlayerPosition(
                                mc.thePlayer.posX,
                                mc.thePlayer.posY,
                                mc.thePlayer.posZ,
                                true
                            )
                        )
                    }
                    state = false
                }
            }

            "desync" -> {
                mc.thePlayer.noClip = true
                mc.thePlayer.onGround = false

                mc.thePlayer.capabilities.isFlying = false
                mc.thePlayer.motionX = 0.0
                mc.thePlayer.motionY = 0.0
                mc.thePlayer.motionZ = 0.0

                if (GameSettings.isKeyDown(mc.gameSettings.keyBindJump)) {
                    mc.thePlayer.motionY += vanillaVSpeed.toDouble()
                }
                if (GameSettings.isKeyDown(mc.gameSettings.keyBindSneak)) {
                    mc.thePlayer.motionY -= vanillaVSpeed.toDouble()
                    mc.gameSettings.keyBindSneak.pressed = false
                }
                MovementUtils.strafe(vanillaSpeed)

                if (mc.thePlayer.ticksExisted % 180 == 0) {
                    while (packetLol.size > 22) {
                        PacketUtils.sendPacketNoEvent(packetLol.poll())
                    }
                }
            }

            "packet" -> {
                MovementUtils.strafe(0f)
                mc.thePlayer.motionY = 0.0
                mc.thePlayer.onGround = true
                mc.timer.timerSpeed = 1.3f
                val playerYaw = Math.toRadians(mc.thePlayer.rotationYaw.toDouble())
                val x = -sin(playerYaw) * 0.2873
                val z = cos(playerYaw) * 0.2873

                if (MovementUtils.isMoving() && !GameSettings.isKeyDown(mc.gameSettings.keyBindJump) && !GameSettings.isKeyDown(
                        mc.gameSettings.keyBindSneak
                    )
                ) {
                    mc.netHandler.addToSendQueue(
                        C04PacketPlayerPosition(
                            mc.thePlayer.posX + x,
                            mc.thePlayer.posY,
                            mc.thePlayer.posZ + z,
                            false
                        )
                    )
                    mc.netHandler.addToSendQueue(
                        C04PacketPlayerPosition(
                            mc.thePlayer.posX + x,
                            mc.thePlayer.posY + 60,
                            mc.thePlayer.posZ + z,
                            true
                        )
                    )
                }

                if (GameSettings.isKeyDown(mc.gameSettings.keyBindJump)) {
                    mc.netHandler.addToSendQueue(
                        C04PacketPlayerPosition(
                            mc.thePlayer.posX,
                            mc.thePlayer.posY,
                            mc.thePlayer.posZ,
                            false
                        )
                    )
                    mc.netHandler.addToSendQueue(
                        C04PacketPlayerPosition(
                            mc.thePlayer.posX,
                            mc.thePlayer.posY + 20,
                            mc.thePlayer.posZ,
                            true
                        )
                    )
                }

                if (GameSettings.isKeyDown(mc.gameSettings.keyBindSneak)) {
                    mc.gameSettings.keyBindSneak.pressed = false
                    mc.netHandler.addToSendQueue(
                        C04PacketPlayerPosition(
                            mc.thePlayer.posX,
                            mc.thePlayer.posY,
                            mc.thePlayer.posZ,
                            false
                        )
                    )
                    mc.netHandler.addToSendQueue(
                        C04PacketPlayerPosition(
                            mc.thePlayer.posX,
                            mc.thePlayer.posY - 20,
                            mc.thePlayer.posZ,
                            true
                        )
                    )
                }
            }

            "blockdrop" -> {
                mc.thePlayer.capabilities.isFlying = false
                mc.thePlayer.motionY = 0.0
                mc.thePlayer.motionX = 0.0
                mc.thePlayer.motionZ = 0.0
                if (GameSettings.isKeyDown(mc.gameSettings.keyBindJump)) {
                    mc.thePlayer.motionY += vanillaVSpeed.toDouble()
                }
                if (GameSettings.isKeyDown(mc.gameSettings.keyBindSneak)) {
                    mc.thePlayer.motionY -= vanillaVSpeed.toDouble()
                    mc.gameSettings.keyBindSneak.pressed = false
                }
                MovementUtils.strafe(vanillaSpeed)
            }

            "sentinel" -> {
                mc.thePlayer.motionY = 0.0
                cubecraftTeleportTickTimer.update()
                cubecraftTeleportYTickTimer.update()
                cubecraftTeleportYDownTickTimer.update()
                mc.timer.timerSpeed = 0.6f
                if (GameSettings.isKeyDown(mc.gameSettings.keyBindSneak))
                    mc.gameSettings.keyBindSneak.pressed = false
            }

            "ncp" -> {
                mc.thePlayer.motionY = -ncpMotionValue.get().toDouble()
                if (GameSettings.isKeyDown(mc.gameSettings.keyBindSneak)) {
                    mc.thePlayer.motionY = -0.5
                    mc.gameSettings.keyBindSneak.pressed = false
                }
                MovementUtils.strafe()
            }

            "clip" -> {
                mc.thePlayer.motionY = clipMotionY.get().toDouble()
                mc.timer.timerSpeed = clipTimer.get()
                if (mc.thePlayer.ticksExisted % clipDelay.get() == 0) {
                    val expectMoves = getMoves(clipH.get().toDouble(), clipV.get().toDouble())
                    if (!clipCollisionCheck.get() || mc.theWorld.getCollidingBoundingBoxes(
                            mc.thePlayer, mc.thePlayer.entityBoundingBox.offset(
                                expectMoves[0], expectMoves[1], expectMoves[2]
                            ).expand(0.0, 0.0, 0.0)
                        ).isEmpty()
                    ) hClip(expectMoves[0], expectMoves[1], expectMoves[2])
                }
            }

            "aac5-vanilla", "bugspartan" -> {
                mc.thePlayer.capabilities.isFlying = false
                mc.thePlayer.motionY = 0.0
                mc.thePlayer.motionX = 0.0
                mc.thePlayer.motionZ = 0.0
                if (GameSettings.isKeyDown(mc.gameSettings.keyBindJump)) {
                    mc.thePlayer.motionY += vanillaVSpeed.toDouble()
                }
                if (GameSettings.isKeyDown(mc.gameSettings.keyBindSneak)) {
                    mc.thePlayer.motionY -= vanillaVSpeed.toDouble()
                    mc.gameSettings.keyBindSneak.pressed = false
                }
                MovementUtils.strafe(vanillaSpeed)
            }

            "verus" -> {
                mc.thePlayer.capabilities.isFlying = false
                run {
                    mc.thePlayer.motionZ = 0.0
                    mc.thePlayer.motionX = mc.thePlayer.motionZ
                }
                if (!verusDmgModeValue.get()
                        .equals("Jump", ignoreCase = true) || shouldActiveDmg || verusDmged
                ) mc.thePlayer.motionY = 0.0
                if (verusDmgModeValue.get().equals("Jump", ignoreCase = true) && verusJumpTimes < 5) {
                    if (mc.thePlayer.onGround) {
                        mc.thePlayer.jump()
                        verusJumpTimes += 1
                    }
                    return
                }
                if (shouldActiveDmg) {
                    if (dmgCooldown > 0) dmgCooldown-- else if (verusDmged) {
                        verusDmged = false
                        val y = mc.thePlayer.posY
                        if (verusDmgModeValue.get().equals("Instant", ignoreCase = true)) {
                            if (mc.theWorld.getCollidingBoundingBoxes(
                                    mc.thePlayer,
                                    mc.thePlayer.entityBoundingBox.offset(0.0, 4.0, 0.0).expand(0.0, 0.0, 0.0)
                                ).isEmpty()
                            ) {
                                PacketUtils.sendPacketNoEvent(
                                    C04PacketPlayerPosition(
                                        mc.thePlayer.posX,
                                        y + 4,
                                        mc.thePlayer.posZ,
                                        false
                                    )
                                )
                                PacketUtils.sendPacketNoEvent(
                                    C04PacketPlayerPosition(
                                        mc.thePlayer.posX,
                                        y,
                                        mc.thePlayer.posZ,
                                        false
                                    )
                                )
                                PacketUtils.sendPacketNoEvent(
                                    C04PacketPlayerPosition(
                                        mc.thePlayer.posX,
                                        y,
                                        mc.thePlayer.posZ,
                                        true
                                    )
                                )
                                mc.thePlayer.motionZ = 0.0
                                mc.thePlayer.motionX = mc.thePlayer.motionZ
                            }
                        } else if (verusDmgModeValue.get().equals("InstantC06", ignoreCase = true)) {
                            if (mc.theWorld.getCollidingBoundingBoxes(
                                    mc.thePlayer,
                                    mc.thePlayer.entityBoundingBox.offset(0.0, 4.0, 0.0).expand(0.0, 0.0, 0.0)
                                ).isEmpty()
                            ) {
                                PacketUtils.sendPacketNoEvent(
                                    C06PacketPlayerPosLook(
                                        mc.thePlayer.posX,
                                        y + 4,
                                        mc.thePlayer.posZ,
                                        mc.thePlayer.rotationYaw,
                                        mc.thePlayer.rotationPitch,
                                        false
                                    )
                                )
                                PacketUtils.sendPacketNoEvent(
                                    C06PacketPlayerPosLook(
                                        mc.thePlayer.posX,
                                        y,
                                        mc.thePlayer.posZ,
                                        mc.thePlayer.rotationYaw,
                                        mc.thePlayer.rotationPitch,
                                        false
                                    )
                                )
                                PacketUtils.sendPacketNoEvent(
                                    C06PacketPlayerPosLook(
                                        mc.thePlayer.posX,
                                        y,
                                        mc.thePlayer.posZ,
                                        mc.thePlayer.rotationYaw,
                                        mc.thePlayer.rotationPitch,
                                        true
                                    )
                                )
                                mc.thePlayer.motionZ = 0.0
                                mc.thePlayer.motionX = mc.thePlayer.motionZ
                            }
                        }
                        dmgCooldown = verusReDmgTickValue.get()
                    }
                }
                if (!verusDmged && mc.thePlayer.hurtTime > 0) {
                    verusDmged = true
                    boostTicks = verusDmgTickValue.get()
                }
                if (boostTicks > 0) {
                    mc.timer.timerSpeed = verusTimerValue.get()
                    val motion: Float = if (verusBoostModeValue.get()
                            .equals("static", ignoreCase = true)
                    ) verusSpeedValue.get() else boostTicks.toFloat() / verusDmgTickValue.get()
                        .toFloat() * verusSpeedValue.get()
                    boostTicks--
                    MovementUtils.strafe(motion)
                } else if (verusDmged) {
                    mc.timer.timerSpeed = 1f
                    MovementUtils.strafe(MovementUtils.getBaseMoveSpeed().toFloat() * 0.6f)
                } else {
                    mc.thePlayer.movementInput.moveForward = 0f
                    mc.thePlayer.movementInput.moveStrafe = 0f
                }
            }

            "creative" -> {
                mc.thePlayer.capabilities.isFlying = true
            }

            "aac1.9.10" -> {
                if (GameSettings.isKeyDown(mc.gameSettings.keyBindJump)) {
                    aacJump += 0.2
                }
                if (GameSettings.isKeyDown(mc.gameSettings.keyBindSneak)) {
                    aacJump -= 0.2
                    mc.gameSettings.keyBindSneak.pressed = false
                }
                if (startY + aacJump > mc.thePlayer.posY) {
                    mc.netHandler.addToSendQueue(C03PacketPlayer(true))
                    mc.thePlayer.motionY = 0.8
                    MovementUtils.strafe(aacSpeedValue.get())
                }
                MovementUtils.strafe()
            }

            "aac3.0.5" -> {
                if (aac3delay == 2) mc.thePlayer.motionY = 0.1 else if (aac3delay > 2) aac3delay = 0
                if (aacFast.get()) {
                    if (mc.thePlayer.movementInput.moveStrafe.toDouble() == 0.0) mc.thePlayer.jumpMovementFactor =
                        0.08f else mc.thePlayer.jumpMovementFactor = 0f
                }
                aac3delay++
            }

            "aac3.1.6" -> {
                mc.thePlayer.capabilities.isFlying = true
                if (aac3delay == 2) {
                    mc.thePlayer.motionY += 0.05
                } else if (aac3delay > 2) {
                    mc.thePlayer.motionY -= 0.05
                    aac3delay = 0
                }
                aac3delay++
                if (!noFlag) mc.netHandler.addToSendQueue(
                    C04PacketPlayerPosition(
                        mc.thePlayer.posX,
                        mc.thePlayer.posY,
                        mc.thePlayer.posZ,
                        mc.thePlayer.onGround
                    )
                )
                if (mc.thePlayer.posY <= 0.0) noFlag = true
            }

            "flag" -> {
                mc.netHandler.addToSendQueue(
                    C06PacketPlayerPosLook(
                        mc.thePlayer.posX + mc.thePlayer.motionX * 999,
                        mc.thePlayer.posY + (if (GameSettings.isKeyDown(mc.gameSettings.keyBindJump)) 1.5624 else 0.00000001) - if (mc.gameSettings.keyBindSneak.isKeyDown) 0.0624 else 0.00000002,
                        mc.thePlayer.posZ + mc.thePlayer.motionZ * 999,
                        mc.thePlayer.rotationYaw,
                        mc.thePlayer.rotationPitch,
                        true
                    )
                )
                mc.netHandler.addToSendQueue(
                    C06PacketPlayerPosLook(
                        mc.thePlayer.posX + mc.thePlayer.motionX * 999,
                        mc.thePlayer.posY - 6969,
                        mc.thePlayer.posZ + mc.thePlayer.motionZ * 999,
                        mc.thePlayer.rotationYaw,
                        mc.thePlayer.rotationPitch,
                        true
                    )
                )
                mc.thePlayer.setPosition(
                    mc.thePlayer.posX + mc.thePlayer.motionX * 11,
                    mc.thePlayer.posY,
                    mc.thePlayer.posZ + mc.thePlayer.motionZ * 11
                )
                mc.thePlayer.motionY = 0.0
            }

            "keepalive" -> {
                mc.netHandler.addToSendQueue(C00PacketKeepAlive())
                mc.thePlayer.capabilities.isFlying = false
                mc.thePlayer.motionY = 0.0
                mc.thePlayer.motionX = 0.0
                mc.thePlayer.motionZ = 0.0
                if (GameSettings.isKeyDown(mc.gameSettings.keyBindJump)) {
                    mc.thePlayer.motionY += vanillaVSpeed.toDouble()
                }
                if (GameSettings.isKeyDown(mc.gameSettings.keyBindSneak)) {
                    mc.thePlayer.motionY -= vanillaVSpeed.toDouble()
                    mc.gameSettings.keyBindSneak.pressed = false
                }
                MovementUtils.strafe(vanillaSpeed)
            }

            "jetpack" -> {
                if (GameSettings.isKeyDown(mc.gameSettings.keyBindJump)) {
                    mc.thePlayer.motionY += 0.14
                    if (mc.thePlayer.isSprinting) {
                        mc.thePlayer.motionX *= 1.12
                        mc.thePlayer.motionZ *= 1.12
                    }
                }
            }

            "aac3.3.12" -> {
                if (mc.thePlayer.posY < -70) mc.thePlayer.motionY = aacMotion.get().toDouble()
                mc.timer.timerSpeed = 1f
            }

            "aac3.3.13" -> {
                if (mc.thePlayer.isDead) wasDead = true
                if (wasDead || mc.thePlayer.onGround) {
                    wasDead = false
                    mc.thePlayer.motionY = aacMotion2.get().toDouble()
                    mc.thePlayer.onGround = false
                }
                mc.timer.timerSpeed = 1f
            }

            "oldspartan" -> {
                mc.thePlayer.motionY = 0.0
                spartanTimer.update()
                if (spartanTimer.hasTimePassed(12)) {
                    mc.netHandler.addToSendQueue(
                        C04PacketPlayerPosition(
                            mc.thePlayer.posX,
                            mc.thePlayer.posY + 8,
                            mc.thePlayer.posZ,
                            true
                        )
                    )
                    mc.netHandler.addToSendQueue(
                        C04PacketPlayerPosition(
                            mc.thePlayer.posX,
                            mc.thePlayer.posY - 8,
                            mc.thePlayer.posZ,
                            true
                        )
                    )
                    spartanTimer.reset()
                }
            }

            "pearl" -> {
                mc.thePlayer.capabilities.isFlying = false
                run {
                    mc.thePlayer.motionZ = 0.0
                    mc.thePlayer.motionY = mc.thePlayer.motionZ
                    mc.thePlayer.motionX = mc.thePlayer.motionY
                }
                val enderPearlSlot = pearlSlot
                if (pearlState == 0) {
                    if (enderPearlSlot == -1) {
                        Client.hud.addNotification(
                            Notification(
                                "You don't have any ender pearl!",
                                Notification.Type.ERROR
                            )
                        )
                        pearlState = -1
                        state = false
                        return
                    }
                    if (mc.thePlayer.inventory.currentItem != enderPearlSlot) {
                        mc.thePlayer.sendQueue.addToSendQueue(C09PacketHeldItemChange(enderPearlSlot))
                    }
                    mc.thePlayer.sendQueue.addToSendQueue(
                        C05PacketPlayerLook(
                            mc.thePlayer.rotationYaw,
                            90f,
                            mc.thePlayer.onGround
                        )
                    )
                    mc.thePlayer.sendQueue.addToSendQueue(
                        C08PacketPlayerBlockPlacement(
                            BlockPos(-1, -1, -1),
                            255,
                            mc.thePlayer.inventoryContainer.getSlot(enderPearlSlot + 36).stack,
                            0f,
                            0f,
                            0f
                        )
                    )
                    if (enderPearlSlot != mc.thePlayer.inventory.currentItem) {
                        mc.thePlayer.sendQueue.addToSendQueue(C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem))
                    }
                    pearlState = 1
                }
                if (pearlActivateCheck.get()
                        .equals("damage", ignoreCase = true) && pearlState == 1 && mc.thePlayer.hurtTime > 0
                ) pearlState = 2
                if (pearlState == 2) {
                    if (GameSettings.isKeyDown(mc.gameSettings.keyBindJump)) {
                        mc.thePlayer.motionY += vanillaSpeed.toDouble()
                    }
                    if (GameSettings.isKeyDown(mc.gameSettings.keyBindSneak)) {
                        mc.thePlayer.motionY -= vanillaSpeed.toDouble()
                        mc.gameSettings.keyBindSneak.pressed = false
                    }
                    MovementUtils.strafe(vanillaSpeed)
                }
            }

            "exploit" -> {
                if (wdState == 0) {
                    mc.thePlayer.motionY = 0.1
                    wdState++
                }
                if (wdState == 1 && wdTick == 3) wdState++
                if (wdState == 4) {
                    if (!boostTimer.hasTimePassed(500L)) mc.timer.timerSpeed = 1.6f else if (!boostTimer.hasTimePassed(
                            800L
                        )
                    ) mc.timer.timerSpeed = 1.4f else if (!boostTimer.hasTimePassed(1000L)) mc.timer.timerSpeed =
                        1.2f else mc.timer.timerSpeed = 1f
                    mc.thePlayer.motionY = 0.0001
                    MovementUtils.strafe((MovementUtils.getBaseMoveSpeed(1.0) * if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) 0.81 else 0.77).toFloat())
                }
            }

            "neruxvace" -> {
                if (!mc.thePlayer.onGround) aac3glideDelay++
                if (aac3glideDelay >= neruxVaceTicks.get() && !mc.thePlayer.onGround) {
                    aac3glideDelay = 0
                    mc.thePlayer.motionY = .015
                }
            }
        }
    }

    @EventTarget
    fun onMotion(event: MotionEvent) {
        if (mc.thePlayer == null) return
        if (MovementUtils.isMoving() && viewBobbingValue.get())
            mc.thePlayer.cameraYaw = bobbingAmountValue.get()
        if (fakeYValue.get())
            mc.thePlayer.cameraPitch = 0f
        when (modeValue.get().lowercase()) {
            "vulcanfast" -> {
                if (event.eventState === EventState.PRE) {
                    mc.netHandler.addToSendQueue(
                        C0BPacketEntityAction(
                            mc.thePlayer,
                            C0BPacketEntityAction.Action.START_SNEAKING
                        )
                    )
                }
            }

            "latestncp" -> {
                val bb = mc.thePlayer.entityBoundingBox.offset(0.0, 1.0, 0.0)
                if (fly) {
                    mc.thePlayer.motionY += 0.025
                    MovementUtilsFix.theStrafe(8.05)
                }
                if (mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, bb).isEmpty() && !fly) {
                    fly = true
                    mc.thePlayer.jump()
                    MovementUtilsFix.theStrafe(9.0)
                }
            }

            "vulcanclip" -> {
                if (event.eventState == EventState.PRE) {
                    mc.timer.timerSpeed = 1f
                    mc.thePlayer.motionY = -if (ticks % 2 == 0) {
                        0.17
                    } else {
                        0.10
                    }
                    if (ticks == 0) {
                        mc.thePlayer.motionY = -0.07
                    }
                    ticks++
                }
            }

            "vulcanglide" -> {
                if (event.eventState == EventState.PRE && !mc.thePlayer.onGround) {
                    mc.timer.timerSpeed = 1f
                    mc.thePlayer.motionY = -if (ticks % 2 == 0) {
                        0.17
                    } else {
                        0.10
                    }
                    if (ticks == 0) {
                        mc.thePlayer.motionY = -0.16
                    }
                    ticks++
                }
            }

            "minemora" -> {
                if (event.eventState != EventState.PRE) return
                tick++
                mc.timer.timerSpeed = 1.0f
                if (tick == 1) {
                    mc.timer.timerSpeed = 0.25f
                    mc.netHandler.addToSendQueue(
                        C04PacketPlayerPosition(
                            mc.thePlayer.posX,
                            mc.thePlayer.posY + 3.42f,
                            mc.thePlayer.posZ,
                            false
                        )
                    )
                    mc.netHandler.addToSendQueue(
                        C04PacketPlayerPosition(
                            mc.thePlayer.posX,
                            mc.thePlayer.posY,
                            mc.thePlayer.posZ,
                            false
                        )
                    )
                    mc.netHandler.addToSendQueue(
                        C04PacketPlayerPosition(
                            mc.thePlayer.posX,
                            mc.thePlayer.posY,
                            mc.thePlayer.posZ,
                            true
                        )
                    )
                    mc.thePlayer.jump()
                } else {
                    if (MovementUtils.isMoving()) {
                        MovementUtils.strafe(1.7f)
                    }

                    if (mc.gameSettings.keyBindJump.pressed) {
                        mc.thePlayer.motionY = 1.7
                    } else if (GameSettings.isKeyDown(mc.gameSettings.keyBindSneak)) {
                        mc.thePlayer.motionY = -1.7
                        if (mc.thePlayer.onGround) {
                            if (boostGround) {
                                boost = true
                            } else {
                                state = false
                            }
                        }
                    } else {
                        mc.thePlayer.motionY = 0.0
                    }
                }
            }

            "blockdrop" -> {
                when (event.eventState) {
                    EventState.PRE -> {
                        mc.thePlayer.motionY =
                            if (GameSettings.isKeyDown(mc.gameSettings.keyBindJump)) 2.0 else if (GameSettings.isKeyDown(
                                    mc.gameSettings.keyBindJump
                                )
                            ) -2.0 else 0.0
                        var var10_8 = 0
                        while (var10_8 < 3) {
                            PacketUtils.sendPacketNoEvent(
                                C06PacketPlayerPosLook(
                                    startVec!!.xCoord,
                                    startVec!!.yCoord,
                                    startVec!!.zCoord,
                                    rotationVec!!.getX(),
                                    rotationVec!!.getY(),
                                    false
                                )
                            )
                            ++var10_8
                        }
                    }

                    EventState.POST -> {
                        var i2 = 0
                        while (i2 < 1) {
                            PacketUtils.sendPacketNoEvent(
                                C06PacketPlayerPosLook(
                                    mc.thePlayer.posX,
                                    mc.thePlayer.posY,
                                    mc.thePlayer.posZ,
                                    rotationVec!!.getX(),
                                    rotationVec!!.getY(),
                                    false
                                )
                            )
                            ++i2
                        }
                    }
                }
            }

            "funcraft" -> {
                event.onGround = true
                mc.thePlayer.capabilities.isFlying = false
                mc.thePlayer.motionY = 0.0
                if (MovementUtils.isMoving() && !mc.thePlayer.onGround) {
                    mc.timer.timerSpeed = 1.65f
                }
                if (!MovementUtils.isMoving() || mc.thePlayer.isCollidedHorizontally) moveSpeed = 0.25
                if (moveSpeed > 0.25) moveSpeed -= moveSpeed / 169
                if (event.eventState == EventState.PRE)
                    mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - 8e-6, mc.thePlayer.posZ)
                MovementUtils.strafe(moveSpeed.toFloat())
            }

            "float" -> if (event.eventState === EventState.PRE) {
                mc.thePlayer.capabilities.isFlying = false
                mc.thePlayer.motionY = 0.0
            }

            "zoom" -> {
                event.onGround = true
                if (!MovementUtils.isMoving()) moveSpeed = 0.25
                if (moveSpeed > 0.25) {
                    moveSpeed -= moveSpeed / 260.0
                }
                if (event.eventState === EventState.POST) {
                    mc.thePlayer.capabilities.isFlying = false
                    mc.thePlayer.motionY = 0.0
                    mc.thePlayer.motionX = 0.0
                    mc.thePlayer.motionZ = 0.0
                    MovementUtils.strafe(moveSpeed.toFloat())
                    mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 1.0E-5, mc.thePlayer.posZ)
                }
            }

            "exploit" -> if (event.eventState === EventState.PRE) wdTick++
            "slime" -> {
                val current = mc.thePlayer.inventory.currentItem
                if (event.eventState === EventState.PRE) {
                    if (wdState == 1 && mc.theWorld.getCollidingBoundingBoxes(
                            mc.thePlayer,
                            mc.thePlayer.entityBoundingBox.offset(0.0, -1.0, 0.0).expand(0.0, 0.0, 0.0)
                        ).isEmpty()
                    ) {
                        PacketUtils.sendPacketNoEvent(C09PacketHeldItemChange(expectItemStack))
                        wdState = 2
                    }
                    mc.timer.timerSpeed = 1f
                    if (wdState == 3 && expectItemStack != -1) {
                        PacketUtils.sendPacketNoEvent(C09PacketHeldItemChange(current))
                        expectItemStack = -1
                    }
                    if (wdState == 4) {
                        if (MovementUtils.isMoving()) MovementUtils.strafe(
                            MovementUtils.getBaseMoveSpeed().toFloat() * 0.938f
                        ) else MovementUtils.strafe(0f)
                        mc.thePlayer.motionY = -0.0015
                    } else if (wdState < 3) {
                        val rot = RotationUtils.getRotationFromPosition(
                            mc.thePlayer.posX,
                            mc.thePlayer.posZ,
                            (mc.thePlayer.posY.toInt() - 1).toDouble()
                        )
                        RotationUtils.setTargetRotation(rot)
                        event.yaw = rot.yaw
                        event.pitch = rot.pitch
                    } else event.y = event.y - 0.08
                } else if (wdState == 2) {
                    if (mc.playerController.onPlayerRightClick(
                            mc.thePlayer, mc.theWorld,
                            mc.thePlayer.inventoryContainer.getSlot(expectItemStack).stack,
                            BlockPos(mc.thePlayer.posX, (mc.thePlayer.posY.toInt() - 2).toDouble(), mc.thePlayer.posZ),
                            EnumFacing.UP,
                            RotationUtils.getVectorForRotation(
                                RotationUtils.getRotationFromPosition(
                                    mc.thePlayer.posX,
                                    mc.thePlayer.posZ,
                                    (mc.thePlayer.posY.toInt() - 1).toDouble()
                                )
                            )
                        )
                    ) mc.netHandler.addToSendQueue(C0APacketAnimation())
                    wdState = 3
                }
            }
        }
    }

    @EventTarget
    fun onAction(event: ActionEvent) {
        if (modeValue.get().lowercase(Locale.getDefault())
                .contains("exploit") && fakeSprintingValue.get()
        ) event.sprinting = false
    }

    @EventTarget
    fun onRender2D(event: Render2DEvent?) {
        val mode = modeValue.get()
        val scaledRes = ScaledResolution(mc)
        if (mode.equals("Verus", ignoreCase = true) && boostTicks > 0) {
            val width = (verusDmgTickValue.get() - boostTicks).toFloat() / verusDmgTickValue.get().toFloat() * 60f
            RenderUtils.drawRect(
                scaledRes.scaledWidth / 2f - 31f,
                scaledRes.scaledHeight / 2f + 14f,
                scaledRes.scaledWidth / 2f + 31f,
                scaledRes.scaledHeight / 2f + 18f,
                -0x60000000
            )
            RenderUtils.drawRect(
                scaledRes.scaledWidth / 2f - 30f,
                scaledRes.scaledHeight / 2f + 15f,
                scaledRes.scaledWidth / 2f - 30f + width,
                scaledRes.scaledHeight / 2f + 17f,
                -0x1
            )
        }
        if (mode.equals("Verus", ignoreCase = true) && shouldActiveDmg) {
            val width = (verusReDmgTickValue.get() - dmgCooldown).toFloat() / verusReDmgTickValue.get().toFloat() * 60f
            RenderUtils.drawRect(
                scaledRes.scaledWidth / 2f - 31f,
                scaledRes.scaledHeight / 2f + 14f + 10f,
                scaledRes.scaledWidth / 2f + 31f,
                scaledRes.scaledHeight / 2f + 18f + 10f,
                -0x60000000
            )
            RenderUtils.drawRect(
                scaledRes.scaledWidth / 2f - 30f,
                scaledRes.scaledHeight / 2f + 15f + 10f,
                scaledRes.scaledWidth / 2f - 30f + width,
                scaledRes.scaledHeight / 2f + 17f + 10f,
                -0xe0e1
            )
        }
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet
        val mode = modeValue.get()
        if (noPacketModify) return
        when (mode.lowercase()) {
            "matrix" -> {
                if (mc.currentScreen == null && packet is S08PacketPlayerPosLook) {
                    TransferUtils.noMotionSet = true
                    if (boostMotion == 1) {
                        boostMotion = 2
                    }
                }
            }

            "vulcanzoom" -> {
                if (packet is C03PacketPlayer && !pog) {
                    event.cancelEvent()
                } else if (packet is C03PacketPlayer && (packet is C04PacketPlayerPosition || packet is C06PacketPlayerPosLook)) {
                    val deltaX = packet.x - lastSentX
                    val deltaY = packet.y - lastSentY
                    val deltaZ = packet.z - lastSentZ

                    if (sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ) > 9.3) {
                        lastSentX = packet.x
                        lastSentY = packet.y
                        lastSentZ = packet.z
                        return
                    }
                    event.cancelEvent()
                } else if (packet is C03PacketPlayer) {
                    event.cancelEvent()
                }
                if (packet is S08PacketPlayerPosLook) {
                    if (!pog) {
                        lastSentX = packet.x
                        lastSentY = packet.y
                        lastSentZ = packet.z
                        pog = true
                        mc.timer.timerSpeed = 1.0f
                        event.cancelEvent()
                    }
                }
            }

            "vulcanfast" -> {
                when (packet) {
                    is C03PacketPlayer -> {
                        if (tickso > 2) {
                            tickso = 0
                            packet.y += 0.5
                        }
                        packet.onGround = true
                    }

                    is S08PacketPlayerPosLook -> {
                        if (stage == FlyStage.WAITING) {
                            flags++
                            if (flags >= 2) {
                                flags = 0
                                stage = FlyStage.FLYING
                            }
                        }
                        if (stage == FlyStage.WAIT_APPLY) {
                            if (sqrt(
                                    (packet.x - groundX) * (packet.x - groundX)
                                            + (packet.z - groundZ) * (packet.z - groundZ)
                                ) < 1.4 && packet.y >= (groundY - 0.5)
                            ) {
                                Client.moduleManager.getModule(Flight::class.java)?.state = false
                                return
                            }
                        }
                        event.cancelEvent()
                    }

                    is C0BPacketEntityAction -> {
                        event.cancelEvent()
                    }
                }
            }

            "desync" -> {
                if (packet is C03PacketPlayer) {
                    val yPos = round(mc.thePlayer.posY / 0.015625) * 0.015625
                    mc.thePlayer.setPosition(mc.thePlayer.posX, yPos, mc.thePlayer.posZ)

                    if (mc.thePlayer.ticksExisted % 45 == 0) {
                        PacketUtils.sendPacketNoEvent(
                            C04PacketPlayerPosition(
                                mc.thePlayer.posX,
                                mc.thePlayer.posY,
                                mc.thePlayer.posZ,
                                true
                            )
                        )
                        PacketUtils.sendPacketNoEvent(
                            C04PacketPlayerPosition(
                                mc.thePlayer.posX,
                                mc.thePlayer.posY - 11.725,
                                mc.thePlayer.posZ,
                                false
                            )
                        )
                        PacketUtils.sendPacketNoEvent(
                            C04PacketPlayerPosition(
                                mc.thePlayer.posX,
                                mc.thePlayer.posY,
                                mc.thePlayer.posZ,
                                true
                            )
                        )
                    }
                }

                if (packet is S08PacketPlayerPosLook) {
                    if (mc.thePlayer == null || mc.thePlayer.ticksExisted <= 0) return

                    val x = packet.getX() - mc.thePlayer.posX
                    val y = packet.getY() - mc.thePlayer.posY
                    val z = packet.getZ() - mc.thePlayer.posZ
                    val diff = sqrt(x * x + y * y + z * z)
                    if (diff <= 8) {
                        event.cancelEvent()
                        PacketUtils.sendPacketNoEvent(
                            C06PacketPlayerPosLook(
                                packet.getX(),
                                packet.getY(),
                                packet.getZ(),
                                packet.getYaw(),
                                packet.getPitch(),
                                true
                            )
                        )
                    }
                }

                if (packet is C0FPacketConfirmTransaction && !isInventory(packet.uid)) {
                    repeat(4) {
                        packetLol.add(packet)
                    }
                    event.cancelEvent()
                }
            }

            "minemora" -> {
                if (mc.thePlayer == null || disableLogger) return

                if (packet is C03PacketPlayer) {
                    event.cancelEvent()
                }
                if (packet is C04PacketPlayerPosition || packet is C06PacketPlayerPosLook ||
                    packet is C08PacketPlayerBlockPlacement ||
                    packet is C0APacketAnimation ||
                    packet is C0BPacketEntityAction || packet is C02PacketUseEntity
                ) {
                    event.cancelEvent()
                    packetBuffer.add(packet as Packet<INetHandlerPlayServer>)
                }
            }

            "vulcanclip" -> {
                if (packet is S08PacketPlayerPosLook && waitFlag) {
                    waitFlag = false
                    mc.thePlayer.setPosition(packet.x, packet.y, packet.z)
                    mc.netHandler.addToSendQueue(
                        C06PacketPlayerPosLook(
                            mc.thePlayer.posX,
                            mc.thePlayer.posY,
                            mc.thePlayer.posZ,
                            mc.thePlayer.rotationYaw,
                            mc.thePlayer.rotationPitch,
                            false
                        )
                    )
                    event.cancelEvent()
                    mc.thePlayer.jump()
                    clip(0.127318f, 0f)
                    clip(3.425559f, 3.7f)
                    clip(3.14285f, 3.54f)
                    clip(2.88522f, 3.4f)
                    canGlide = true
                }
            }

            "blockdrop" -> {
                if (packet is S08PacketPlayerPosLook) {
                    if (mc.thePlayer.ticksExisted <= 20) return
                    val i2 = event.packet as S08PacketPlayerPosLook
                    event.cancelEvent()
                    startVec = Vec3(i2.getX(), i2.getY(), i2.getZ())
                    rotationVec = Vector2f(i2.getYaw(), i2.getPitch())
                }
                if (packet is C03PacketPlayer) {
                    event.cancelEvent()
                    return
                }
                if (packet !is C02PacketUseEntity) return
                PacketUtils.sendPacketNoEvent(
                    C06PacketPlayerPosLook(
                        mc.thePlayer.posX,
                        mc.thePlayer.posY,
                        mc.thePlayer.posZ,
                        mc.thePlayer.rotationYaw,
                        mc.thePlayer.rotationPitch,
                        false
                    )
                )
            }

            "exploit" -> {
                if (packet is S08PacketPlayerPosLook && wdState == 3) {
                    wdState = 4
                    if (boostTimer.hasTimePassed(8000L)) {
                        Client.hud.addNotification(Notification("Exploit Activated!", Notification.Type.SUCCESS))
                        boostTimer.reset()
                    } else {
                        Client.hud.addNotification(Notification("Exploit Activated!", Notification.Type.SUCCESS))
                    }
                    if (fakeDmgValue.get() && mc.thePlayer != null) mc.thePlayer.handleStatusUpdate(2.toByte())
                }
            }

            "slime" -> {
                if (packet is C09PacketHeldItemChange && wdState < 4)
                    event.cancelEvent()
                if (packet is S08PacketPlayerPosLook && wdState == 3) {
                    wdState = 4
                    if (fakeDmgValue.get() && mc.thePlayer != null) mc.thePlayer.handleStatusUpdate(2.toByte())
                }
            }

            "pearl" -> {
                if (packet is S08PacketPlayerPosLook && pearlActivateCheck.get()
                        .equals("teleport", ignoreCase = true) && pearlState == 1
                )
                    pearlState = 2
            }
        }
        if (packet is C03PacketPlayer) {
            if (mode.equals("NCP", ignoreCase = true) || mode.equals(
                    "Verus",
                    ignoreCase = true
                ) && verusSpoofGround.get() && verusDmged
            ) packet.onGround = true
            if (mode.equals("AAC5-Vanilla", ignoreCase = true) && !mc.isIntegratedServerRunning) {
                if (aac5NofallValue.get()) packet.onGround = true
                aac5C03List.add(packet)
                event.cancelEvent()
                if (aac5C03List.size > aac5PursePacketsValue.get()) sendAAC5Packets()
            }
            if (mode.equals("clip", ignoreCase = true) && clipGroundSpoof.get()) packet.onGround = true
            if ((mode.equals("motion", ignoreCase = true) || mode.equals(
                    "creative",
                    ignoreCase = true
                ) || mode.equals(
                    "noclip",
                    ignoreCase = true
                )) && groundSpoofValue.get()
            ) packet.onGround = true
            if (verusDmgModeValue.get().equals("Jump", ignoreCase = true) && verusJumpTimes < 5 && mode.equals(
                    "Verus",
                    ignoreCase = true
                )
            ) {
                packet.onGround = false
            }
            if (mode.equals("exploit", ignoreCase = true)) {
                if (wdState == 2) {
                    packet.y -= 0.187
                    wdState++
                }
                if (wdState > 3) {
                    if (fakeNoMoveValue.get()) packet.isMoving = false
                }
            }
        }
    }

    private fun clip(dist: Float, y: Float) {
        val yaw = Math.toRadians(mc.thePlayer.rotationYaw.toDouble())
        val x = -sin(yaw) * dist
        val z = cos(yaw) * dist
        mc.thePlayer.setPosition(mc.thePlayer.posX + x, mc.thePlayer.posY + y, mc.thePlayer.posZ + z)
        mc.netHandler.addToSendQueue(
            C04PacketPlayerPosition(
                mc.thePlayer.posX,
                mc.thePlayer.posY,
                mc.thePlayer.posZ,
                false
            )
        )
    }

    enum class FlyStage {
        WAITING,
        FLYING,
        WAIT_APPLY
    }

    private fun sendAAC5Packets() {
        var yaw = mc.thePlayer.rotationYaw
        var pitch = mc.thePlayer.rotationPitch
        for (packet in aac5C03List) {
            PacketUtils.sendPacketNoEvent(packet)
            if (packet.isMoving) {
                if (packet.getRotating()) {
                    yaw = packet.yaw
                    pitch = packet.pitch
                }
                when (aac5Packet.get()) {
                    "Original" -> if (aac5UseC04Packet.get()) {
                        PacketUtils.sendPacketNoEvent(C04PacketPlayerPosition(packet.x, 1e+159, packet.z, true))
                        PacketUtils.sendPacketNoEvent(C04PacketPlayerPosition(packet.x, packet.y, packet.z, true))
                    } else {
                        PacketUtils.sendPacketNoEvent(
                            C06PacketPlayerPosLook(
                                packet.x,
                                1e+159,
                                packet.z,
                                yaw,
                                pitch,
                                true
                            )
                        )
                        PacketUtils.sendPacketNoEvent(
                            C06PacketPlayerPosLook(
                                packet.x,
                                packet.y,
                                packet.z,
                                yaw,
                                pitch,
                                true
                            )
                        )
                    }

                    "Rise" -> if (aac5UseC04Packet.get()) {
                        PacketUtils.sendPacketNoEvent(C04PacketPlayerPosition(packet.x, -1e+159, packet.z + 10, true))
                        PacketUtils.sendPacketNoEvent(C04PacketPlayerPosition(packet.x, packet.y, packet.z, true))
                    } else {
                        PacketUtils.sendPacketNoEvent(
                            C06PacketPlayerPosLook(
                                packet.x,
                                -1e+159,
                                packet.z + 10,
                                yaw,
                                pitch,
                                true
                            )
                        )
                        PacketUtils.sendPacketNoEvent(
                            C06PacketPlayerPosLook(
                                packet.x,
                                packet.y,
                                packet.z,
                                yaw,
                                pitch,
                                true
                            )
                        )
                    }

                    "Other" -> if (aac5UseC04Packet.get()) {
                        PacketUtils.sendPacketNoEvent(
                            C04PacketPlayerPosition(
                                packet.x,
                                1.7976931348623157E+308,
                                packet.z,
                                true
                            )
                        )
                        PacketUtils.sendPacketNoEvent(C04PacketPlayerPosition(packet.x, packet.y, packet.z, true))
                    } else {
                        PacketUtils.sendPacketNoEvent(
                            C06PacketPlayerPosLook(
                                packet.x,
                                1.7976931348623157E+308,
                                packet.z,
                                yaw,
                                pitch,
                                true
                            )
                        )
                        PacketUtils.sendPacketNoEvent(
                            C06PacketPlayerPosLook(
                                packet.x,
                                packet.y,
                                packet.z,
                                yaw,
                                pitch,
                                true
                            )
                        )
                    }
                }
            }
        }
        aac5C03List.clear()
    }

    @EventTarget
    fun onMove(event: MoveEvent) {
        when (modeValue.get().lowercase(Locale.getDefault())) {
            "pearl" -> if (pearlState != 2 && pearlState != -1) {
                event.cancelEvent()
            }

            "verus" -> if (!verusDmged) if (verusDmgModeValue.get()
                    .equals("Jump", ignoreCase = true)
            ) event.zeroXZ() else event.cancelEvent()

            "clip" -> if (clipNoMove.get()) event.zeroXZ()

            "veruslowhop" -> {
                if (!mc.thePlayer.isInWeb && !mc.thePlayer.isInLava && !mc.thePlayer.isInWater && !mc.thePlayer.isOnLadder && !GameSettings.isKeyDown(
                        mc.gameSettings.keyBindJump
                    ) && mc.thePlayer.ridingEntity == null
                ) {
                    if (MovementUtils.isMoving()) {
                        mc.gameSettings.keyBindJump.pressed = false
                        if (mc.thePlayer.onGround) {
                            mc.thePlayer.jump()
                            mc.thePlayer.motionY = 0.0
                            MovementUtils.strafe(0.61f)
                            event.y = (0.41999998688698)
                        }
                        MovementUtils.strafe()
                    }
                }
            }

            "slime" -> if (wdState < 4) event.zeroXZ()

            "sentinel" -> {
                if (MovementUtils.isMoving() && cubecraftTeleportTickTimer.hasTimePassed(2)) {
                    event.x = -sin(Math.toRadians(mc.thePlayer.rotationYaw.toDouble())) * 2.4
                    event.z = cos(Math.toRadians(mc.thePlayer.rotationYaw.toDouble())) * 2.4
                    cubecraftTeleportTickTimer.reset()
                }
                if (GameSettings.isKeyDown(mc.gameSettings.keyBindJump) && cubecraftTeleportYTickTimer.hasTimePassed(2)) {
                    event.y = 1.6
                    cubecraftTeleportYTickTimer.reset()
                }
                if (GameSettings.isKeyDown(mc.gameSettings.keyBindSneak) && cubecraftTeleportYDownTickTimer.hasTimePassed(
                        2
                    )
                ) {
                    event.y = -1.6
                    cubecraftTeleportYDownTickTimer.reset()
                }
            }
        }
    }

    @EventTarget
    fun onBlockBB(event: BlockBBEvent) {
        if (mc.thePlayer == null) return
        val mode = modeValue.get()
        if (event.block is BlockAir && mode.equals(
                "Verus",
                ignoreCase = true
            ) && (verusDmgModeValue.get()
                .equals("none", ignoreCase = true) || verusDmged) && event.y < mc.thePlayer.posY
        ) event.boundingBox = AxisAlignedBB.fromBounds(
            event.x.toDouble(),
            event.y.toDouble(),
            event.z.toDouble(),
            (event.x + 1).toDouble(),
            mc.thePlayer.posY,
            (event.z + 1).toDouble()
        )
        if ((mode.equals("Jump", true) || mode.equals("VulcanZoom", true)) && event.y < startY) {
            event.boundingBox = (AxisAlignedBB.fromBounds(
                event.x.toDouble(),
                event.y.toDouble(),
                event.z.toDouble(),
                event.x.toDouble() + 1,
                startY,
                event.z.toDouble() + 1
            ))
        }

        if (event.block is BlockAir && mode.equals(
                "FakeGround",
                true
            ) && !GameSettings.isKeyDown(mc.gameSettings.keyBindSneak) || mode.equals(
                "VerusLowHop",
                true
            ) && !GameSettings.isKeyDown(mc.gameSettings.keyBindSneak)
        ) {
            event.boundingBox = (
                    AxisAlignedBB(-2.0, -1.0, -2.0, 2.0, 1.0, 2.0).offset(
                        event.x.toDouble(),
                        event.y.toDouble(),
                        event.z.toDouble()
                    )
                    )
        }
    }

    @EventTarget
    fun onJump(e: JumpEvent) {
        val mode = modeValue.get()
        if (mode.equals(
                "funcraft",
                ignoreCase = true
            ) && moveSpeed > 0 || mode.equals("exploit", ignoreCase = true) && wdState >= 1 || mode.equals(
                "slime",
                ignoreCase = true
            ) && wdState >= 1
        ) e.cancelEvent()
    }

    @EventTarget
    fun onStep(e: StepEvent) {
        val mode = modeValue.get()
        if (mode.equals(
                "funcraft",
                ignoreCase = true
            ) || mode.equals("exploit", ignoreCase = true) && wdState > 2 || mode.equals("slime", ignoreCase = true)
        ) e.stepHeight = 0f
    }

    private val pearlSlot: Int
        get() {
            for (i in 36..44) {
                val stack = mc.thePlayer.inventoryContainer.getSlot(i).stack
                if (stack != null && stack.item is ItemEnderPearl) {
                    return i - 36
                }
            }
            return -1
        }
    private val slimeSlot: Int
        get() {
            for (i in 36..44) {
                val stack = mc.thePlayer.inventoryContainer.getSlot(i).stack
                if (stack != null && stack.item != null && stack.item is ItemBlock) {
                    val itemBlock = stack.item as ItemBlock
                    if (itemBlock.getBlock() is BlockSlime) return i - 36
                }
            }
            return -1
        }
    override val tag: String
        get() = modeValue.get()
}
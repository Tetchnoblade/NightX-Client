package net.aspw.nightx.features.module.modules.movement

import net.aspw.nightx.NightX
import net.aspw.nightx.event.*
import net.aspw.nightx.features.module.Module
import net.aspw.nightx.features.module.ModuleCategory
import net.aspw.nightx.features.module.ModuleInfo
import net.aspw.nightx.features.module.modules.exploit.Damage
import net.aspw.nightx.utils.ClientUtils
import net.aspw.nightx.utils.MovementUtils
import net.aspw.nightx.utils.PacketUtils
import net.aspw.nightx.utils.RotationUtils
import net.aspw.nightx.utils.misc.RandomUtils
import net.aspw.nightx.utils.render.RenderUtils
import net.aspw.nightx.utils.timer.MSTimer
import net.aspw.nightx.utils.timer.TickTimer
import net.aspw.nightx.value.BoolValue
import net.aspw.nightx.value.FloatValue
import net.aspw.nightx.value.IntegerValue
import net.aspw.nightx.value.ListValue
import net.aspw.nightx.visual.hud.element.elements.Notification
import net.minecraft.block.BlockAir
import net.minecraft.block.BlockSlime
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.settings.GameSettings
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemEnderPearl
import net.minecraft.network.play.client.*
import net.minecraft.network.play.client.C03PacketPlayer.*
import net.minecraft.network.play.server.S08PacketPlayerPosLook
import net.minecraft.potion.Potion
import net.minecraft.util.*
import org.lwjgl.input.Keyboard
import java.awt.Color
import java.util.*
import javax.vecmath.Vector2f
import kotlin.math.cos
import kotlin.math.round
import kotlin.math.sin
import kotlin.math.sqrt

@ModuleInfo(name = "Flight", category = ModuleCategory.MOVEMENT)
class Flight : Module() {
    @JvmField
    val modeValue = ListValue(
        "Mode", arrayOf(
            "Motion",
            "Creative",
            "Pearl",
            "Packet",
            "Desync",
            "NCP",
            "OldNCP",
            "AAC1.9.10",
            "AAC3.0.5",
            "AAC3.1.6-Gomme",
            "AAC3.3.12",
            "AAC3.3.12-Glide",
            "AAC3.3.13",
            "AAC5-Vanilla",
            "Exploit",
            "Zoom",
            "Zonecraft",
            "PurplePrison",
            "BlockDrop",
            "Cubecraft",
            "TeleportRewinside",
            "Funcraft",
            "Mineplex",
            "NeruxVace",
            "Minesucht",
            "Verus",
            "VerusLowHop",
            "Vulcan1",
            "Vulcan2",
            "LatestSpartan",
            "Spartan1",
            "Spartan2",
            "BugSpartan",
            "Hypixel",
            "BoostHypixel",
            "MineSecure",
            "HawkEye",
            "HAC",
            "WatchCat",
            "Slime",
            "Float",
            "Jetpack",
            "KeepAlive",
            "Flag",
            "Clip",
            "Jump",
            "Derp",
            "Collide"
        ), "Motion"
    )
    private val vanillaSpeedValue = FloatValue("Speed", 1f, 0f, 5f) {
        modeValue.get().equals("motion", ignoreCase = true) || modeValue.get()
            .equals("blockdrop", ignoreCase = true) || modeValue.get()
            .equals("latestspartan", ignoreCase = true) || modeValue.get()
            .equals("desync", ignoreCase = true) || modeValue.get()
            .equals("pearl", ignoreCase = true) || modeValue.get()
            .equals("aac5-vanilla", ignoreCase = true) || modeValue.get()
            .equals("bugspartan", ignoreCase = true) || modeValue.get()
            .equals("keepalive", ignoreCase = true) || modeValue.get().equals("derp", ignoreCase = true)
    }
    private val vanillaVSpeedValue = FloatValue("V-Speed", 0.6f, 0f, 5f) {
        modeValue.get().equals("motion", ignoreCase = true) || modeValue.get()
            .equals("blockdrop", ignoreCase = true) || modeValue.get()
            .equals("desync", ignoreCase = true) || modeValue.get()
            .equals("latestspartan", ignoreCase = true) || modeValue.get().equals("bugspartan", ignoreCase = true)
    }
    private val vanillaMotionYValue = FloatValue("Y-Motion", 0f, -1f, 1f) {
        modeValue.get().equals("motion", ignoreCase = true) || modeValue.get()
            .equals("latestspartan", ignoreCase = true)
    }
    private val vanillaKickBypassValue = BoolValue("AntiKick", false) {
        modeValue.get().equals("motion", ignoreCase = true) || modeValue.get()
            .equals("latestspartan", ignoreCase = true) || modeValue.get().equals("creative", ignoreCase = true)
    }
    private val groundSpoofValue = BoolValue("SpoofGround", false) {
        modeValue.get().equals("motion", ignoreCase = true) || modeValue.get()
            .equals("latestspartan", ignoreCase = true) || modeValue.get().equals("creative", ignoreCase = true)
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

    // AAC
    private val aac5NofallValue =
        BoolValue("AAC5-NoFall", true) { modeValue.get().equals("aac5-vanilla", ignoreCase = true) }
    private val aac5UseC04Packet =
        BoolValue("AAC5-UseC04", true) { modeValue.get().equals("aac5-vanilla", ignoreCase = true) }
    private val aac5Packet = ListValue("AAC5-Packet", arrayOf("Original", "Rise", "Other"), "Original") {
        modeValue.get().equals("aac5-vanilla", ignoreCase = true)
    } // Original is from UnlegitMC/FDPClient.
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
    private val aacFast = BoolValue("AAC3.0.5-Fast", true) { modeValue.get().equals("aac3.0.5", ignoreCase = true) }
    private val aacMotion =
        FloatValue("AAC3.3.12-Motion", 10f, 0.1f, 10f) { modeValue.get().equals("aac3.3.12", ignoreCase = true) }
    private val aacMotion2 =
        FloatValue("AAC3.3.13-Motion", 10f, 0.1f, 10f) { modeValue.get().equals("aac3.3.13", ignoreCase = true) }
    private val hypixelBoostMode = ListValue("BoostHypixel-Mode", arrayOf("Default", "MorePackets", "NCP"), "Default") {
        modeValue.get().equals("boosthypixel", ignoreCase = true)
    }
    private val hypixelVisualY =
        BoolValue("BoostHypixel-VisualY", true) { modeValue.get().equals("boosthypixel", ignoreCase = true) }
    private val hypixelC04 =
        BoolValue("BoostHypixel-MoreC04s", false) { modeValue.get().equals("boosthypixel", ignoreCase = true) }

    // Hypixel
    private val hypixelBoost =
        BoolValue("Hypixel-Boost", false) { modeValue.get().equals("hypixel", ignoreCase = true) }
    private val hypixelBoostDelay =
        IntegerValue("Hypixel-BoostDelay", 1200, 0, 2000) { modeValue.get().equals("hypixel", ignoreCase = true) }
    private val hypixelBoostTimer =
        FloatValue("Hypixel-BoostTimer", 1f, 0f, 5f) { modeValue.get().equals("hypixel", ignoreCase = true) }
    private val mineplexSpeedValue =
        FloatValue("MineplexSpeed", 1f, 0.5f, 10f) { modeValue.get().equals("mineplex", ignoreCase = true) }
    private val neruxVaceTicks =
        IntegerValue("NeruxVace-Ticks", 6, 0, 20) { modeValue.get().equals("neruxvace", ignoreCase = true) }
    private val fakeSprintingValue =
        BoolValue("FakeSprinting", false) { modeValue.get().lowercase(Locale.getDefault()).contains("exploit") }
    private val fakeNoMoveValue =
        BoolValue("FakeNoMove", false) { modeValue.get().lowercase(Locale.getDefault()).contains("exploit") }

    // Visuals
    private val fakeDmgValue = BoolValue("FakeDamage", false)
    private val bobbingValue = BoolValue("Bobbing", false)
    private val bobbingAmountValue = FloatValue("BobbingAmount", 0.07f, 0f, 1f) { bobbingValue.get() }
    private val markValue = BoolValue("Mark", false)
    private val flyTimer = MSTimer()
    private val groundTimer = MSTimer()
    private val boostTimer = MSTimer()
    private val mineSecureVClipTimer = MSTimer()
    private val mineplexTimer = MSTimer()
    private val spartanTimer = TickTimer()
    private val verusTimer = TickTimer()
    private val hypixelTimer = TickTimer()
    private val tickTimer = TickTimer()
    private val cubecraftTeleportTickTimer = TickTimer()
    private val freeHypixelTimer = TickTimer()
    private val aac5C03List = ArrayList<C03PacketPlayer>()
    var wdState = 0
    var wdTick = 0
    private val lastPosition: BlockPos? = null
    private val timer = MSTimer()
    private val packetLol = LinkedList<C0FPacketConfirmTransaction>()
    private var flag = false
    private var startY = 0.0
    private var shouldFakeJump = false
    private var shouldActive = false
    private var noPacketModify = false
    private var isBoostActive = false
    private var noFlag = false
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
    private val lastValue = false
    private val teleport = false
    private var waitFlag = false
    private var canGlide = false
    private var ticks = 0
    private var expectItemStack = -1
    private var aacJump = 0.0
    private var aac3delay = 0
    private var aac3glideDelay = 0
    private var minesuchtTP: Long = 0
    private var boostHypixelState = 1
    private var lastDistance = 0.0
    private var failedStart = false
    private val freeHypixelYaw = 0f
    private val freeHypixelPitch = 0f
    private fun doMove(h: Double, v: Double) {
        if (mc.thePlayer == null) return
        val x = mc.thePlayer.posX
        val y = mc.thePlayer.posY
        val z = mc.thePlayer.posZ
        val yaw = Math.toRadians(mc.thePlayer.rotationYaw.toDouble())
        val expectedX = x + -Math.sin(yaw) * h
        val expectedY = y + v
        val expectedZ = z + Math.cos(yaw) * h
        mc.netHandler.addToSendQueue(C04PacketPlayerPosition(expectedX, expectedY, expectedZ, mc.thePlayer.onGround))
        mc.thePlayer.setPosition(expectedX, expectedY, expectedZ)
    }

    fun isInventory(action: Short): Boolean = action > 0 && action < 100

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
        val expectedX = -Math.sin(yaw) * h
        val expectedZ = Math.cos(yaw) * h
        return doubleArrayOf(expectedX, v, expectedZ)
    }

    override fun onEnable() {
        if (mc.thePlayer == null) return
        noPacketModify = true
        verusTimer.reset()
        flyTimer.reset()
        bypassValue = 0.0
        packetLol.clear()
        shouldFakeJump = false
        shouldActive = true
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

            "vulcan1" -> {
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

            "vulcan2" -> {
                if (mc.thePlayer.onGround) {
                    PacketUtils.sendPacketNoEvent(
                        C04PacketPlayerPosition(
                            mc.thePlayer.posX,
                            mc.thePlayer.posY - 2 + Math.random() / 2,
                            mc.thePlayer.posZ,
                            false
                        )
                    )
                } else {
                    state = false
                }
            }

            "blockdrop" -> {
                startVec = Vec3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ)
                rotationVec = Vector2f(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch)
            }

            "oldncp" -> {
                if (startY > mc.thePlayer.posY) mc.thePlayer.motionY = -0.000000000000000000000000000000001
                if (mc.gameSettings.keyBindSneak.isKeyDown) mc.thePlayer.motionY = -0.2
                if (mc.gameSettings.keyBindJump.isKeyDown && mc.thePlayer.posY < startY - 0.1) mc.thePlayer.motionY =
                    0.2
                MovementUtils.strafe()
            }

            "latestspartan" -> {
                mc.thePlayer.sendQueue.addToSendQueue(
                    C04PacketPlayerPosition(
                        mc.thePlayer.posX,
                        mc.thePlayer.posY,
                        mc.thePlayer.posZ,
                        true
                    )
                )
                mc.thePlayer.sendQueue.addToSendQueue(
                    C04PacketPlayerPosition(
                        mc.thePlayer.posX,
                        mc.thePlayer.posY + 1,
                        mc.thePlayer.posZ,
                        true
                    )
                )
                mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 1.0, mc.thePlayer.posZ)
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
                mc.thePlayer.swingItem()
            }

            "funcraft" -> {
                if (mc.thePlayer.onGround) mc.thePlayer.jump()
                moveSpeed = 1.2
            }

            "zoom" -> {
                NightX.moduleManager.getModule(Damage::class.java)!!.state = true
                if (mc.thePlayer.onGround) mc.thePlayer.jump()
                moveSpeed = 2.0
            }

            "slime" -> {
                expectItemStack = slimeSlot
                if (expectItemStack == -1) {
                    NightX.hud.addNotification(Notification("The fly requires slime blocks to be activated properly."))
                }
                if (mc.thePlayer.onGround) {
                    mc.thePlayer.jump()
                    wdState = 1
                }
            }

            "boosthypixel" -> {
                if (!mc.thePlayer.onGround)
                    if (hypixelC04.get()) {
                        var i = 0
                        while (i < 10) {
                            //Imagine flagging to NCP.
                            mc.netHandler.addToSendQueue(
                                C04PacketPlayerPosition(
                                    mc.thePlayer.posX,
                                    mc.thePlayer.posY,
                                    mc.thePlayer.posZ,
                                    true
                                )
                            )
                            i++
                        }
                    }
                if (hypixelBoostMode.get().equals("ncp", ignoreCase = true)) {
                    var i = 0
                    while (i < 65) {
                        mc.netHandler.addToSendQueue(
                            C04PacketPlayerPosition(
                                mc.thePlayer.posX,
                                mc.thePlayer.posY + 0.049,
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
                        i++
                    }
                } else {
                    var fallDistance = if (hypixelBoostMode.get()
                            .equals("morepackets", ignoreCase = true)
                    ) 3.4025 else 3.0125 //add 0.0125 to ensure we get the fall dmg
                    while (fallDistance > 0) {
                        mc.netHandler.addToSendQueue(
                            C04PacketPlayerPosition(
                                mc.thePlayer.posX,
                                mc.thePlayer.posY + 0.0624986421,
                                mc.thePlayer.posZ,
                                false
                            )
                        )
                        mc.netHandler.addToSendQueue(
                            C04PacketPlayerPosition(
                                mc.thePlayer.posX,
                                mc.thePlayer.posY + 0.0625,
                                mc.thePlayer.posZ,
                                false
                            )
                        )
                        mc.netHandler.addToSendQueue(
                            C04PacketPlayerPosition(
                                mc.thePlayer.posX,
                                mc.thePlayer.posY + 0.0624986421,
                                mc.thePlayer.posZ,
                                false
                            )
                        )
                        mc.netHandler.addToSendQueue(
                            C04PacketPlayerPosition(
                                mc.thePlayer.posX,
                                mc.thePlayer.posY + 0.0000013579,
                                mc.thePlayer.posZ,
                                false
                            )
                        )
                        fallDistance -= 0.0624986421
                    }
                }
                mc.netHandler.addToSendQueue(
                    C04PacketPlayerPosition(
                        mc.thePlayer.posX,
                        mc.thePlayer.posY,
                        mc.thePlayer.posZ,
                        true
                    )
                )
                if (hypixelVisualY.get()) {
                    mc.thePlayer.jump()
                    mc.thePlayer.posY += 0.42 // Visual
                }
                boostHypixelState = 1
                moveSpeed = 0.1
                lastDistance = 0.0
                failedStart = false
            }
        }
        startY = mc.thePlayer.posY
        noPacketModify = false
        aacJump = -3.8
        if (!mode.equals("slime", ignoreCase = true) && !mode.equals("exploit", ignoreCase = true)
            && !mode.equals("bugspartan", ignoreCase = true) && !mode.equals(
                "verus",
                ignoreCase = true
            ) && !mode.lowercase(
                Locale.getDefault()
            ).contains("hypixel")
            && fakeDmgValue.get()
        ) {
            mc.thePlayer.handleStatusUpdate(2.toByte())
        }
        super.onEnable()
    }

    override fun onDisable() {
        val speed = NightX.moduleManager.getModule(
            Speed::class.java
        )
        wasDead = false
        packetLol.clear()
        mc.thePlayer?.noClip = false
        if (mc.thePlayer == null) return
        noFlag = false
        val mode = modeValue.get()
        if (!speed!!.state && !mode.uppercase(Locale.getDefault()).startsWith("NCP") && !mode.equals(
                "float",
                ignoreCase = true
            ) && !mode.equals("veruslowhop", ignoreCase = true) && !mode.equals(
                "aac1.9.10",
                ignoreCase = true
            ) && !mode.equals(
                "aac3.3.12",
                ignoreCase = true
            ) && !mode.equals("aac3.3.12-glide", ignoreCase = true) && !mode.equals(
                "oldncp",
                ignoreCase = true
            ) && !mode.equals(
                "teleportrewinside",
                ignoreCase = true
            ) && !mode.equals(
                "neruxvace",
                ignoreCase = true
            ) && !mode.equals("minesucht", ignoreCase = true) && !mode.equals(
                "spartan1",
                ignoreCase = true
            ) && !mode.equals("spartan2", ignoreCase = true) && !mode.equals(
                "hypixel",
                ignoreCase = true
            ) && !mode.equals("hawkeye", ignoreCase = true) && !mode.equals(
                "hac",
                ignoreCase = true
            ) && !mode.equals("watchcat", ignoreCase = true) && !mode.equals(
                "slime",
                ignoreCase = true
            ) && !mode.equals("jetpack", ignoreCase = true) && !mode.equals("clip", ignoreCase = true) && !mode.equals(
                "jump",
                ignoreCase = true
            ) && !mode.equals("derp", ignoreCase = true) && !mode.equals("collide", ignoreCase = true)
        ) {
            MovementUtils.strafe(0.2f)
        }
        if (mode.equals("AAC5-Vanilla", ignoreCase = true) && !mc.isIntegratedServerRunning) {
            sendAAC5Packets()
        }
        mc.thePlayer.capabilities.isFlying = false
        mc.timer.timerSpeed = 1f
        mc.thePlayer.speedInAir = 0.02f
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
            "motion", "latestspartan" -> {
                mc.thePlayer.capabilities.isFlying = false
                mc.thePlayer.motionY = vanillaMotionYValue.get().toDouble()
                mc.thePlayer.motionX = 0.0
                mc.thePlayer.motionZ = 0.0
                if (mc.gameSettings.keyBindJump.isKeyDown) {
                    mc.thePlayer.motionY += vanillaVSpeed.toDouble()
                }
                if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
                    mc.thePlayer.motionY -= vanillaVSpeed.toDouble()
                    mc.gameSettings.keyBindSneak.pressed = false
                }
                MovementUtils.strafe(vanillaSpeed)
                handleVanillaKickBypass()
            }

            "vulcan2" -> {
                mc.gameSettings.keyBindJump.pressed = false
                mc.gameSettings.keyBindSneak.pressed = false
                MovementUtils.strafe((1.2 + Math.random() / 10).toFloat())
                if (mc.gameSettings.keyBindJump.pressed) {
                    mc.thePlayer.motionY = 0.42
                } else if (mc.gameSettings.keyBindSneak.pressed) {
                    mc.thePlayer.motionY = 0.42
                } else {
                    mc.thePlayer.motionY = 0.0
                }
            }

            "desync" -> {
                mc.thePlayer.noClip = true
                mc.thePlayer.onGround = false

                mc.thePlayer.capabilities.isFlying = false
                mc.thePlayer.motionX = 0.0
                mc.thePlayer.motionY = 0.0
                mc.thePlayer.motionZ = 0.0

                if (mc.gameSettings.keyBindJump.isKeyDown) {
                    mc.thePlayer.motionY += vanillaVSpeed.toDouble()
                }
                if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
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
                var x = -Math.sin(playerYaw) * 0.2873
                var z = Math.cos(playerYaw) * 0.2873

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

                if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
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
                if (mc.gameSettings.keyBindJump.isKeyDown) {
                    mc.thePlayer.motionY += vanillaVSpeed.toDouble()
                }
                if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
                    mc.thePlayer.motionY -= vanillaVSpeed.toDouble()
                    mc.gameSettings.keyBindSneak.pressed = false
                }
                MovementUtils.strafe(vanillaSpeed)
            }

            "cubecraft" -> {
                mc.timer.timerSpeed = 0.6f
                cubecraftTeleportTickTimer.update()
            }

            "ncp" -> {
                mc.thePlayer.motionY = -ncpMotionValue.get().toDouble()
                if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
                    mc.thePlayer.motionY = -0.5
                    mc.gameSettings.keyBindSneak.pressed = false
                }
                MovementUtils.strafe()
            }

            "oldncp" -> {
                if (startY > mc.thePlayer.posY) mc.thePlayer.motionY = -0.000000000000000000000000000000001
                if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
                    mc.thePlayer.motionY = -0.2
                    mc.gameSettings.keyBindSneak.pressed = false
                }
                if (mc.gameSettings.keyBindJump.isKeyDown && mc.thePlayer.posY < startY - 0.1) {
                    mc.thePlayer.motionY = 0.2
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

            "derp", "aac5-vanilla", "bugspartan" -> {
                mc.thePlayer.capabilities.isFlying = false
                mc.thePlayer.motionY = 0.0
                mc.thePlayer.motionX = 0.0
                mc.thePlayer.motionZ = 0.0
                if (mc.gameSettings.keyBindJump.isKeyDown) {
                    mc.thePlayer.motionY += vanillaVSpeed.toDouble()
                }
                if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
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
                    var motion = 0f
                    motion = if (verusBoostModeValue.get()
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
                handleVanillaKickBypass()
            }

            "aac1.9.10" -> {
                if (mc.gameSettings.keyBindJump.isKeyDown) {
                    aacJump += 0.2
                }
                if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
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

            "aac3.1.6-gomme" -> {
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
                        mc.thePlayer.posY + (if (mc.gameSettings.keyBindJump.isKeyDown) 1.5624 else 0.00000001) - if (mc.gameSettings.keyBindSneak.isKeyDown) 0.0624 else 0.00000002,
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
                if (mc.gameSettings.keyBindJump.isKeyDown) {
                    mc.thePlayer.motionY += vanillaSpeed.toDouble()
                }
                if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
                    mc.thePlayer.motionY -= vanillaSpeed.toDouble()
                }
                MovementUtils.strafe(vanillaSpeed)
            }

            "minesecure" -> {
                mc.thePlayer.capabilities.isFlying = false
                if (!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
                    mc.thePlayer.motionY = -0.01
                }
                mc.thePlayer.motionX = 0.0
                mc.thePlayer.motionZ = 0.0
                MovementUtils.strafe(vanillaSpeed)
                if (mineSecureVClipTimer.hasTimePassed(150) && mc.gameSettings.keyBindJump.isKeyDown) {
                    mc.netHandler.addToSendQueue(
                        C04PacketPlayerPosition(
                            mc.thePlayer.posX,
                            mc.thePlayer.posY + 5,
                            mc.thePlayer.posZ,
                            false
                        )
                    )
                    mc.netHandler.addToSendQueue(C04PacketPlayerPosition(0.5, -1000.0, 0.5, false))
                    val yaw = Math.toRadians(mc.thePlayer.rotationYaw.toDouble())
                    val x = -Math.sin(yaw) * 0.4
                    val z = Math.cos(yaw) * 0.4
                    mc.thePlayer.setPosition(mc.thePlayer.posX + x, mc.thePlayer.posY, mc.thePlayer.posZ + z)
                    mineSecureVClipTimer.reset()
                }
            }

            "hac" -> {
                mc.thePlayer.motionX *= 0.8
                mc.thePlayer.motionZ *= 0.8
                mc.thePlayer.motionY = if (mc.thePlayer.motionY <= -0.42) 0.42 else -0.42
            }

            "hawkeye" -> mc.thePlayer.motionY = if (mc.thePlayer.motionY <= -0.42) 0.42 else -0.42
            "teleportrewinside" -> {
                val vectorStart = Vec3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ)
                val yaw = -mc.thePlayer.rotationYaw
                val pitch = -mc.thePlayer.rotationPitch
                val length = 9.9
                val vectorEnd = Vec3(
                    Math.sin(Math.toRadians(yaw.toDouble())) * Math.cos(Math.toRadians(pitch.toDouble())) * length + vectorStart.xCoord,
                    Math.sin(Math.toRadians(pitch.toDouble())) * length + vectorStart.yCoord,
                    Math.cos(Math.toRadians(yaw.toDouble())) * Math.cos(Math.toRadians(pitch.toDouble())) * length + vectorStart.zCoord
                )
                mc.netHandler.addToSendQueue(
                    C04PacketPlayerPosition(
                        vectorEnd.xCoord,
                        mc.thePlayer.posY + 2,
                        vectorEnd.zCoord,
                        true
                    )
                )
                mc.netHandler.addToSendQueue(
                    C04PacketPlayerPosition(
                        vectorStart.xCoord,
                        mc.thePlayer.posY + 2,
                        vectorStart.zCoord,
                        true
                    )
                )
                mc.thePlayer.motionY = 0.0
            }

            "minesucht" -> {
                val posX = mc.thePlayer.posX
                val posY = mc.thePlayer.posY
                val posZ = mc.thePlayer.posZ
                if (!mc.gameSettings.keyBindForward.isKeyDown)
                    if (System.currentTimeMillis() - minesuchtTP > 99) {
                        val vec3 = mc.thePlayer.getPositionEyes(0f)
                        val vec31 = mc.thePlayer.getLook(0f)
                        val vec32 = vec3.addVector(vec31.xCoord * 7, vec31.yCoord * 7, vec31.zCoord * 7)
                        if (mc.thePlayer.fallDistance > 0.8) {
                            mc.thePlayer.sendQueue.addToSendQueue(C04PacketPlayerPosition(posX, posY + 50, posZ, false))
                            mc.thePlayer.fall(100f, 100f)
                            mc.thePlayer.fallDistance = 0f
                            mc.thePlayer.sendQueue.addToSendQueue(C04PacketPlayerPosition(posX, posY + 20, posZ, true))
                        }
                        mc.thePlayer.sendQueue.addToSendQueue(
                            C04PacketPlayerPosition(
                                vec32.xCoord,
                                mc.thePlayer.posY + 50,
                                vec32.zCoord,
                                true
                            )
                        )
                        mc.thePlayer.sendQueue.addToSendQueue(C04PacketPlayerPosition(posX, posY, posZ, false))
                        mc.thePlayer.sendQueue.addToSendQueue(
                            C04PacketPlayerPosition(
                                vec32.xCoord,
                                posY,
                                vec32.zCoord,
                                true
                            )
                        )
                        mc.thePlayer.sendQueue.addToSendQueue(C04PacketPlayerPosition(posX, posY, posZ, false))
                        minesuchtTP = System.currentTimeMillis()
                    } else {
                        mc.thePlayer.sendQueue.addToSendQueue(
                            C04PacketPlayerPosition(
                                mc.thePlayer.posX,
                                mc.thePlayer.posY,
                                mc.thePlayer.posZ,
                                false
                            )
                        )
                        mc.thePlayer.sendQueue.addToSendQueue(C04PacketPlayerPosition(posX, posY, posZ, true))
                    }
            }

            "jetpack" -> {
                if (mc.gameSettings.keyBindJump.isKeyDown) {
                    mc.thePlayer.motionY += 0.15
                    mc.thePlayer.motionX *= 1.1
                    mc.thePlayer.motionZ *= 1.1
                }
                if (!mc.thePlayer.onGround) mc.effectRenderer.spawnEffectParticle(
                    EnumParticleTypes.FLAME.particleID,
                    mc.thePlayer.posX,
                    mc.thePlayer.posY + 0.2,
                    mc.thePlayer.posZ,
                    -mc.thePlayer.motionX,
                    -0.5,
                    -mc.thePlayer.motionZ
                )
            }

            "mineplex" -> if (mc.thePlayer.inventory.getCurrentItem() == null) {
                if (mc.gameSettings.keyBindJump.isKeyDown && mineplexTimer.hasTimePassed(100)) {
                    mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 1, mc.thePlayer.posZ)
                    mineplexTimer.reset()
                }
                if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && mineplexTimer.hasTimePassed(100)) {
                    mc.gameSettings.keyBindSneak.pressed = false
                    mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - 0.6, mc.thePlayer.posZ)
                    mineplexTimer.reset()
                }
                val blockPos = BlockPos(mc.thePlayer.posX, mc.thePlayer.entityBoundingBox.minY - 1, mc.thePlayer.posZ)
                val vec = Vec3(blockPos).addVector(0.4, 0.4, 0.4).add(Vec3(EnumFacing.UP.directionVec))
                mc.playerController.onPlayerRightClick(
                    mc.thePlayer,
                    mc.theWorld,
                    mc.thePlayer.inventory.getCurrentItem(),
                    blockPos,
                    EnumFacing.UP,
                    Vec3(vec.xCoord * 0.4f, vec.yCoord * 0.4f, vec.zCoord * 0.4f)
                )
                MovementUtils.strafe(0.27f)
                mc.timer.timerSpeed = 1 + mineplexSpeedValue.get()
            } else {
                mc.timer.timerSpeed = 1f
                state = false
                ClientUtils.displayChatMessage(NightX.CLIENT_CHAT + "§aSelect an empty slot to fly.")
            }

            "aac3.3.12" -> {
                if (mc.thePlayer.posY < -70) mc.thePlayer.motionY = aacMotion.get().toDouble()
                mc.timer.timerSpeed = 1f
                if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
                    mc.timer.timerSpeed = 0.2f
                    mc.rightClickDelayTimer = 0
                }
            }

            "aac3.3.12-glide" -> {
                if (!mc.thePlayer.onGround) aac3glideDelay++
                if (aac3glideDelay == 2) mc.timer.timerSpeed = 1f
                if (aac3glideDelay == 12) mc.timer.timerSpeed = 0.1f
                if (aac3glideDelay >= 12 && !mc.thePlayer.onGround) {
                    aac3glideDelay = 0
                    mc.thePlayer.motionY = .015
                }
            }

            "aac3.3.13" -> {
                if (mc.thePlayer.isDead) wasDead = true
                if (wasDead || mc.thePlayer.onGround) {
                    wasDead = false
                    mc.thePlayer.motionY = aacMotion2.get().toDouble()
                    mc.thePlayer.onGround = false
                }
                mc.timer.timerSpeed = 1f
                if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
                    mc.timer.timerSpeed = 0.2f
                    mc.rightClickDelayTimer = 0
                }
            }

            "watchcat" -> {
                MovementUtils.strafe(0.15f)
                if (mc.thePlayer.posY < startY + 2) {
                    mc.thePlayer.motionY = Math.random() * 0.5
                }
                if (startY > mc.thePlayer.posY) MovementUtils.strafe(0f)
            }

            "spartan1" -> {
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

            "spartan2" -> {
                MovementUtils.strafe(0.264f)
                if (mc.thePlayer.ticksExisted % 8 == 0) mc.thePlayer.sendQueue.addToSendQueue(
                    C04PacketPlayerPosition(
                        mc.thePlayer.posX,
                        mc.thePlayer.posY + 10,
                        mc.thePlayer.posZ,
                        true
                    )
                )
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
                        NightX.hud.addNotification(
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
                    if (mc.gameSettings.keyBindJump.isKeyDown) {
                        mc.thePlayer.motionY += vanillaSpeed.toDouble()
                    }
                    if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
                        mc.thePlayer.motionY -= vanillaSpeed.toDouble()
                        mc.gameSettings.keyBindSneak.pressed = false
                    }
                    MovementUtils.strafe(vanillaSpeed)
                }
            }

            "jump" -> if (mc.thePlayer.onGround) mc.thePlayer.jump()
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

            "hypixel" -> {
                val boostDelay = hypixelBoostDelay.get()
                if (hypixelBoost.get() && !flyTimer.hasTimePassed(boostDelay.toLong())) {
                    mc.timer.timerSpeed = 1f + hypixelBoostTimer.get() * (flyTimer.hasTimeLeft(boostDelay.toLong())
                        .toFloat() / boostDelay.toFloat())
                }
                hypixelTimer.update()
                if (hypixelTimer.hasTimePassed(2)) {
                    mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 1.0E-5, mc.thePlayer.posZ)
                    hypixelTimer.reset()
                }
            }
        }
    }

    @EventTarget
    fun onMotion(event: MotionEvent) {
        if (mc.thePlayer == null) return
        if (bobbingValue.get()) {
            mc.thePlayer.cameraYaw = bobbingAmountValue.get()
        }
        if (modeValue.get().equals("boosthypixel", ignoreCase = true)) {
            when (event.eventState) {
                EventState.PRE -> {
                    hypixelTimer.update()
                    if (hypixelTimer.hasTimePassed(2)) {
                        mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 1.0E-5, mc.thePlayer.posZ)
                        hypixelTimer.reset()
                    }
                    if (!failedStart) mc.thePlayer.motionY = 0.0
                }

                EventState.POST -> {
                    val xDist = mc.thePlayer.posX - mc.thePlayer.prevPosX
                    val zDist = mc.thePlayer.posZ - mc.thePlayer.prevPosZ
                    lastDistance = Math.sqrt(xDist * xDist + zDist * zDist)
                }
            }
        }
        if (event.eventState == EventState.PRE && modeValue.get().equals("vulcan1", ignoreCase = true)) {
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
        if (event.eventState == EventState.PRE && !mc.thePlayer.onGround && modeValue.get()
                .equals("purpleprison", ignoreCase = true)
        ) {
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
        if (modeValue.get().equals("blockdrop", ignoreCase = true)) {
            when (event.eventState) {
                EventState.PRE -> {
                    mc.thePlayer.motionY =
                        if (mc.gameSettings.keyBindJump.isKeyDown) 2.0 else if (mc.gameSettings.keyBindJump.isKeyDown) -2.0 else 0.0
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
        when (modeValue.get().lowercase(Locale.getDefault())) {
            "funcraft" -> {
                if (mc.thePlayer.onGround) {
                    mc.timer.timerSpeed = 1f
                }
                if (MovementUtils.isMoving() && !mc.thePlayer.onGround) {
                    event.onGround = true
                    mc.timer.timerSpeed = 1.8f
                }
                if (!MovementUtils.isMoving()) moveSpeed = 0.25
                if (moveSpeed > 0.25) {
                    moveSpeed -= moveSpeed / 159.0
                }
                if (event.eventState === EventState.POST) {
                    mc.thePlayer.capabilities.isFlying = false
                    mc.thePlayer.motionY = 0.0
                    mc.thePlayer.motionX = 0.0
                    mc.thePlayer.motionZ = 0.0
                    MovementUtils.strafe(moveSpeed.toFloat())
                    mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - 8e-6, mc.thePlayer.posZ)
                }
            }

            "zonecraft" -> if (event.eventState === EventState.PRE) {
                if (event.onGround) {
                    mc.thePlayer.motionY = 0.42
                } else {
                    mc.thePlayer.motionY = 0.0
                }
                MovementUtils.strafe(0.259f)
                val magicValue = (0.92160f / 8f).toDouble()
                val rounded = mc.thePlayer.posY - mc.thePlayer.posY % magicValue
                mc.thePlayer.setPosition(mc.thePlayer.posX, rounded, mc.thePlayer.posZ)
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

    fun coerceAtMost(value: Double, max: Double): Float {
        return Math.min(value, max).toFloat()
    }

    @EventTarget
    fun onAction(event: ActionEvent) {
        if (modeValue.get().lowercase(Locale.getDefault())
                .contains("exploit") && fakeSprintingValue.get()
        ) event.sprinting = false
    }

    @EventTarget
    fun onRender3D(event: Render3DEvent?) {
        val mode = modeValue.get()
        if (!markValue.get() || mode.equals("Motion", ignoreCase = true) || modeValue.get()
                .equals("blockdrop", ignoreCase = true) || modeValue.get()
                .equals("latestspartan", ignoreCase = true) || mode.equals(
                "Creative",
                ignoreCase = true
            ) || mode.equals(
                "AAC5-Vanilla",
                ignoreCase = true
            ) || mode.equals("Derp", ignoreCase = true) || mode.equals("KeepAlive", ignoreCase = true)
        ) return
        val y = startY + 2.0
        RenderUtils.drawPlatform(
            y,
            if (mc.thePlayer.entityBoundingBox.maxY < y) Color(0, 255, 0, 90) else Color(255, 0, 0, 90),
            1.0
        )
        when (mode.lowercase(Locale.getDefault())) {
            "aac1.9.10" -> RenderUtils.drawPlatform(startY + aacJump, Color(0, 0, 255, 90), 1.0)
            "aac3.3.12" -> RenderUtils.drawPlatform(-70.0, Color(0, 0, 255, 90), 1.0)
        }
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
        if (mode.equals("latestspartan", ignoreCase = true)) {
            if (packet is C03PacketPlayer) {
                packet.onGround = true
            }
        }
        if (mode.equals("desync", ignoreCase = true)) {
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

                var x = packet.getX() - mc.thePlayer.posX
                var y = packet.getY() - mc.thePlayer.posY
                var z = packet.getZ() - mc.thePlayer.posZ
                var diff = sqrt(x * x + y * y + z * z)
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
        if (mode.equals("vulcan1", ignoreCase = true)) {
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
        if (mode.equals("blockdrop", ignoreCase = true)) {
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
        if (packet is S08PacketPlayerPosLook && mode.equals("vulcan2", ignoreCase = true)) {
            if (packet is C03PacketPlayer) {
                event.cancelEvent()
            }
            if (packet is S08PacketPlayerPosLook) {
                if (!flag) {
                    val deltaX = packet.x - mc.thePlayer.posX
                    val deltaY = packet.y - mc.thePlayer.posY
                    val deltaZ = packet.z - mc.thePlayer.posZ

                    if (sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ) < 10) {
                        event.cancelEvent()
                        PacketUtils.sendPacketNoEvent(
                            C06PacketPlayerPosLook(
                                packet.x,
                                packet.y,
                                packet.z,
                                packet.getYaw(),
                                packet.getPitch(),
                                false
                            )
                        )
                    }
                }
            }
        }
        if (packet is S08PacketPlayerPosLook && mode.equals("exploit", ignoreCase = true) && wdState == 3) {
            wdState = 4
            if (boostTimer.hasTimePassed(8000L)) {
                NightX.hud.addNotification(Notification("Exploit activated.", Notification.Type.SUCCESS))
                boostTimer.reset()
            } else {
                NightX.hud.addNotification(Notification("Exploit activated.", Notification.Type.SUCCESS))
            }
            if (fakeDmgValue.get() && mc.thePlayer != null) mc.thePlayer.handleStatusUpdate(2.toByte())
        }
        if (packet is C09PacketHeldItemChange && mode.equals(
                "slime",
                ignoreCase = true
            ) && wdState < 4
        ) event.cancelEvent()
        if (packet is S08PacketPlayerPosLook) {
            if (mode.equals("slime", ignoreCase = true) && wdState == 3) {
                wdState = 4
                if (fakeDmgValue.get() && mc.thePlayer != null) mc.thePlayer.handleStatusUpdate(2.toByte())
            }
            if (mode.equals("pearl", ignoreCase = true) && pearlActivateCheck.get()
                    .equals("teleport", ignoreCase = true) && pearlState == 1
            ) pearlState = 2
            if (mode.equals("BoostHypixel", ignoreCase = true)) {
                failedStart = true
            }
        }
        if (packet is C03PacketPlayer) {
            val packetPlayer = packet
            if (mode.equals("NCP", ignoreCase = true) || mode.equals(
                    "Mineplex",
                    ignoreCase = true
                ) && mc.thePlayer.inventory.getCurrentItem() == null || mode.equals(
                    "Verus",
                    ignoreCase = true
                ) && verusSpoofGround.get() && verusDmged
            ) packetPlayer.onGround = true
            if (mode.equals("Hypixel", ignoreCase = true) || mode.equals(
                    "BoostHypixel",
                    ignoreCase = true
                )
            ) packetPlayer.onGround = false
            if (mode.equals("Derp", ignoreCase = true)) {
                packetPlayer.yaw = RandomUtils.nextFloat(0f, 360f)
                packetPlayer.pitch = RandomUtils.nextFloat(-90f, 90f)
            }
            if (mode.equals("AAC5-Vanilla", ignoreCase = true) && !mc.isIntegratedServerRunning) {
                if (aac5NofallValue.get()) packetPlayer.onGround = true
                aac5C03List.add(packetPlayer)
                event.cancelEvent()
                if (aac5C03List.size > aac5PursePacketsValue.get()) sendAAC5Packets()
            }
            if (mode.equals("clip", ignoreCase = true) && clipGroundSpoof.get()) packetPlayer.onGround = true
            if ((mode.equals("motion", ignoreCase = true) || mode.equals(
                    "creative",
                    ignoreCase = true
                )) && groundSpoofValue.get()
            ) packetPlayer.onGround = true
            if (verusDmgModeValue.get().equals("Jump", ignoreCase = true) && verusJumpTimes < 5 && mode.equals(
                    "Verus",
                    ignoreCase = true
                )
            ) {
                packetPlayer.onGround = false
            }
            if (mode.equals("exploit", ignoreCase = true)) {
                if (wdState == 2) {
                    packetPlayer.y -= 0.187
                    wdState++
                }
                if (wdState > 3) {
                    if (fakeNoMoveValue.get()) packetPlayer.isMoving = false
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
            "veruslowhop" -> if (!mc.gameSettings.keyBindSneak.isKeyDown && !mc.thePlayer.isInWeb && !mc.thePlayer.isInLava && !mc.thePlayer.isInWater && !mc.thePlayer.isOnLadder && !mc.gameSettings.keyBindJump.isKeyDown && mc.thePlayer.ridingEntity == null) {
                if (!mc.gameSettings.keyBindSneak.isKeyDown && MovementUtils.isMoving()) {
                    mc.gameSettings.keyBindJump.pressed = false
                    if (!mc.gameSettings.keyBindSneak.isKeyDown && mc.thePlayer.onGround) {
                        mc.thePlayer.jump()
                        mc.thePlayer.motionY = 0.0
                        MovementUtils.strafe(0.61f)
                        event.y = 0.41999998688698
                    }
                    MovementUtils.strafe()
                }
            }

            "slime" -> if (wdState < 4) event.zeroXZ()
            "cubecraft" -> {
                val yaw = Math.toRadians(mc.thePlayer.rotationYaw.toDouble())
                if (cubecraftTeleportTickTimer.hasTimePassed(2)) {
                    event.x = -Math.sin(yaw) * 2.4
                    event.z = Math.cos(yaw) * 2.4
                    cubecraftTeleportTickTimer.reset()
                } else {
                    event.x = -Math.sin(yaw) * 0.2
                    event.z = Math.cos(yaw) * 0.2
                }
            }

            "boosthypixel" -> {
                if (!MovementUtils.isMoving()) {
                    event.x = 0.0
                    event.z = 0.0
                }
                val amplifier: Double = 1.0
                if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) 1.2 *
                        (mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).amplifier + 1) else 0.0
                val baseSpeed = 0.29 * amplifier
                when (boostHypixelState) {
                    1 -> {
                        moveSpeed = (if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) 1.56 else 2.034) * baseSpeed
                        boostHypixelState = 2
                    }

                    2 -> {
                        moveSpeed *= 2.16
                        boostHypixelState = 3
                    }

                    3 -> {
                        moveSpeed =
                            lastDistance - (if (mc.thePlayer.ticksExisted % 2 == 0) 0.0103 else 0.0123) * (lastDistance - baseSpeed)
                        boostHypixelState = 4
                    }

                    else -> moveSpeed = lastDistance - lastDistance / 159.8
                }
                moveSpeed = Math.max(moveSpeed, 0.3)
                val yaw = MovementUtils.getDirection()
                event.x = -Math.sin(yaw) * moveSpeed
                event.z = Math.cos(yaw) * moveSpeed
                mc.thePlayer.motionX = event.x
                mc.thePlayer.motionZ = event.z
            }
        }
    }

    @EventTarget
    fun onBB(event: BlockBBEvent) {
        if (mc.thePlayer == null) return
        val mode = modeValue.get()
        if (event.block is BlockAir && mode.equals("Jump", ignoreCase = true) && event.y < startY) event.boundingBox =
            AxisAlignedBB.fromBounds(
                event.x.toDouble(),
                event.y.toDouble(),
                event.z.toDouble(),
                (event.x + 1).toDouble(),
                startY,
                (event.z + 1).toDouble()
            )
        if (event.block is BlockAir && (mode.equals(
                "collide",
                ignoreCase = true
            ) && !mc.thePlayer.isSneaking || mode.equals(
                "mineplex",
                ignoreCase = true
            ) && mc.thePlayer.inventory.getCurrentItem() == null || mode.equals(
                "Hypixel",
                ignoreCase = true
            ) || mode.equals(
                "veruslowhop",
                ignoreCase = true
            ) && !mc.gameSettings.keyBindSneak.isKeyDown)
        ) event.boundingBox = AxisAlignedBB(-2.0, -1.0, -2.0, 2.0, 1.0, 2.0).offset(
            event.x.toDouble(),
            event.y.toDouble(),
            event.z.toDouble()
        )
        if (event.block is BlockAir && (mode.equals("BoostHypixel", ignoreCase = true) || mode.equals(
                "Verus",
                ignoreCase = true
            )) && (verusDmgModeValue.get()
                .equals("none", ignoreCase = true) || verusDmged) && event.y < mc.thePlayer.posY
        ) event.boundingBox = AxisAlignedBB.fromBounds(
            event.x.toDouble(),
            event.y.toDouble(),
            event.z.toDouble(),
            (event.x + 1).toDouble(),
            mc.thePlayer.posY,
            (event.z + 1).toDouble()
        )
    }

    @EventTarget
    fun onJump(e: JumpEvent) {
        val mode = modeValue.get()
        if (mode.equals("Hypixel", ignoreCase = true) || mode.equals("BoostHypixel", ignoreCase = true) || mode.equals(
                "Mineplex",
                ignoreCase = true
            ) && mc.thePlayer.inventory.getCurrentItem() == null || mode.equals(
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
        if (mode.equals("Hypixel", ignoreCase = true) || mode.equals("BoostHypixel", ignoreCase = true) || mode.equals(
                "Mineplex",
                ignoreCase = true
            ) && mc.thePlayer.inventory.getCurrentItem() == null || mode.equals(
                "funcraft",
                ignoreCase = true
            ) || mode.equals("exploit", ignoreCase = true) && wdState > 2 || mode.equals("slime", ignoreCase = true)
        ) e.stepHeight = 0f
    }

    private fun handleVanillaKickBypass() {
        if (!vanillaKickBypassValue.get() || !groundTimer.hasTimePassed(1000)) return
        val ground = calculateGround()
        run {
            var posY = mc.thePlayer.posY
            while (posY > ground) {
                mc.netHandler.addToSendQueue(C04PacketPlayerPosition(mc.thePlayer.posX, posY, mc.thePlayer.posZ, true))
                if (posY - 8.0 < ground) break // Prevent next step
                posY -= 8.0
            }
        }
        mc.netHandler.addToSendQueue(C04PacketPlayerPosition(mc.thePlayer.posX, ground, mc.thePlayer.posZ, true))
        var posY = ground
        while (posY < mc.thePlayer.posY) {
            mc.netHandler.addToSendQueue(C04PacketPlayerPosition(mc.thePlayer.posX, posY, mc.thePlayer.posZ, true))
            if (posY + 8.0 > mc.thePlayer.posY) break // Prevent next step
            posY += 8.0
        }
        mc.netHandler.addToSendQueue(
            C04PacketPlayerPosition(
                mc.thePlayer.posX,
                mc.thePlayer.posY,
                mc.thePlayer.posZ,
                true
            )
        )
        groundTimer.reset()
    }

    private fun calculateGround(): Double {
        val playerBoundingBox = mc.thePlayer.entityBoundingBox
        var blockHeight = 1.0
        var ground = mc.thePlayer.posY
        while (ground > 0.0) {
            val customBox = AxisAlignedBB(
                playerBoundingBox.maxX,
                ground + blockHeight,
                playerBoundingBox.maxZ,
                playerBoundingBox.minX,
                ground,
                playerBoundingBox.minZ
            )
            if (mc.theWorld.checkBlockCollision(customBox)) {
                if (blockHeight <= 0.05) return ground + blockHeight
                ground += blockHeight
                blockHeight = 0.05
            }
            ground -= blockHeight
        }
        return 0.0
    }

    private val pearlSlot: Int
        private get() {
            for (i in 36..44) {
                val stack = mc.thePlayer.inventoryContainer.getSlot(i).stack
                if (stack != null && stack.item is ItemEnderPearl) {
                    return i - 36
                }
            }
            return -1
        }
    private val slimeSlot: Int
        private get() {
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
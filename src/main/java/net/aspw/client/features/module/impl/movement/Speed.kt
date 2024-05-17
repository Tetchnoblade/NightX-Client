package net.aspw.client.features.module.impl.movement

import net.aspw.client.Launch
import net.aspw.client.event.*
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.features.module.impl.movement.speeds.SpeedMode
import net.aspw.client.features.module.impl.movement.speeds.aac.*
import net.aspw.client.features.module.impl.movement.speeds.matrix.Matrix670
import net.aspw.client.features.module.impl.movement.speeds.matrix.Matrix692
import net.aspw.client.features.module.impl.movement.speeds.matrix.MatrixHop
import net.aspw.client.features.module.impl.movement.speeds.matrix.MatrixYPort
import net.aspw.client.features.module.impl.movement.speeds.ncp.*
import net.aspw.client.features.module.impl.movement.speeds.server.*
import net.aspw.client.features.module.impl.movement.speeds.spartan.SpartanYPort
import net.aspw.client.features.module.impl.movement.speeds.velocity.Velocity
import net.aspw.client.features.module.impl.movement.speeds.verus.VerusFloat
import net.aspw.client.features.module.impl.movement.speeds.verus.VerusHop
import net.aspw.client.features.module.impl.movement.speeds.verus.VerusLowHop
import net.aspw.client.features.module.impl.movement.speeds.vulcan.VulcanYPort
import net.aspw.client.features.module.impl.movement.speeds.watchdog.WatchdogCustom
import net.aspw.client.features.module.impl.movement.speeds.watchdog.WatchdogGround
import net.aspw.client.features.module.impl.visual.Interface
import net.aspw.client.utils.MovementUtils
import net.aspw.client.value.BoolValue
import net.aspw.client.value.FloatValue
import net.aspw.client.value.IntegerValue
import net.aspw.client.value.ListValue
import net.minecraft.client.settings.GameSettings

@ModuleInfo(name = "Speed", category = ModuleCategory.MOVEMENT)
class Speed : Module() {
    private var wasDown: Boolean = false
    private val speedModes = arrayOf(
        NCPBHop(),
        NCPFHop(),
        SNCPBHop(),
        NCPHop(),
        NCPYPort(),
        AAC4Hop(),
        AAC4SlowHop(),
        AACv4BHop(),
        AACBHop(),
        AAC2BHop(),
        AAC3BHop(),
        AAC4BHop(),
        AAC5BHop(),
        AAC6BHop(),
        AAC7BHop(),
        OldAACBHop(),
        AACPort(),
        AACLowHop(),
        AACLowHop2(),
        AACLowHop3(),
        AACGround(),
        AACGround2(),
        AACHop350(),
        AACHop438(),
        AACYPort(),
        AACYPort2(),
        WatchdogGround(),
        WatchdogCustom(),
        Velocity(),
        SpartanYPort(),
        SlowHop(),
        Custom(),
        Jump(),
        NCPSemiStrafe(),
        NCPBoost(),
        NCPFrame(),
        NCPMiniJump(),
        NCPOnGround(),
        YPort(),
        YPort2(),
        Minemen(),
        NoRules(),
        Sparky(),
        VerusHop(),
        VerusLowHop(),
        VerusFloat(),
        VulcanYPort(),
        MatrixHop(),
        MatrixYPort(),
        Matrix670(),
        Matrix692()
    )
    val typeValue: ListValue = object : ListValue(
        "Type",
        arrayOf(
            "NCP",
            "AAC",
            "Spartan",
            "Watchdog",
            "Verus",
            "Vulcan",
            "Matrix",
            "Custom",
            "Velocity",
            "Server"
        ),
        "Velocity"
    ) {
        override fun onChange(oldValue: String, newValue: String) {
            if (state) onDisable()
        }

        override fun onChanged(oldValue: String, newValue: String) {
            if (state) onEnable()
        }
    }

    var y = 0.0

    @EventTarget
    fun onUpdate(event: UpdateEvent?) {
        if (mc.thePlayer.isSneaking) return
        val speedMode = mode
        speedMode?.onUpdate()
        if (typeValue.get().equals("velocity", true) && !velocityBHop.get()) return
        if (GameSettings.isKeyDown(mc.gameSettings.keyBindJump) && !mc.thePlayer.isInWater && !mc.thePlayer.isInLava && !mc.thePlayer.capabilities.isFlying && MovementUtils.isMoving())
            mc.gameSettings.keyBindJump.pressed = false
    }

    private val ncpModeValue: ListValue = object : ListValue(
        "NCP-Mode",
        arrayOf("BHop", "FHop", "SBHop", "Hop", "SemiStrafe", "YPort", "Boost", "Frame", "MiniJump", "OnGround"),
        "BHop",
        { typeValue.get().equals("ncp", ignoreCase = true) }) {
        override fun onChange(oldValue: String, newValue: String) {
            if (state) onDisable()
        }

        override fun onChanged(oldValue: String, newValue: String) {
            if (state) onEnable()
        }
    }

    @EventTarget
    fun onMotion(event: MotionEvent) {
        if (noBob.get()) {
            mc.thePlayer.cameraPitch = 0f
            mc.thePlayer.cameraYaw = 0f
        }
        if (mc.thePlayer.isSneaking || event.eventState !== EventState.PRE) return
        val speedMode = mode
        if (speedMode != null) {
            speedMode.onMotion(event)
            speedMode.onMotion()
        }
    }

    @EventTarget
    fun onMove(event: MoveEvent?) {
        if (mc.thePlayer.isSneaking) return
        val speedMode = mode
        speedMode?.onMove(event)
    }

    private val aacModeValue: ListValue = object : ListValue(
        "AAC-Mode", arrayOf(
            "4Hop",
            "4SlowHop",
            "v4BHop",
            "BHop",
            "2BHop",
            "3BHop",
            "4BHop",
            "5BHop",
            "6BHop",
            "7BHop",
            "OldBHop",
            "Port",
            "LowHop",
            "LowHop2",
            "LowHop3",
            "Ground",
            "Ground2",
            "Hop3.5.0",
            "Hop4.3.8",
            "YPort",
            "YPort2"
        ), "4Hop", { typeValue.get().equals("aac", ignoreCase = true) }) {
        override fun onChange(oldValue: String, newValue: String) {
            if (state) onDisable()
        }

        override fun onChanged(oldValue: String, newValue: String) {
            if (state) onEnable()
        }
    }

    @EventTarget
    fun onWorld(event: WorldEvent) {
        if (worldCheck.get()) {
            state = false
            chat("Speed was disabled")
        }
    }

    @EventTarget
    fun onTeleport(event: TeleportEvent) {
        if (lagCheck.get()) {
            state = false
            chat("Disabling Speed due to lag back")
            if (Launch.moduleManager.getModule(Interface::class.java)?.flagSoundValue!!.get()) {
                Launch.tipSoundManager.popSound.asyncPlay(Launch.moduleManager.popSoundPower)
            }
        }
    }

    @EventTarget
    fun onTick(event: TickEvent?) {
        if (mc.thePlayer.isSneaking) return
        val speedMode = mode
        speedMode?.onTick()
    }

    @EventTarget
    fun onPacket(event: PacketEvent?) {
        if (mc.thePlayer.isSneaking) return
        val speedMode = mode
        speedMode?.onPacket(event)
    }

    @EventTarget
    fun onJump(event: JumpEvent?) {
        val speedMode = mode
        speedMode?.onJump(event)
    }

    private val hypixelModeValue: ListValue = object : ListValue(
        "Watchdog-Mode",
        arrayOf("Ground", "Custom"),
        "Ground",
        { typeValue.get().equals("watchdog", ignoreCase = true) }) {
        override fun onChange(oldValue: String, newValue: String) {
            if (state) onDisable()
        }

        override fun onChanged(oldValue: String, newValue: String) {
            if (state) onEnable()
        }
    }

    override fun onEnable() {
        wasDown = false
        if (mc.thePlayer == null) return
        mc.timer.timerSpeed = 1f
        y = mc.thePlayer.posY
        val speedMode = mode
        speedMode?.onEnable()
    }

    override fun onDisable() {
        if (GameSettings.isKeyDown(mc.gameSettings.keyBindJump))
            mc.gameSettings.keyBindJump.pressed = true
        if (mc.thePlayer == null) return
        mc.timer.timerSpeed = 1f
        val speedMode = mode
        speedMode?.onDisable()
    }

    override val tag: String
        get() = typeValue.get()

    private val serverModeValue: ListValue = object : ListValue(
        "Server-Mode",
        arrayOf(
            "YPort",
            "YPort2",
            "SlowHop",
            "Jump",
            "Minemen",
            "NoRules",
            "Sparky"
        ),
        "YPort",
        { typeValue.get().equals("server", ignoreCase = true) }) {
        override fun onChange(oldValue: String, newValue: String) {
            if (state) onDisable()
        }

        override fun onChanged(oldValue: String, newValue: String) {
            if (state) onEnable()
        }
    }
    val modeName: String
        get() {
            var mode = ""
            when (typeValue.get()) {
                "NCP" -> mode = if (ncpModeValue.get()
                        .equals("SBHop", ignoreCase = true)
                ) "SNCPBHop" else "NCP" + ncpModeValue.get()

                "AAC" -> mode = if (aacModeValue.get()
                        .equals("oldbhop", ignoreCase = true)
                ) "OldAACBHop" else "AAC" + aacModeValue.get()

                "Spartan" -> mode = "SpartanYPort"
                "Watchdog" -> mode = "Watchdog" + hypixelModeValue.get()
                "Verus" -> mode = "Verus" + verusModeValue.get()
                "Vulcan" -> mode = "Vulcan" + vulcanModeValue.get()
                "Matrix" -> mode = "Matrix" + matrixModeValue.get()
                "Velocity" -> mode = "Velocity"
                "Custom" -> mode = "Custom"
                "Server" -> mode = serverModeValue.get()
            }
            return mode
        }
    val mode: SpeedMode?
        get() {
            for (speedMode in speedModes) if (speedMode.modeName.equals(modeName, ignoreCase = true)) return speedMode
            return null
        }
    private val verusModeValue: ListValue = object : ListValue(
        "Verus-Mode",
        arrayOf("Hop", "LowHop", "Float"),
        "Hop",
        { typeValue.get().equals("verus", ignoreCase = true) }) {
        override fun onChange(oldValue: String, newValue: String) {
            if (state) onDisable()
        }

        override fun onChanged(oldValue: String, newValue: String) {
            if (state) onEnable()
        }
    }
    private val vulcanModeValue: ListValue = object : ListValue("Vulcan-Mode", arrayOf(
        "YPort"
    ), "YPort", { typeValue.get().equals("vulcan", ignoreCase = true) }) {
        override fun onChange(oldValue: String, newValue: String) {
            if (state) onDisable()
        }

        override fun onChanged(oldValue: String, newValue: String) {
            if (state) onEnable()
        }
    }
    private val matrixModeValue: ListValue = object : ListValue("Matrix-Mode", arrayOf(
        "Hop",
        "YPort",
        "6.7.0",
        "6.9.2"
    ), "Hop", { typeValue.get().equals("matrix", ignoreCase = true) }) {
        override fun onChange(oldValue: String, newValue: String) {
            if (state) onDisable()
        }

        override fun onChanged(oldValue: String, newValue: String) {
            if (state) onEnable()
        }
    }
    val timerValue = BoolValue("UseTimer", true) {
        modeName.equals(
            "watchdogcustom",
            ignoreCase = true
        )
    }
    val customSpeedValue =
        FloatValue("StrSpeed", 0.42f, 0.2f, 2f) {
            modeName.equals(
                "watchdogcustom",
                ignoreCase = true
            )
        }
    val motionYValue = FloatValue("MotionY", 0.42f, 0f, 2f) {
        modeName.equals("watchdogcustom", ignoreCase = true)
    }

    @JvmField
    val speedValue = FloatValue("CustomSpeed", 1.0f, 0.2f, 2f) { typeValue.get().equals("custom", ignoreCase = true) }

    @JvmField
    val launchSpeedValue =
        FloatValue("CustomLaunchSpeed", 1.6f, 0.2f, 2f) { typeValue.get().equals("custom", ignoreCase = true) }

    @JvmField
    val addYMotionValue =
        FloatValue("CustomAddYMotion", 0f, 0f, 2f) { typeValue.get().equals("custom", ignoreCase = true) }

    @JvmField
    val yValue = FloatValue("CustomY", 0.42f, 0f, 4f) { typeValue.get().equals("custom", ignoreCase = true) }

    @JvmField
    val upTimerValue = FloatValue("CustomUpTimer", 1f, 0.1f, 2f) { typeValue.get().equals("custom", ignoreCase = true) }

    @JvmField
    val downTimerValue =
        FloatValue("CustomDownTimer", 1f, 0.1f, 2f) { typeValue.get().equals("custom", ignoreCase = true) }

    @JvmField
    val strafeValue = ListValue(
        "CustomStrafe",
        arrayOf("Strafe", "Boost", "Plus", "PlusOnlyUp", "Non-Strafe"),
        "Strafe"
    ) { typeValue.get().equals("custom", ignoreCase = true) }

    @JvmField
    val groundStay = IntegerValue("CustomGroundStay", 0, 0, 10) { typeValue.get().equals("custom", ignoreCase = true) }

    @JvmField
    val groundResetXZValue =
        BoolValue("CustomGroundResetXZ", false) { typeValue.get().equals("custom", ignoreCase = true) }

    @JvmField
    val resetXZValue = BoolValue("CustomResetXZ", false) { typeValue.get().equals("custom", ignoreCase = true) }

    @JvmField
    val resetYValue = BoolValue("CustomResetY", false) { typeValue.get().equals("custom", ignoreCase = true) }

    @JvmField
    val doLaunchSpeedValue =
        BoolValue("CustomDoLaunchSpeed", false) { typeValue.get().equals("custom", ignoreCase = true) }

    @JvmField
    val jumpStrafe = BoolValue("JumpStrafe", false) {
        typeValue.get().equals("server", ignoreCase = true) && serverModeValue.get().equals("jump", true)
    }

    @JvmField
    val portMax = FloatValue("AAC-PortLength", 1f, 1f, 20f) {
        typeValue.get().equals("aac", ignoreCase = true) && aacModeValue.get().equals("port", true)
    }

    @JvmField
    val aacGroundTimerValue =
        FloatValue("AACGround-Timer", 3f, 1.1f, 10f) {
            typeValue.get().equals("aac", ignoreCase = true) && (aacModeValue.get()
                .equals("ground", true) || aacModeValue.get().equals("ground2", true))
        }

    @JvmField
    val velocitySpeed =
        FloatValue("Velocity-Speed", 0.9f, 0.0f, 5f) { typeValue.get().equals("velocity", ignoreCase = true) }

    @JvmField
    val velocityBHop = BoolValue("Velocity-BHop", true) { typeValue.get().equals("velocity", ignoreCase = true) }

    private val lagCheck = BoolValue("LagCheck", true)
    private val worldCheck = BoolValue("WorldCheck", true)

    @JvmField
    val noBob = BoolValue("NoBob", false)
}
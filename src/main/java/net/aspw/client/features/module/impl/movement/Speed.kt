package net.aspw.client.features.module.impl.movement

import net.aspw.client.Client
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
import net.aspw.client.features.module.impl.movement.speeds.other.*
import net.aspw.client.features.module.impl.movement.speeds.spartan.SpartanYPort
import net.aspw.client.features.module.impl.movement.speeds.spectre.SpectreBHop
import net.aspw.client.features.module.impl.movement.speeds.spectre.SpectreLowHop
import net.aspw.client.features.module.impl.movement.speeds.spectre.SpectreOnGround
import net.aspw.client.features.module.impl.movement.speeds.vanillabhop.VanillaBhop
import net.aspw.client.features.module.impl.movement.speeds.verus.VerusFloat
import net.aspw.client.features.module.impl.movement.speeds.verus.VerusHard
import net.aspw.client.features.module.impl.movement.speeds.verus.VerusHop
import net.aspw.client.features.module.impl.movement.speeds.verus.VerusLowHop
import net.aspw.client.features.module.impl.movement.speeds.vulcan.VulcanGround
import net.aspw.client.features.module.impl.movement.speeds.vulcan.VulcanHop1
import net.aspw.client.features.module.impl.movement.speeds.vulcan.VulcanHop2
import net.aspw.client.features.module.impl.movement.speeds.vulcan.VulcanYPort
import net.aspw.client.features.module.impl.movement.speeds.watchdog.WatchdogBoost
import net.aspw.client.features.module.impl.movement.speeds.watchdog.WatchdogCustom
import net.aspw.client.features.module.impl.movement.speeds.watchdog.WatchdogNew
import net.aspw.client.features.module.impl.movement.speeds.watchdog.WatchdogStable
import net.aspw.client.value.BoolValue
import net.aspw.client.value.FloatValue
import net.aspw.client.value.IntegerValue
import net.aspw.client.value.ListValue
import net.minecraft.client.gui.GuiChat
import net.minecraft.client.gui.GuiIngameMenu
import net.minecraft.client.settings.GameSettings

@ModuleInfo(name = "Speed", category = ModuleCategory.MOVEMENT)
class Speed : Module() {
    private var wasDown: Boolean = false
    val speedModes = arrayOf(
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
        AACHop3313(),
        AACHop438(),
        AACYPort(),
        AACYPort2(),
        WatchdogNew(),
        WatchdogBoost(),
        WatchdogStable(),
        WatchdogCustom(),
        VanillaBhop(),
        SpartanYPort(),
        SpectreBHop(),
        SpectreLowHop(),
        SpectreOnGround(),
        SlowHop(),
        Custom(),
        Jump(),
        Legit(),
        AEMine(),
        NCPSemiStrafe(),
        GWEN(),
        NCPBoost(),
        NCPFrame(),
        NCPMiniJump(),
        NCPOnGround(),
        YPort(),
        YPort2(),
        HiveHop(),
        MineplexGround(),
        TeleportCubeCraft(),
        VerusHop(),
        VerusLowHop(),
        VerusHard(),
        VerusFloat(),
        VulcanHop1(),
        VulcanHop2(),
        VulcanYPort(),
        VulcanGround(),
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
            "Spectre",
            "Watchdog",
            "Verus",
            "Vulcan",
            "Matrix",
            "Custom",
            "VanillaBhop",
            "Other"
        ),
        "VanillaBhop"
    ) {
        override fun onChange(oldValue: String, newValue: String) {
            if (state) onDisable()
        }

        override fun onChanged(oldValue: String, newValue: String) {
            if (state) onEnable()
        }
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent?) {
        if (mc.thePlayer.isSneaking) return
        val speedMode = mode

        speedMode?.onUpdate()
    }

    val ncpModeValue: ListValue = object : ListValue(
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

    val aacModeValue: ListValue = object : ListValue("AAC-Mode", arrayOf(
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
        "Hop3.3.13",
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
    fun onTick(event: TickEvent?) {
        if (mc.thePlayer.isSneaking) return
        val speedMode = mode
        speedMode?.onTick()
    }

    @EventTarget
    fun onJump(event: JumpEvent?) {
        val speedMode = mode
        speedMode?.onJump(event)
    }

    val hypixelModeValue: ListValue = object : ListValue(
        "Watchdog-Mode",
        arrayOf("New", "Boost", "Stable", "Custom"),
        "New",
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
        val speedMode = mode
        speedMode?.onEnable()
    }

    override fun onDisable() {
        if (mc.thePlayer == null) return
        mc.timer.timerSpeed = 1f
        mc.gameSettings.keyBindJump.pressed =
            mc.thePlayer != null && (mc.inGameHasFocus || Client.moduleManager.getModule(
                Inventory::class.java
            )!!.state) && !(mc.currentScreen is GuiIngameMenu || mc.currentScreen is GuiChat) && GameSettings.isKeyDown(
                mc.gameSettings.keyBindJump
            )
        val speedMode = mode
        speedMode?.onDisable()
    }

    val spectreModeValue: ListValue = object : ListValue(
        "Spectre-Mode",
        arrayOf("BHop", "LowHop", "OnGround"),
        "BHop",
        { typeValue.get().equals("spectre", ignoreCase = true) }) {
        override fun onChange(oldValue: String, newValue: String) {
            if (state) onDisable()
        }

        override fun onChanged(oldValue: String, newValue: String) {
            if (state) onEnable()
        }
    }

    override val tag: String
        get() = typeValue.get()

    private val onlySingleName: String
        private get() {
            var mode = ""
            when (typeValue.get()) {
                "NCP" -> mode = ncpModeValue.get()
                "AAC" -> mode = aacModeValue.get()
                "Spartan" -> mode = "Spartan"
                "Spectre" -> mode = spectreModeValue.get()
                "Watchdog" -> mode = hypixelModeValue.get()
                "Verus" -> mode = verusModeValue.get()
                "Vulcan" -> mode = vulcanModeValue.get()
                "Matrix" -> mode = matrixModeValue.get()
            }
            return mode
        }
    val otherModeValue: ListValue = object : ListValue(
        "Other-Mode",
        arrayOf(
            "YPort",
            "YPort2",
            "SlowHop",
            "Jump",
            "Legit",
            "AEMine",
            "GWEN",
            "HiveHop",
            "MineplexGround",
            "TeleportCubeCraft"
        ),
        "YPort",
        { typeValue.get().equals("other", ignoreCase = true) }) {
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
                "Spectre" -> mode = "Spectre" + spectreModeValue.get()
                "Watchdog" -> mode = "Watchdog" + hypixelModeValue.get()
                "Verus" -> mode = "Verus" + verusModeValue.get()
                "Vulcan" -> mode = "Vulcan" + vulcanModeValue.get()
                "Matrix" -> mode = "Matrix" + matrixModeValue.get()
                "VanillaBhop" -> mode = "VanillaBhop"
                "Custom" -> mode = "Custom"
                "Other" -> mode = otherModeValue.get()
            }
            return mode
        }
    val mode: SpeedMode?
        get() {
            for (speedMode in speedModes) if (speedMode.modeName.equals(modeName, ignoreCase = true)) return speedMode
            return null
        }
    val verusModeValue: ListValue = object : ListValue(
        "Verus-Mode",
        arrayOf("Hop", "LowHop", "Hard", "Float"),
        "LowHop",
        { typeValue.get().equals("verus", ignoreCase = true) }) {
        override fun onChange(oldValue: String, newValue: String) {
            if (state) onDisable()
        }

        override fun onChanged(oldValue: String, newValue: String) {
            if (state) onEnable()
        }
    }
    val vulcanModeValue: ListValue = object : ListValue("Vulcan-Mode", arrayOf(
        "Hop1",
        "Hop2",
        "YPort",
        "Ground"
    ), "YPort", { typeValue.get().equals("vulcan", ignoreCase = true) }) {
        override fun onChange(oldValue: String, newValue: String) {
            if (state) onDisable()
        }

        override fun onChanged(oldValue: String, newValue: String) {
            if (state) onEnable()
        }
    }
    val matrixModeValue: ListValue = object : ListValue("Matrix-Mode", arrayOf(
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
        ) && !modeName.equals("watchdognew", ignoreCase = true) || modeName.equals(
            "watchdogcustom",
            ignoreCase = true
        )
    }
    val smoothStrafe = BoolValue("SmoothStrafe", true) {
        modeName.equals(
            "watchdogcustom",
            ignoreCase = true
        ) && !modeName.equals("watchdognew", ignoreCase = true)
    }
    val customSpeedValue =
        FloatValue("StrSpeed", 0.42f, 0.2f, 2f) {
            modeName.equals(
                "watchdogcustom",
                ignoreCase = true
            ) && !modeName.equals("watchdognew", ignoreCase = true)
        }
    val motionYValue = FloatValue("MotionY", 0.42f, 0f, 2f) {
        modeName.equals("watchdogcustom", ignoreCase = true) && !modeName.equals(
            "watchdognew",
            ignoreCase = true
        )
    }

    @JvmField
    val boostSpeedValue = BoolValue("Ground-Boost", true) { modeName.equals("vulcanground", ignoreCase = true) }

    @JvmField
    val boostDelayValue = IntegerValue("Boost-Delay", 8, 2, 15) { modeName.equals("vulcanground", ignoreCase = true) }

    @JvmField
    val verusTimer = FloatValue("Verus-Timer", 1f, 0.1f, 10f) { modeName.equals("verushard", ignoreCase = true) }

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
    val jumpStrafe = BoolValue("JumpStrafe", false) { typeValue.get().equals("other", ignoreCase = true) }

    @JvmField
    val sendJumpValue = BoolValue("SendJump", true) {
        typeValue.get().equals("watchdog", ignoreCase = true) && !modeName.equals(
            "watchdognew",
            ignoreCase = true
        ) && !modeName.equals(
            "watchdogcustom",
            ignoreCase = true
        )
    }

    @JvmField
    val recalcValue = BoolValue("ReCalculate", false) {
        typeValue.get().equals("watchdog", ignoreCase = true) && sendJumpValue.get() && !modeName.equals(
            "watchdogcustom",
            ignoreCase = true
        ) && !modeName.equals(
            "watchdognew",
            ignoreCase = true
        )
    }

    @JvmField
    val glideStrengthValue = FloatValue("GlideStrength", 0f, 0f, 0.05f) {
        typeValue.get().equals("watchdog", ignoreCase = true) && !modeName.equals(
            "watchdognew",
            ignoreCase = true
        ) && !modeName.equals("watchdogcustom", ignoreCase = true)
    }

    @JvmField
    val moveSpeedValue = FloatValue("MoveSpeed", 1.7f, 1f, 1.7f) {
        typeValue.get().equals("watchdog", ignoreCase = true) && !modeName.equals(
            "watchdognew",
            ignoreCase = true
        ) && !modeName.equals("watchdogcustom", ignoreCase = true)
    }

    @JvmField
    val jumpYValue = FloatValue("JumpY", 0.42f, 0f, 1f) {
        typeValue.get().equals("watchdog", ignoreCase = true) && !modeName.equals(
            "watchdognew",
            ignoreCase = true
        ) && !modeName.equals("watchdogcustom", ignoreCase = true)
    }

    @JvmField
    val baseStrengthValue = FloatValue("BaseMultiplier", 1f, 0.5f, 1f) {
        typeValue.get().equals("watchdog", ignoreCase = true) && !modeName.equals(
            "watchdognew",
            ignoreCase = true
        ) && !modeName.equals("watchdogcustom", ignoreCase = true)
    }

    @JvmField
    val baseTimerValue = FloatValue("BaseTimer", 1.5f, 1f, 3f) {
        modeName.equals(
            "watchdogboost",
            ignoreCase = true && !modeName.equals("watchdognew", ignoreCase = true)
        )
    }

    @JvmField
    val baseMTimerValue =
        FloatValue("BaseMultiplierTimer", 1f, 0f, 3f) {
            modeName.equals(
                "watchdogboost",
                ignoreCase = true && !modeName.equals("watchdognew", ignoreCase = true)
            )
        }

    @JvmField
    val portMax = FloatValue("AAC-PortLength", 1f, 1f, 20f) { typeValue.get().equals("aac", ignoreCase = true) }

    @JvmField
    val aacGroundTimerValue =
        FloatValue("AACGround-Timer", 3f, 1.1f, 10f) { typeValue.get().equals("aac", ignoreCase = true) }

    @JvmField
    val vanillaBhopSpeed =
        FloatValue("Hop-Speed", 0.9f, 0.0f, 5f) { typeValue.get().equals("vanillabhop", ignoreCase = true) }

    @JvmField
    val cubecraftPortLengthValue =
        FloatValue("CubeCraft-PortLength", 1f, 0.1f, 2f) { modeName.equals("teleportcubecraft", ignoreCase = true) }

    @JvmField
    val mineplexGroundSpeedValue =
        FloatValue("MineplexGround-Speed", 0.6f, 0.1f, 1f) { modeName.equals("mineplexground", ignoreCase = true) }

    @JvmField
    val noBob = BoolValue("NoBob", false)
}
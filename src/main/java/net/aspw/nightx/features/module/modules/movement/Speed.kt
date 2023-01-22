package net.aspw.nightx.features.module.modules.movement

import net.aspw.nightx.NightX
import net.aspw.nightx.event.*
import net.aspw.nightx.features.module.Module
import net.aspw.nightx.features.module.ModuleCategory
import net.aspw.nightx.features.module.ModuleInfo
import net.aspw.nightx.features.module.modules.movement.speeds.SpeedMode
import net.aspw.nightx.features.module.modules.movement.speeds.aac.*
import net.aspw.nightx.features.module.modules.movement.speeds.blocksmc.BlocksMC
import net.aspw.nightx.features.module.modules.movement.speeds.matrix.MatrixHop
import net.aspw.nightx.features.module.modules.movement.speeds.ncp.*
import net.aspw.nightx.features.module.modules.movement.speeds.other.*
import net.aspw.nightx.features.module.modules.movement.speeds.spartan.SpartanYPort
import net.aspw.nightx.features.module.modules.movement.speeds.spectre.SpectreBHop
import net.aspw.nightx.features.module.modules.movement.speeds.spectre.SpectreLowHop
import net.aspw.nightx.features.module.modules.movement.speeds.spectre.SpectreOnGround
import net.aspw.nightx.features.module.modules.movement.speeds.vanillabhop.VanillaBhop
import net.aspw.nightx.features.module.modules.movement.speeds.verus.VerusHard
import net.aspw.nightx.features.module.modules.movement.speeds.verus.VerusHop
import net.aspw.nightx.features.module.modules.movement.speeds.verus.VerusLowHop
import net.aspw.nightx.features.module.modules.movement.speeds.vulcan.VulcanHop1
import net.aspw.nightx.features.module.modules.movement.speeds.vulcan.VulcanHop2
import net.aspw.nightx.features.module.modules.movement.speeds.vulcan.VulcanYPort
import net.aspw.nightx.features.module.modules.movement.speeds.watchdog.WatchdogBoost
import net.aspw.nightx.features.module.modules.movement.speeds.watchdog.WatchdogCustom
import net.aspw.nightx.features.module.modules.movement.speeds.watchdog.WatchdogNew
import net.aspw.nightx.features.module.modules.movement.speeds.watchdog.WatchdogStable
import net.aspw.nightx.features.module.modules.player.Inventory
import net.aspw.nightx.value.BoolValue
import net.aspw.nightx.value.FloatValue
import net.aspw.nightx.value.IntegerValue
import net.aspw.nightx.value.ListValue
import net.minecraft.client.gui.GuiChat
import net.minecraft.client.gui.GuiIngameMenu
import net.minecraft.client.settings.GameSettings
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

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
        GWEN(),
        Boost(),
        Frame(),
        MiJump(),
        OnGround(),
        YPort(),
        YPort2(),
        HiveHop(),
        MineplexGround(),
        TeleportCubeCraft(),
        VerusHop(),
        VerusLowHop(),
        VerusHard(),
        VulcanHop1(),
        VulcanHop2(),
        VulcanYPort(),
        BlocksMC(),
        MatrixHop()
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
            "MatrixHop",
            "BlocksMC",
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

    private fun getMoveYaw(): Float {
        var moveYaw = mc.thePlayer!!.rotationYaw
        if (mc.thePlayer!!.moveForward != 0F && mc.thePlayer!!.moveStrafing == 0F) {
            moveYaw += if (mc.thePlayer!!.moveForward > 0) 0 else 180
        } else if (mc.thePlayer!!.moveForward != 0F && mc.thePlayer!!.moveStrafing != 0F) {
            if (mc.thePlayer!!.moveForward > 0) {
                moveYaw += if (mc.thePlayer!!.moveStrafing > 0) -45 else 45
            } else {
                moveYaw -= if (mc.thePlayer!!.moveStrafing > 0) -45 else 45
            }
            moveYaw += if (mc.thePlayer!!.moveForward > 0) 0 else 180
        } else if (mc.thePlayer!!.moveStrafing != 0F && mc.thePlayer!!.moveForward == 0F) {
            moveYaw += if (mc.thePlayer!!.moveStrafing > 0) -90 else 90
        }
        return moveYaw
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent?) {
        if (mc.thePlayer.isSneaking) return
        val speedMode = mode

        speedMode?.onUpdate()

        if (typeValue.get().equals("BlocksMC") || typeValue.get()
                .equals("NCP") || modeName.equals("WatchdogCustom") || typeValue.get()
                .equals("Verus") || typeValue.get().equals("Spartan") || typeValue.equals("Verus") || typeValue.get()
                .equals("VanillaBhop") || typeValue.get().equals("Spectre")
        ) {
            if (mc.thePlayer!!.onGround && mc.gameSettings.keyBindJump.isKeyDown && (mc.thePlayer!!.movementInput.moveForward != 0F || mc.thePlayer!!.movementInput.moveStrafe != 0F) && !(mc.thePlayer!!.isInWater || mc.thePlayer!!.isInLava || mc.thePlayer!!.isOnLadder || mc.thePlayer!!.isInWeb)) {
                if (mc.gameSettings.keyBindJump.isKeyDown) {
                    mc.gameSettings.keyBindJump.pressed = false
                    wasDown = true
                }
                val yaw = mc.thePlayer!!.rotationYaw
                mc.thePlayer!!.rotationYaw = getMoveYaw()
                mc.thePlayer!!.jump()
                mc.thePlayer!!.rotationYaw = yaw
                if (wasDown) {
                    mc.gameSettings.keyBindJump.pressed = true
                    wasDown = false
                }
            }
        }
    }

    @EventTarget
    fun onStrafe(event: StrafeEvent) {
        val shotSpeed =
            sqrt((mc.thePlayer!!.motionX * mc.thePlayer!!.motionX) + (mc.thePlayer!!.motionZ * mc.thePlayer!!.motionZ))
        val speed = (shotSpeed * 1)
        val motionX = (mc.thePlayer!!.motionX * (1 - 1))
        val motionZ = (mc.thePlayer!!.motionZ * (1 - 1))
        if (typeValue.get().equals("BlocksMC") || typeValue.get()
                .equals("NCP") || modeName.equals("WatchdogCustom") || typeValue.get()
                .equals("Verus") || typeValue.get().equals("Spartan") || typeValue.equals("Verus") || typeValue.get()
                .equals("VanillaBhop") || typeValue.get().equals("Spectre")
        ) {
            if (!(mc.thePlayer!!.movementInput.moveForward != 0F || mc.thePlayer!!.movementInput.moveStrafe != 0F)) {
                return
            }
            if (!mc.thePlayer!!.onGround) {
                val yaw = getMoveYaw()
                mc.thePlayer!!.motionX = (((-sin(Math.toRadians(yaw.toDouble())) * speed) + motionX))
                mc.thePlayer!!.motionZ = (((cos(Math.toRadians(yaw.toDouble())) * speed) + motionZ))
            }
        }
    }

    val ncpModeValue: ListValue = object : ListValue(
        "NCP-Mode",
        arrayOf("BHop", "FHop", "SBHop", "Hop", "YPort"),
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
            mc.thePlayer != null && (mc.inGameHasFocus || NightX.moduleManager.getModule(
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
        get() = modeName

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
            }
            return mode
        }
    val otherModeValue: ListValue = object : ListValue(
        "Other-Mode",
        arrayOf(
            "YPort",
            "YPort2",
            "Boost",
            "Frame",
            "MiJump",
            "OnGround",
            "SlowHop",
            "Jump",
            "Legit",
            "AEMine",
            "GWEN",
            "HiveHop",
            "VulcanHop1",
            "VulcanHop2",
            "VulcanYPort",
            "MineplexGround",
            "TeleportCubeCraft"
        ),
        "Boost",
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
                "MatrixHop" -> mode = "MatrixHop"
                "BlocksMC" -> mode = "BlocksMC"
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
        arrayOf("Hop", "LowHop", "Hard"),
        "LowHop",
        { typeValue.get().equals("verus", ignoreCase = true) }) {
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
        ) && !modeName.equals("watchdognew", ignoreCase = true)
    }
    val smoothStrafe = BoolValue("SmoothStrafe", true) {
        modeName.equals(
            "watchdogcustom",
            ignoreCase = true
        ) && !modeName.equals("watchdognew", ignoreCase = true)
    }
    val strafing = BoolValue("Strafing", true) { modeName.equals("blocksmc", ignoreCase = true) }
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
            "watchdogcustom",
            ignoreCase = true
        )
    }

    @JvmField
    val recalcValue = BoolValue("ReCalculate", false) {
        typeValue.get().equals("watchdog", ignoreCase = true) && sendJumpValue.get() && !modeName.equals(
            "watchdognew",
            ignoreCase = true
        ) && !modeName.equals(
            "watchdogcustom",
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
    val cubecraftPortLengthValue =
        FloatValue("CubeCraft-PortLength", 1f, 0.1f, 2f) { modeName.equals("teleportcubecraft", ignoreCase = true) }

    @JvmField
    val mineplexGroundSpeedValue =
        FloatValue("MineplexGround-Speed", 0.6f, 0.1f, 1f) { modeName.equals("mineplexground", ignoreCase = true) }

    @JvmField
    val noBob = BoolValue("NoBob", false)
}
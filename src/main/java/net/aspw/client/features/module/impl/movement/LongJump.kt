package net.aspw.client.features.module.impl.movement

import net.aspw.client.event.*
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.utils.MovementUtils
import net.aspw.client.utils.PacketUtils
import net.aspw.client.utils.PosLookInstance
import net.aspw.client.utils.timer.MSTimer
import net.aspw.client.value.BoolValue
import net.aspw.client.value.FloatValue
import net.aspw.client.value.IntegerValue
import net.aspw.client.value.ListValue
import net.minecraft.client.settings.GameSettings
import net.minecraft.init.Blocks
import net.minecraft.item.ItemEnderPearl
import net.minecraft.item.ItemStack
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.network.play.client.C03PacketPlayer.*
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement
import net.minecraft.network.play.client.C09PacketHeldItemChange
import net.minecraft.network.play.client.C0BPacketEntityAction
import net.minecraft.network.play.server.S12PacketEntityVelocity
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing
import java.util.*

@ModuleInfo(name = "LongJump", spacedName = "Long Jump", category = ModuleCategory.MOVEMENT)
class LongJump : Module() {
    val modeValue = ListValue(
        "Mode",
        arrayOf(
            "NCP",
            "AACv1",
            "AACv2",
            "AACv3",
            "AACv4",
            "Mineplex1",
            "Mineplex2",
            "Mineplex3",
            "RedeskyMaki",
            "Redesky",
            "InfiniteRedesky",
            "VerusHigh",
            "VerusDmg",
            "Pearl"
        ),
        "NCP"
    )
    private val ncpBoostValue =
        FloatValue("NCP-Boost", 20f, 10f, 40f) { modeValue.get().equals("ncp", ignoreCase = true) }
    private val redeskyTimerBoostValue =
        BoolValue("Redesky-TimerBoost", false) { modeValue.get().equals("redesky", ignoreCase = true) }
    private val redeskyTimerBoostStartValue = FloatValue("Redesky-TimerBoostStart", 1.85f, 0.05f, 10f) {
        modeValue.get().equals("redesky", ignoreCase = true) && redeskyTimerBoostValue.get()
    }
    private val redeskyTimerBoostEndValue = FloatValue("Redesky-TimerBoostEnd", 1.0f, 0.05f, 10f) {
        modeValue.get().equals("redesky", ignoreCase = true) && redeskyTimerBoostValue.get()
    }
    private val redeskyTimerBoostSlowDownSpeedValue = IntegerValue("Redesky-TimerBoost-SlowDownSpeed", 2, 1, 10) {
        modeValue.get().equals("redesky", ignoreCase = true) && redeskyTimerBoostValue.get()
    }
    private val redeskyGlideAfterTicksValue =
        BoolValue("Redesky-GlideAfterTicks", false) { modeValue.get().equals("redesky", ignoreCase = true) }
    private val redeskyTickValue =
        IntegerValue("Redesky-Ticks", 21, 1, 25) { modeValue.get().equals("redesky", ignoreCase = true) }
    private val redeskyYMultiplier =
        FloatValue("Redesky-YMultiplier", 0.77f, 0.1f, 1f) { modeValue.get().equals("redesky", ignoreCase = true) }
    private val redeskyXZMultiplier =
        FloatValue("Redesky-XZMultiplier", 0.9f, 0.1f, 1f) { modeValue.get().equals("redesky", ignoreCase = true) }
    private val verusDmgModeValue =
        ListValue("VerusDmg-DamageMode", arrayOf("Instant", "InstantC06", "Jump"), "Instant") {
            modeValue.get().equals("verusdmg", ignoreCase = true)
        }
    private val verusBoostValue =
        FloatValue("VerusDmg-Boost", 1.5f, 0f, 10f) { modeValue.get().equals("verusdmg", ignoreCase = true) }
    private val verusHeightValue =
        FloatValue("VerusDmg-Height", 0.42f, 0f, 10f) { modeValue.get().equals("verusdmg", ignoreCase = true) }
    private val verusTimerValue =
        FloatValue("VerusDmg-Timer", 1f, 0.05f, 10f) { modeValue.get().equals("verusdmg", ignoreCase = true) }
    private val pearlBoostValue =
        FloatValue("Pearl-Boost", 4.25f, 0f, 10f) { modeValue.get().equals("pearl", ignoreCase = true) }
    private val pearlHeightValue =
        FloatValue("Pearl-Height", 0.42f, 0f, 10f) { modeValue.get().equals("pearl", ignoreCase = true) }
    private val pearlTimerValue =
        FloatValue("Pearl-Timer", 1f, 0.05f, 10f) { modeValue.get().equals("pearl", ignoreCase = true) }
    private val verusHighTimerValue =
        BoolValue("VerusHigh-TimerBoost", false) { modeValue.get().equals("verushigh", ignoreCase = true) }
    private val verusHighHeightValue =
        FloatValue("VerusHigh-Height", 10f, 0.05f, 10f) { modeValue.get().equals("verushigh", ignoreCase = true) }
    private val lagCheck = BoolValue("LagCheck", true)
    private val worldCheck = BoolValue("WorldCheck", true)
    private val autoDisableValue = BoolValue("AutoDisable", true)
    private val fakeDmgValue = BoolValue("FakeDamage", false)
    val fakeYValue = BoolValue("FakeY", false)
    private val viewBobbingValue = BoolValue("ViewBobbing", false)
    private val bobbingAmountValue = FloatValue("BobbingAmount", 0.1f, 0f, 0.1f) { viewBobbingValue.get() }
    private val dmgTimer = MSTimer()
    private val posLookInstance = PosLookInstance()
    private var hasJumped = false
    private var no = false
    private var jumped = false
    private var jumpState = 0
    private var canBoost = false
    private var teleported = false
    private var canMineplexBoost = false
    private var ticks = 0
    private var currentTimer = 1f
    private var verusDmged = false
    private var hpxDamage = false
    private var damaged = false
    private var verusJumpTimes = 0
    private var pearlState = 0
    private var started = false
    private var stage = 0
    private var jumpWaiting = false
    private var fulljumped = false
    var y = 0.0

    @EventTarget
    fun onMotion(event: MotionEvent?) {
        if (modeValue.get().equals("verushigh", true) && (stage < 4 || stage <= 5)) {
            mc.thePlayer.cameraPitch = 0f
            mc.thePlayer.cameraYaw = 0f
        }
        if (MovementUtils.isMoving() && viewBobbingValue.get())
            mc.thePlayer.cameraYaw = bobbingAmountValue.get()
        if (fakeYValue.get())
            mc.thePlayer.cameraPitch = 0f
    }

    override fun onEnable() {
        if (fakeDmgValue.get()) {
            mc.thePlayer.handleStatusUpdate(2.toByte())
        }
        y = mc.thePlayer.posY
        if (modeValue.get().equals("redesky", ignoreCase = true) && redeskyTimerBoostValue.get()) currentTimer =
            redeskyTimerBoostStartValue.get()
        jumped = false
        hasJumped = false
        no = false
        jumpState = 0
        ticks = 0
        verusDmged = false
        hpxDamage = false
        damaged = false
        pearlState = 0
        verusJumpTimes = 0
        dmgTimer.reset()
        posLookInstance.reset()
        val x = mc.thePlayer.posX
        val y = mc.thePlayer.posY
        val z = mc.thePlayer.posZ
        if (modeValue.get().equals("verusdmg", ignoreCase = true)) {
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
                }
            } else if (verusDmgModeValue.get().equals("Jump", ignoreCase = true)) {
                if (mc.thePlayer.onGround) {
                    mc.thePlayer.jump()
                    verusJumpTimes = 1
                }
            }
        }
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent?) {
        if (modeValue.get().equals("verushigh", true)) {
            if (stage < 4 && verusHighTimerValue.get())
                mc.timer.timerSpeed = 2f
            else mc.timer.timerSpeed = 1f
            if (!mc.thePlayer.onGround || jumpWaiting || stage == 5) return
            mc.timer.timerSpeed = 1f
            if (stage < 3) {
                stage += 1
                if (stage < 2)
                    mc.thePlayer.jump()
                chat(stage.toString())
            }
            if (stage == 3) {
                jumpWaiting = true
            }
        }
        if (modeValue.get().equals("ncp", ignoreCase = true)) {
            mc.gameSettings.keyBindJump.pressed = false
        }
        if (!no && mc.thePlayer.onGround) {
            jumped = true
            if (hasJumped && autoDisableValue.get()) {
                jumpState = 0
                state = false
                return
            }
            mc.thePlayer.jump()
            hasJumped = true
        }
        if (modeValue.get().equals("verusdmg", ignoreCase = true)) {
            if (mc.thePlayer.hurtTime > 0 && !verusDmged) {
                verusDmged = true
                MovementUtils.strafe(verusBoostValue.get())
                mc.thePlayer.motionY = verusHeightValue.get().toDouble()
            }
            if (verusDmgModeValue.get().equals("Jump", ignoreCase = true) && verusJumpTimes < 5) {
                if (mc.thePlayer.onGround) {
                    mc.thePlayer.jump()
                    verusJumpTimes += 1
                }
                return
            }
            if (verusDmged) mc.timer.timerSpeed = verusTimerValue.get() else {
                mc.thePlayer.movementInput.moveForward = 0f
                mc.thePlayer.movementInput.moveStrafe = 0f
                if (!verusDmgModeValue.get().equals("Jump", ignoreCase = true)) mc.thePlayer.motionY = 0.0
            }
            return
        }
        if (modeValue.get().equals("ncp", ignoreCase = true)) {
            if (mc.thePlayer.onGround)
                canBoost = true
        }
        if (modeValue.get().equals("pearl", ignoreCase = true)) {
            val enderPearlSlot = pearlSlot
            if (pearlState == 0) {
                if (enderPearlSlot == -1) {
                    chat("You don't have any ender pearl")
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
            if (pearlState == 1 && mc.thePlayer.hurtTime > 0) {
                pearlState = 2
                MovementUtils.strafe(pearlBoostValue.get())
                mc.thePlayer.motionY = pearlHeightValue.get().toDouble()
            }
            if (pearlState == 2) mc.timer.timerSpeed = pearlTimerValue.get()
            return
        }
        val mode = modeValue.get()
        if (mc.thePlayer.onGround || mc.thePlayer.capabilities.isFlying) {
            jumped = false
            canMineplexBoost = false
            if (mode.equals("NCP", ignoreCase = true)) {
                mc.thePlayer.motionX = 0.0
                mc.thePlayer.motionZ = 0.0
            }
            return
        }
        when (mode.lowercase(Locale.getDefault())) {
            "ncp" -> {
                MovementUtils.strafe(MovementUtils.getSpeed() * if (canBoost) ncpBoostValue.get() else 1f)
                canBoost = false
            }

            "aacv1" -> {
                mc.thePlayer.motionY += 0.05999
                MovementUtils.strafe(MovementUtils.getSpeed() * 1.08f)
            }

            "aacv2", "mineplex3" -> {
                mc.thePlayer.jumpMovementFactor = 0.09f
                mc.thePlayer.motionY += 0.01321
                mc.thePlayer.jumpMovementFactor = 0.08f
                MovementUtils.strafe()
            }

            "aacv3" -> {
                val player = mc.thePlayer
                if (player.fallDistance > 0.5f && !teleported) {
                    val value = 3.0
                    val horizontalFacing = player.horizontalFacing
                    var x = 0.0
                    var z = 0.0
                    when (horizontalFacing) {
                        EnumFacing.NORTH -> z = -value
                        EnumFacing.EAST -> x = value
                        EnumFacing.SOUTH -> z = value
                        EnumFacing.WEST -> x = -value
                        else -> {}
                    }
                    player.setPosition(player.posX + x, player.posY, player.posZ + z)
                    teleported = true
                }
            }

            "mineplex1" -> {
                mc.thePlayer.motionY += 0.01321
                mc.thePlayer.jumpMovementFactor = 0.08f
                MovementUtils.strafe()
            }

            "mineplex2" -> {
                if (!canMineplexBoost) {
                    mc.thePlayer.jumpMovementFactor = 0.1f
                    if (mc.thePlayer.fallDistance > 1.5f) {
                        mc.thePlayer.jumpMovementFactor = 0f
                        mc.thePlayer.motionY = -10.0
                    }
                    MovementUtils.strafe()
                }
            }

            "aacv4" -> {
                mc.thePlayer.jumpMovementFactor = 0.05837456f
                mc.timer.timerSpeed = 0.5f
            }

            "redeskymaki" -> {
                mc.thePlayer.jumpMovementFactor = 0.15f
                mc.thePlayer.motionY += 0.05
            }

            "redesky" -> {
                if (redeskyTimerBoostValue.get()) {
                    mc.timer.timerSpeed = currentTimer
                }
                if (ticks < redeskyTickValue.get()) {
                    mc.thePlayer.jump()
                    mc.thePlayer.motionY *= redeskyYMultiplier.get().toDouble()
                    mc.thePlayer.motionX *= redeskyXZMultiplier.get().toDouble()
                    mc.thePlayer.motionZ *= redeskyXZMultiplier.get().toDouble()
                } else {
                    if (redeskyGlideAfterTicksValue.get()) {
                        mc.thePlayer.motionY += 0.03
                    }
                    if (redeskyTimerBoostValue.get() && currentTimer > redeskyTimerBoostEndValue.get()) {
                        currentTimer =
                            0.08f.coerceAtLeast(currentTimer - 0.05f * redeskyTimerBoostSlowDownSpeedValue.get()) // zero-timer protection
                    }
                }
                ticks++
            }

            "infiniteredesky" -> {
                if (mc.thePlayer.fallDistance > 0.6f) mc.thePlayer.motionY += 0.02
                MovementUtils.strafe(
                    0.85.coerceAtMost(0.25.coerceAtLeast(MovementUtils.getSpeed() * 1.05878)).toFloat()
                )
            }
        }
    }

    @EventTarget
    fun onWorld(event: WorldEvent) {
        if (worldCheck.get()) {
            state = false
            chat("LongJump was disabled")
        }
    }

    @EventTarget
    fun onTeleport(event: TeleportEvent) {
        if (lagCheck.get()) {
            state = false
            chat("Disabling LongJump due to lag back")
        }
    }

    @EventTarget
    fun onMove(event: MoveEvent) {
        val mode = modeValue.get()
        if (mode.equals("mineplex3", ignoreCase = true)) {
            if (mc.thePlayer.fallDistance != 0f) mc.thePlayer.motionY += 0.037
        } else if (mode.equals("ncp", ignoreCase = true) && !MovementUtils.isMoving() && jumped) {
            mc.thePlayer.motionX = 0.0
            mc.thePlayer.motionZ = 0.0
            event.zeroXZ()
        }
        if (mode.equals("verushigh", true)) {
            if (mc.thePlayer.hurtTime > 0 && started || stage > 5 && fulljumped || !fulljumped || stage == 4 && jumpWaiting || stage == 5 && mc.thePlayer.onGround) {
                event.zeroXZ()
            }
            if (mc.thePlayer.hurtTime > 0) {
                started = true
                PacketUtils.sendPacketNoEvent(
                    C08PacketPlayerBlockPlacement(
                        mc.thePlayer.position.add(0.0, -1.5, 0.0),
                        1,
                        ItemStack(Blocks.stone.getItem(mc.theWorld, mc.thePlayer.position.add(0.0, -1.5, 0.0))),
                        0.0F,
                        0.5F + Math.random().toFloat() * 0.44.toFloat(),
                        0.0F
                    )
                )
            } else {
                if (stage >= 5) {
                    PacketUtils.sendPacketNoEvent(
                        C08PacketPlayerBlockPlacement(
                            mc.thePlayer.position.add(0.0, -1.5, 0.0),
                            1,
                            ItemStack(Blocks.stone.getItem(mc.theWorld, mc.thePlayer.position.add(0.0, -1.5, 0.0))),
                            0.0F,
                            0.5F + Math.random().toFloat() * 0.44.toFloat(),
                            0.0F
                        )
                    )
                    event.y = 0.0
                }
                started = false
            }
            if (started) {
                stage = 5
                event.y += verusHighHeightValue.get()
                mc.timer.timerSpeed = 1f
            }
        }
        if (mode.equals(
                "verusdmg",
                ignoreCase = true
            ) && !verusDmged
        ) event.zeroXZ()
        if (mode.equals("pearl", ignoreCase = true) && pearlState != 2) event.cancelEvent()
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val mode = modeValue.get()
        if (mode.equals("verushigh", true)) {
            val packet = event.packet
            if (mc.thePlayer.onGround && mc.thePlayer.hurtTime > 0 && (packet is C03PacketPlayer || packet is C0BPacketEntityAction)) {
                event.cancelEvent()
            }
            if (packet is S12PacketEntityVelocity && stage <= 5) {
                event.cancelEvent()
            }
            if (stage == 5) return
            if (!jumpWaiting) {
                if (packet is C03PacketPlayer) {
                    packet.onGround = false
                }
            } else if (packet is C03PacketPlayer && mc.thePlayer.onGround) {
                packet.onGround = true
                fulljumped = true
                if (stage == 3)
                    chat("started")
                stage = 4
            }
        }
        if (event.packet is C03PacketPlayer) {
            val packetPlayer = event.packet
            if (mode.equals("verusdmg", ignoreCase = true) && verusDmgModeValue.get()
                    .equals("Jump", ignoreCase = true) && verusJumpTimes < 5
            ) {
                packetPlayer.onGround = false
            }
        }
    }

    @EventTarget(ignoreCondition = true)
    fun onJump(event: JumpEvent) {
        jumped = true
        teleported = false
        if (state) {
            when (modeValue.get().lowercase(Locale.getDefault())) {
                "mineplex1" -> event.motion = event.motion * 4.08f
                "mineplex2" -> if (mc.thePlayer.isCollidedHorizontally) {
                    event.motion = 2.31f
                    canMineplexBoost = true
                    mc.thePlayer.onGround = false
                }

                "aacv4" -> event.motion = event.motion * 1.0799f
            }
        }
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

    override fun onDisable() {
        if (modeValue.get().equals("ncp", ignoreCase = true)) {
            if (GameSettings.isKeyDown(mc.gameSettings.keyBindJump))
                mc.gameSettings.keyBindJump.pressed = true
        }
        if (modeValue.get().equals("verushigh", true)) {
            mc.thePlayer.motionY = 0.0
            started = false
            jumpWaiting = false
            fulljumped = false
            stage = 0
        }
        mc.thePlayer.eyeHeight = mc.thePlayer.defaultEyeHeight
        mc.timer.timerSpeed = 1.0f
        if (!mc.thePlayer.isSneaking && (modeValue.get().equals("ncp", true) || modeValue.get()
                .equals("mineplex1", true) || modeValue.get().equals("mineplex2", true) || modeValue.get()
                .equals("mineplex3", true) || modeValue.get().equals("verusdmg", true) || modeValue.get()
                .equals("pearl", true))
        ) {
            mc.thePlayer.motionX = 0.0
            mc.thePlayer.motionZ = 0.0
        }
    }

    override val tag: String
        get() = modeValue.get()
}
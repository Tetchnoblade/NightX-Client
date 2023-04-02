package net.aspw.client.features.module.impl.combat

import net.aspw.client.Client
import net.aspw.client.event.*
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.features.module.impl.movement.Speed
import net.aspw.client.utils.MovementUtils
import net.aspw.client.utils.RotationUtils
import net.aspw.client.utils.timer.MSTimer
import net.aspw.client.value.BoolValue
import net.aspw.client.value.FloatValue
import net.aspw.client.value.ListValue
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.network.play.client.C0FPacketConfirmTransaction
import net.minecraft.network.play.server.S12PacketEntityVelocity
import net.minecraft.network.play.server.S27PacketExplosion
import net.minecraft.util.BlockPos
import net.minecraft.util.MathHelper
import java.util.*
import kotlin.math.cos
import kotlin.math.sin

@ModuleInfo(name = "Velocity", category = ModuleCategory.COMBAT)
class Velocity : Module() {

    /**
     * OPTIONS
     */
    private val horizontalValue = FloatValue("Horizontal", 0F, -1F, 1F, "%")
    private val verticalValue = FloatValue("Vertical", 0F, -1F, 1F, "%")
    private val horizontalExplosionValue = FloatValue("HorizontalExplosion", 0F, 0F, 1F, "%")
    private val verticalExplosionValue = FloatValue("VerticalExplosion", 0F, 0F, 1F, "%")
    private val modeValue = ListValue(
        "Mode", arrayOf(
            "Cancel",
            "Simple",
            "AACv4",
            "AAC4Reduce",
            "AAC5Reduce",
            "AAC5.2.0",
            "AAC",
            "AACPush",
            "AACZero",
            "Reverse",
            "SmoothReverse",
            "Jump",
            "Phase",
            "Vulcan",
            "MatrixReduce",
            "MatrixSimple",
            "MatrixReverse",
            "MatrixSpoof",
            "MatrixGround",
            "Legit",
            "AEMine"
        ), "Cancel"
    )

    private val aac5KillAuraValue =
        BoolValue("AAC5.2.0-Attack-Only", true, { modeValue.get().equals("aac5.2.0", true) })

    // Affect chance
    private val reduceChance = FloatValue("Reduce-Chance", 100F, 0F, 100F, "%")
    private var shouldAffect: Boolean = true

    // Reverse
    private val reverseStrengthValue =
        FloatValue("ReverseStrength", 1F, 0.1F, 1F, "x", { modeValue.get().equals("reverse", true) })
    private val reverse2StrengthValue =
        FloatValue("SmoothReverseStrength", 0.05F, 0.02F, 0.1F, "x", { modeValue.get().equals("smoothreverse", true) })

    // AAC Push
    private val aacPushXZReducerValue =
        FloatValue("AACPushXZReducer", 2F, 1F, 3F, "x", { modeValue.get().equals("aacpush", true) })
    private val aacPushYReducerValue = BoolValue("AACPushYReducer", true, { modeValue.get().equals("aacpush", true) })

    // legit
    private val legitStrafeValue = BoolValue("LegitStrafe", false, { modeValue.get().equals("legit", true) })
    private val legitFaceValue = BoolValue("LegitFace", true, { modeValue.get().equals("legit", true) })

    //add strafe in aac
    private val aacStrafeValue = BoolValue("AACStrafeValue", false, { modeValue.get().equals("aac", true) })

    //epic
    private val phaseOffsetValue =
        FloatValue("Phase-Offset", 0.05F, -10F, 10F, "m", { modeValue.get().equals("phase", true) })

    /**
     * VALUES
     */
    private var velocityTimer = MSTimer()
    private var velocityInput = false

    // Legit
    private var pos: BlockPos? = null

    // SmoothReverse
    private var reverseHurt = false

    // AACPush
    private var jump = false

    // Minemen
    private var ticks = 0
    private var lastCancel = false
    private var canCancel = false

    override fun onDisable() {
        mc.thePlayer?.speedInAir = 0.02F
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (mc.thePlayer.hurtTime <= 0) shouldAffect = (Math.random().toFloat() < reduceChance.get() / 100F)
        if (mc.thePlayer.isInWater || mc.thePlayer.isInLava || mc.thePlayer.isInWeb || !shouldAffect)
            return

        when (modeValue.get().lowercase(Locale.getDefault())) {
            "jump" -> if (mc.thePlayer.hurtTime > 0 && mc.thePlayer.onGround) {
                mc.thePlayer.motionY = 0.41999998688698

                val yaw = mc.thePlayer.rotationYaw * 0.017453292F
                mc.thePlayer.motionX -= MathHelper.sin(yaw) * 0.2
                mc.thePlayer.motionZ += MathHelper.cos(yaw) * 0.2
            }

            "reverse" -> {
                if (!velocityInput)
                    return

                if (!mc.thePlayer.onGround) {
                    MovementUtils.strafe(MovementUtils.getSpeed() * reverseStrengthValue.get())
                } else if (velocityTimer.hasTimePassed(80L))
                    velocityInput = false
            }

            "aacv4" -> {
                if (!mc.thePlayer.onGround) {
                    if (velocityInput) {
                        mc.thePlayer.speedInAir = 0.02f
                        mc.thePlayer.motionX *= 0.6
                        mc.thePlayer.motionZ *= 0.6
                    }
                } else if (velocityTimer.hasTimePassed(80L)) {
                    velocityInput = false
                    mc.thePlayer.speedInAir = 0.02f
                }
            }

            "aac4reduce" -> {
                if (mc.thePlayer.hurtTime > 0 && !mc.thePlayer.onGround && velocityInput && velocityTimer.hasTimePassed(
                        80L
                    )
                ) {
                    mc.thePlayer.motionX *= 0.62
                    mc.thePlayer.motionZ *= 0.62
                }
                if (velocityInput && (mc.thePlayer.hurtTime < 4 || mc.thePlayer.onGround) && velocityTimer.hasTimePassed(
                        120L
                    )
                ) {
                    velocityInput = false
                }
            }

            "aac5reduce" -> {
                if (mc.thePlayer.hurtTime > 1 && velocityInput) {
                    mc.thePlayer.motionX *= 0.81
                    mc.thePlayer.motionZ *= 0.81
                }
                if (velocityInput && (mc.thePlayer.hurtTime < 5 || mc.thePlayer.onGround) && velocityTimer.hasTimePassed(
                        120L
                    )
                ) {
                    velocityInput = false
                }
            }

            "smoothreverse" -> {
                if (!velocityInput) {
                    mc.thePlayer.speedInAir = 0.02F
                    return
                }

                if (mc.thePlayer.hurtTime > 0)
                    reverseHurt = true

                if (!mc.thePlayer.onGround) {
                    if (reverseHurt)
                        mc.thePlayer.speedInAir = reverse2StrengthValue.get()
                } else if (velocityTimer.hasTimePassed(80)) {
                    velocityInput = false
                    reverseHurt = false
                }
            }

            "aac" -> if (velocityInput && velocityTimer.hasTimePassed(50)) {
                mc.thePlayer.motionX *= horizontalValue.get()
                mc.thePlayer.motionZ *= horizontalValue.get()
                mc.thePlayer.motionY *= verticalValue.get()
                if (aacStrafeValue.get()) {
                    MovementUtils.strafe()
                }
                velocityInput = false
            }

            "aacpush" -> {
                if (jump) {
                    if (mc.thePlayer.onGround)
                        jump = false
                } else {
                    // Strafe
                    if (mc.thePlayer.hurtTime > 0 && mc.thePlayer.motionX != 0.0 && mc.thePlayer.motionZ != 0.0)
                        mc.thePlayer.onGround = true

                    // Reduce Y
                    if (mc.thePlayer.hurtResistantTime > 0 && aacPushYReducerValue.get()
                        && !Client.moduleManager[Speed::class.java]!!.state
                    )
                        mc.thePlayer.motionY -= 0.014999993
                }

                // Reduce XZ
                if (mc.thePlayer.hurtResistantTime >= 19) {
                    val reduce = aacPushXZReducerValue.get()

                    mc.thePlayer.motionX /= reduce
                    mc.thePlayer.motionZ /= reduce
                }
            }

            "aaczero" -> if (mc.thePlayer.hurtTime > 0) {
                if (!velocityInput || mc.thePlayer.onGround || mc.thePlayer.fallDistance > 2F)
                    return

                mc.thePlayer.addVelocity(0.0, -1.0, 0.0)
                mc.thePlayer.onGround = true
            } else
                velocityInput = false

            "matrixreduce" -> {
                if (mc.thePlayer.hurtTime > 0) {
                    if (mc.thePlayer.onGround) {
                        if (mc.thePlayer.hurtTime <= 6) {
                            mc.thePlayer.motionX *= 0.70
                            mc.thePlayer.motionZ *= 0.70
                        }
                        if (mc.thePlayer.hurtTime <= 5) {
                            mc.thePlayer.motionX *= 0.80
                            mc.thePlayer.motionZ *= 0.80
                        }
                    } else if (mc.thePlayer.hurtTime <= 10) {
                        mc.thePlayer.motionX *= 0.60
                        mc.thePlayer.motionZ *= 0.60
                    }
                }
            }

            "matrixground" -> {
                if (mc.thePlayer.onGround && !mc.gameSettings.keyBindJump.isKeyDown)
                    mc.thePlayer.onGround = false
            }

            "aemine" -> {
                if (mc.thePlayer.hurtTime <= 0) {
                    return
                }
                if (mc.thePlayer.hurtTime >= 6) {
                    mc.thePlayer.motionX *= 0.605001
                    mc.thePlayer.motionZ *= 0.605001
                    mc.thePlayer.motionY *= 0.727
                } else if (!mc.thePlayer.onGround) {
                    mc.thePlayer.motionX *= 0.305001
                    mc.thePlayer.motionZ *= 0.305001
                    mc.thePlayer.motionY -= 0.095
                }
            }
        }
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet
        val killAura = Client.moduleManager[KillAura::class.java] as KillAura

        if (packet is S12PacketEntityVelocity) {
            if (packet is C0FPacketConfirmTransaction && modeValue.get().equals("Vulcan")) {
                val transUID = (packet.uid).toInt()
                if (transUID >= -31767 && transUID <= -30769) {
                    event.cancelEvent()
                }
            }

            if (mc.thePlayer == null || (mc.theWorld?.getEntityByID(packet.entityID)
                    ?: return) != mc.thePlayer || !shouldAffect
            )
                return

            velocityTimer.reset()

            when (modeValue.get().lowercase(Locale.getDefault())) {
                "cancel", "vulcan" -> event.cancelEvent()
                "simple" -> {
                    val horizontal = horizontalValue.get()
                    val vertical = verticalValue.get()

                    packet.motionX = (packet.getMotionX() * horizontal).toInt()
                    packet.motionY = (packet.getMotionY() * vertical).toInt()
                    packet.motionZ = (packet.getMotionZ() * horizontal).toInt()
                }

                "aac4reduce" -> {
                    velocityInput = true
                    packet.motionX = (packet.getMotionX() * 0.6).toInt()
                    packet.motionZ = (packet.getMotionZ() * 0.6).toInt()
                }

                "aac", "aac5reduce", "reverse", "aacv4", "smoothreverse", "aaczero" -> velocityInput = true

                "aac5.2.0" -> {
                    event.cancelEvent()
                    if (!mc.isIntegratedServerRunning && (!aac5KillAuraValue.get() || killAura.target != null)) mc.netHandler.addToSendQueue(
                        C03PacketPlayer.C04PacketPlayerPosition(
                            mc.thePlayer.posX,
                            1.7976931348623157E+308,
                            mc.thePlayer.posZ,
                            true
                        )
                    )
                }

                "matrixsimple" -> {
                    packet.motionX = (packet.getMotionX() * 0.36).toInt()
                    packet.motionZ = (packet.getMotionZ() * 0.36).toInt()
                    if (mc.thePlayer.onGround) {
                        packet.motionX = (packet.getMotionX() * 0.9).toInt()
                        packet.motionZ = (packet.getMotionZ() * 0.9).toInt()
                    }
                }

                "matrixground" -> {
                    packet.motionX = (packet.getMotionX() * 0.36).toInt()
                    packet.motionZ = (packet.getMotionZ() * 0.36).toInt()
                    if (mc.thePlayer.onGround && !mc.gameSettings.keyBindJump.isKeyDown) {
                        packet.motionY = (-628.7).toInt()
                        packet.motionX = (packet.getMotionX() * 0.6).toInt()
                        packet.motionZ = (packet.getMotionZ() * 0.6).toInt()
                    }
                }

                "matrixreverse" -> {
                    packet.motionX = (packet.getMotionX() * -0.3).toInt()
                    packet.motionZ = (packet.getMotionZ() * -0.3).toInt()
                }

                "matrixspoof" -> {
                    event.cancelEvent()
                    mc.netHandler.addToSendQueue(
                        C03PacketPlayer.C04PacketPlayerPosition(
                            mc.thePlayer.posX + packet.motionX / -24000.0,
                            mc.thePlayer.posY + packet.motionY / -24000.0,
                            mc.thePlayer.posZ + packet.motionZ / 8000.0,
                            false
                        )
                    )
                }

                "glitch" -> {
                    if (!mc.thePlayer.onGround)
                        return

                    velocityInput = true
                    event.cancelEvent()
                }

                "phase" -> mc.thePlayer.setPositionAndUpdate(
                    mc.thePlayer.posX,
                    mc.thePlayer.posY + phaseOffsetValue.get().toDouble(),
                    mc.thePlayer.posZ
                )

                "legit" -> {
                    pos = BlockPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ)
                }
            }
        }

        if (packet is S27PacketExplosion) {
            mc.thePlayer.motionX = mc.thePlayer.motionX + packet.func_149149_c() * (horizontalExplosionValue.get())
            mc.thePlayer.motionY = mc.thePlayer.motionY + packet.func_149144_d() * (verticalExplosionValue.get())
            mc.thePlayer.motionZ = mc.thePlayer.motionZ + packet.func_149147_e() * (horizontalExplosionValue.get())
            event.cancelEvent()
        }
    }

    @EventTarget
    fun onStrafe(event: StrafeEvent) {
        when (modeValue.get().lowercase(Locale.getDefault())) {
            "legit" -> {
                if (pos == null || mc.thePlayer.hurtTime <= 0)
                    return

                val rot = RotationUtils.getRotations(pos!!.x.toDouble(), pos!!.y.toDouble(), pos!!.z.toDouble())
                if (legitFaceValue.get()) {
                    RotationUtils.setTargetRotation(rot)
                }
                val yaw = rot.yaw
                if (legitStrafeValue.get()) {
                    val speed = MovementUtils.getSpeed()
                    val yaw1 = Math.toRadians(yaw.toDouble())
                    mc.thePlayer.motionX = -sin(yaw1) * speed
                    mc.thePlayer.motionZ = cos(yaw1) * speed
                } else {
                    var strafe = event.strafe
                    var forward = event.forward
                    val friction = event.friction

                    var f = strafe * strafe + forward * forward

                    if (f >= 1.0E-4F) {
                        f = MathHelper.sqrt_float(f)

                        if (f < 1.0F)
                            f = 1.0F

                        f = friction / f
                        strafe *= f
                        forward *= f

                        val yawSin = MathHelper.sin((yaw * Math.PI / 180F).toFloat())
                        val yawCos = MathHelper.cos((yaw * Math.PI / 180F).toFloat())

                        mc.thePlayer.motionX += strafe * yawCos - forward * yawSin
                        mc.thePlayer.motionZ += forward * yawCos + strafe * yawSin
                    }
                }
            }
        }
    }

    @EventTarget
    fun onJump(event: JumpEvent) {
        if (mc.thePlayer == null || mc.thePlayer.isInWater || mc.thePlayer.isInLava || mc.thePlayer.isInWeb || !shouldAffect)
            return

        when (modeValue.get().lowercase(Locale.getDefault())) {
            "aacpush" -> {
                jump = true

                if (!mc.thePlayer.isCollidedVertically)
                    event.cancelEvent()
            }

            "aacv4" -> {
                if (mc.thePlayer.hurtTime > 0) {
                    event.cancelEvent()
                }
            }

            "aaczero" -> if (mc.thePlayer.hurtTime > 0)
                event.cancelEvent()
        }
    }
}

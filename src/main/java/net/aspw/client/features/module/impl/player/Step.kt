package net.aspw.client.features.module.impl.player

import net.aspw.client.Client
import net.aspw.client.event.*
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.utils.MovementUtils
import net.aspw.client.utils.timer.MSTimer
import net.aspw.client.value.BoolValue
import net.aspw.client.value.FloatValue
import net.aspw.client.value.IntegerValue
import net.aspw.client.value.ListValue
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement
import net.minecraft.stats.StatList
import net.minecraft.util.MathHelper
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.sin

@ModuleInfo(name = "Step", category = ModuleCategory.PLAYER)
class Step : Module() {

    /**
     * OPTIONS
     */

    private val modeValue = ListValue(
        "Mode", arrayOf(
            "Vanilla",
            "Jump",
            "NewNCP",
            "NCPPacket",
            "NCP",
            "MotionNCP",
            "OldNCP",
            "BlocksMC",
            "Verus",
            "Vulcan",
            "Matrix",
            "AAC",
            "LAAC",
            "AAC3.3.4",
            "Spartan",
            "Rewinside",
            "1.5Twillight"
        ), "Vanilla"
    )

    private val heightValue = FloatValue("Height", 1.5F, 0.6F, 10F)

    private val jumpHeightValue = FloatValue("JumpHeight", 0.42F, 0.37F, 0.42F)
    private val delayValue = IntegerValue("Delay", 150, 0, 500, "ms")
    private val useTimer = BoolValue("UseTimer", true)

    /**
     * VALUES
     */

    private var isStep = false
    private var stepX = 0.0
    private var stepY = 0.0
    private var stepZ = 0.0

    private var ncpNextStep = 0
    private var spartanSwitch = false
    private var isAACStep = false
    private var ticks = 0

    private val timer = MSTimer()
    private var usedTimer = false

    private val ncp1Values = arrayOf(0.425, 0.821, 0.699, 0.599, 1.022, 1.372, 1.652, 1.869, 2.019, 1.919)
    private val ncp2Values = arrayOf(0.42, 0.7532, 1.01, 1.093, 1.015)

    override fun onDisable() {
        mc.thePlayer ?: return

        // Change step height back to default (0.5 is default)
        mc.thePlayer.stepHeight = 0.5F
        mc.timer.timerSpeed = 1.0F
        mc.thePlayer.speedInAir = 0.02F
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (usedTimer) {
            mc.timer.timerSpeed = 1F
            usedTimer = false
        }
        val mode = modeValue.get()

        // Motion steps
        when {
            mode.equals("jump", true) && mc.thePlayer.isCollidedHorizontally && mc.thePlayer.onGround
                    && !mc.gameSettings.keyBindJump.isKeyDown -> {
                fakeJump()
                mc.thePlayer.motionY = jumpHeightValue.get().toDouble()
            }

            mode.equals("laac", true) -> if (mc.thePlayer.isCollidedHorizontally && !mc.thePlayer.isOnLadder
                && !mc.thePlayer.isInWater && !mc.thePlayer.isInLava && !mc.thePlayer.isInWeb
            ) {
                if (mc.thePlayer.onGround && timer.hasTimePassed(delayValue.get().toLong())) {
                    isStep = true

                    fakeJump()
                    mc.thePlayer.motionY += 0.620000001490116

                    val f = mc.thePlayer.rotationYaw * 0.017453292F
                    mc.thePlayer.motionX -= MathHelper.sin(f) * 0.2
                    mc.thePlayer.motionZ += MathHelper.cos(f) * 0.2
                    timer.reset()
                }

                mc.thePlayer.onGround = true
            } else
                isStep = false

            mode.equals("aac3.3.4", true) -> if (mc.thePlayer.isCollidedHorizontally
                && MovementUtils.isMoving()
            ) {
                if (mc.thePlayer.onGround && couldStep()) {
                    mc.thePlayer.motionX *= 1.26
                    mc.thePlayer.motionZ *= 1.26
                    mc.thePlayer.jump()
                    isAACStep = true
                }
                if (isAACStep) {
                    mc.thePlayer.motionY -= 0.015

                    if (!mc.thePlayer.isUsingItem && mc.thePlayer.movementInput.moveStrafe == 0F)
                        mc.thePlayer.jumpMovementFactor = 0.3F
                }
            } else
                isAACStep = false

            mode.equals("1.5Twillight", true) -> if (MovementUtils.isMoving() &&
                mc.thePlayer.isCollidedHorizontally
            ) {
                ticks++
                if (ticks == 1) mc.thePlayer.motionY = 0.4399
                if (ticks == 12) mc.thePlayer.motionY = 0.4399
                if (ticks >= 40) ticks = 0
            } else if (mc.thePlayer.onGround) {
                ticks = 0
            }
        }
    }

    @EventTarget
    fun onMove(event: MoveEvent) {
        val mode = modeValue.get()

        // Motion steps
        when {
            mode.equals(
                "motionncp",
                true
            ) && mc.thePlayer.isCollidedHorizontally && !mc.gameSettings.keyBindJump.isKeyDown -> {
                when {
                    mc.thePlayer.onGround && couldStep() -> {
                        fakeJump()
                        mc.thePlayer.motionY = 0.0
                        event.y = 0.41999998688698
                        ncpNextStep = 1
                    }

                    ncpNextStep == 1 -> {
                        event.y = 0.7531999805212 - 0.41999998688698
                        ncpNextStep = 2
                    }

                    ncpNextStep == 2 -> {
                        val yaw = MovementUtils.getDirection()

                        event.y = 1.001335979112147 - 0.7531999805212
                        event.x = -sin(yaw) * 0.7
                        event.z = cos(yaw) * 0.7

                        ncpNextStep = 0
                    }
                }
            }
        }
    }

    @EventTarget
    fun onStep(event: StepEvent) {
        mc.thePlayer ?: return

        val mode = modeValue.get()

        // Set step to default in some cases
        if (!mc.thePlayer.onGround || !timer.hasTimePassed(delayValue.get().toLong()) ||
            mode.equals("Jump", true) || mode.equals("MotionNCP", true)
            || mode.equals("LAAC", true) || mode.equals("AAC3.3.4", true)
            || mode.equals("AACv4", true) || mode.equals("1.5Twillight", true)
        ) {
            mc.thePlayer.stepHeight = 0.5F
            event.stepHeight = 0.5F
            return
        }

        // Set step height
        val height = heightValue.get()
        mc.thePlayer.stepHeight = height
        usedTimer = true
        event.stepHeight = height

        // Detect possible step
        if (event.stepHeight > 0.5F) {
            isStep = true
            stepX = mc.thePlayer.posX
            stepY = mc.thePlayer.posY
            stepZ = mc.thePlayer.posZ
        }
    }

    @EventTarget(ignoreCondition = true)
    fun onStepConfirm(event: StepConfirmEvent) {
        if (mc.thePlayer == null || !isStep) // Check if step
            return

        if (mc.thePlayer.entityBoundingBox.minY - stepY > 0.8) {
            if (Client.moduleManager[Step::class.java]!!.state && useTimer.get() && mc.thePlayer.onGround && !modeValue.get()
                    .equals("Matrix")
            ) {
                mc.timer.timerSpeed = 0.52f
            }
            if (Client.moduleManager[Step::class.java]!!.state && useTimer.get() && mc.thePlayer.onGround && modeValue.get()
                    .equals("Matrix")
            ) {
                mc.timer.timerSpeed = 0.12f
            }
        } else {
            mc.timer.timerSpeed = 1.0f
        }

        if (mc.thePlayer.entityBoundingBox.minY - stepY > 0.8) { // Check if full block step
            val mode = modeValue.get()

            when {
                mode.equals("NCPPacket", true) -> {
                    val rHeight = mc.thePlayer.entityBoundingBox.minY - stepY
                    when {
                        rHeight > 2.019 -> {
                            ncp1Values.forEach {
                                mc.netHandler.addToSendQueue(
                                    C03PacketPlayer.C04PacketPlayerPosition(
                                        stepX,
                                        stepY + it,
                                        stepZ,
                                        false
                                    )
                                )
                            }
                            mc.thePlayer.motionX = 0.0
                            mc.thePlayer.motionZ = 0.0
                        }

                        rHeight > 1.869 -> {
                            for (i in 0..7)
                                mc.netHandler.addToSendQueue(
                                    C03PacketPlayer.C04PacketPlayerPosition(
                                        stepX,
                                        stepY + ncp1Values[i],
                                        stepZ,
                                        false
                                    )
                                )
                            mc.thePlayer.motionX = 0.0
                            mc.thePlayer.motionZ = 0.0
                        }

                        rHeight > 1.5 -> {
                            for (i in 0..6)
                                mc.netHandler.addToSendQueue(
                                    C03PacketPlayer.C04PacketPlayerPosition(
                                        stepX,
                                        stepY + ncp1Values[i],
                                        stepZ,
                                        false
                                    )
                                )
                            mc.thePlayer.motionX = 0.0
                            mc.thePlayer.motionZ = 0.0
                        }

                        rHeight > 1.015 -> {
                            ncp2Values.forEach {
                                mc.netHandler.addToSendQueue(
                                    C03PacketPlayer.C04PacketPlayerPosition(
                                        stepX,
                                        stepY + it,
                                        stepZ,
                                        false
                                    )
                                )
                            }
                            mc.thePlayer.motionX = 0.0
                            mc.thePlayer.motionZ = 0.0
                        }

                        rHeight > 0.875 -> {
                            mc.netHandler.addToSendQueue(
                                C03PacketPlayer.C04PacketPlayerPosition(
                                    stepX,
                                    stepY + 0.41999998688698, stepZ, false
                                )
                            )
                            mc.netHandler.addToSendQueue(
                                C03PacketPlayer.C04PacketPlayerPosition(
                                    stepX,
                                    stepY + 0.7531999805212, stepZ, false
                                )
                            )
                            mc.thePlayer.motionX = 0.0
                            mc.thePlayer.motionZ = 0.0
                        }

                        rHeight > 0.6 -> {
                            mc.netHandler.addToSendQueue(
                                C03PacketPlayer.C04PacketPlayerPosition(
                                    stepX,
                                    stepY + 0.39, stepZ, false
                                )
                            )
                            mc.netHandler.addToSendQueue(
                                C03PacketPlayer.C04PacketPlayerPosition(
                                    stepX,
                                    stepY + 0.6938, stepZ, false
                                )
                            )
                            mc.thePlayer.motionX = 0.0
                            mc.thePlayer.motionZ = 0.0
                        }
                    }
                }

                mode.equals("BlocksMC", true) -> {
                    fakeJump()

                    val pos = mc.thePlayer.position.add(0.0, -1.5, 0.0)

                    mc.netHandler.addToSendQueue(
                        C08PacketPlayerBlockPlacement(
                            pos,
                            1,
                            ItemStack(Blocks.stone.getItem(mc.theWorld, pos)),
                            0.0F,
                            0.5F + Math.random().toFloat() * 0.44.toFloat(),
                            0.0F
                        )
                    )

                    mc.netHandler.addToSendQueue(
                        C03PacketPlayer.C04PacketPlayerPosition(
                            stepX,
                            stepY + 0.41999998688698, stepZ, false
                        )
                    )
                    mc.netHandler.addToSendQueue(
                        C03PacketPlayer.C04PacketPlayerPosition(
                            stepX,
                            stepY + 0.7531999805212, stepZ, false
                        )
                    )
                    mc.netHandler.addToSendQueue(
                        C03PacketPlayer.C04PacketPlayerPosition(
                            stepX,
                            stepY + 1, stepZ, true
                        )
                    )
                    timer.reset()
                }

                mode.equals("NCP", true) || mode.equals("AAC", true) -> {
                    fakeJump()

                    // Half legit step (1 packet missing) [COULD TRIGGER TOO MANY PACKETS]
                    mc.netHandler.addToSendQueue(
                        C03PacketPlayer.C04PacketPlayerPosition(
                            stepX,
                            stepY + 0.41999998688698, stepZ, false
                        )
                    )
                    mc.netHandler.addToSendQueue(
                        C03PacketPlayer.C04PacketPlayerPosition(
                            stepX,
                            stepY + 0.7531999805212, stepZ, false
                        )
                    )
                    timer.reset()
                }

                mode.equals("Spartan", true) -> {
                    fakeJump()

                    if (spartanSwitch) {
                        // Vanilla step (3 packets) [COULD TRIGGER TOO MANY PACKETS]
                        mc.netHandler.addToSendQueue(
                            C03PacketPlayer.C04PacketPlayerPosition(
                                stepX,
                                stepY + 0.41999998688698, stepZ, false
                            )
                        )
                        mc.netHandler.addToSendQueue(
                            C03PacketPlayer.C04PacketPlayerPosition(
                                stepX,
                                stepY + 0.7531999805212, stepZ, false
                            )
                        )
                        mc.netHandler.addToSendQueue(
                            C03PacketPlayer.C04PacketPlayerPosition(
                                stepX,
                                stepY + 1.001335979112147, stepZ, false
                            )
                        )
                    } else // Force step
                        mc.netHandler.addToSendQueue(
                            C03PacketPlayer.C04PacketPlayerPosition(
                                stepX,
                                stepY + 0.6, stepZ, false
                            )
                        )

                    // Spartan allows one unlegit step so just swap between legit and unlegit
                    spartanSwitch = !spartanSwitch

                    // Reset timer
                    timer.reset()
                }

                mode.equals("Vulcan", true) -> {
                    val superHeight = mc.thePlayer.entityBoundingBox.minY - stepY
                    fakeJump()
                    when {
                        superHeight > 2.0 -> {
                            val stpPacket = arrayOf(0.5, 1.0, 1.5, 2.0)
                            stpPacket.forEach {
                                mc.thePlayer.sendQueue.addToSendQueue(
                                    C03PacketPlayer.C04PacketPlayerPosition(
                                        stepX,
                                        stepY + it, stepZ, true
                                    )
                                )
                            }
                        }

                        superHeight <= 2.0 && superHeight > 1.5 -> {
                            val stpPacket = arrayOf(0.5, 1.0, 1.5)
                            stpPacket.forEach {
                                mc.thePlayer.sendQueue.addToSendQueue(
                                    C03PacketPlayer.C04PacketPlayerPosition(
                                        stepX,
                                        stepY + it, stepZ, true
                                    )
                                )
                            }
                        }

                        superHeight <= 1.5 && superHeight > 1.0 -> {
                            val stpPacket = arrayOf(0.5, 1.0)
                            stpPacket.forEach {
                                mc.thePlayer.sendQueue.addToSendQueue(
                                    C03PacketPlayer.C04PacketPlayerPosition(
                                        stepX,
                                        stepY + it, stepZ, true
                                    )
                                )
                            }
                        }

                        superHeight <= 1.0 && superHeight > 0.6 -> {
                            val stpPacket = arrayOf(0.5)
                            stpPacket.forEach {
                                mc.thePlayer.sendQueue.addToSendQueue(
                                    C03PacketPlayer.C04PacketPlayerPosition(
                                        stepX,
                                        stepY + it, stepZ, true
                                    )
                                )
                            }
                        }
                    }
                    timer.reset()
                }

                mode.equals("Verus", true) -> {
                    val superHeight = mc.thePlayer.entityBoundingBox.minY - stepY
                    mc.timer.timerSpeed = 1f / ceil(superHeight * 2.0).toFloat()
                    var superHighest = 0.0
                    fakeJump()
                    repeat((ceil(superHeight * 2.0) - 1.0).toInt()) {
                        superHighest += 0.5
                        mc.thePlayer.sendQueue.addToSendQueue(
                            C03PacketPlayer.C04PacketPlayerPosition(
                                stepX,
                                stepY + superHighest,
                                stepZ,
                                true
                            )
                        )
                    }
                    timer.reset()
                }

                mode.equals("NewNCP", true) -> {
                    fakeJump()
                    mc.netHandler.addToSendQueue(
                        C03PacketPlayer.C04PacketPlayerPosition(
                            stepX,
                            stepY + 0.41999998688698, stepZ, false
                        )
                    )
                    mc.netHandler.addToSendQueue(
                        C03PacketPlayer.C04PacketPlayerPosition(
                            stepX,
                            stepY + 0.7531999805212, stepZ, false
                        )
                    )
                    mc.netHandler.addToSendQueue(
                        C03PacketPlayer.C04PacketPlayerPosition(
                            stepX,
                            stepY + 1, stepZ, true
                        )
                    )
                }

                mode.equals("Matrix", true) -> {
                    fakeJump()

                    // Vanilla step (3 packets) [COULD TRIGGER TOO MANY PACKETS]
                    mc.netHandler.addToSendQueue(
                        C03PacketPlayer.C04PacketPlayerPosition(
                            stepX,
                            stepY + 0.41999998688698, stepZ, false
                        )
                    )
                    mc.netHandler.addToSendQueue(
                        C03PacketPlayer.C04PacketPlayerPosition(
                            stepX,
                            stepY + 0.7531999805212, stepZ, false
                        )
                    )
                    mc.netHandler.addToSendQueue(
                        C03PacketPlayer.C04PacketPlayerPosition(
                            stepX,
                            stepY + 1.001335979112147, stepZ, false
                        )
                    )

                    // Reset timer
                    timer.reset()
                }

                mode.equals("Rewinside", true) -> {
                    fakeJump()

                    // Vanilla step (3 packets) [COULD TRIGGER TOO MANY PACKETS]
                    mc.netHandler.addToSendQueue(
                        C03PacketPlayer.C04PacketPlayerPosition(
                            stepX,
                            stepY + 0.41999998688698, stepZ, false
                        )
                    )
                    mc.netHandler.addToSendQueue(
                        C03PacketPlayer.C04PacketPlayerPosition(
                            stepX,
                            stepY + 0.7531999805212, stepZ, false
                        )
                    )
                    mc.netHandler.addToSendQueue(
                        C03PacketPlayer.C04PacketPlayerPosition(
                            stepX,
                            stepY + 1.001335979112147, stepZ, false
                        )
                    )

                    // Reset timer
                    timer.reset()
                }

            }
        }

        isStep = false
        stepX = 0.0
        stepY = 0.0
        stepZ = 0.0
    }

    @EventTarget(ignoreCondition = true)
    fun onPacket(event: PacketEvent) {
        val packet = event.packet

        if (packet is C03PacketPlayer && isStep && modeValue.get().equals("OldNCP", true)) {
            packet.y += 0.07
            isStep = false
        }
    }

    // There could be some anti cheats which tries to detect step by checking for achievements and stuff
    private fun fakeJump() {
        mc.thePlayer.isAirBorne = true
        mc.thePlayer.triggerAchievement(StatList.jumpStat)
    }

    private fun couldStep(): Boolean {
        val yaw = MovementUtils.getDirection()
        val x = -sin(yaw) * 0.4
        val z = cos(yaw) * 0.4

        return mc.theWorld.getCollisionBoxes(mc.thePlayer.entityBoundingBox.offset(x, 1.001335979112147, z))
            .isEmpty()
    }

    override val tag: String
        get() = modeValue.get()
}
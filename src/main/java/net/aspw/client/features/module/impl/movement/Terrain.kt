package net.aspw.client.features.module.impl.movement

import net.aspw.client.event.BlockBBEvent
import net.aspw.client.event.EventTarget
import net.aspw.client.event.MoveEvent
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.utils.block.BlockUtils.collideBlockIntersects
import net.aspw.client.utils.block.BlockUtils.getBlock
import net.aspw.client.value.BoolValue
import net.aspw.client.value.FloatValue
import net.aspw.client.value.ListValue
import net.minecraft.block.BlockLadder
import net.minecraft.block.BlockVine
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing

@ModuleInfo(name = "Terrain", category = ModuleCategory.MOVEMENT)
class Terrain : Module() {

    val modeValue = ListValue(
        "Mode",
        arrayOf(
            "Vanilla",
            "Clip",
            "AAC3.0.0",
            "AAC3.0.5",
            "SAAC3.1.2",
            "AAC3.1.2",
            "Spartan",
            "Negativity",
            "Horizon1.4.6",
            "HiveMC"
        ), "Vanilla"
    )
    private val upSpeedValue = FloatValue("UpSpeed", 0.3F, 0.01F, 10F)
    private val downSpeedValue = FloatValue("DownSpeed", 0.15F, 0.01F, 10F)
    private val timerValue = FloatValue("Timer", 1F, 0.1F, 10F, "x")
    private val spartanTimerBoostValue = BoolValue("SpartanTimerBoost", false)

    private var usedTimer = false

    @EventTarget
    fun onMove(event: MoveEvent) {
        if (usedTimer) {
            mc.timer.timerSpeed = 1F
            usedTimer = false
        }
        val mode = modeValue.get()

        when {
            mode.equals("Vanilla", ignoreCase = true) && mc.thePlayer.isOnLadder -> {
                if (mc.thePlayer.isCollidedHorizontally) {
                    event.y = upSpeedValue.get().toDouble()
                } else if (!mc.gameSettings.keyBindSneak.pressed) {
                    event.y = -downSpeedValue.get().toDouble()
                }
                mc.thePlayer.motionY = 0.0

                mc.timer.timerSpeed = timerValue.get()
                usedTimer = true
            }

            mode.equals("AAC3.0.0", ignoreCase = true) && mc.thePlayer.isCollidedHorizontally -> {
                var x = 0.0
                var z = 0.0

                when (mc.thePlayer.horizontalFacing) {
                    EnumFacing.NORTH -> z = -0.99
                    EnumFacing.EAST -> x = +0.99
                    EnumFacing.SOUTH -> z = +0.99
                    EnumFacing.WEST -> x = -0.99
                    else -> {}
                }

                val block = getBlock(BlockPos(mc.thePlayer.posX + x, mc.thePlayer.posY, mc.thePlayer.posZ + z))
                if (block is BlockLadder || block is BlockVine) {
                    event.y = 0.5
                    mc.thePlayer.motionY = 0.0
                }
            }

            mode.equals("AAC3.0.5", ignoreCase = true) && mc.gameSettings.keyBindForward.isKeyDown &&
                    collideBlockIntersects(
                        mc.thePlayer.entityBoundingBox,
                        { it is BlockLadder || it is BlockVine }) -> {
                event.x = 0.0
                event.y = 0.5
                event.z = 0.0

                mc.thePlayer.motionX = 0.0
                mc.thePlayer.motionY = 0.0
                mc.thePlayer.motionZ = 0.0
            }

            mode.equals("SAAC3.1.2", ignoreCase = true) && mc.thePlayer.isCollidedHorizontally &&
                    mc.thePlayer.isOnLadder -> {
                event.y = 0.1649
                mc.thePlayer.motionY = 0.0
            }

            mode.equals("AAC3.1.2", ignoreCase = true) && mc.thePlayer.isCollidedHorizontally &&
                    mc.thePlayer.isOnLadder -> {
                event.y = 0.1699
                mc.thePlayer.motionY = 0.0


            }

            mode.equals("Spartan", ignoreCase = true) && mc.thePlayer.isOnLadder -> {
                if (mc.thePlayer.isCollidedHorizontally) {
                    event.y = 0.199
                } else if (!mc.gameSettings.keyBindSneak.pressed) {
                    event.y = -1.3489
                }
                mc.thePlayer.motionY = 0.0
                // timer op in spartan XDDD
                if (spartanTimerBoostValue.get()) {
                    if (mc.thePlayer.isOnLadder) {
                        if (mc.thePlayer.ticksExisted % 2 == 0) {
                            mc.timer.timerSpeed = 2.5F
                        }
                        if (mc.thePlayer.ticksExisted % 30 == 0) {
                            mc.timer.timerSpeed = 3F
                        }
                        usedTimer = true
                    }
                }
            }
            //these fastclimb mode are just presets. not special
            mode.equals("Negativity", ignoreCase = true) && mc.thePlayer.isOnLadder -> {
                if (mc.thePlayer.isCollidedHorizontally) {
                    event.y = 0.2299
                } else if (!mc.gameSettings.keyBindSneak.pressed) {
                    event.y = -0.226
                }
                mc.thePlayer.motionY = 0.0
            }

            mode.equals("Twillight", ignoreCase = true) && mc.thePlayer.isOnLadder -> {
                if (mc.thePlayer.isCollidedHorizontally) {
                    event.y = 0.16
                } else if (!mc.gameSettings.keyBindSneak.pressed) {
                    event.y = -7.99
                }
                mc.thePlayer.motionY = 0.0
            }

            mode.equals("Horizon1.4.6", ignoreCase = true) && mc.thePlayer.isOnLadder -> {
                if (mc.thePlayer.isCollidedHorizontally) {
                    event.y = 0.125
                } else if (!mc.gameSettings.keyBindSneak.pressed) {
                    event.y = -0.16
                }
                mc.thePlayer.motionY = 0.0
            }

            mode.equals("HiveMC", ignoreCase = true) && mc.thePlayer.isOnLadder -> {
                if (mc.thePlayer.isCollidedHorizontally) {
                    event.y = 0.179
                } else if (!mc.gameSettings.keyBindSneak.pressed) {
                    event.y = -0.225
                }
                mc.thePlayer.motionY = 0.0
            }


            mode.equals(
                "Clip",
                ignoreCase = true
            ) && mc.thePlayer.isOnLadder && mc.gameSettings.keyBindForward.isKeyDown -> {
                for (i in mc.thePlayer.posY.toInt()..mc.thePlayer.posY.toInt() + 8) {
                    val block = getBlock(BlockPos(mc.thePlayer.posX, i.toDouble(), mc.thePlayer.posZ))

                    if (block !is BlockLadder) {
                        var x = 0.0
                        var z = 0.0
                        when (mc.thePlayer.horizontalFacing) {
                            EnumFacing.NORTH -> z = -1.0
                            EnumFacing.EAST -> x = +1.0
                            EnumFacing.SOUTH -> z = +1.0
                            EnumFacing.WEST -> x = -1.0
                            else -> {}
                        }

                        mc.thePlayer.setPosition(mc.thePlayer.posX + x, i.toDouble(), mc.thePlayer.posZ + z)
                        break
                    } else {
                        mc.thePlayer.setPosition(mc.thePlayer.posX, i.toDouble(), mc.thePlayer.posZ)
                    }
                }
            }
        }
    }

    @EventTarget
    fun onBlockBB(event: BlockBBEvent) {
        if (mc.thePlayer != null && (event.block is BlockLadder || event.block is BlockVine) &&
            modeValue.get().equals("AAC3.0.5", ignoreCase = true) && mc.thePlayer.isOnLadder
        )
            event.boundingBox = null
    }

    override val tag: String
        get() = modeValue.get()
}
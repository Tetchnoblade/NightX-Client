package net.aspw.client.features.module.impl.player

import net.aspw.client.Client
import net.aspw.client.event.EventState
import net.aspw.client.event.EventTarget
import net.aspw.client.event.MotionEvent
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.utils.RotationUtils
import net.aspw.client.utils.block.BlockUtils
import net.aspw.client.utils.extensions.getVec
import net.aspw.client.utils.timer.MSTimer
import net.aspw.client.value.BlockValue
import net.aspw.client.value.BoolValue
import net.aspw.client.value.FloatValue
import net.aspw.client.value.IntegerValue
import net.minecraft.block.Block
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.init.Blocks
import net.minecraft.network.play.client.C0APacketAnimation
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing
import net.minecraft.util.Vec3

@ModuleInfo(name = "StealAura", spacedName = "Steal Aura", category = ModuleCategory.PLAYER)
class StealAura : Module() {

    private val rangeValue = FloatValue("Range", 4F, 1F, 6F, "m")
    private val delayValue = IntegerValue("Delay", 50, 50, 200, "ms")
    private val throughWallsValue = BoolValue("ThroughWalls", true)
    private val visualSwing = BoolValue("VisualSwing", true)
    private val chestValue = BlockValue("Chest", Block.getIdFromBlock(Blocks.chest))
    private val rotationsValue = BoolValue("Rotations", true)

    private var currentBlock: BlockPos? = null
    private val timer = MSTimer()

    val clickedBlocks = mutableListOf<BlockPos>()

    @EventTarget
    fun onMotion(event: MotionEvent) {
        if (Client.moduleManager[Blink::class.java]!!.state)
            return

        when (event.eventState) {
            EventState.PRE -> {
                if (mc.currentScreen is GuiContainer)
                    timer.reset()

                val radius = rangeValue.get() + 1

                val eyesPos = Vec3(
                    mc.thePlayer.posX, mc.thePlayer.entityBoundingBox.minY + mc.thePlayer.getEyeHeight(),
                    mc.thePlayer.posZ
                )

                currentBlock = BlockUtils.searchBlocks(radius.toInt())
                    .filter {
                        Block.getIdFromBlock(it.value) == chestValue.get() && !clickedBlocks.contains(it.key)
                                && BlockUtils.getCenterDistance(it.key) < rangeValue.get()
                    }
                    .filter {
                        if (throughWallsValue.get())
                            return@filter true

                        val blockPos = it.key
                        val movingObjectPosition = mc.theWorld.rayTraceBlocks(
                            eyesPos,
                            blockPos.getVec(), false, true, false
                        )

                        movingObjectPosition != null && movingObjectPosition.blockPos == blockPos
                    }
                    .minByOrNull { BlockUtils.getCenterDistance(it.key) }?.key

                if (rotationsValue.get())
                    RotationUtils.setTargetRotation(
                        (RotationUtils.faceBlock(currentBlock ?: return)
                            ?: return).rotation
                    )
            }

            EventState.POST -> if (currentBlock != null && timer.hasTimePassed(delayValue.get().toLong())) {
                if (mc.playerController.onPlayerRightClick(
                        mc.thePlayer, mc.theWorld, mc.thePlayer.heldItem, currentBlock,
                        EnumFacing.DOWN, currentBlock!!.getVec()
                    )
                ) {
                    if (visualSwing.get())
                        mc.thePlayer.swingItem()
                    else
                        mc.netHandler.addToSendQueue(C0APacketAnimation())

                    clickedBlocks.add(currentBlock!!)
                    currentBlock = null
                    timer.reset()
                }
            }
        }
    }

    override fun onDisable() {
        clickedBlocks.clear()
    }
}
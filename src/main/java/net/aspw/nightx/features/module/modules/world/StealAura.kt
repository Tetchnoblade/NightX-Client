package net.aspw.nightx.features.module.modules.world

import net.aspw.nightx.NightX
import net.aspw.nightx.event.EventState
import net.aspw.nightx.event.EventTarget
import net.aspw.nightx.event.MotionEvent
import net.aspw.nightx.features.module.Module
import net.aspw.nightx.features.module.ModuleCategory
import net.aspw.nightx.features.module.ModuleInfo
import net.aspw.nightx.features.module.modules.player.Blink
import net.aspw.nightx.utils.RotationUtils
import net.aspw.nightx.utils.block.BlockUtils
import net.aspw.nightx.utils.extensions.getVec
import net.aspw.nightx.utils.timer.MSTimer
import net.aspw.nightx.value.BlockValue
import net.aspw.nightx.value.BoolValue
import net.aspw.nightx.value.FloatValue
import net.aspw.nightx.value.IntegerValue
import net.minecraft.block.Block
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.init.Blocks
import net.minecraft.network.play.client.C0APacketAnimation
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing
import net.minecraft.util.Vec3

@ModuleInfo(name = "StealAura", spacedName = "Steal Aura", category = ModuleCategory.WORLD)
object StealAura : Module() {

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
        if (NightX.moduleManager[Blink::class.java]!!.state)
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
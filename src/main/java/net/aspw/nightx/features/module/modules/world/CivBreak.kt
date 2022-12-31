package net.aspw.nightx.features.module.modules.world

import net.aspw.nightx.event.*
import net.aspw.nightx.features.module.Module
import net.aspw.nightx.features.module.ModuleCategory
import net.aspw.nightx.features.module.ModuleInfo
import net.aspw.nightx.utils.PacketUtils
import net.aspw.nightx.utils.RotationUtils
import net.aspw.nightx.utils.block.BlockUtils
import net.aspw.nightx.utils.render.RenderUtils
import net.aspw.nightx.value.BoolValue
import net.aspw.nightx.value.FloatValue
import net.minecraft.block.BlockAir
import net.minecraft.network.play.client.C07PacketPlayerDigging
import net.minecraft.network.play.client.C0APacketAnimation
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing
import java.awt.Color

@ModuleInfo(name = "CivBreak", spacedName = "Civ Break", category = ModuleCategory.WORLD)
class CivBreak : Module() {

    private var blockPos: BlockPos? = null
    private var enumFacing: EnumFacing? = null

    private val range = FloatValue("Range", 5F, 1F, 6F)
    private val rotationsValue = BoolValue("Rotations", true)
    private val visualSwingValue = BoolValue("VisualSwing", false)

    private val airResetValue = BoolValue("Air-Reset", false)
    private val rangeResetValue = BoolValue("Range-Reset", false)
    private val redValue = IntegerValue("Red", 255, 0, 255)
    private val greenValue = IntegerValue("Green", 120, 0, 255)
    private val blueValue = IntegerValue("Blue", 255, 0, 255)


    @EventTarget
    fun onBlockClick(event: ClickBlockEvent) {
        blockPos = event.clickedBlock
        enumFacing = event.enumFacing
    }

    override fun onDisable() {
        blockPos ?: return
        blockPos = null
    }

    @EventTarget
    fun onUpdate(event: MotionEvent) {
        val pos = blockPos ?: return

        if (airResetValue.get() && BlockUtils.getBlock(pos) is BlockAir ||
            rangeResetValue.get() && BlockUtils.getCenterDistance(pos) > range.get()
        ) {
            blockPos = null
            return
        }

        if (BlockUtils.getBlock(pos) is BlockAir || BlockUtils.getCenterDistance(pos) > range.get()) {
            return
        }

        if (blockPos !== null) {
            event.onGround = true
        }

        when (event.eventState) {
            EventState.PRE -> if (rotationsValue.get()) {
                RotationUtils.setTargetRotation((RotationUtils.faceBlock(pos) ?: return).rotation)
            }

            EventState.POST -> {
                if (visualSwingValue.get()) {
                    mc.thePlayer.swingItem()
                } else {
                    mc.netHandler.addToSendQueue(C0APacketAnimation())
                }

                // Break
                PacketUtils.sendPacketNoEvent(
                    C07PacketPlayerDigging(
                        C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK,
                        blockPos,
                        enumFacing
                    )
                )
                PacketUtils.sendPacketNoEvent(
                    C07PacketPlayerDigging(
                        C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK,
                        blockPos,
                        enumFacing
                    )
                )
            }
        }
    }

    @EventTarget
    fun onRender3D(event: Render3DEvent) {
        RenderUtils.drawBlockBox(blockPos ?: return, Color(redValue.get(), greenValue.get(), blueValue.get()), true)
    }
}

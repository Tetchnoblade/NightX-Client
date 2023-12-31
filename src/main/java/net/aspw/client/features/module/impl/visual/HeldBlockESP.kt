package net.aspw.client.features.module.impl.visual

import net.aspw.client.event.EventTarget
import net.aspw.client.event.Render3DEvent
import net.aspw.client.event.UpdateEvent
import net.aspw.client.event.WorldEvent
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.util.block.BlockUtils
import net.aspw.client.util.render.RenderUtils
import net.aspw.client.value.IntegerValue
import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.util.BlockPos
import java.awt.Color

@ModuleInfo(name = "HeldBlockESP", spacedName = "Held Block ESP", description = "", category = ModuleCategory.VISUAL)
class HeldBlockESP : Module() {
    private val rangeValue = IntegerValue("Range", 10, 1, 10, "m")
    private var pos: BlockPos? = null

    override fun onDisable() {
        pos = null
    }

    @EventTarget
    fun onWorld(event: WorldEvent) {
        pos = null
    }

    @EventTarget
    fun onRender3D(event: Render3DEvent) {
        RenderUtils.drawBlockBox(pos ?: return, Color.YELLOW, true)
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (pos != null && mc.thePlayer.ticksExisted % 3 == 0)
            pos = null
        if (pos == null || BlockUtils.getCenterDistance(pos!!) > rangeValue.get())
            pos = find()
    }

    private fun find(): BlockPos? {
        val radius = rangeValue.get() + 1

        var nearestBlockDistance = Double.MAX_VALUE
        var nearestBlock: BlockPos? = null

        for (x in radius downTo -radius + 1) {
            for (y in radius downTo -radius + 1) {
                for (z in radius downTo -radius + 1) {
                    val blockPos = BlockPos(
                        mc.thePlayer.posX.toInt() + x, mc.thePlayer.posY.toInt() + y,
                        mc.thePlayer.posZ.toInt() + z
                    )
                    val block = BlockUtils.getBlock(blockPos) ?: continue

                    if (mc.thePlayer.heldItem == null) return null

                    if (Block.getIdFromBlock(block) != Item.getIdFromItem(mc.thePlayer.heldItem.item)) continue

                    val distance = BlockUtils.getCenterDistance(blockPos)
                    if (distance > rangeValue.get()) continue
                    if (nearestBlockDistance < distance) continue

                    nearestBlockDistance = distance
                    nearestBlock = blockPos
                }
            }
        }
        return nearestBlock
    }
}
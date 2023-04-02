package net.aspw.client.features.module.impl.other

import net.aspw.client.event.EventTarget
import net.aspw.client.event.UpdateEvent
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.minecraft.block.Block
import net.minecraft.item.ItemShears
import net.minecraft.item.ItemSword
import net.minecraft.item.ItemTool
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing
import net.minecraft.util.Vec3

@ModuleInfo(name = "AntiObsidian", spacedName = "Anti Obsidian", category = ModuleCategory.OTHER)
class AntiObsidian : Module() {

    @EventTarget
    fun onUpdate(e: UpdateEvent) {
        val sand = BlockPos(Vec3(mc.thePlayer.posX, mc.thePlayer.posY + 3, mc.thePlayer.posZ))
        val sandBlock = mc.theWorld.getBlockState(sand).block
        val forge = BlockPos(Vec3(mc.thePlayer.posX, mc.thePlayer.posY + 2, mc.thePlayer.posZ))
        val forgeblock = mc.theWorld.getBlockState(forge).block
        val obsidianPos = BlockPos(Vec3(mc.thePlayer.posX, mc.thePlayer.posY + 1, mc.thePlayer.posZ))
        val obsidianBlock = mc.theWorld.getBlockState(obsidianPos).block
        if (obsidianBlock === Block.getBlockById(49)) {
            bestTool(
                mc.objectMouseOver.blockPos.x, mc.objectMouseOver.blockPos.y,
                mc.objectMouseOver.blockPos.z
            )
            val downpos = BlockPos(Vec3(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ))
            mc.playerController.onPlayerDamageBlock(downpos, EnumFacing.UP)
        }
        if (forgeblock === Block.getBlockById(61)) {
            bestTool(
                mc.objectMouseOver.blockPos.x, mc.objectMouseOver.blockPos.y,
                mc.objectMouseOver.blockPos.z
            )
            val downpos = BlockPos(Vec3(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ))
            mc.playerController.onPlayerDamageBlock(downpos, EnumFacing.UP)
        }
        if (sandBlock === Block.getBlockById(12) || sandBlock === Block.getBlockById(13)) {
            bestTool(
                mc.objectMouseOver.blockPos.x, mc.objectMouseOver.blockPos.y,
                mc.objectMouseOver.blockPos.z
            )
            val downpos = BlockPos(Vec3(mc.thePlayer.posX, mc.thePlayer.posY + 3, mc.thePlayer.posZ))
            mc.playerController.onPlayerDamageBlock(downpos, EnumFacing.UP)
        }
    }

    private fun bestTool(x: Int, y: Int, z: Int) {
        val blockId = Block.getIdFromBlock(mc.theWorld.getBlockState(BlockPos(x, y, z)).block)
        var bestSlot = 0
        var f = -1.0f
        for (i1 in 36..44) {
            try {
                val curSlot = mc.thePlayer.inventoryContainer.getSlot(i1).stack
                if ((curSlot.item is ItemTool || curSlot.item is ItemSword || curSlot.item is ItemShears) && curSlot.getStrVsBlock(
                        Block.getBlockById(blockId)
                    ) > f
                ) {
                    bestSlot = i1 - 36
                    f = curSlot.getStrVsBlock(Block.getBlockById(blockId))
                }
            } catch (var9: Exception) {
            }
        }
        if (f != -1.0f) {
            mc.thePlayer.inventory.currentItem = bestSlot
            mc.playerController.updateController()
        }
    }
}
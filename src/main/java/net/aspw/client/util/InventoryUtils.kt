package net.aspw.client.util

import net.aspw.client.event.ClickWindowEvent
import net.aspw.client.event.EventTarget
import net.aspw.client.event.Listenable
import net.aspw.client.event.PacketEvent
import net.aspw.client.util.timer.MSTimer
import net.minecraft.init.Blocks
import net.minecraft.item.Item
import net.minecraft.item.ItemBlock
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement
import java.util.*

/**
 * The type Inventory utils.
 */
class InventoryUtils : MinecraftInstance(), Listenable {
    /**
     * On click.
     *
     * @param event the event
     */
    @EventTarget
    fun onClick(event: ClickWindowEvent?) {
        CLICK_TIMER.reset()
    }

    /**
     * On packet.
     *
     * @param event the event
     */
    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet
        if (packet is C08PacketPlayerBlockPlacement) CLICK_TIMER.reset()
    }

    override fun handleEvents(): Boolean {
        return true
    }

    companion object {
        /**
         * The constant CLICK_TIMER.
         */
        val CLICK_TIMER = MSTimer()

        /**
         * The constant BLOCK_BLACKLIST.
         */
        val BLOCK_BLACKLIST = Arrays.asList(
            Blocks.enchanting_table,
            Blocks.chest,
            Blocks.ender_chest,
            Blocks.trapped_chest,
            Blocks.anvil,
            Blocks.sand,
            Blocks.web,
            Blocks.torch,
            Blocks.crafting_table,
            Blocks.furnace,
            Blocks.waterlily,
            Blocks.dispenser,
            Blocks.stone_pressure_plate,
            Blocks.wooden_pressure_plate,
            Blocks.noteblock,
            Blocks.dropper,
            Blocks.tnt,
            Blocks.standing_banner,
            Blocks.wall_banner,
            Blocks.redstone_torch,  // recently added
            Blocks.gravel,
            Blocks.cactus,
            Blocks.bed,
            Blocks.lever,
            Blocks.standing_sign,
            Blocks.wall_sign,
            Blocks.jukebox,
            Blocks.oak_fence,
            Blocks.spruce_fence,
            Blocks.birch_fence,
            Blocks.jungle_fence,
            Blocks.dark_oak_fence,
            Blocks.oak_fence_gate,
            Blocks.spruce_fence_gate,
            Blocks.birch_fence_gate,
            Blocks.jungle_fence_gate,
            Blocks.dark_oak_fence_gate,
            Blocks.nether_brick_fence,  //Blocks.cake,
            Blocks.trapdoor,
            Blocks.melon_block,
            Blocks.brewing_stand,
            Blocks.cauldron,
            Blocks.skull,
            Blocks.hopper,
            Blocks.carpet,
            Blocks.redstone_wire,
            Blocks.light_weighted_pressure_plate,
            Blocks.heavy_weighted_pressure_plate,
            Blocks.daylight_detector
        )

        /**
         * Find item int.
         *
         * @param startSlot the start slot
         * @param endSlot   the end slot
         * @param item      the item
         * @return the int
         */
        fun findItem(startSlot: Int, endSlot: Int, item: Item): Int {
            for (i in startSlot until endSlot) {
                val stack = mc.thePlayer.inventoryContainer.getSlot(i).stack
                if (stack != null && stack.item === item) return i
            }
            return -1
        }

        /**
         * Has space hotbar boolean.
         *
         * @return the boolean
         */
        fun hasSpaceHotbar(): Boolean {
            for (i in 36..44) {
                val itemStack = mc.thePlayer.inventoryContainer.getSlot(i).stack ?: return true
            }
            return false
        }

        /**
         * Find auto block block int.
         *
         * @return the int
         */
        fun findAutoBlockBlock(): Int {
            for (i in 36..44) {
                val itemStack = mc.thePlayer.inventoryContainer.getSlot(i).stack
                if (itemStack != null && itemStack.item is ItemBlock && itemStack.stackSize > 0) {
                    val itemBlock = itemStack.item as ItemBlock
                    val block = itemBlock.getBlock()
                    if (block.isFullCube && !BLOCK_BLACKLIST.contains(block)) return i
                }
            }
            return -1
        }

        /**
         * Swap.
         *
         * @param slot         the slot
         * @param hotBarNumber the hot bar number
         */
        fun swap(slot: Int, hotBarNumber: Int) {
            mc.playerController.windowClick(
                mc.thePlayer.inventoryContainer.windowId,
                slot,
                hotBarNumber,
                2,
                mc.thePlayer
            )
        }
    }
}
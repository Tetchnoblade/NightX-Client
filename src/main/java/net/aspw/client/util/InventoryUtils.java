package net.aspw.client.util;

import net.aspw.client.event.ClickWindowEvent;
import net.aspw.client.event.EventTarget;
import net.aspw.client.event.Listenable;
import net.aspw.client.event.PacketEvent;
import net.aspw.client.util.timer.MSTimer;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;

import java.util.Arrays;
import java.util.List;

/**
 * The type Inventory utils.
 */
public final class InventoryUtils extends MinecraftInstance implements Listenable {

    /**
     * The constant CLICK_TIMER.
     */
    public static final MSTimer CLICK_TIMER = new MSTimer();
    /**
     * The constant BLOCK_BLACKLIST.
     */
    public static final List<Block> BLOCK_BLACKLIST = Arrays.asList(
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
            Blocks.redstone_torch,
            // recently added
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
            Blocks.nether_brick_fence,
            //Blocks.cake,
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
    );

    /**
     * Find item int.
     *
     * @param startSlot the start slot
     * @param endSlot   the end slot
     * @param item      the item
     * @return the int
     */
    public static int findItem(final int startSlot, final int endSlot, final Item item) {
        for (int i = startSlot; i < endSlot; i++) {
            final ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();

            if (stack != null && stack.getItem() == item)
                return i;
        }
        return -1;
    }

    /**
     * Has space hotbar boolean.
     *
     * @return the boolean
     */
    public static boolean hasSpaceHotbar() {
        for (int i = 36; i < 45; i++) {
            final ItemStack itemStack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();

            if (itemStack == null)
                return true;
        }
        return false;
    }

    /**
     * Find auto block block int.
     *
     * @return the int
     */
    public static int findAutoBlockBlock() {
        for (int i = 36; i < 45; i++) {
            final ItemStack itemStack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();

            if (itemStack != null && itemStack.getItem() instanceof ItemBlock && itemStack.stackSize > 0) {
                final ItemBlock itemBlock = (ItemBlock) itemStack.getItem();
                final Block block = itemBlock.getBlock();

                if (block.isFullCube() && !BLOCK_BLACKLIST.contains(block))
                    return i;
            }
        }
        return -1;
    }

    /**
     * On click.
     *
     * @param event the event
     */
    @EventTarget
    public void onClick(final ClickWindowEvent event) {
        CLICK_TIMER.reset();
    }

    /**
     * On packet.
     *
     * @param event the event
     */
    @EventTarget
    public void onPacket(final PacketEvent event) {
        final Packet packet = event.getPacket();

        if (packet instanceof C08PacketPlayerBlockPlacement)
            CLICK_TIMER.reset();
    }

    /**
     * Swap.
     *
     * @param slot         the slot
     * @param hotBarNumber the hot bar number
     */
    public static void swap(int slot, int hotBarNumber) {
        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, hotBarNumber, 2, mc.thePlayer);
    }

    @Override
    public boolean handleEvents() {
        return true;
    }
}

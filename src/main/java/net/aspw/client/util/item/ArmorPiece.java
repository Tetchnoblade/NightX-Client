package net.aspw.client.util.item;

import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

/**
 * The type Armor piece.
 */
public class ArmorPiece {

    private final ItemStack itemStack;
    private final int slot;

    /**
     * Instantiates a new Armor piece.
     *
     * @param itemStack the item stack
     * @param slot      the slot
     */
    public ArmorPiece(ItemStack itemStack, int slot) {
        this.itemStack = itemStack;
        this.slot = slot;
    }

    /**
     * Gets armor type.
     *
     * @return the armor type
     */
    public int getArmorType() {
        return ((ItemArmor) itemStack.getItem()).armorType;
    }

    /**
     * Gets slot.
     *
     * @return the slot
     */
    public int getSlot() {
        return slot;
    }

    /**
     * Gets item stack.
     *
     * @return the item stack
     */
    public ItemStack getItemStack() {
        return itemStack;
    }
}

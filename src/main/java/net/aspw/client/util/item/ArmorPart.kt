package net.aspw.client.util.item

import net.minecraft.item.ItemArmor
import net.minecraft.item.ItemStack

class ArmorPart(val itemStack: ItemStack, val slot: Int) {
    val armorType = (itemStack.item as ItemArmor).armorType
}
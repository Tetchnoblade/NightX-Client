package net.aspw.client.features.api

import net.aspw.client.utils.item.ItemUtils
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.init.Items
import net.minecraft.item.Item
import net.minecraft.item.ItemStack

class StackItems : CreativeTabs("Stack Items") {
    override fun displayAllReleventItems(itemList: MutableList<ItemStack>) {
        itemList.add(ItemUtils.createItem("bow 64 0"))
        itemList.add(ItemUtils.createItem("arrow 64 0"))
        itemList.add(ItemUtils.createItem("iron_sword 64 0"))
        itemList.add(ItemUtils.createItem("wooden_sword 64 0"))
        itemList.add(ItemUtils.createItem("stone_sword 64 0"))
        itemList.add(ItemUtils.createItem("diamond_sword 64 0"))
        itemList.add(ItemUtils.createItem("golden_sword 64 0"))
        itemList.add(ItemUtils.createItem("leather_helmet 64 0"))
        itemList.add(ItemUtils.createItem("leather_chestplate 64 0"))
        itemList.add(ItemUtils.createItem("leather_leggings 64 0"))
        itemList.add(ItemUtils.createItem("leather_boots 64 0"))
        itemList.add(ItemUtils.createItem("chainmail_helmet 64 0"))
        itemList.add(ItemUtils.createItem("chainmail_chestplate 64 0"))
        itemList.add(ItemUtils.createItem("chainmail_leggings 64 0"))
        itemList.add(ItemUtils.createItem("chainmail_boots 64 0"))
        itemList.add(ItemUtils.createItem("iron_helmet 64 0"))
        itemList.add(ItemUtils.createItem("iron_chestplate 64 0"))
        itemList.add(ItemUtils.createItem("iron_leggings 64 0"))
        itemList.add(ItemUtils.createItem("iron_boots 64 0"))
        itemList.add(ItemUtils.createItem("diamond_helmet 64 0"))
        itemList.add(ItemUtils.createItem("diamond_chestplate 64 0"))
        itemList.add(ItemUtils.createItem("diamond_leggings 64 0"))
        itemList.add(ItemUtils.createItem("diamond_boots 64 0"))
        itemList.add(ItemUtils.createItem("golden_helmet 64 0"))
        itemList.add(ItemUtils.createItem("golden_chestplate 64 0"))
        itemList.add(ItemUtils.createItem("golden_leggings 64 0"))
        itemList.add(ItemUtils.createItem("golden_boots 64 0"))
        itemList.add(ItemUtils.createItem("fishing_rod 64 0"))
        itemList.add(ItemUtils.createItem("carrot_on_a_stick 64 0"))
        itemList.add(ItemUtils.createItem("water_bucket 64 0"))
        itemList.add(ItemUtils.createItem("lava_bucket 64 0"))
        itemList.add(ItemUtils.createItem("milk_bucket 64 0"))
        itemList.add(ItemUtils.createItem("snowball 64 0"))
        itemList.add(ItemUtils.createItem("ender_pearl 64 0"))
        itemList.add(ItemUtils.createItem("writable_book 64 0"))
        itemList.add(ItemUtils.createItem("written_book 64 0"))
        itemList.add(ItemUtils.createItem("iron_horse_armor 64 0"))
        itemList.add(ItemUtils.createItem("golden_horse_armor 64 0"))
        itemList.add(ItemUtils.createItem("diamond_horse_armor 64 0"))
        itemList.add(ItemUtils.createItem("clock 64 0"))
        itemList.add(ItemUtils.createItem("shears 64 0"))
        itemList.add(ItemUtils.createItem("saddle 64 0"))
        itemList.add(ItemUtils.createItem("boat 64 0"))
        itemList.add(ItemUtils.createItem("minecart 64 0"))
        itemList.add(ItemUtils.createItem("chest_minecart 64 0"))
        itemList.add(ItemUtils.createItem("furnace_minecart 64 0"))
        itemList.add(ItemUtils.createItem("tnt_minecart 64 0"))
        itemList.add(ItemUtils.createItem("hopper_minecart 64 0"))
        itemList.add(ItemUtils.createItem("cake 64 0"))
        itemList.add(ItemUtils.createItem("mushroom_stew 64 0"))
        itemList.add(ItemUtils.createItem("rabbit_stew 64 0"))
        itemList.add(ItemUtils.createItem("record_13 64 0"))
        itemList.add(ItemUtils.createItem("record_cat 64 0"))
        itemList.add(ItemUtils.createItem("record_blocks 64 0"))
        itemList.add(ItemUtils.createItem("record_chirp 64 0"))
        itemList.add(ItemUtils.createItem("record_far 64 0"))
        itemList.add(ItemUtils.createItem("record_mall 64 0"))
        itemList.add(ItemUtils.createItem("record_mellohi 64 0"))
        itemList.add(ItemUtils.createItem("record_stal 64 0"))
        itemList.add(ItemUtils.createItem("record_strad 64 0"))
        itemList.add(ItemUtils.createItem("record_ward 64 0"))
        itemList.add(ItemUtils.createItem("record_11 64 0"))
        itemList.add(ItemUtils.createItem("record_wait 64 0"))
    }

    override fun getTabIconItem(): Item = ItemStack(Items.golden_sword).item
    override fun getTranslatedTabLabel() = "Stack Items"
}
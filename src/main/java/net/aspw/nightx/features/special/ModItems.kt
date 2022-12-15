package net.aspw.nightx.features.special

import net.aspw.nightx.utils.item.ItemUtils
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.init.Blocks
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import java.util.*

class ModItems : CreativeTabs("Mod Items") {
    override fun displayAllReleventItems(itemList: MutableList<ItemStack>) {
        val lagStringBuilder = StringBuilder()
        for (i in 0..499)
            lagStringBuilder.append("/(!§()%/§)=/(!§()%/§)=/(!§()%/§)=")
        itemList.add(ItemUtils.createItem("barrier 64 0"))
        itemList.add(ItemUtils.createItem("command_block 64 0"))
        itemList.add(ItemUtils.createItem("command_block_minecart 64 0"))
        itemList.add(ItemUtils.createItem("dragon_egg 64 0"))
        itemList.add(ItemUtils.createItem("mob_spawner 64 0"))
        itemList.add(ItemUtils.createItem("farmland 64 0"))
        itemList.add(ItemUtils.createItem("lit_furnace 64 0"))
        itemList.add(ItemUtils.createItem("spawn_egg 64 0"))
        itemList.add(ItemUtils.createItem("spawn_egg 64 64"))
        itemList.add(ItemUtils.createItem("spawn_egg 64 63"))
        itemList.add(ItemUtils.createItem("spawn_egg 64 53"))
        itemList.add(ItemUtils.createItem("name_tag 64 0 {display:{Name: \"$lagStringBuilder\"}}"))
        itemList.add(
            Objects.requireNonNull(ItemUtils.createItem("fireworks 64 0 {HideFlags:63,Fireworks:{Flight:127b,Explosions:[0:{Type:0b,Trail:1b,Colors:[16777215,],Flicker:1b,FadeColors:[0,]}]}}"))
                .setStackDisplayName("§cInfinite §a§lFirework")
        )
    }

    override fun getTabIconItem(): Item = ItemStack(Blocks.command_block).item
    override fun getTranslatedTabLabel() = "Mod Items"
}
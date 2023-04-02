package net.aspw.client.features.module.impl.visual

import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.minecraft.block.Block
import net.minecraft.init.Blocks
import org.lwjgl.input.Keyboard

@ModuleInfo(name = "XRay", category = ModuleCategory.VISUAL, keyBind = Keyboard.KEY_F7)
class XRay : Module() {
    val xrayBlocks = mutableListOf<Block>(
        Blocks.coal_ore,
        Blocks.iron_ore,
        Blocks.gold_ore,
        Blocks.redstone_ore,
        Blocks.lapis_ore,
        Blocks.diamond_ore,
        Blocks.emerald_ore,
        Blocks.quartz_ore,
        Blocks.clay,
        Blocks.glowstone,
        Blocks.crafting_table,
        Blocks.torch,
        Blocks.ladder,
        Blocks.tnt,
        Blocks.coal_block,
        Blocks.iron_block,
        Blocks.gold_block,
        Blocks.diamond_block,
        Blocks.emerald_block,
        Blocks.redstone_block,
        Blocks.lapis_block,
        Blocks.fire,
        Blocks.mossy_cobblestone,
        Blocks.mob_spawner,
        Blocks.end_portal_frame,
        Blocks.enchanting_table,
        Blocks.bookshelf,
        Blocks.command_block,
        Blocks.lava,
        Blocks.flowing_lava,
        Blocks.water,
        Blocks.flowing_water,
        Blocks.furnace,
        Blocks.lit_furnace
    )

    override fun onToggle(state: Boolean) {
        mc.renderGlobal.loadRenderers()
    }
}

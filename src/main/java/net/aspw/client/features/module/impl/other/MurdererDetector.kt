package net.aspw.client.features.module.impl.other

import net.aspw.client.Launch
import net.aspw.client.event.EventState
import net.aspw.client.event.EventTarget
import net.aspw.client.event.MotionEvent
import net.aspw.client.event.WorldEvent
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.features.module.impl.visual.Interface
import net.aspw.client.value.BoolValue
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.Item

@ModuleInfo(name = "MurdererDetector", spacedName = "Murderer Detector", category = ModuleCategory.OTHER)
class MurdererDetector : Module() {
    private val chatValue = BoolValue("Chat", true)

    companion object {
        var murderers = mutableListOf<EntityLivingBase>()

        @JvmStatic
        fun isMurderer(entity: EntityLivingBase): Boolean {
            if (entity !is EntityPlayer) return false
            if (entity in murderers) return true
            return false
        }
    }

    // Murderer Items Updated 2024 04/21
    private val murdererItems = mutableListOf(
        267,  // Items.iron_sword
        130,  // Blocks.ender_chest
        272,  // Items.stone_sword
        256,  // Items.iron_shovel
        280,  // Items.stick
        271,  // Items.wooden_axe
        268,  // Items.wooden_sword
        32,   // Blocks.deadbush
        273,  // Items.stone_shovel
        369,  // Items.blaze_rod
        277,  // Items.diamond_shovel
        406,  // Items.quartz
        400,  // Items.pumpkin_pie
        285,  // Items.golden_pickaxe
        260,  // Items.apple
        421,  // Items.name_tag
        19,   // Blocks.sponge
        398,  // Items.carrot_on_a_stick
        352,  // Items.bone
        391,  // Items.carrot
        396,  // Items.golden_carrot
        357,  // Items.cookie
        279,  // Items.diamond_axe
        175,  // Blocks.double_plant
        409,  // Items.prismarine_shard
        364,  // Items.cooked_beef
        405,  // Blocks.nether_brick
        366,  // Items.cooked_chicken
        2258, // Items.record_blocks
        294,  // Items.golden_hoe
        283,  // Items.golden_sword
        276,  // Items.diamond_sword
        293,  // Items.diamond_hoe
        359,  // Items.shears
        349,  // Items.fish
        351,  // Items.dye
        333,  // Items.boat
        382,  // Items.speckled_melon
        340,  // Items.book
        6     // Blocks.sapling
    )

    override fun onDisable() {
        murderers.clear()
    }

    @EventTarget
    fun onWorld(event: WorldEvent) {
        murderers.clear()
    }

    @EventTarget
    fun onMotion(event: MotionEvent) {
        if (event.eventState == EventState.PRE) {
            for (player in mc.theWorld.playerEntities) {
                if (player.heldItem != null && (player.heldItem.displayName.contains(
                        "Knife",
                        ignoreCase = true
                    ) || murdererItems.contains(Item.getIdFromItem(player.heldItem.item)))
                    && player !in murderers
                ) {
                    if (Launch.moduleManager.getModule(Interface::class.java)?.flagSoundValue!!.get()) {
                        Launch.tipSoundManager.popSound.asyncPlay(Launch.moduleManager.popSoundPower)
                    }
                    if (chatValue.get())
                        chat("§e" + player.name + "§r is Murderer!")
                    murderers.add(player)
                }
            }
        }
    }
}
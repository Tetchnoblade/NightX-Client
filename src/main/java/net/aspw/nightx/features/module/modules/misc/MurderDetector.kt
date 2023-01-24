package net.aspw.nightx.features.module.modules.misc

import net.aspw.nightx.NightX
import net.aspw.nightx.event.EventTarget
import net.aspw.nightx.event.UpdateEvent
import net.aspw.nightx.event.WorldEvent
import net.aspw.nightx.features.module.Module
import net.aspw.nightx.features.module.ModuleCategory
import net.aspw.nightx.features.module.ModuleInfo
import net.aspw.nightx.features.module.modules.client.Hud
import net.aspw.nightx.utils.ClientUtils
import net.aspw.nightx.visual.hud.element.elements.Notification
import net.minecraft.client.Minecraft
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.item.Item
import net.minecraft.item.ItemBlock

@ModuleInfo(name = "MurderDetector", spacedName = "Murder Detector", category = ModuleCategory.MISC)
class MurderDetector : Module() {
    override fun onDisable() {
        detectedPlayers.clear()
    }

    companion object {
        var mc = Minecraft.getMinecraft()
        private var mode = true
        var detectedPlayers = ArrayList<EntityPlayer>()
        var itemIds = intArrayOf(288, 396, 412, 398, 75, 50)
        var itemTypes = arrayOf(
            Items.fishing_rod,
            Items.diamond_hoe,
            Items.golden_hoe,
            Items.iron_hoe,
            Items.stone_hoe,
            Items.wooden_hoe,
            Items.stone_sword,
            Items.diamond_sword,
            Items.golden_sword,
            ItemBlock.getItemFromBlock(Blocks.sponge),
            Items.iron_sword,
            Items.wooden_sword,
            Items.diamond_axe,
            Items.golden_axe,
            Items.iron_axe,
            Items.stone_axe,
            Items.diamond_pickaxe,
            Items.wooden_axe,
            Items.golden_pickaxe,
            Items.iron_pickaxe,
            Items.stone_pickaxe,
            Items.wooden_pickaxe,
            Items.stone_shovel,
            Items.diamond_shovel,
            Items.golden_shovel,
            Items.iron_shovel,
            Items.wooden_shovel
        )

        @EventTarget
        fun onUpdate(ignored: UpdateEvent?) {
            for (entity in mc.theWorld.loadedEntityList) {
                if (entity is EntityPlayer) {
                    val player = entity
                    if (entity === mc.thePlayer) continue
                    if (detectedPlayers.contains(player)) continue
                    if (player.inventory.getCurrentItem() != null) {
                        if (isWeapon(player.inventory.getCurrentItem().item)) {
                            ClientUtils.displayChatMessage(NightX.CLIENT_CHAT + "§a" + player.name + " is Killer!")
                            NightX.hud.addNotification(Notification(player.name + " is Killer!"))
                            if (NightX.moduleManager.getModule(Hud::class.java)?.flagSoundValue!!.get()) {
                                NightX.tipSoundManager.popSound.asyncPlay(90f)
                            }
                            detectedPlayers.add(player)
                        }
                    }
                }
            }
        }

        @EventTarget
        fun onWorldChange(ignored: WorldEvent?) {
            detectedPlayers.clear()
        }

        fun isWeapon(item: Item): Boolean {
            if (mode) {
                return item === Items.bow
            }
            for (id in itemIds) {
                val itemId = Item.getItemById(id)
                if (item === itemId) {
                    return true
                }
            }
            for (id in itemTypes) {
                if (item === id) {
                    return true
                }
            }
            return false
        }
    }
}
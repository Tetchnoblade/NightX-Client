package net.aspw.client.features.module.impl.other

import net.aspw.client.Client
import net.aspw.client.event.*
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.features.module.impl.visual.Interface
import net.aspw.client.value.BoolValue
import net.aspw.client.visual.font.semi.Fonts
import net.aspw.client.visual.hud.element.elements.Notification
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import java.awt.Color

@ModuleInfo(name = "MurderDetector", spacedName = "Murder Detector", description = "", category = ModuleCategory.OTHER)
class MurderDetector : Module() {
    private val showText = BoolValue("ShowText", true)
    private val chatValue = BoolValue("ChatNotify", true)
    private val notifyValue = BoolValue("Notification", true)

    private var murder1: EntityPlayer? = null
    private var murder2: EntityPlayer? = null

    override fun onDisable() {
        murder1 = null
        murder2 = null
    }

    @EventTarget
    fun onWorld(event: WorldEvent) {
        murder1 = null
        murder2 = null
    }

    @EventTarget
    fun onRender2D(event: Render2DEvent) {
        val sc = ScaledResolution(mc)
        if (showText.get()) {
            Fonts.minecraftFont.drawString(
                if (murder1 != null) "Murder1: §c" + murder1?.name else "Murder1: §cNone",
                sc.scaledWidth / 2F - Fonts.minecraftFont.getStringWidth(if (murder1 != null) "Murder1: §c" + murder1?.name else "Murder1: §cNone") / 2F,
                66.5F,
                Color(255, 255, 255).rgb,
                true
            )
            Fonts.minecraftFont.drawString(
                if (murder2 != null) "Murder2: §c" + murder2?.name else "Murder2: §cNone",
                sc.scaledWidth / 2F - Fonts.minecraftFont.getStringWidth(if (murder2 != null) "Murder2: §c" + murder2?.name else "Murder2: §cNone") / 2F,
                77.5F,
                Color(255, 255, 255).rgb,
                true
            )
        }
    }

    @EventTarget
    fun onMotion(event: MotionEvent) {
        if (event.eventState == EventState.PRE) {
            for (player in mc.theWorld.playerEntities) {
                if (mc.thePlayer.ticksExisted % 2 == 0) return
                if (player.heldItem != null && (player.heldItem.item == Items.iron_sword || player.heldItem.item == Items.stone_sword || player.heldItem.item == Items.iron_shovel || player.heldItem.item == Items.stick || player.heldItem.item == Items.wooden_axe || player.heldItem.item == Items.wooden_sword || player.heldItem.item == Items.stone_shovel || player.heldItem.item == Items.blaze_rod || player.heldItem.item == Items.diamond_shovel || player.heldItem.item == Items.feather || player.heldItem.item == Items.pumpkin_pie || player.heldItem.item == Items.golden_pickaxe || player.heldItem.item == Items.carrot_on_a_stick || player.heldItem.item == Items.cookie || player.heldItem.item == Items.diamond_axe || player.heldItem.item == Items.golden_sword || player.heldItem.item == Items.diamond_sword || player.heldItem.item == Items.diamond_hoe || player.heldItem.item == Items.shears || player.heldItem.item == Blocks.redstone_torch || player.heldItem.item == Blocks.deadbush || player.heldItem.item == Items.name_tag || player.heldItem.item == Blocks.sponge || player.heldItem.item == Items.boat || player.heldItem.item == Blocks.dragon_egg || player.heldItem.item == Items.prismarine_shard || player.heldItem.item == Items.fish || player.heldItem.item == Blocks.double_plant || player.heldItem.item == Blocks.nether_brick || player.heldItem.item == Items.cooked_beef || player.heldItem.item == Items.speckled_melon || player.heldItem.item == Items.dye || player.heldItem.item == Items.golden_axe || player.heldItem.item == Items.book || player.heldItem.item == Items.quartz || player.heldItem.item == Items.carrot || player.heldItem.item == Items.golden_carrot || player.heldItem.item == Items.apple || player.heldItem.item == Items.record_blocks)
                ) {
                    if (this.murder1 == null) {
                        if (Client.moduleManager.getModule(Interface::class.java)?.flagSoundValue!!.get()) {
                            Client.tipSoundManager.popSound.asyncPlay(Client.moduleManager.popSoundPower)
                        }
                        if (chatValue.get())
                            chat(player.name + "§r is Murder!")
                        if (notifyValue.get())
                            Client.hud.addNotification(
                                Notification(
                                    player.name + " is Murder!",
                                    Notification.Type.INFO,
                                    6000L
                                )
                            )
                        murder1 = player
                        return
                    }
                    if (this.murder2 == null && player != this.murder1) {
                        if (Client.moduleManager.getModule(Interface::class.java)?.flagSoundValue!!.get()) {
                            Client.tipSoundManager.popSound.asyncPlay(Client.moduleManager.popSoundPower)
                        }
                        if (chatValue.get())
                            chat(player.name + "§r is Murder!")
                        if (notifyValue.get())
                            Client.hud.addNotification(
                                Notification(
                                    player.name + " is Murder!",
                                    Notification.Type.INFO,
                                    6000L
                                )
                            )
                        this.murder2 = player
                    }
                }
            }
        }
    }
}
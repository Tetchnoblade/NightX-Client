package net.aspw.client.features.module.impl.combat

import net.aspw.client.Client
import net.aspw.client.event.EventTarget
import net.aspw.client.event.UpdateEvent
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.value.BoolValue
import net.minecraft.init.Items
import net.minecraft.network.play.client.C07PacketPlayerDigging
import net.minecraft.network.play.client.C07PacketPlayerDigging.Action
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing

@ModuleInfo(name = "AutoProjectile", spacedName = "Auto Projectile", category = ModuleCategory.COMBAT)
class AutoProjectile : Module() {

    private val waitForBowAimbot = BoolValue("WaitForBowAim", true)

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        val bowAimbot = Client.moduleManager[BowAim::class.java] as BowAim

        if (mc.thePlayer.isUsingItem && mc.thePlayer.heldItem?.item == Items.bow &&
            mc.thePlayer.itemInUseDuration > 20 && (!waitForBowAimbot.get() || !bowAimbot.state || bowAimbot.hasTarget())
        ) {
            mc.thePlayer.stopUsingItem()
            mc.netHandler.addToSendQueue(
                C07PacketPlayerDigging(
                    Action.RELEASE_USE_ITEM,
                    BlockPos.ORIGIN,
                    EnumFacing.DOWN
                )
            )
        }
    }
}

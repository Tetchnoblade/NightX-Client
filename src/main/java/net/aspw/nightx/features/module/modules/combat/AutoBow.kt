package net.aspw.nightx.features.module.modules.combat

import net.aspw.nightx.NightX
import net.aspw.nightx.event.EventTarget
import net.aspw.nightx.event.UpdateEvent
import net.aspw.nightx.features.module.Module
import net.aspw.nightx.features.module.ModuleCategory
import net.aspw.nightx.features.module.ModuleInfo
import net.aspw.nightx.value.BoolValue
import net.minecraft.init.Items
import net.minecraft.network.play.client.C07PacketPlayerDigging
import net.minecraft.network.play.client.C07PacketPlayerDigging.Action
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing

@ModuleInfo(name = "AutoBow", spacedName = "Auto Bow", category = ModuleCategory.COMBAT)
class AutoBow : Module() {

    private val waitForBowAimbot = BoolValue("WaitForBowAim", true)

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        val bowAimbot = NightX.moduleManager[BowAim::class.java] as BowAim

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

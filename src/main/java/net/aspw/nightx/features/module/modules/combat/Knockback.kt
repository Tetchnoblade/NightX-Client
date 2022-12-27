package net.aspw.nightx.features.module.modules.combat

import net.aspw.nightx.event.AttackEvent
import net.aspw.nightx.event.EventTarget
import net.aspw.nightx.features.module.Module
import net.aspw.nightx.features.module.ModuleCategory
import net.aspw.nightx.features.module.ModuleInfo
import net.minecraft.network.play.client.C0BPacketEntityAction

@ModuleInfo(
    name = "Knockback", spacedName = "Knock back",
    category = ModuleCategory.COMBAT
)
class Knockback : Module() {
    @EventTarget
    fun onAttack(event: AttackEvent) {
        if (mc.thePlayer.isSprinting)
            mc.thePlayer.isSprinting = false
        mc.netHandler.addToSendQueue(C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING))
        mc.thePlayer.serverSprintState = true
    }
}
package net.aspw.client.features.module.impl.combat

import net.aspw.client.event.AttackEvent
import net.aspw.client.event.EventTarget
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.minecraft.network.play.client.C0BPacketEntityAction

@ModuleInfo(
    name = "Knockback",
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
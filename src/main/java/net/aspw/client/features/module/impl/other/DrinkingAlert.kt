package net.aspw.client.features.module.impl.other

import net.aspw.client.event.EventState
import net.aspw.client.event.EventTarget
import net.aspw.client.event.MotionEvent
import net.aspw.client.event.WorldEvent
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.utils.timer.MSTimer
import net.minecraft.entity.EntityLivingBase
import net.minecraft.item.ItemPotion

@ModuleInfo(name = "DrinkingAlert", spacedName = "Drinking Alert", category = ModuleCategory.OTHER)
class DrinkingAlert : Module() {
    private val alertTimer = MSTimer()
    private val drinkers = arrayListOf<EntityLivingBase>()

    override fun onDisable() {
        clearDrag()
    }

    @EventTarget
    fun onWorld(event: WorldEvent) {
        clearDrag()
    }

    @EventTarget
    fun onMotion(event: MotionEvent) {
        if (event.eventState == EventState.PRE) {
            for (player in mc.theWorld.playerEntities) {
                if (player !in drinkers && player != mc.thePlayer && player.isUsingItem && player.heldItem != null && player.heldItem.item is ItemPotion) {
                    chat("§e" + player.name + "§r is drinking!")
                    drinkers.add(player)
                    alertTimer.reset()
                }
            }
            if (alertTimer.hasTimePassed(3000L) && drinkers.isNotEmpty()) {
                clearDrag()
            }
        }
    }

    private fun clearDrag() {
        drinkers.clear()
        alertTimer.reset()
    }
}
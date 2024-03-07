package net.aspw.client.features.module.impl.combat

import net.aspw.client.event.EventTarget
import net.aspw.client.event.UpdateEvent
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.utils.EntityUtils
import net.aspw.client.utils.RotationUtils
import net.aspw.client.value.BoolValue
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.item.ItemBow

@ModuleInfo(name = "BowAura", spacedName = "Bow Aura", category = ModuleCategory.COMBAT)
class BowAura : Module() {

    private val throughWallsValue = BoolValue("ThroughWalls", false)

    private var target: Entity? = null

    override fun onDisable() {
        target = null
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        target = null

        if (mc.thePlayer.itemInUse?.item is ItemBow) {
            val entity = getTarget(throughWallsValue.get()) ?: return

            target = entity
            RotationUtils.faceBow(target!!, true, false, 5f)
        }
    }

    private fun getTarget(throughWalls: Boolean): Entity? {
        val targets = mc.theWorld.loadedEntityList.filter {
            it is EntityLivingBase && EntityUtils.isSelected(it, true) &&
                    (throughWalls || mc.thePlayer.canEntityBeSeen(it))
        }

        return targets.minByOrNull { mc.thePlayer.getDistanceToEntity(it) }
    }

    fun hasTarget() = target != null && mc.thePlayer.canEntityBeSeen(target)
}
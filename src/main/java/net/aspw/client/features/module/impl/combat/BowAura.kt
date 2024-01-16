package net.aspw.client.features.module.impl.combat

import net.aspw.client.event.EventTarget
import net.aspw.client.event.JumpEvent
import net.aspw.client.event.StrafeEvent
import net.aspw.client.event.UpdateEvent
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.util.EntityUtils
import net.aspw.client.util.RotationUtils
import net.aspw.client.value.BoolValue
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.item.ItemBow

@ModuleInfo(name = "BowAura", spacedName = "Bow Aura", description = "", category = ModuleCategory.COMBAT)
class BowAura : Module() {

    private val silentValue = BoolValue("Silent", true)
    private val movementFix = BoolValue("MovementFix", false) { silentValue.get() }
    private val throughWallsValue = BoolValue("ThroughWalls", false)

    private var target: Entity? = null

    override fun onDisable() {
        target = null
    }

    @EventTarget
    fun onJump(event: JumpEvent) {
        if (silentValue.get() && movementFix.get() && target != null)
            event.yaw = RotationUtils.serverRotation?.yaw!!
    }

    @EventTarget
    fun onStrafe(event: StrafeEvent) {
        if (event.isCancelled || !movementFix.get() || !silentValue.get() || target == null) return
        val (yaw) = RotationUtils.targetRotation ?: return
        event.yaw = yaw
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        target = null

        if (mc.thePlayer.itemInUse?.item is ItemBow) {
            val entity = getTarget(throughWallsValue.get()) ?: return

            target = entity
            RotationUtils.faceBow(target!!, silentValue.get(), false, 5f)
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
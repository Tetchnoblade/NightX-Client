package net.aspw.client.features.module.impl.visual

import net.aspw.client.event.EventTarget
import net.aspw.client.event.MotionEvent
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.util.EntityUtils
import net.aspw.client.util.extensions.getDistanceToEntityBox
import net.aspw.client.value.BoolValue
import net.minecraft.entity.Entity
import net.minecraft.entity.item.*

@ModuleInfo(
    name = "OptiFine+",
    description = "",
    category = ModuleCategory.VISUAL
)
class OptiFinePlus : Module() {

    @JvmField
    var entityOptimization = BoolValue("Entity-Optimization", true)

    @JvmField
    var noHitDelay = BoolValue("NoHitDelay", true)

    @JvmField
    var mouseDelayFix = BoolValue("MouseDelayFix", true)

    @EventTarget
    fun onMotion(event: MotionEvent?) {
        for (en in mc.theWorld.loadedEntityList) {
            val entity = en!!
            if (shouldStopRender(entity))
                entity.renderDistanceWeight = 0.0
            else entity.renderDistanceWeight = 1.0
        }
    }

    override fun onDisable() {
        for (en in mc.theWorld.loadedEntityList) {
            val entity = en!!
            if (entity != mc.thePlayer!! && entity.renderDistanceWeight <= 0.0)
                entity.renderDistanceWeight = 1.0
        }
    }

    fun shouldStopRender(entity: Entity): Boolean {
        return entityOptimization.get() && (EntityUtils.isMob(entity) || EntityUtils.isAnimal(entity) || entity is EntityBoat || entity is EntityMinecart || entity is EntityItemFrame || entity is EntityTNTPrimed || entity is EntityArmorStand) && entity != mc.thePlayer!! && mc.thePlayer!!.getDistanceToEntityBox(
            entity
        ).toFloat() > 40
    }

    init {
        state = true
    }
}
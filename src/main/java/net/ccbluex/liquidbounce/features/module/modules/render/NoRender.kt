package net.ccbluex.liquidbounce.features.module.modules.render

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.MotionEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.EntityUtils
import net.ccbluex.liquidbounce.utils.extensions.getDistanceToEntityBox
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.minecraft.entity.Entity
import net.minecraft.entity.item.EntityArmorStand
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.player.EntityPlayer

@ModuleInfo(
    name = "NoRender",
    spacedName = "No Render",
    category = ModuleCategory.RENDER,
    array = false
)
class NoRender : Module() {

    val allValue = BoolValue("All", false)
    val nameTagsValue = BoolValue("NameTags", false)
    private val itemsValue = BoolValue("Items", true, { !allValue.get() })
    private val playersValue = BoolValue("Players", false, { !allValue.get() })
    private val mobsValue = BoolValue("Mobs", false, { !allValue.get() })
    private val animalsValue = BoolValue("Animals", false, { !allValue.get() })
    val armorStandValue = BoolValue("ArmorStand", true, { !allValue.get() })
    private val autoResetValue = BoolValue("AutoReset", true)
    private val maxRenderRange = FloatValue("MaxRenderRange", 25F, 0F, 100F, "m")

    @EventTarget
    fun onMotion(event: MotionEvent) {
        for (en in mc.theWorld.loadedEntityList) {
            val entity = en!!
            if (shouldStopRender(entity))
                entity.renderDistanceWeight = 0.0
            else if (autoResetValue.get())
                entity.renderDistanceWeight = 1.0
        }
    }

    fun shouldStopRender(entity: Entity): Boolean {
        return (allValue.get()
                || (itemsValue.get() && entity is EntityItem)
                || (playersValue.get() && entity is EntityPlayer)
                || (mobsValue.get() && EntityUtils.isMob(entity))
                || (animalsValue.get() && EntityUtils.isAnimal(entity))
                || (armorStandValue.get() && entity is EntityArmorStand))
                && entity != mc.thePlayer!!
                && (mc.thePlayer!!.getDistanceToEntityBox(entity).toFloat() > maxRenderRange.get())
    }

    override fun onDisable() {
        for (en in mc.theWorld.loadedEntityList) {
            val entity = en!!
            if (entity != mc.thePlayer!! && entity.renderDistanceWeight <= 0.0)
                entity.renderDistanceWeight = 1.0
        }
    }

}
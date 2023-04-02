package net.aspw.client.features.module.impl.other

import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.value.BoolValue
import net.aspw.client.value.IntegerValue
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemArmor

@ModuleInfo(name = "Teams", category = ModuleCategory.OTHER)
class Teams : Module() {

    private val scoreboardValue = BoolValue("ScoreboardTeam", true)
    private val colorValue = BoolValue("Color", false)
    private val gommeSWValue = BoolValue("GommeSW", false)
    private val armorColorValue = BoolValue("ArmorColor", false)
    private val armorIndexValue = IntegerValue("ArmorIndex", 3, 0, 3, { armorColorValue.get() })

    /**
     * Check if [entity] is in your own team using scoreboard, name color or team prefix
     */
    fun isInYourTeam(entity: EntityLivingBase): Boolean {
        mc.thePlayer ?: return false

        if (scoreboardValue.get() && mc.thePlayer.team != null && entity.team != null &&
            mc.thePlayer.team.isSameTeam(entity.team)
        )
            return true

        if (armorColorValue.get()) {
            val entityPlayer = entity as EntityPlayer
            if (mc.thePlayer.inventory.armorInventory[armorIndexValue.get()] != null && entityPlayer.inventory.armorInventory[armorIndexValue.get()] != null) {
                val myHead = mc.thePlayer.inventory.armorInventory[armorIndexValue.get()]
                val myItemArmor = myHead!!.item!! as ItemArmor


                val entityHead = entityPlayer.inventory.armorInventory[armorIndexValue.get()]
                var entityItemArmor = myHead.item!! as ItemArmor

                if (myItemArmor.getColor(myHead) == entityItemArmor.getColor(entityHead!!)) {
                    return true
                }
            }
        }

        if (gommeSWValue.get() && mc.thePlayer.displayName != null && entity.displayName != null) {
            val targetName = entity.displayName.formattedText.replace("§r", "")
            val clientName = mc.thePlayer.displayName.formattedText.replace("§r", "")
            if (targetName.startsWith("T") && clientName.startsWith("T"))
                if (targetName[1].isDigit() && clientName[1].isDigit())
                    return targetName[1] == clientName[1]
        }

        if (colorValue.get() && mc.thePlayer.displayName != null && entity.displayName != null) {
            val targetName = entity.displayName.formattedText.replace("§r", "")
            val clientName = mc.thePlayer.displayName.formattedText.replace("§r", "")
            return targetName.startsWith("§${clientName[1]}")
        }

        return false
    }

}

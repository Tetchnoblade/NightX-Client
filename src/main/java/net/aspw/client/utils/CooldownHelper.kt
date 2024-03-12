package net.aspw.client.utils

import net.minecraft.item.Item
import net.minecraft.item.ItemAxe
import net.minecraft.item.ItemHoe
import net.minecraft.item.ItemPickaxe
import net.minecraft.item.ItemSpade
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemSword
import net.minecraft.potion.Potion
import net.minecraft.util.MathHelper
import kotlin.math.min

object CooldownHelper {

    private var lastAttackedTicks = 0

    private var genericAttackSpeed = 0.0

    fun updateGenericAttackSpeed(itemStack: ItemStack?) {
        genericAttackSpeed = when (itemStack?.item) {
            is ItemSword -> 1.6
            is ItemAxe -> {
                val axe = itemStack.item as ItemAxe
                when (axe.toolMaterial) {
                    Item.ToolMaterial.IRON -> 0.9
                    Item.ToolMaterial.WOOD, Item.ToolMaterial.STONE -> 0.8
                    else -> 1.0
                }
            }

            is ItemPickaxe -> 1.2
            is ItemSpade -> 1.0
            is ItemHoe -> {
                val hoe = itemStack.item as ItemHoe
                when (hoe.materialName) {
                    "STONE" -> 2.0
                    "IRON" -> 3.0
                    "DIAMOND" -> 4.0
                    else -> 1.0
                }
            }

            else -> 4.0
        }

        if (MinecraftInstance.mc.thePlayer.isPotionActive(Potion.digSlowdown)) {
            genericAttackSpeed *= 1.0 - min(
                1.0,
                0.1 * (MinecraftInstance.mc.thePlayer.getActivePotionEffect(Potion.digSlowdown).amplifier + 1)
            )
        }

        if (MinecraftInstance.mc.thePlayer.isPotionActive(Potion.digSpeed)) {
            genericAttackSpeed *= 1.0 + (0.1 * (MinecraftInstance.mc.thePlayer.getActivePotionEffect(Potion.digSpeed).amplifier + 1))
        }
    }

    private fun getAttackCooldownProgressPerTick() = 1.0 / genericAttackSpeed * 20.0

    fun getAttackCooldownProgress() = MathHelper.clamp_double(
        (lastAttackedTicks + MinecraftInstance.mc.timer.renderPartialTicks) / getAttackCooldownProgressPerTick(),
        0.0,
        1.0
    )

    fun resetLastAttackedTicks() {
        lastAttackedTicks = 0
    }

    fun incrementLastAttackedTicks() {
        lastAttackedTicks++
    }
}
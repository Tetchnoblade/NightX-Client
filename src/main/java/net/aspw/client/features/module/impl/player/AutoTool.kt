package net.aspw.client.features.module.impl.player

import net.aspw.client.event.AttackEvent
import net.aspw.client.event.EventTarget
import net.aspw.client.event.PacketEvent
import net.aspw.client.event.Render2DEvent
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.utils.item.ItemUtils
import net.minecraft.enchantment.Enchantment
import net.minecraft.item.ItemSword
import net.minecraft.network.play.client.C02PacketUseEntity
import net.minecraft.util.MovingObjectPosition

@ModuleInfo(name = "AutoTool", spacedName = "Auto Tool", category = ModuleCategory.PLAYER)
class AutoTool : Module() {

    private var attackEnemy = false
    var isBreaking = false
    var lastSlot = 0

    override fun onDisable() {
        attackEnemy = false
        isBreaking = false
        lastSlot = 0
    }

    @EventTarget
    fun onRender2D(event: Render2DEvent) {
        if (!mc.thePlayer.isUsingItem && mc.gameSettings.keyBindAttack.isKeyDown && mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            var bestSpeed = 1F
            var bestSlot = -1

            val block = mc.theWorld.getBlockState(mc.objectMouseOver.blockPos).block

            for (i in 0..8) {
                val item = mc.thePlayer.inventory.getStackInSlot(i) ?: continue
                val speed = item.getStrVsBlock(block)

                if (speed > bestSpeed) {
                    bestSpeed = speed
                    bestSlot = i
                }
            }

            if (bestSlot != -1) {
                if (mc.thePlayer.inventory.currentItem != bestSlot && !isBreaking)
                    lastSlot = mc.thePlayer.inventory.currentItem
                isBreaking = true
                mc.thePlayer.inventory.currentItem = bestSlot
            }
        } else if (isBreaking) {
            mc.thePlayer.inventory.currentItem = lastSlot
            isBreaking = false
        }
    }

    @EventTarget
    fun onAttack(event: AttackEvent) {
        attackEnemy = true
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        if (event.packet is C02PacketUseEntity && event.packet.action == C02PacketUseEntity.Action.ATTACK
            && attackEnemy
        ) {
            attackEnemy = false

            val (slot, _) = (0..8)
                .map { Pair(it, mc.thePlayer.inventory.getStackInSlot(it)) }
                .filter { it.second != null && (it.second.item is ItemSword) }
                .maxByOrNull {
                    (it.second.attributeModifiers["generic.attackDamage"].first()?.amount
                        ?: 0.0) + 1.25 * ItemUtils.getEnchantment(it.second, Enchantment.sharpness)
                } ?: return

            if (slot == mc.thePlayer.inventory.currentItem)
                return

            mc.thePlayer.inventory.currentItem = slot
            mc.playerController.updateController()

            mc.netHandler.addToSendQueue(event.packet)
            event.cancelEvent()
        }
    }
}
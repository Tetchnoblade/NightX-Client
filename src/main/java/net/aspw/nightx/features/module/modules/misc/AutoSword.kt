package net.aspw.nightx.features.module.modules.misc

import net.aspw.nightx.event.AttackEvent
import net.aspw.nightx.event.EventTarget
import net.aspw.nightx.event.PacketEvent
import net.aspw.nightx.event.UpdateEvent
import net.aspw.nightx.features.module.Module
import net.aspw.nightx.features.module.ModuleCategory
import net.aspw.nightx.features.module.ModuleInfo
import net.aspw.nightx.utils.item.ItemUtils
import net.aspw.nightx.value.BoolValue
import net.aspw.nightx.value.IntegerValue
import net.minecraft.enchantment.Enchantment
import net.minecraft.item.ItemSword
import net.minecraft.network.play.client.C02PacketUseEntity
import net.minecraft.network.play.client.C09PacketHeldItemChange

@ModuleInfo(name = "AutoSword", spacedName = "Auto Sword", category = ModuleCategory.MISC)
class AutoSword : Module() {

    private val silentValue = BoolValue("SpoofItem", false)
    private val ticksValue = IntegerValue("SpoofTicks", 20, 1, 20)
    private var attackEnemy = false

    private var spoofedSlot = 0

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

            // Find best sword in hotbar
            val (slot, _) = (0..8)
                .map { Pair(it, mc.thePlayer.inventory.getStackInSlot(it)) }
                .filter { it.second != null && (it.second.item is ItemSword) }
                .maxByOrNull {
                    (it.second.attributeModifiers["generic.attackDamage"].first()?.amount
                        ?: 0.0) + 1.25 * ItemUtils.getEnchantment(it.second, Enchantment.sharpness)
                } ?: return

            if (slot == mc.thePlayer.inventory.currentItem) // If in hand no need to swap
                return

            // Switch to best sword
            if (silentValue.get()) {
                mc.netHandler.addToSendQueue(C09PacketHeldItemChange(slot))
                spoofedSlot = ticksValue.get()
            } else {
                mc.thePlayer.inventory.currentItem = slot
                mc.playerController.updateController()
            }

            // Resend attack packet
            mc.netHandler.addToSendQueue(event.packet)
            event.cancelEvent()
        }
    }

    @EventTarget
    fun onUpdate(update: UpdateEvent) {
        // Switch back to old item after some time
        if (spoofedSlot > 0) {
            if (spoofedSlot == 1)
                mc.netHandler.addToSendQueue(C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem))
            spoofedSlot--
        }
    }
}
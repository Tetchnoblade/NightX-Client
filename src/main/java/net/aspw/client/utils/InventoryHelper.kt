package net.aspw.client.utils

import net.aspw.client.event.ClickWindowEvent
import net.aspw.client.event.EventTarget
import net.aspw.client.event.Listenable
import net.aspw.client.event.PacketEvent
import net.aspw.client.utils.timer.MSTimer
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemPotion
import net.minecraft.item.ItemStack
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement
import net.minecraft.network.play.client.C0DPacketCloseWindow
import net.minecraft.network.play.client.C16PacketClientStatus
import net.minecraft.potion.Potion

object InventoryHelper : MinecraftInstance(), Listenable {
    private val CLICK_TIMER = MSTimer()

    fun isBlockListBlock(itemBlock: ItemBlock): Boolean {
        val block = itemBlock.getBlock()
        return InventoryUtils.BLOCK_BLACKLIST.contains(block) || !block.isFullCube
    }

    @EventTarget
    fun onClickWindow(event: ClickWindowEvent) {
        CLICK_TIMER.reset()
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet

        if (packet is C08PacketPlayerBlockPlacement) {
            CLICK_TIMER.reset()
        }
    }

    fun openPacket() {
        mc.netHandler.addToSendQueue(C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT))
    }

    fun closePacket() {
        mc.netHandler.addToSendQueue(C0DPacketCloseWindow())
    }

    private fun isPositivePotionEffect(id: Int): Boolean {
        if (id == Potion.regeneration.id || id == Potion.moveSpeed.id ||
            id == Potion.heal.id || id == Potion.nightVision.id ||
            id == Potion.jump.id || id == Potion.invisibility.id ||
            id == Potion.resistance.id || id == Potion.waterBreathing.id ||
            id == Potion.absorption.id || id == Potion.digSpeed.id ||
            id == Potion.damageBoost.id || id == Potion.healthBoost.id ||
            id == Potion.fireResistance.id
        ) {
            return true
        }
        return false
    }

    fun isPositivePotion(item: ItemPotion, stack: ItemStack): Boolean {
        item.getEffects(stack).forEach {
            if (isPositivePotionEffect(it.potionID)) {
                return true
            }
        }

        return false
    }

    override fun handleEvents() = true
}

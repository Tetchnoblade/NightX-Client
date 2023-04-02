package net.aspw.client.features.module.impl.combat

import net.aspw.client.event.EventTarget
import net.aspw.client.event.UpdateEvent
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.utils.RotationUtils
import net.aspw.client.utils.timer.MSTimer
import net.aspw.client.value.IntegerValue
import net.minecraft.item.ItemBow
import net.minecraft.network.play.client.C03PacketPlayer.C05PacketPlayerLook
import net.minecraft.network.play.client.C07PacketPlayerDigging
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing

@ModuleInfo(name = "FastBow", spacedName = "Fast Bow", category = ModuleCategory.COMBAT)
class FastBow : Module() {

    private val packetsValue = IntegerValue("Packets", 20, 3, 20)

    // :V i saw someone want to add delay
    private val delay = IntegerValue("Delay", 0, 0, 500, "ms")


    val timer = MSTimer()

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (!mc.thePlayer.isUsingItem)
            return

        if (mc.thePlayer.inventory.getCurrentItem() != null && mc.thePlayer.inventory.getCurrentItem().item is ItemBow) {
            mc.netHandler.addToSendQueue(
                C08PacketPlayerBlockPlacement(
                    BlockPos.ORIGIN,
                    255,
                    mc.thePlayer.currentEquippedItem,
                    0F,
                    0F,
                    0F
                )
            )

            val yaw = if (RotationUtils.targetRotation != null)
                RotationUtils.targetRotation!!.yaw
            else
                mc.thePlayer.rotationYaw

            val pitch = if (RotationUtils.targetRotation != null)
                RotationUtils.targetRotation!!.pitch
            else
                mc.thePlayer.rotationPitch
            for (i in 0 until packetsValue.get())
                mc.netHandler.addToSendQueue(C05PacketPlayerLook(yaw, pitch, true))
            if (timer.hasTimePassed(delay.get().toLong())) {
                mc.netHandler.addToSendQueue(
                    C07PacketPlayerDigging(
                        C07PacketPlayerDigging.Action.RELEASE_USE_ITEM,
                        BlockPos.ORIGIN,
                        EnumFacing.DOWN
                    )
                )
                timer.reset()
            }
            mc.thePlayer.itemInUseCount = mc.thePlayer.inventory.getCurrentItem().maxItemUseDuration - 1


        }
    }
}
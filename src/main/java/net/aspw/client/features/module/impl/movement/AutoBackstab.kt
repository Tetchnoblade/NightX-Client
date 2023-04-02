package net.aspw.client.features.module.impl.movement

import net.aspw.client.event.BlockBBEvent
import net.aspw.client.event.EventTarget
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.value.BoolValue
import net.minecraft.init.Blocks
import net.minecraft.util.AxisAlignedBB

@ModuleInfo(name = "AutoBackstab", spacedName = "Auto Backstab", category = ModuleCategory.MOVEMENT)
class AutoBackstab : Module() {
    private val cobwebValue = BoolValue("Cobweb", true)
    private val snowValue = BoolValue("Snow", true)

    @EventTarget
    fun onBlockBB(event: BlockBBEvent) {
        if (cobwebValue.get() && event.block == Blocks.web || snowValue.get() && event.block == Blocks.snow_layer)
            event.boundingBox = AxisAlignedBB.fromBounds(
                event.x.toDouble(), event.y.toDouble(), event.z.toDouble(),
                event.x + 1.0, event.y + 1.0, event.z + 1.0
            )
    }
}
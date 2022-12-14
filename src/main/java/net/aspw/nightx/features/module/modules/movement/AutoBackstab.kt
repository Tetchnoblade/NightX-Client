package net.aspw.nightx.features.module.modules.movement

import net.aspw.nightx.event.BlockBBEvent
import net.aspw.nightx.event.EventTarget
import net.aspw.nightx.features.module.Module
import net.aspw.nightx.features.module.ModuleCategory
import net.aspw.nightx.features.module.ModuleInfo
import net.aspw.nightx.value.BoolValue
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
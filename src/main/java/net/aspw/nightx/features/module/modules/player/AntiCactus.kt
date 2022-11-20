package net.aspw.nightx.features.module.modules.player

import net.aspw.nightx.event.BlockBBEvent
import net.aspw.nightx.event.EventTarget
import net.aspw.nightx.features.module.Module
import net.aspw.nightx.features.module.ModuleCategory
import net.aspw.nightx.features.module.ModuleInfo
import net.minecraft.block.BlockCactus
import net.minecraft.util.AxisAlignedBB

@ModuleInfo(name = "AntiCactus", spacedName = "Anti Cactus", category = ModuleCategory.PLAYER)
class AntiCactus : Module() {

    @EventTarget
    fun onBlockBB(event: BlockBBEvent) {
        if (event.block is BlockCactus)
            event.boundingBox = AxisAlignedBB(
                event.x.toDouble(), event.y.toDouble(), event.z.toDouble(),
                event.x + 1.0, event.y + 1.0, event.z + 1.0
            )
    }
}

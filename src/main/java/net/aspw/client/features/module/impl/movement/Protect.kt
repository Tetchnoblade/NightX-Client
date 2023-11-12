package net.aspw.client.features.module.impl.movement

import net.aspw.client.event.BlockBBEvent
import net.aspw.client.event.EventTarget
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.value.BoolValue
import net.minecraft.init.Blocks
import net.minecraft.util.AxisAlignedBB

@ModuleInfo(name = "Protect", description = "", category = ModuleCategory.MOVEMENT)
class Protect : Module() {
    private val fire = BoolValue("Fire", true)
    private val cobweb = BoolValue("Cobweb", true)
    private val cactus = BoolValue("Cactus", true)

    @EventTarget
    fun onBlockBB(e: BlockBBEvent) {
        when (e.block) {
            Blocks.fire -> if (!fire.get()) return

            Blocks.web -> if (!cobweb.get()) return

            Blocks.cactus -> if (!cactus.get()) return

            else -> return
        }

        e.boundingBox = AxisAlignedBB(e.x.toDouble(), e.y.toDouble(), e.z.toDouble(), e.x + 1.0, e.y + 1.0, e.z + 1.0)
    }
}
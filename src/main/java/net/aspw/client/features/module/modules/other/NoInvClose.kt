package net.aspw.client.features.module.modules.other

import net.aspw.client.event.EventTarget
import net.aspw.client.event.PacketEvent
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.minecraft.client.gui.inventory.GuiInventory
import net.minecraft.network.play.server.S2EPacketCloseWindow

@ModuleInfo(name = "NoInvClose", spacedName = "NoInv Close", category = ModuleCategory.OTHER, array = false)
class NoInvClose : Module() {
    @EventTarget
    fun onPacket(event: PacketEvent) {
        if (mc.theWorld == null || mc.thePlayer == null) return

        if (event.packet is S2EPacketCloseWindow && mc.currentScreen is GuiInventory)
            event.cancelEvent()
    }

    init {
        state = true
    }
}
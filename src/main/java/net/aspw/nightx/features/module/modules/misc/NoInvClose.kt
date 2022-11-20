package net.aspw.nightx.features.module.modules.misc

import net.aspw.nightx.event.EventTarget
import net.aspw.nightx.event.PacketEvent
import net.aspw.nightx.features.module.Module
import net.aspw.nightx.features.module.ModuleCategory
import net.aspw.nightx.features.module.ModuleInfo
import net.minecraft.client.gui.inventory.GuiInventory
import net.minecraft.network.play.server.S2EPacketCloseWindow

@ModuleInfo(name = "NoInvClose", spacedName = "NoInv Close", category = ModuleCategory.MISC)
class NoInvClose : Module() {
    @EventTarget
    fun onPacket(event: PacketEvent) {
        if (mc.theWorld == null || mc.thePlayer == null) return

        if (event.packet is S2EPacketCloseWindow && mc.currentScreen is GuiInventory)
            event.cancelEvent()
    }
}
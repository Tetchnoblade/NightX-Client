package net.aspw.client.features.module.impl.other

import net.aspw.client.event.EventTarget
import net.aspw.client.event.KeyEvent
import net.aspw.client.event.PacketEvent
import net.aspw.client.event.ScreenEvent
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.util.PacketUtils
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.client.gui.inventory.GuiInventory
import net.minecraft.network.play.client.C0DPacketCloseWindow
import net.minecraft.network.play.server.S2EPacketCloseWindow
import org.lwjgl.input.Keyboard

@ModuleInfo(name = "KeepChest", spacedName = "Keep Chest", description = "", category = ModuleCategory.OTHER)
class KeepChest : Module() {

    private var container: GuiContainer? = null

    override fun onDisable() {
        if (container != null)
            PacketUtils.sendPacketNoEvent(C0DPacketCloseWindow(container!!.inventorySlots.windowId))

        container = null
    }

    @EventTarget
    fun onGui(event: ScreenEvent) {
        if (event.guiScreen is GuiContainer && event.guiScreen !is GuiInventory)
            container = event.guiScreen
    }

    @EventTarget
    fun onKey(event: KeyEvent) {
        if (event.key == Keyboard.KEY_INSERT) {
            if (container == null)
                return

            mc.displayGuiScreen(container)
        }
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        if (event.packet is C0DPacketCloseWindow)
            event.cancelEvent()
        else if (event.packet is S2EPacketCloseWindow) {
            if (event.packet.windowId == container?.inventorySlots?.windowId)
                container = null
        }
    }
}
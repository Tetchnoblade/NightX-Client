package net.aspw.nightx.features.command.commands

import net.aspw.nightx.features.command.Command
import net.aspw.nightx.ui.client.GuiMainMenu
import net.minecraft.client.gui.GuiMultiplayer
import net.minecraft.client.multiplayer.GuiConnecting
import net.minecraft.client.multiplayer.ServerData

class ConnectCommand : Command("connect", emptyArray()) {

    /**
     * Execute commands with provided [args]
     */
    override fun execute(args: Array<String>) {
        if (args.size == 3 && args[2].equals("silent")) {
            chat("Connecting...")
            mc.displayGuiScreen(GuiConnecting(GuiMultiplayer(GuiMainMenu()), mc, ServerData("", args[1], false)))
        } else if (args.size == 2) {
            chat("Logging in...")
            mc.theWorld.sendQuittingDisconnectingPacket()
            mc.displayGuiScreen(GuiConnecting(GuiMultiplayer(GuiMainMenu()), mc, ServerData("", args[1], false)))
        } else
            chatSyntax("connect <ip:port> (silent)")
    }

}
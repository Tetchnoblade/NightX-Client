package net.aspw.nightx.features.command.commands

import net.aspw.nightx.features.command.Command

class LoginCommand : Command("login", arrayOf("l")) {
    /**
     * Execute commands with provided [args]
     */
    override fun execute(args: Array<String>) {
        mc.thePlayer.sendChatMessage("/register rrrr rrrr")
        mc.thePlayer.sendChatMessage("/login rrrr")
    }
}
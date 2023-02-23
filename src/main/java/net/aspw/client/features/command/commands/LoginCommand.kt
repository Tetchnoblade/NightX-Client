package net.aspw.client.features.command.commands

import net.aspw.client.Client
import net.aspw.client.features.command.Command
import net.aspw.client.features.module.modules.client.Hud

class LoginCommand : Command("login", arrayOf("l")) {
    /**
     * Execute commands with provided [args]
     */
    override fun execute(args: Array<String>) {
        if (Client.moduleManager.getModule(Hud::class.java)?.flagSoundValue!!.get()) {
            Client.tipSoundManager.popSound.asyncPlay(90f)
        }
        mc.thePlayer.sendChatMessage("/register rrrr rrrr")
        mc.thePlayer.sendChatMessage("/login rrrr")
    }
}
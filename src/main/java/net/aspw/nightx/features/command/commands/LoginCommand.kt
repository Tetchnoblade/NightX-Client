package net.aspw.nightx.features.command.commands

import net.aspw.nightx.NightX
import net.aspw.nightx.features.command.Command
import net.aspw.nightx.features.module.modules.client.Hud

class LoginCommand : Command("login", arrayOf("l")) {
    /**
     * Execute commands with provided [args]
     */
    override fun execute(args: Array<String>) {
        if (NightX.moduleManager.getModule(Hud::class.java)?.flagSoundValue!!.get()) {
            NightX.tipSoundManager.popSound.asyncPlay(90f)
        }
        mc.thePlayer.sendChatMessage("/register rrrr rrrr")
        mc.thePlayer.sendChatMessage("/login rrrr")
    }
}
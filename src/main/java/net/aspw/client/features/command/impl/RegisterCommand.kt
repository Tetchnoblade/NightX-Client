package net.aspw.client.features.command.impl

import net.aspw.client.Launch
import net.aspw.client.features.command.Command
import net.aspw.client.features.module.impl.visual.Interface

class RegisterCommand : Command("register", arrayOf("r")) {
    /**
     * Execute commands with provided [args]
     */
    override fun execute(args: Array<String>) {
        if (Launch.moduleManager.getModule(Interface::class.java)?.flagSoundValue!!.get()) {
            Launch.tipSoundManager.popSound.asyncPlay(Launch.moduleManager.popSoundPower)
        }
        mc.thePlayer.sendChatMessage("/register rrrrr rrrrr")
        chat("Registered with <rrrrrr> !")
    }
}
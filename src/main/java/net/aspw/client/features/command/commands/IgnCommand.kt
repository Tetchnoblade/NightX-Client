package net.aspw.client.features.command.commands

import net.aspw.client.Client
import net.aspw.client.features.command.Command
import net.aspw.client.features.module.modules.client.Hud
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

class IgnCommand : Command("ign", emptyArray()) {
    /**
     * Execute commands with provided [args]
     */
    override fun execute(args: Array<String>) {
        val username = mc.thePlayer.name

        if (Client.moduleManager.getModule(Hud::class.java)?.flagSoundValue!!.get()) {
            Client.tipSoundManager.popSound.asyncPlay(90f)
        }
        chat("Copied Username: $username")

        val stringSelection = StringSelection(username)
        Toolkit.getDefaultToolkit().systemClipboard.setContents(stringSelection, stringSelection)
    }
}
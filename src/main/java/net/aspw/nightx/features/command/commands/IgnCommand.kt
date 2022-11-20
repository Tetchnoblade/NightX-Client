package net.aspw.nightx.features.command.commands

import net.aspw.nightx.features.command.Command
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

class IgnCommand : Command("ign", emptyArray()) {
    /**
     * Execute commands with provided [args]
     */
    override fun execute(args: Array<String>) {
        val username = mc.thePlayer.name

        chat("Copied Username: $username")

        val stringSelection = StringSelection(username)
        Toolkit.getDefaultToolkit().systemClipboard.setContents(stringSelection, stringSelection)
    }
}
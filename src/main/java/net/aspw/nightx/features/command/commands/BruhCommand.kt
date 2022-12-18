package net.aspw.nightx.features.command.commands

import net.aspw.nightx.features.command.Command

class BruhCommand : Command("bruh", emptyArray()) {
    /**
     * Execute commands with provided [args]
     */
    override fun execute(args: Array<String>) {
        chat("Bruh")
    }
}
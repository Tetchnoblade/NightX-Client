package net.aspw.nightx.features.command.commands

import net.aspw.nightx.NightX
import net.aspw.nightx.features.command.Command

class PrefixCommand : Command("prefix", emptyArray()) {
    /**
     * Execute commands with provided [args]
     */
    override fun execute(args: Array<String>) {
        if (args.size <= 1) {
            chatSyntax("prefix <character>")
            return
        }

        val prefix = args[1]

        if (prefix.length > 1) {
            chat("§cPrefix can only be one character long!")
            return
        }

        NightX.commandManager.prefix = prefix.single()
        NightX.fileManager.saveConfig(NightX.fileManager.valuesConfig)

        chat("Successfully changed command prefix to '§8$prefix§3'")
    }
}
package net.aspw.client.features.command.impl

import net.aspw.client.Client
import net.aspw.client.features.command.Command

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

        Client.commandManager.prefix = prefix.single()
        Client.fileManager.saveConfig(Client.fileManager.valuesConfig)

        chat("Successfully changed command prefix to '§8$prefix§3'")
    }
}
package net.aspw.client.features.command.impl

import joptsimple.internal.Strings
import net.aspw.client.Client
import net.aspw.client.features.command.Command
import net.aspw.client.utils.ClientUtils

class HelpCommand : Command("help", emptyArray()) {
    /**
     * Execute commands with provided [args]
     */
    override fun execute(args: Array<String>) {
        var page = 1


        if (args.size > 1) {
            try {
                page = args[1].toInt()
            } catch (e: NumberFormatException) {
                chatSyntaxError()
            }
        }

        if (page <= 0) {
            chat("The number you have entered is too low, it must be over 0")
            return
        }

        val maxPageDouble = Client.commandManager.commands.size / 8.0
        val maxPage = if (maxPageDouble > maxPageDouble.toInt())
            maxPageDouble.toInt() + 1
        else
            maxPageDouble.toInt()

        if (page > maxPage) {
            chat("The number you have entered is too big, it must be under $maxPage.")
            return
        }

        chat("§c§lHelp")
        ClientUtils.displayChatMessage(Client.CLIENT_CHAT + "§7Page: §8$page / $maxPage")

        val commands = Client.commandManager.commands.sortedBy { it.command }

        var i = 8 * (page - 1)
        while (i < 8 * page && i < commands.size) {
            val command = commands[i]

            ClientUtils.displayChatMessage(
                Client.CLIENT_CHAT + "§7${Client.commandManager.prefix}${command.command}${
                    if (command.alias.isEmpty()) "" else " §7(§8" + Strings.join(
                        command.alias,
                        "§7, §8"
                    ) + "§7)"
                }"
            )
            i++
        }

        ClientUtils.displayChatMessage("§a------------\n§7> §c${Client.commandManager.prefix}help §8<§7§lpage§8>")
    }
}
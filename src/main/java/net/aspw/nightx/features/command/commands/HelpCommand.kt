package net.aspw.nightx.features.command.commands

import joptsimple.internal.Strings
import net.aspw.nightx.NightX
import net.aspw.nightx.features.command.Command
import net.aspw.nightx.utils.ClientUtils

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

        val maxPageDouble = NightX.commandManager.commands.size / 8.0
        val maxPage = if (maxPageDouble > maxPageDouble.toInt())
            maxPageDouble.toInt() + 1
        else
            maxPageDouble.toInt()

        if (page > maxPage) {
            chat("The number you have entered is too big, it must be under $maxPage.")
            return
        }

        chat("§c§lHelp")
        ClientUtils.displayChatMessage("§c>>§7 Page: §8$page / $maxPage")

        val commands = NightX.commandManager.commands.sortedBy { it.command }

        var i = 8 * (page - 1)
        while (i < 8 * page && i < commands.size) {
            val command = commands[i]

            ClientUtils.displayChatMessage(
                "§c>> §7${NightX.commandManager.prefix}${command.command}${
                    if (command.alias.isEmpty()) "" else " §7(§8" + Strings.join(
                        command.alias,
                        "§7, §8"
                    ) + "§7)"
                }"
            )
            i++
        }

        ClientUtils.displayChatMessage("§a------------\n§7> §c${NightX.commandManager.prefix}help §8<§7§lpage§8>")
    }
}
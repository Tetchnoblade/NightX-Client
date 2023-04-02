package net.aspw.client.features.command.impl

import net.aspw.client.Client
import net.aspw.client.features.command.Command
import net.aspw.client.utils.misc.StringUtils

class ShortcutCommand : Command("shortcut", arrayOf()) {
    /**
     * Execute commands with provided [args]
     */
    override fun execute(args: Array<String>) {
        when {
            args.size > 3 && args[1].equals("add", true) -> {
                try {
                    Client.commandManager.registerShortcut(
                        args[2],
                        StringUtils.toCompleteString(args, 3)
                    )

                    chat("Successfully added shortcut.")
                } catch (e: IllegalArgumentException) {
                    chat(e.message!!)
                }
            }

            args.size >= 3 && args[1].equals("remove", true) -> {
                if (Client.commandManager.unregisterShortcut(args[2]))
                    chat("Successfully removed shortcut.")
                else
                    chat("Shortcut does not exist.")
            }

            else -> chat("shortcut <add <shortcut_name> <script>/remove <shortcut_name>>")
        }
    }
}

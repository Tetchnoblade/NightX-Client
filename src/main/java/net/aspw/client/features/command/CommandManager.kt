package net.aspw.client.features.command

import net.aspw.client.Client
import net.aspw.client.features.command.api.XrayCommand
import net.aspw.client.features.command.impl.*
import net.aspw.client.features.command.shortcuts.Shortcut
import net.aspw.client.features.command.shortcuts.ShortcutParser
import net.aspw.client.utils.ClientUtils

class CommandManager {
    val commands = mutableListOf<Command>()
    var latestAutoComplete: Array<String> = emptyArray()

    var prefix = '.'

    /**
     * Register all default commands
     */
    fun registerCommands() {
        registerCommand(BindCommand())
        registerCommand(VClipCommand())
        registerCommand(HelpCommand())
        registerCommand(SayCommand())
        registerCommand(MacroCommand())
        registerCommand(ShortcutCommand())
        registerCommand(FriendCommand())
        registerCommand(ConfigCommand())
        registerCommand(ToggleCommand())
        registerCommand(TacoCommand())
        registerCommand(BindsCommand())
        registerCommand(PingCommand())
        registerCommand(ReloadCommand())
        registerCommand(PrefixCommand())
        registerCommand(HideCommand())
        registerCommand(TeleportCommand())
        registerCommand(EnchantCommand())
        registerCommand(GiveCommand())
        registerCommand(HurtCommand())
        registerCommand(RemoteViewCommand())
        registerCommand(RenameCommand())
        registerCommand(IgnCommand())
        registerCommand(XrayCommand())
        registerCommand(ThemeCommand())
        registerCommand(LoginCommand())
        registerCommand(ClipCommand())
        registerCommand(RepeatCommand())
        registerCommand(StuckCommand())
    }

    /**
     * Execute command by given [input]
     */
    fun executeCommands(input: String) {
        for (command in commands) {
            val args = input.split(" ").toTypedArray()

            if (args[0].equals(prefix.toString() + command.command, ignoreCase = true)) {
                command.execute(args)
                return
            }

            for (alias in command.alias) {
                if (!args[0].equals(prefix.toString() + alias, ignoreCase = true))
                    continue

                command.execute(args)
                return
            }
        }

        ClientUtils.displayChatMessage(Client.CLIENT_CHAT + "§cUnknown command. Try ${prefix}help for a list of commands.")
    }

    /**
     * Updates the [latestAutoComplete] array based on the provided [input].
     *
     * @param input text that should be used to check for auto completions.
     * @author NurMarvin
     */
    fun autoComplete(input: String): Boolean {
        this.latestAutoComplete = this.getCompletions(input) ?: emptyArray()
        return input.startsWith(this.prefix) && this.latestAutoComplete.isNotEmpty()
    }

    /**
     * Returns the auto completions for [input].
     *
     * @param input text that should be used to check for auto completions.
     * @author NurMarvin
     */
    private fun getCompletions(input: String): Array<String>? {
        if (input.isNotEmpty() && input.toCharArray()[0] == this.prefix) {
            val args = input.split(" ")

            return if (args.size > 1) {
                val command = getCommand(args[0].substring(1))
                val tabCompletions = command?.tabComplete(args.drop(1).toTypedArray())

                tabCompletions?.toTypedArray()
            } else {
                val rawInput = input.substring(1)
                commands
                    .filter {
                        it.command.startsWith(rawInput, true)
                                || it.alias.any { alias -> alias.startsWith(rawInput, true) }
                    }
                    .map {
                        val alias: String = if (it.command.startsWith(rawInput, true))
                            it.command
                        else {
                            it.alias.first { alias -> alias.startsWith(rawInput, true) }
                        }

                        this.prefix + alias
                    }
                    .toTypedArray()
            }
        }
        return null
    }

    /**
     * Get command instance by given [name]
     */
    fun getCommand(name: String): Command? {
        return commands.find {
            it.command.equals(name, ignoreCase = true)
                    || it.alias.any { alias -> alias.equals(name, true) }
        }
    }

    /**
     * Register [command] by just adding it to the commands registry
     */
    fun registerCommand(command: Command) = commands.add(command)

    fun registerShortcut(name: String, script: String) {
        if (getCommand(name) == null) {
            registerCommand(Shortcut(name, ShortcutParser.parse(script).map {
                val command = getCommand(it[0]) ?: throw IllegalArgumentException("Command ${it[0]} not found!")

                Pair(command, it.toTypedArray())
            }))

            Client.fileManager.saveConfig(Client.fileManager.shortcutsConfig)
        } else {
            throw IllegalArgumentException("Command already exists!")
        }
    }

    fun unregisterShortcut(name: String): Boolean {
        val removed = commands.removeIf {
            it is Shortcut && it.command.equals(name, ignoreCase = true)
        }

        Client.fileManager.saveConfig(Client.fileManager.shortcutsConfig)

        return removed
    }
}

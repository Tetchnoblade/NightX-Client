package net.aspw.client.features.command

import net.aspw.client.Client
import net.aspw.client.features.command.impl.*
import net.aspw.client.util.ClientUtils

class CommandManager {
    val commands = mutableListOf<Command>()
    var latestAutoComplete: Array<String> = emptyArray()

    /**
     * Register all default commands
     */
    fun registerCommands() {
        registerCommand(BindCommand())
        registerCommand(VClipCommand())
        registerCommand(SayCommand())
        registerCommand(MacroCommand())
        registerCommand(FriendCommand())
        registerCommand(ConfigCommand())
        registerCommand(ToggleCommand())
        registerCommand(BindsCommand())
        registerCommand(PingCommand())
        registerCommand(ReloadCommand())
        registerCommand(HideCommand())
        registerCommand(TeleportCommand())
        registerCommand(EnchantCommand())
        registerCommand(GiveCommand())
        registerCommand(DamageCommand())
        registerCommand(RemoteViewCommand())
        registerCommand(RenameCommand())
        registerCommand(IgnCommand())
        registerCommand(ThemeCommand())
        registerCommand(LoginCommand())
        registerCommand(ClipCommand())
        registerCommand(RegisterCommand())
        registerCommand(RepeatCommand())
        registerCommand(MagicTrickCommand())
        registerCommand(PluginsCommand())
        registerCommand(SkinStealerCommand())
        registerCommand(SpotCommand())
        registerCommand(HelpCommand())
        registerCommand(PayPayCommand())
    }

    /**
     * Execute command by given [input]
     */
    fun executeCommands(input: String) {
        for (command in commands) {
            val args = input.split(" ").toTypedArray()

            if (args[0].equals("." + command.command, ignoreCase = true)) {
                command.execute(args)
                return
            }

            for (alias in command.alias) {
                if (!args[0].equals(".$alias", ignoreCase = true))
                    continue

                command.execute(args)
                return
            }
        }

        ClientUtils.displayChatMessage(Client.CLIENT_CHAT + "§cUnknown command. Try .help for a list of commands.")
    }

    fun autoComplete(input: String): Boolean {
        this.latestAutoComplete = this.getCompletions(input) ?: emptyArray()
        return input.startsWith(".") && this.latestAutoComplete.isNotEmpty()
    }

    private fun getCompletions(input: String): Array<String>? {
        if (input.isNotEmpty() && input.toCharArray()[0] == '.') {
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

                        ".$alias"
                    }
                    .toTypedArray()
            }
        }
        return null
    }

    private fun getCommand(name: String): Command? {
        return commands.find {
            it.command.equals(name, ignoreCase = true)
                    || it.alias.any { alias -> alias.equals(name, true) }
        }
    }

    /**
     * Register [command] by just adding it to the commands registry
     */
    fun registerCommand(command: Command) = commands.add(command)

    /**
     * Unregister [command] by just removing it from the commands registry
     */
    fun unregisterCommand(command: Command?) = commands.remove(command)
}
